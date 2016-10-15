(ns chatter.handler
  (:require [clojure.test :refer [deftest run-tests is are]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [wrap-defaults
                                              site-defaults]]
            [ring.middleware.params :refer [wrap-params]]
            [hiccup.page :as page])
  (:import  [java.util HashMap LinkedList Map]
            [java.util.concurrent ConcurrentHashMap
                                  ConcurrentLinkedDeque]))

(defn new-mutable-conversation-db
  "Using simple, mutable Java collections, construct a new
   Conversation Database that will hold a limited number
   of messages."
  [message-limit]
  ;;;;;  Comments show comparable Java source
  ;;
  ;; HashMap conversation = new Conversation();
  ;; conversation.put( :limit, message-limit );
  ;; conversation.put( :total, 0 );
  ;; conversation.put( :counts, new HashMap() );
  ;; conversation.put( :messages, new LinkedList() );
  ;; return conversation;
  (doto (new HashMap)
    (.put :limit    message-limit)
    (.put :total    0)
    (.put :counts   (new HashMap))
    (.put :messages (new LinkedList))))

(defn mutating-add-message
  "Using standard Java collections APIs, add a new message
   to a conversation and perform all necessary accounting."
  [conversation name new-message]
  ;;;;;  Comments show comparable Java source
  ;;
  ;; Integer limit = (Integer) conversation.get(:limit);
  ;; Integer total = (Integer) conversation.get(:total);
  ;; Map counts    = (Map) conversation.get(:counts);
  ;; List          = (List) messages = conversation.get(:messages);
  (let [limit    (.get conversation :limit)
        total    (.get conversation :total)
        counts   (.get conversation :counts)
        messages (.get conversation :messages)]
    ;; counts.put( name,
    ;;             1 + counts.getOrDefault( name, 0 ) );
    (.put counts
      name (inc (.getOrDefault counts name 0)))
    ;; Map tmp = new HashMap();
    ;; tmp.put( :name, name );
    ;; tmp.put( :message, new-message );
    ;; messages.addFirst( tmp );
    (.addFirst messages
      (doto (new HashMap)
            (.put :name name)
            (.put :message new-message)))
    ;; while( limit < messages.size() ) {
    ;;    messages.removeLast();
    ;; }
    (loop []
      (when (< limit (.size messages))
            (.removeLast messages)
            (recur)))
    ;; conversation.put( :total, 1 + total );
    ;; return conversation;
    (doto conversation
      (.put :total (inc total)))))

(defn new-mutable-concurrent-conversation-db
  "Using Java concurrent collections, construct a new
   Conversation Database that will hold a limited number
   of messages"
  [message-limit]
  (doto (new ConcurrentHashMap)
    (.put :limit    message-limit)
    (.put :total    0)
    (.put :counts   (new ConcurrentHashMap))
    (.put :messages (new ConcurrentLinkedDeque))))

(defn locking-add-message
  "Add a message to a conversation after locking
   the conversation"
  [conversation name message]
  (locking conversation
    (mutating-add-message conversation name message)))

(def safe-inc
  "Increments a number.  Treats nil as 0"
  (fnil inc 0))

(defn new-immutable-conversation-db
  [message-limit]
  {:limit message-limit})

(defn immutable-add-message
  "Uses immutable Clojure collections to combine a
   conversation and a new message into a new conversation"
  [{:keys [limit total counts messages] :as conversation}
   name message]
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
  (atom (new-immutable-conversation-db message-limit)))

(defn atomic-add-message
  "Add a message to an Atomic Conversation Database"
  [conversation-atom name message]
  (swap! conversation-atom immutable-add-message name message))

(defn desc-sort-by
  "Sort a collection in descending order based on the
   result of applying key-fn to each member."
  [keyfn coll]
  (sort-by keyfn
           (fn [a b] (compare b a))
           coll))

(defn input-text
  "Return a Hiccup style input field with a label"
  ([name label]
    (input-text name label nil))
  ([name label value]
    [:div.form-group
     [:label.sr-only {:for name} label]
     [(keyword (format "input#%s.form-control" name))
      {:type "text" :name name
       :placeholder label
       :value value}]]))

(defn generate-message-view
  "This generates the HTML for displaying messages"
  ([conversation]
    (generate-message-view conversation nil))
  ([conversation name]
    (page/html5
      [:head
       [:title "chatter"]
       [:meta {:http-equiv "refresh" :content "10"}]
       (page/include-css
        "//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css")
       (page/include-js
        "//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js")
       (page/include-css "/chatter.css")]
      [:body
       [:div.container
        [:h1 "Our Chat App"] [:br]
        [:div.row
         [:form
          {:method "POST" :action "/"}
          [:div.col-xs-2.col-xs-offset-2
           (input-text :name "Your name" name)]
          [:div.col-xs-5
           (input-text :msg "What to say")]
          [:div.col-xs-1
           [:input#post.btn.btn-default
            {:type "submit" :value "Say It!"}]]]]
        [:div.row
         [:div.col-xs-8.col-md-9.col-lg-9
          [:h3 "Conversation"]
          [:table#messages.table.table-striped.table-condensed
           (map (fn [m] [:tr [:td.name (:name m)]
                             [:td.message (:message m)]])
                (:messages conversation))]]
         [:div.col-xs-4.col-md-3.col-lg-3
          [:h3 "Stats"]
          [:table#stats.table.table-striped.table-condensed
           (cons [:tr [:td.name "Total Messages"]
                      [:td#total (:total conversation 0)]]
                 (map (fn [[name count]]
                        [:tr.count
                         [:td.name name]
                         [:td.count count]])
                      (desc-sort-by
                        val (:counts conversation))))]]]]])))

(defonce CONVERSATION-DB
  (new-mutable-conversation-db 20))

(defn reset-conversation-db
  [constructor & constructor-args]
  (alter-var-root #'CONVERSATION-DB
                  (constantly
                    (apply constructor constructor-args))))

(defroutes app-routes
  (GET  "/" []
    (generate-message-view CONVERSATION-DB))
  (POST "/"
    {params :params}
    (let [name-param (get params "name")
          msg-param (get params "msg")
          new-messages (mutating-add-message CONVERSATION-DB
                         name-param msg-param)]
      (generate-message-view new-messages name-param)))
  (route/resources "/")
  (route/not-found "This is not the page you are looking for"))

(defn wrap-with-lock
  "Ensure concurrent requests don't compete with each"
  [handler]
  (fn [request]
    (locking CONVERSATION-DB
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
              (jetty/run-jetty #'app
                {:join? false
                 :port 3000
                 :host "0.0.0.0"})
              s))))

(defn stop-server
  "Stop the application's web server"
  []
  (swap! SERVER
         (fn [s]
           (when (some? s)
             (.stop s)))))
