(ns counter.csv-spec
  (:require [cljs.test :refer-macros [deftest testing is]]
            [counter.csv :refer [parse-csv]]))

(def csv "STRING;NUMBER;EMPTY
          \"Lorem ipsum\";123;
          \"dolor sit amet\";456;")

(def expected [["Lorem ipsum" 123 nil]
               ["dolor sit amet" 456 nil]])

(deftest csv-spec
  (testing "parse-csv"
    (is (= (parse-csv csv) expected))))
