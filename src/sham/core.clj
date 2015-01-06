(ns sham.core
  (:require [sham.file       :as file]
            [sham.server     :as server]
            [compojure.core  :as compojure]
            [compojure.route :as route]))

(def app-routes
  (atom 
   [(compojure/GET "/" [] "Hello world")]))

(defn keywords->resource-path-str
  [resource-path]
  (->> resource-path
       (map name)
       (clojure.string/join "/")
       (str "/")))

(defn mock-get
  [resource-path]
  (swap! app-routes conj (compojure/GET (keywords->resource-path-str resource-path) [] "hello")))

(defn gen-mock-routes
  [])

(defn init!
  [{:keys [port] :as opts}]
  (file/load-responses!)
  (server/start-server! (apply compojure/routes @app-routes) port)
  :mock-server-started)
