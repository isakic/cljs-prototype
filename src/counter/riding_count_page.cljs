(ns counter.riding-count-page
  (:require [react :as react]
            [counter.util :refer [row]]
            [counter.routing-data :as routing-data :refer [routing-data-context]]))

(defrecord RidingCount [timestamp platform ingress egress total])
(defn new->RidingCount [platform] (->RidingCount (.now js/Date) platform nil nil nil))

(defn- count-list [counts]
  (let [data (react/useContext routing-data-context)]
    [:div
     (for [c (reverse counts)]
       ^{:key (:timestamp c)}
       [:div {:class "flex flex-row gap-2 justify-between items-center"}
        (let [platform (:platform c)
              station (routing-data/get-station data platform)
              last-station (routing-data/get-last-station data platform)
              line (routing-data/get-line data platform)]
          [:<>
           [:span
            [:div (.toLocaleString (js/Date. (:timestamp c)))]
            [:div (str "Line: " (:name line) " " (:name last-station))]
            [:div (str "Station: " (:name station))]]
           [:label {:class "align-middle"} (str "In: " (:ingress c))]
           [:label {:class "align-middle"} (str "Out: " (:egress c))]
           [:label {:class "align-middle"} (str "Total: " (:total c))]])])]))

(defn riding-count-page [starting-platform]
  (let [data (react/useContext routing-data-context)
        [platform set-platform] (react/useState starting-platform)
        [_ _ next-platform] (routing-data/get-platforms data platform)
        [current-count set-current-count] (react/useState (new->RidingCount platform))
        [counts set-counts] (react/useState [])]
    [:<>
     [:div {:class "grid grid-cols-2"}
      (let [station (routing-data/get-station data platform)
            last-station (routing-data/get-last-station data platform)
            line (routing-data/get-line data platform)]
        [:<>
         [:label {:class "text-left py-2"} (str "Line: " (:name line) " " (:name last-station))]
         [:label {:class "text-right py-2"} (str "Station: " (:name station))]])]
     [:hr]
     [row
      (let [update-ingress #(set-current-count (assoc current-count :ingress %))]
        [:input {:class "bg-slate-300 hover:bg-slate-400 py-2 px-4 rounded-lg grow"
                 :placeholder "Ingress"
                 :field "text"
                 :value (:ingress current-count)
                 :on-change #(-> % .-target .-value update-ingress)}])]
     [row
      (let [update-egress #(set-current-count (assoc current-count :egress %))]
        [:input {:class "bg-slate-300 hover:bg-slate-400 py-2 px-4 rounded-lg grow"
                 :placeholder "Egress"
                 :field "text"
                 :value (:egress current-count)
                 :on-change #(-> % .-target .-value update-egress)}])]
     [row
      (let [update-total #(set-current-count (assoc current-count :total %))]
        [:input {:class "bg-slate-300 hover:bg-slate-400 py-2 px-4 rounded-lg grow"
                 :placeholder "Current total"
                 :field "text"
                 :value (:total current-count)
                 :on-change #(-> % .-target .-value update-total)}])]
     [row
      [:button {:class "bg-blue-500 hover:bg-blue-700 text-white w-32 py-2 px-4 rounded-lg"
                :on-click (fn [] (set-counts (conj counts current-count))
                            (set-platform next-platform)
                            (set-current-count (new->RidingCount next-platform)))}
       "Add count"]
      [:button {:class "bg-blue-500 hover:bg-blue-700 text-white w-32 py-2 px-4 rounded-lg"
                :on-click (fn [] nil)}
       "Finish"]]
     [:hr]
     [count-list counts]]))
