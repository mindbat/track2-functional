(ns chatter.handler-test
  (:require [clojure.test :as test :refer [deftest are is testing run-tests]]
            [ring.mock.request :as mock]
            [chatter.handler :refer :all]
            [clj-http.client :as http]
            [net.cgrand.enlive-html :as html]
            [net.cgrand.jsoup :as jsoup]))


(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))

(deftest test-safe-inc
  (is (= 1 (safe-inc nil)))
  (is (= 1 (safe-inc 0)))
  (is (= 2 (safe-inc 1))))


(defn simulate-conversation
  "This function will simulate a number of participants
simultaneously partipating in a chat.
Arguments:
  constructor:   A function that will create a new conversation
  message-limit: The maximum number of messages to keep as history
  add-message:   A function for adding a message to the conversation
  message-count: The total number of messages to add to the conversation"
  [constructor message-limit add-message message-count]
  (let [conv     (constructor message-limit)
        names    ["Alice" "Bob" "Cindy" "Doug"]
        messages ["Hello"
                  "Good news everyone!"
                  "What are we going to do tonight Brain?"]]
    (doall
      (pmap (partial add-message conv)
            (take message-count (cycle names))
            (take message-count (cycle messages))
            ))
    conv))


(deftest parallel-add-messages
  (let [messages 50000
        message-limit 20]
    (are [constructor add-message extract]
         (let [start  (System/currentTimeMillis)
               result (simulate-conversation constructor
                                             message-limit
                                             add-message
                                             messages)
               finish (System/currentTimeMillis)
               conversation (extract result)]
           (printf "\nSimulation completed in %dms: %s %s\n"
                   (- finish start) 'constructor 'add-message)

           (is (= messages (:total conversation))
               (format "Total Message Count must match iteration count: %s %s"
                       'constructor 'add-message))

           (is (= messages (reduce + (vals (:counts conversation))))
               (format "Sum of user message counts must match iteration count: %s %s"
                       'constructor 'add-message))

           (is (= message-limit (:limit conversation) )
               (format "Conversation's limit must match expected message limit: %s %s"
                       'constructor 'add-message))

           (is (= message-limit (count (:messages conversation)))
               (format "Number of messages must equal the message limit: %s %s"
                       'constructor 'add-message)))

         ;;  This will likely throw exceptions and not work at all
         ;new-mutable-conversation-db
         ;mutating-add-message
         ;identity

         ;;  This won't throw exceptions, but usually fails with
         ;;  random, wrong results
         ;new-mutable-concurrent-conversation-db
         ;mutating-add-message
         ;identity

         ;;  This should work
         new-mutable-conversation-db
         locking-add-message
         identity

         ;;  This should work too
         ;new-mutable-concurrent-conversation-db
         ;locking-add-message
         ;identity

         ;;  This should work and should perform noticeably better
         ;new-atomic-conversation-db
         ;atomic-add-message
         ;deref
         )))


(defn integer-value
  "Convert a string or other value into an integer"
  [x]
  (cond (number? x) (.longValue x)
        (string? x) (try (Long/parseLong x)
                      (catch Exception e 0))
        (nil? x)    0
        :else       (recur (str x))))


(defn extract-chat-messages
  "Extract the message history from the HTML of a chat page"
  [chat-html]
  (let [msg-rows (html/select chat-html [:#messages :tr])]
    (html/let-select msg-rows
      [name [:td.name html/text]
       msg  [:td.message html/text]]
      {:name (first name)
       :message (first msg)})))


(defn extract-chat-user-counts
  "Extract the per-user message counts from the HTML of a chat page"
  [chat-html]
  (let [count-rows (html/select
                     chat-html [:#stats :tr.count])]
    (into {}
          (html/let-select count-rows
            [name   [:td.name html/text]
             counts [:td.count html/text]]
            [(first name) (integer-value (first counts))]))))


(defn parse-chat-response
  "Take the response from a chat server and turn it back into
conversation data"
  [response]
  (let [chat-html (jsoup/parser (:body response))
        messages  (extract-chat-messages chat-html)
        counts    (extract-chat-user-counts chat-html)
        total     (integer-value
                    (first
                      (html/select chat-html [:#total html/text])))]
    {:messages    messages
     :counts      counts
     :total       total}))


(defn post-message
  "Post a new message to a chat server.
Returns the HTML response converted back into conversation data."
  [url name message]
  (parse-chat-response
    (http/post url {:form-params {:name name :msg message}
                    :as :stream})))


(defn read-messages
  "Read the current messages on a chat server.
Returns the HTML response converted back into conversation data."
  [url]
  (parse-chat-response
    (http/get url {:as :stream})))


(defn post-message-and-check
  "Post a message to a chat server, and sanity check the response"
  [url name message]
  (let [expected-message {:name name :message message}
        conv (post-message url name message)]
    (and (is (some (partial = expected-message)
                   (:messages conv))
             "Must see my own new message")

         (is (= (:total conv)
                (reduce + (vals (:counts conv))))
             "Total must equal the sum of the user counts"))
    ))


(defn chat-bot [url message-count names messages]
  (doall
    (pmap (partial post-message-and-check url)
          (take message-count (cycle names))
          (take message-count (cycle messages))))
  (read-messages url))

#_(chat-bot "http://localhost:8000/"
           50
           ["Thing One" "Thing Two"]
           ["They can"
            "Find Anything Anything Anything"
            "Under the sun"])