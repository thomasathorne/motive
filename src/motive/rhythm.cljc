(ns motive.rhythm
  (:require [motive.generator :as g]
            [com.rpl.specter :refer [select-one FIRST]]))

(defn tempo
  [bpm rhythm]
  (let [bps (/ bpm 60.0)]
    (g/parallel #(/ % bps) #(* % bps) rhythm)))

(defn motive
  "Combine a rhythm generator and a melody generator into a simple
  motive generator."
  [part rhythm melody]
  (g/parallel (fn [dur pitch]
                (when (and dur pitch)
                  {:dur    dur
                   :events [{:at    0
                             :part  part
                             :pitch pitch
                             :dur   dur}]}))
              :dur                                   rhythm
              #(select-one [:events FIRST :pitch] %) melody))
