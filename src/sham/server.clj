(ns sham.server
  (:require [org.httpkit.server :as http]))

(defn start-server!
  [app port]
  (http/run-server app {:port port}))
