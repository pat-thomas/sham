(ns sham.session
  (:require [cheshire.core  :as cheshire]
            [compojure.core :as compojure]))

(def session-state (atom {:status "NotAuthorized"}))

(defn serialize-session
  []
  (-> session-state
      deref
      cheshire/encode))

(def session-get
  (compojure/GET "/session" [] (serialize-session)))

(def session-post
  (compojure/POST "/session" request (let [status (-> request :body slurp cheshire/decode (get "status"))]
                                       (do (swap! session-state assoc :status status)
                                           (serialize-session)))))

(def mock-session-routes
  ;; need to also implement post route
  {:get  session-get
   :post session-post})
