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
    (g/parallel scale gen)))
