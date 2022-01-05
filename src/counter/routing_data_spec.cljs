(ns counter.routing-data-spec
  (:require [cljs.test :refer-macros [deftest are testing]]
            [counter.routing-data :as routing-data :refer [->RoutingData ->Station ->Line ->Platform]]
            [counter.util :refer [coll->hashmap-by]]))

; Route of line L1
;     ╔S1╗              ╔S3╗     ╔S4╗     ╔S5╗
; H:╔═╬O0╬═>>>══════>>>═╬O1╬═>>>═╬O2╬═>>>═╬O3╬═╗
;   ║ ║  ║     ╔S2╗     ╚══╝     ║  ║     ║  ║ ║
; R:╚═║07║═<<<═╬O6╬═<<<══════<<<═╬O5╬═<<<═╬O4╬═╝
;     ╚══╝     ╚══╝              ╚══╝     ╚══╝

(def parsed-stations [(->Station "S1" "Station 1")
                      (->Station "S2" "Station 2")
                      (->Station "S3" "Station 3")
                      (->Station "S4" "Station 4")
                      (->Station "S5" "Station 5")])

(def parsed-lines [(->Line "L1" "Line 1")
                   (->Line "L2" "Line 2")]) ; Line L2 is not constructed yet, has no platforms

(def parsed-platforms [(->Platform "L1-S1-H" "L1" "S1" "H" 1 "00")
                       (->Platform "L1-S3-H" "L1" "S3" "H" 2 "01")
                       (->Platform "L1-S4-H" "L1" "S4" "H" 3 "02")
                       (->Platform "L1-S5-H" "L1" "S5" "H" 4 "03")
                       (->Platform "L1-S5-R" "L1" "S5" "R" 1 "04")
                       (->Platform "L1-S4-R" "L1" "S4" "R" 2 "05")
                       (->Platform "L1-S2-R" "L1" "S2" "R" 3 "06")
                       (->Platform "L1-S1-R" "L1" "S1" "R" 4 "07")])

(defn platform [index] (nth parsed-platforms index))
(defn platforms [& indices] (map #(nth parsed-platforms %) indices))

; Shuffle collections when loading to eliminate reliance of queries on element order
(def data (->RoutingData
           (coll->hashmap-by (shuffle parsed-stations) :id)
           (coll->hashmap-by (shuffle parsed-lines) :id)
           (coll->hashmap-by (shuffle parsed-platforms) :id)))

(deftest routing-data
  (testing "get-platforms-in-same-direction"
    (let [query (partial routing-data/get-platforms-in-same-direction data)]

      (are [param] (every? #(= (:direction param) (:direction %)) (query param))
        (platform 0)
        (platform 4))

      (are [param result] (= (query param) result)
        (platform 0) (platforms 0 1 2 3)
        (platform 1) (platforms 0 1 2 3)
        (platform 4) (platforms 4 5 6 7))))

  (testing "get-platforms-in-other-direction"
    (let [query (partial routing-data/get-platforms-in-other-direction data)]

      (are [param] (every? #(not= (:direction param) (:direction %)) (query param))
        (platform 0)
        (platform 4))

      (are [param result] (= (query param) result)
        (platform 0) (platforms 4 5 6 7)
        (platform 1) (platforms 4 5 6 7)
        (platform 4) (platforms 0 1 2 3))))

  (testing "get-reverse-platform"
    (let [query (partial routing-data/get-reverse-platform data)]

      (are [param] (= param (query (query param)))
        (platform 0)
        (platform 5))

      (are [param] (not= (:direction param) (:direction (query param)))
        (platform 0)
        (platform 5))

      (are [param result] (= (query param) result)
        (platform 0) (platform 7)
        (platform 1) nil
        (platform 5) (platform 2)
        (platform 6) nil)))

  (testing "get-reverse-platform"
    (let [query (partial routing-data/get-platforms data)]

      (are [param result] (= (query param) result)
        (platform 0) (platforms 7 0 1)
        (platform 4) (platforms 3 4 5)
        (platform 7) (platforms 6 7 0))))

  (testing "get-last-platform"
    (let [query (partial routing-data/get-last-platform data)]

      (are [param result] (= (query param) result)
        (platform 0) (platform 3)
        (platform 1) (platform 3)
        (platform 3) (platform 3)
        (platform 4) (platform 7))))

  (testing "find-platform"
    (let [query (partial routing-data/find-platform data)]

      (are [param result] (= (query param) result)
        "04" (platform 4)
        "10" nil))))
