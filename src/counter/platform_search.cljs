(ns counter.platform-search
  (:require [counter.routing-data :as routing-data]
            [counter.util :refer [row]]))

(defn platform-search [data set-platform]
  (let [set-if-found #(when % (set-platform %))
        try-set-platform #(set-if-found (routing-data/find-platform data %))]
    [row
     [:input {:class "bg-slate-300 hover:bg-slate-400 py-2 px-4 rounded-lg grow"
              :placeholder "Search by RBL number"
              :field "text"
              :on-change #(-> % .-target .-value try-set-platform)}]]))
