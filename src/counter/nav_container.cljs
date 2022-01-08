(ns counter.nav-container
  (:require [counter.util :refer [nav-provider]]
            [counter.platform-selection-page :refer [platform-selection-page]]
            [counter.riding-count-page :refer [riding-count-page]]
            [react :as react]))

(defn nav-container []
  (let [[nav-state go-to] (react/useState {:page :platform-selection :params []})]
    [nav-provider {:value go-to}
     (condp = (:page nav-state)
       :platform-selection [platform-selection-page (:params nav-state)]
       :riding-count [riding-count-page (:params nav-state)]
       [:h1 "Navigation error!"])]))
