(ns counter.csv 
  (:require [clojure.string :as string]
            [clojure.edn :as edn]))

(defn convert-value [value]
  (cond
    (re-matches #"^\".*\"$" value) (edn/read-string value)
    (re-matches #"^\d+$" value) (edn/read-string value)
    :else nil))

(defn parse-value [value]
  (->> value
       string/trim
       convert-value))

(defn parse-csv ([contents]
  (->> contents
       string/split-lines
       rest ;skip header row
       (map #(string/split % #";" -1))
       (map #(map parse-value %)))))
