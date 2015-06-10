(ns chatter.handler
  (:require [clojure.test   :refer [deftest run-tests is are]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.server.standalone :as server]
            [hiccup.page :as page]
            [hiccup.form :as form])

  (:import  [java.util HashMap LinkedList Map]
            [java.util.concurrent ConcurrentHashMap
                                  ConcurrentLinkedDeque]))

(defn new-mutable-conversation-db
  "Using simple, mutable Java collections, construct a new
Conversation Database that will hold a limited number of messages."
  [message-limit]
  (doto (HashMap.)
    (.put :limit    message-limit)
    (.put :total    0)
    (.put :counts   (HashMap.))
    (.put :messages (LinkedList.))))


(defn mutating-add-message
  "Using standard Java collections APIs, add a new message
to a conversation and perform all necessary accounting."
  [^Map conversation name new-message]
  (let [limit    (.get conversation :limit)
        total    (.get conversation :total)
        counts   (.get conversation :counts)
        messages (.get conversation :messages)]

    (doto counts
      (.put name (inc (.getOrDefault counts name 0))))

    (doto messages
      (.addFirst (doto (HashMap.)
                   (.put :name name)
                   (.put :message new-message))))

    (loop []
      (when (< limit (.size messages))
            (.removeLast messages)
            (recur)))

    (doto conversation
      (.put :total (inc total)))))


(defn new-mutable-concurrent-conversation-db
  "Using Java concurrent collections, construct a new
Conversation Database that will hold a limited number
of messages"
  [message-limit]
  (doto (ConcurrentHashMap.)
    (.put :limit    message-limit)
    (.put :total    0)
    (.put :counts   (ConcurrentHashMap.))
    (.put :messages (ConcurrentLinkedDeque.))))


(defn locking-add-message
  "Add a message to a conversation after locking
the conversation"
  [conv name message]
  (locking conv
    (mutating-add-message conv name message)))

(def safe-inc
  "Increments a number.  Treats nil as 0"
  (fnil inc 0))

(defn immutable-add-message
  "Uses immutable Clojure collections to combine a
conversation and a new message into a new conversation"
  [{:keys [limit total counts messages] :as conversation} name message]
  {:limit    limit
   :total    (safe-inc total)
   :counts   (update-in counts [name] safe-inc)
   :messages (take limit
                   (cons {:name name :message message}
                         messages))})
  
(defn new-atomic-conversation-db
  "Construct a new Atomic Conversation Database: 
Conversation data wrapped in an Atom"
  [message-limit]
  (atom {:limit message-limit}))

(defn atomic-add-message
  "Add a message to an Atomic Conversation Database"
  [conversation-atom name message]
  (swap! conversation-atom immutable-add-message name message))

(defn desc-sort-by [keyfn coll]
  (sort-by keyfn
           (fn [a b] (compare b a))
           coll))


(defn generate-message-view
  "This generates the HTML for displaying messages"
  ([conversation]
    (generate-message-view conversation ""))
  ([conversation name]
    (page/html5
      [:head
       [:title "chatter"]
       [:meta {:http-equiv "refresh" :content "10"}]
       (page/include-css "//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css")
       (page/include-js  "//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js")
       (page/include-css "/chatter.css")]
      [:body
       [:h1 "Our Chat App"]
       [:p
        (form/form-to
          [:post "/"]
          "Name: " (form/text-field {:id "name" :value name} "name")
          [:br]
          "Message: " (form/text-field {:id "msg"} "msg")
          [:br]
          (form/submit-button "Submit"))]
       [:p
        [:table#messages.table.table-striped
         (map (fn [m] [:tr [:td.name (:name m)]
                           [:td.message (:message m)]])
              (:messages conversation))]]
       [:p [:h3 "Conversation Statistics"]
        [:table#stats.table.table-striped
         (cons [:tr [:td.name "Total Messages"]
                    [:td#total (:total conversation 0)]]
               (map (fn [[name count]]
                      [:tr.count
                       [:td.name name]
                       [:td.count count]])
                    (desc-sort-by
                      val (:counts conversation))))]]])))

(defonce CONVERSATION-DB
  (new-atomic-conversation-db 20))

(defroutes app-routes
  (GET "/" [] (generate-message-view @CONVERSATION-DB))
  (POST "/" {params :params}
    (let [name-param (get params "name")
          msg-param (get params "msg")
          new-messages (atomic-add-message CONVERSATION-DB
                                           name-param msg-param)]
      (generate-message-view new-messages name-param)
      ))
  (route/resources "/")
  (route/not-found "Not Found"))


(defn wrap-with-lock
  "Ensure concurrent requests don't compete with each"
  [handler]
  (fn [request]
    (locking handler
      (handler request))))


(def app
  (wrap-params app-routes))



(defonce SERVER (atom nil))

(defn start-server
  "Start a web server for the application"
  []
  (swap! SERVER
         (fn [s]
           (if (nil? s)
              (server/serve #'app {:port 8000})
              s))))

(defn stop-server
  "Stop the application's web server"
  []
  (swap! SERVER
         (fn [s]
           (when (some? s)
             (.stop s)))))