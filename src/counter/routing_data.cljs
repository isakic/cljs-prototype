(ns counter.routing-data
  (:require-macros
   [cljs.core.async.macros :refer [go]])
  (:require
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   [clojure.string :as string]
   [counter.util :refer [coll->hashmap-by =by not=by distinct-by]]
   [counter.csv :refer [parse-csv]]))

(def station-data-url "https://data.wien.gv.at/csv/wienerlinien-ogd-haltestellen.csv")

(def line-data-url "https://data.wien.gv.at/csv/wienerlinien-ogd-linien.csv")

(def platform-data-url "https://data.wien.gv.at/csv/wienerlinien-ogd-steige.csv")

(defrecord Station [id name])
(defn csv->Station [id _ _ name _ _ _ _ _] (->Station id name))

(defrecord Line [id name])
(defn csv->Line [id name _ _ _ _ _] (->Line id name))

(defrecord Platform [id line-id station-id direction order rbl-number])
(defn csv->Platform [id line-id station-id direction order rbl-number _ _ _ _ _] (->Platform id line-id station-id direction order rbl-number))

(defrecord RoutingData [stations lines platforms])

(defn get-station [data platform]
  ((:stations data) (:station-id platform)))

(defn get-line [data platform]
  ((:lines data) (:line-id platform)))

(defn find-platform [data rbl-number]
  (->> data
       :platforms
       vals
       (filter #(= (:rbl-number %) rbl-number))
       first))

(defn get-station-lines [data platform]
  (->> data
       :platforms
       vals
       (filter #(=by :station-id % platform))
       (map #(get-line data %))
       (distinct-by :id)))

(defn get-line-platform-at-station [data line-id station-id]
  (->> data
       :platforms
       vals
       (filter #(= (str (:line-id %)) (str line-id)))
       (filter #(= (str (:station-id %)) (str station-id)))
       first))

(defn get-platforms-in-same-direction [data platform]
  (->> data
       :platforms
       vals
       (filter #(and (=by :line-id % platform)
                     (=by :direction % platform)))
       (sort-by :order)))

(defn get-platforms-in-other-direction [data platform]
  (->> data
       :platforms
       vals
       (filter #(and (=by :line-id % platform)
                     (not=by :direction % platform)))
       (sort-by :order)))

(defn seek-platform [platforms platform]
  (let [prev (first platforms)
        curr (second platforms)
        next (nth platforms 2)]
    (if (=by :id curr platform)
      [prev curr next]
      (seek-platform (rest platforms) platform))))

(defn get-platforms [data platform]
  (let [same-direction (get-platforms-in-same-direction data platform)
        other-direction (get-platforms-in-other-direction data platform)
        wrapped-platforms (concat [(last other-direction)] same-direction [(first other-direction)])]
    (when platform (seek-platform wrapped-platforms platform))))

(defn get-last-platform
  "Returns the last platform of the same line in the same direction."
  [data platform]
  (last (get-platforms-in-same-direction data platform)))

(defn get-last-station
  "Returns the last station of the same line in the same direction."
  [data platform]
  (when platform
    (get-station data (get-last-platform data platform))))

(defn get-reverse-platform
  "Returns the platform of the same line at the same station, but in the opposite direction. 
   Returns nil if such platform does not exist."
  [data platform]
  (->> (get-platforms-in-other-direction data platform)
       (filter #(=by :station-id % platform))
       first))

(defn- fetch-csv [url]
  (go (->> (<! (http/get url {:with-credentials? false}))
           :body
           parse-csv)))

(defn fetch-routing-data []
  (go (let [stations (->> (fetch-csv station-data-url) <! (map #(apply csv->Station %)))
            lines (->> (fetch-csv line-data-url) <! (map #(apply csv->Line %)))
            platforms (->> (fetch-csv platform-data-url) <! (map #(apply csv->Platform %)) (filter #(not(string/blank? (:rbl-number %)))))]
        (->RoutingData
         (coll->hashmap-by stations :id)
         (coll->hashmap-by lines :id)
         (coll->hashmap-by platforms :id)))))
