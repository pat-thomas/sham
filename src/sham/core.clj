(ns sham.core
  (:require [sham.file      :as file]
            [sham.server    :as server]
            [sham.session   :as session]
            [cheshire.core  :as cheshire]
            [compojure.core :as compojure]
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

(defn register-mock-route
  [route-impl]
  (swap! app-routes conj route-impl))

(defn mock-get
  [resource-path]
  (register-mock-route
   (compojure/GET (keywords->resource-path-str resource-path) [] (fn [_]
                                                                   (-> file/ws-responses
                                                                       deref
                                                                       (get-in resource-path)
                                                                       cheshire/encode)))))

(def not-found-route
  (route/not-found "Resource not found."))

(defn register-mock-session-routes
  []
  (doseq [method [:get :post]]
    (register-mock-route (method session/mock-session-routes))))

(defn gen-mock-routes!
  []
  (register-mock-session-routes)
  (doseq [resource-path (file/file-data->tables (file/file-data-from-file))]
    (mock-get resource-path))
  (register-mock-route not-found-route))

(defonce web-server (atom nil))

(defn init!
  [{:keys [port] :as opts}]
  (file/load-responses!)
  (gen-mock-routes!)
  (reset! web-server (server/start-server! (apply compojure/routes @app-routes) port))
  :mock-server-started)

(defn reload!
  [{:keys [port] :as opts}]
  (do (when-not (nil? @web-server)
        (@web-server))
      (init! {:port port})))
