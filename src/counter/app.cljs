(ns counter.app
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<!]]
            [counter.routing-data :as routing-data :refer [routing-data-provider]]
            [react :as react]
            [reagent.dom :as rdom]
            [counter.util :refer [row fn-compiler]]
            [counter.nav-container :refer [nav-container]]))


(defn app []
  (let [[data set-data] (react/useState nil)]
    (react/useEffect (fn []
                       (go (set-data (<! (routing-data/fetch-routing-data))))
                       (constantly nil)) (array))
    [:div {:class "sm:container sm:mx-auto flex flex-col gap-2 justify-center py-12 sm:px-6 lg:px-8"}
     [routing-data-provider {:value data}
      (if data
        [nav-container]
        [row [:label {:class "py-2"} "Loading data, please wait..."]])]]))

(defn init []
  (rdom/render [app]
               (js/document.getElementById "app")
               fn-compiler))