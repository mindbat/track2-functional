(defproject chatter "0.1.0-SNAPSHOT"
  :description "A web app for displaying posted messages"
  :min-lein-version "2.0.0"
    :dependencies [[org.clojure/clojure "1.7.0"]
                   [compojure "1.4.0"]
                   [ring/ring-defaults "0.1.5"]
                   [ring/ring-core "1.4.0"]
                   [ring/ring-jetty-adapter "1.4.0"]
                   [hiccup "1.0.5"]
                   [clj-http "2.0.0"]
                   [enlive "1.1.6"]]
  :plugins [[lein-ring "0.9.6" :exclusions [org.clojure/clojure]]]
  :ring {:handler chatter.handler/app}
  :profiles {:dev {:dependencies [[ring/ring-mock "0.2.0"]]}}
  )