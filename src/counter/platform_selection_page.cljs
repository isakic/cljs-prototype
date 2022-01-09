(ns counter.platform-selection-page
  (:require [react :as react]
            [counter.platform-search :refer [platform-search]]
            [counter.platform-navigator :refer [platform-navigator]]
            [counter.util :refer [row nav-context]]))

(defn platform-selection-page []
  (let [[platform set-platform] (react/useState nil)
        go-to (react/useContext nav-context)]
    [:<>
     [row
      [:label {:class "grow text-center"} "New passenger count"]]
     [:hr]
     [platform-search set-platform]
     (when platform
       [:<>
        [platform-navigator platform set-platform]
        [:hr]
        [row
         [:button {:class "bg-blue-500 hover:bg-blue-700 text-white py-2 px-4 rounded-lg"
                   :on-click #(go-to {:page :riding-count :params platform})}
          "Start riding count"]
         [:span {:class "grow"}]
         [:button {:class "bg-blue-500 hover:bg-blue-700 text-white py-2 px-4 rounded-lg"
                   :on-click #()}
          "Start station count"]]])]))
