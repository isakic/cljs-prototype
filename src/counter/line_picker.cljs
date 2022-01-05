(ns counter.line-picker
  (:require [counter.routing-data :as routing-data]
            [counter.util :refer [row]]))

(defn line-picker [data platform set-platform]
  (let [selected-line (routing-data/get-line data platform)
        select-line #(set-platform (routing-data/get-line-platform-at-station data % (:station-id platform)))
        station-lines (routing-data/get-station-lines data platform)]
    [row
     [:label {:class "py-2"} "Line"]
     [:select {:class "bg-slate-300 hover:bg-slate-400 py-2 px-4 rounded-lg grow"
               :value (:id selected-line)
               :on-change #(-> % .-target .-value select-line)}
      (for [l station-lines]
        ^{:key (:id l)} 
        [:option {:value (:id l)} (:name l)])]]))
