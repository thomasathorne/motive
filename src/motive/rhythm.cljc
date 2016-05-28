(ns motive.rhythm
  (:require [motive.generator :as g]
            [com.rpl.specter :refer [select-one FIRST]]))

(defn block
  "Combine a rhythm generator and a melody generator into a simple
  block generator."
  [part rhythm melody]
  (g/map-g (fn [dur pitch]
             (when (and dur pitch)
               {:dur    dur
                :events [{:at    0
                          :part  part
                          :pitch pitch
                          :dur   dur}]}))
           rhythm
           melody))
