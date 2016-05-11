(ns motive.beat
  (:require [motive.math :as m]))

(defn distribute-energy
  "The :randomness parameter in `context` can meaningfully vary
  between 0.01 and roughly 50."
  [{:keys [randomness] :as context} sections]
  (if (< randomness 0.01)
    (m/normalize-vector sections)
    (let [alphas (mapv #(* 100 (/ % randomness)) (m/normalize-vector sections))]
      (m/dirichlet alphas))))

(defn split-period
  [{:keys [shape-dist] :as context} period energy]
  (let [shape               (shape-dist)
        energy-distribution (distribute-energy context shape)]
    (mapv (fn [p e]
            [(* p period)
             (* e energy)])
          (m/normalize-vector shape)
          energy-distribution)))

(defn beat-tree
  "Splits a given period and total energy into a tree structure
  representing subdivisions of the time period."
  [context period energy]
  {:period period
   :energy energy
   :children (if (< energy 10)
               []
               (map (partial apply beat-tree context)
                    (split-period context period energy)))})
