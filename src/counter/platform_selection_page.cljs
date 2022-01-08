(ns counter.platform-selection-page
  (:require [react :as react]
            [counter.platform-search :refer [platform-search]]
            [counter.platform-navigator :refer [platform-navigator]]))

(defn platform-selection-page []
  (let [[platform set-platform] (react/useState nil)]
    [:<>
     [platform-search set-platform]
     (when platform
       [platform-navigator platform set-platform])]))
