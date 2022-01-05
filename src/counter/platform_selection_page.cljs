(ns counter.platform-selection-page 
  (:require [react :as react]
            [counter.platform-search :refer [platform-search]]
            [counter.platform-navigator :refer [platform-navigator]]))

(defn platform-selection-page [data]
  (let [[platform set-platform] (react/useState nil)]
    [:<>
     [platform-search data set-platform]
     (when platform
       [platform-navigator data platform set-platform])]))
