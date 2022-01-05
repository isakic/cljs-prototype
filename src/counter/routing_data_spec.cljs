(ns counter.routing-data-spec
  (:require [cljs.test :refer-macros [deftest is]]
            [counter.routing-data :as routing-data :refer [->RoutingData ->Station ->Line ->Platform]]
            [counter.util :refer [coll->hashmap-by]]))

; L1
;     ╔S1╗              ╔S3╗     ╔S4╗     ╔S5╗
; H:╔═╬O0╬═>>>══════>>>═╬O1╬═>>>═╬O2╬═>>>═╬O3╬═╗
;   ║ ║  ║     ╔S2╗     ╚══╝     ║  ║     ║  ║ ║
; R:╚═║07║═<<<═╬O6╬═<<<══════<<<═╬O5╬═<<<═╬O4╬═╝
;     ╚══╝     ╚══╝              ╚══╝     ╚══╝


(def stations [(->Station "S1" "Station 1")
               (->Station "S2" "Station 2")
               (->Station "S3" "Station 3")
               (->Station "S4" "Station 4")
               (->Station "S5" "Station 5")])

(def lines [(->Line "L1" "Line 1")])

(def platforms [(->Platform "L1-S1-H" "L1" "S1" "H" 1 "00")
                (->Platform "L1-S3-H" "L1" "S3" "H" 2 "01")
                (->Platform "L1-S4-H" "L1" "S4" "H" 3 "02")
                (->Platform "L1-S5-H" "L1" "S5" "H" 4 "03")
                (->Platform "L1-S5-R" "L1" "S5" "R" 1 "04")
                (->Platform "L1-S4-R" "L1" "S4" "R" 2 "05")
                (->Platform "L1-S2-R" "L1" "S2" "R" 3 "06")
                (->Platform "L1-S1-R" "L1" "S1" "R" 4 "07")])

(def data (->RoutingData
           (coll->hashmap-by stations :id)
           (coll->hashmap-by lines :id)
           (coll->hashmap-by platforms :id)))

(deftest test-platform-queries-1
  (let [platform (nth platforms 0)
        expected [(nth platforms 0)
                  (nth platforms 1)
                  (nth platforms 2)
                  (nth platforms 3)]]
    (is (= (routing-data/get-platforms-in-same-direction data platform) expected))))

(deftest test-platform-queries-2
  (let [platform (nth platforms 0)
        expected [(nth platforms 4)
                  (nth platforms 5)
                  (nth platforms 6)
                  (nth platforms 7)]]
    (is (= (routing-data/get-platforms-in-other-direction data platform) expected))))

(deftest test-platform-queries-3
  (let [platform (nth platforms 0)
        expected (nth platforms 7)]
    (is (= (routing-data/get-reverse-platform data platform) expected))))

(deftest test-platform-queries-4
  (let [platform (nth platforms 1)
        expected nil]
    (is (= (routing-data/get-reverse-platform data platform) expected))))

(deftest test-platform-queries-5
  (let [platform (nth platforms 0)
        expected [(nth platforms 7)
                  (nth platforms 0)
                  (nth platforms 1)]]
    (is (= (routing-data/get-platforms data platform) expected))))

(deftest test-platform-queries-6
  (let [platform (nth platforms 1)
        expected (nth platforms 3)]
    (is (= (routing-data/get-last-platform data platform) expected))))

(deftest test-platform-queries-7
  (let [rbl-number "04"
        expected (nth platforms 04)]
    (is (= (routing-data/find-platform data rbl-number) expected))))

(deftest test-platform-queries-8
  (let [rbl-number "10"
        expected nil]
    (is (= (routing-data/find-platform data rbl-number) expected))))