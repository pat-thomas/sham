(ns sham.file
  (:require [cheshire.core :as cheshire]
            [clojure.walk  :as walk]))

(def ws-responses (atom nil))

(defn assign-ids
  [rows]
  (for [i (range (count rows))]
    (assoc (nth rows i) :id i)))

(defn file-data->tables
  [file-data]
  (->> file-data
       keys
       (reduce (fn [accum k]
                 (conj accum [k (-> file-data k keys)]))
               [])
       (map (fn [[table schemas]]
              (reduce (fn [accum schema]
                        (conj accum [table schema]))
                      []
                      schemas)))
       flatten
       (partition 2)))

(defn validate!
  []
  (let [parsed-file (cheshire/parse-stream (clojure.java.io/reader "./resources/ws_responses.json") keyword)
        data-rows   (dissoc parsed-file :tables)]
    (reduce (fn [accum [table schema]]
              (update-in accum [table schema] assign-ids))
            data-rows
            (file-data->tables data-rows))))


(defn load-responses!
  "File should be located at ./resources/ws_responses.json"
  [^java.util.Map validated-ws-reponses]
  (reset! ws-responses (assign-ids validated-ws-reponses)))

(defn init!
  []
  (let [validated-ws-responses (validate!)]
    (load-responses! validated-ws-responses)))
