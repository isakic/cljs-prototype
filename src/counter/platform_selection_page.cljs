(ns counter.platform-selection-page
  (:require [react :as react]
            [counter.platform-search :refer [platform-search]]
            [counter.platform-navigator :refer [platform-navigator]]
            [counter.util :refer [row nav-context]]))

(defn platform-selection-page []
  (let [[platform set-platform] (react/useState nil)
        go-to (react/useContext nav-context)]
    [:<>
     [platform-search set-platform]
     (when platform
       [:<>
        [platform-navigator platform set-platform]
        [row
         [:button {:class "bg-blue-500 hover:bg-blue-700 text-white w-32 py-2 px-4 rounded-lg"
                   :on-click #(go-to {:page :riding-count :params platform})}
          "Start count"]]])]))
