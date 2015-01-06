(ns sham.core
  (:require [sham.file :as file]))

(defmacro mock-get
  [])

(defn init!
  []
  (file/load-responses!))
