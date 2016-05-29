(ns motive.block
  (:require [motive.generator :as g]))

(defn with-tempo
  [bpm gen]
  (let [factor (/ 60.0 bpm)
        scale  (fn [{:keys [dur events] :as block}]
                 {:dur    (* factor dur)
                  :events (mapv (fn [event]
                                  (-> event
                                      (update-in [:at] #(* factor %))
                                      (update-in [:dur] #(when % (* factor %)))))
                                events)})]
    (g/map-g scale gen)))

(defn in-sequence
  ([] {:dur 0 :events []})
  ([block & others]
   (if-let [other (first others)]
     (recur {:dur    (+ (:dur block) (:dur other))
             :events (into (:events block)
                           (mapv (fn [e] (update-in e [:at] #(+ (:dur block) %)))
                                 (:events other)))}
            (rest others))
     block)))

(defn block-chunks
  [n gf]
  (->> gf
       (g/partition-g n)
       (g/map-g (partial apply in-sequence))))
