(ns counter.util)

(defn coll->hashmap-by [coll key-fn] (zipmap (map key-fn coll) coll))

(defn =by [fn x y] (= (fn x) (fn y)))
(defn not=by [fn x y] (not (=by fn x y)))

; This fn is "borrowed" from https://gist.github.com/thenonameguy/714b4a4aa5dacc204af60ca0cb15db43 
; since it would take way too long for me to write an elegant solution myself. Sue me.
(defn distinct-by
  "Returns a stateful transducer that removes elements by calling f on each step as a uniqueness key.
   Returns a lazy sequence when provided with a collection."
  ([f]
   (fn [rf]
     (let [seen (volatile! #{})]
       (fn
         ([] (rf))
         ([result] (rf result))
         ([result input]
          (let [v (f input)]
            (if (contains? @seen v)
              result
              (do (vswap! seen conj v)
                  (rf result input)))))))))
  ([f xs]
   (sequence (distinct-by f) xs)))


(defn row [& children] 
  [:div {:class "flex flex-row gap-2 justify-between"} 
    (map-indexed #(with-meta %2  {:key %1}) children)])
