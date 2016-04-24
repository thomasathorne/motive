(ns motive.rhythm
  (:require [motive.generator :as g]
            [com.rpl.specter :refer [select-one FIRST]]))

(def salsa (g/cycle [3 3 2]))

(defn tempo
  [bpm rhythm]
  (let [bps (/ bpm 60.0)]
    (g/parallel #(/ % bps) #(* % bps) rhythm)))

(defn motive
  [part rhythm phrase]
  (g/parallel (fn [dur pitch]
                (when (and dur pitch)
                  {:dur    dur
                   :events [{:at    0
                             :part  part
                             :pitch pitch
                             :dur   dur}]}))
              :dur                                   rhythm
              #(select-one [:events FIRST :pitch] %) phrase))
