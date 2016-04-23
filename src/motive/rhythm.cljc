(ns motive.rhythm
  (:require [motive.generator :as g]
            [com.rpl.specter :refer [select FIRST]]))

(def salsa
  (apply g/looping (mapv g/constant [3 3 4])))

(defn motive
  [part rhythm phrase]
  (g/parallel (fn [dur pitch] {:duration dur
                               :events [{:at 0
                                         :part part
                                         :pitch pitch
                                         :dur dur}]})
              :duration rhythm
              #(select [:events FIRST :pitch] %) phrase))
