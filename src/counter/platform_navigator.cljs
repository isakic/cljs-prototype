(ns counter.platform-navigator 
  (:require [counter.routing-data :as routing-data]
            [counter.line-picker :refer [line-picker]]
            [counter.util :refer [row]]))

(defn platform-navigator [data platform set-platform]
  (let [[prev _ next] (routing-data/get-platforms data platform)
        retn (routing-data/get-reverse-platform data platform)
        curr-station (routing-data/get-station data platform)
        dest-station (routing-data/get-last-station data platform)]
    [:<>
     [line-picker data platform set-platform]
     [row
      [:button {:class "bg-blue-500 hover:bg-blue-700 text-white w-32 py-2 px-4 rounded-lg"
                :on-click #(set-platform prev)} "Previous"]
      [:label {:class "py-2"} (:name curr-station)]
      [:button {:class "bg-blue-500 hover:bg-blue-700 text-white w-32 py-2 px-4 rounded-lg"
                :on-click #(set-platform next)} "Next"]]
     [row
      [:label {:class "py-2"} (str "Destination: " (:name dest-station))]
      [:button {:class "bg-blue-500 hover:bg-blue-700 text-white w-32 py-2 px-4 rounded-lg"
                :disabled (nil? retn) 
                :on-click #(set-platform retn)} "Reverse"]]]))
