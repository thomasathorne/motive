(ns motive.beat
  (:require [motive.math :as m]))

(defn distribute-energy
  "The :randomness parameter in `context` should "
  [{:keys [randomness] :as context} sections]
  (if (< randomness 0.01)
    (m/normalize-vector sections)
    (let [alphas (mapv #(* 100 (/ % randomness)) (m/normalize-vector sections))]
      (m/dirichlet alphas))))

(defn split-period
  [{:keys [shape-fn] :as context} period energy]
  (let [shape               (shape-fn period)
        energy-distribution (distribute-energy context shape)]
    (mapv (fn [p e]
            [(* p period)
             (* e energy)])
          (m/normalize-vector shape)
          energy-distribution)))

(defn beat-tree
  "Splits a given period and total energy into a tree structure
  representing subdivisions of the time period."
  [{:keys [branching-energy] :as context} period energy]
  {:period period
   :energy energy
   :children (if (< energy branching-energy)
               []
               (map (partial apply beat-tree context)
                    (split-period context period energy)))})

(defn realise-trees
  [{:keys [drum-fn] :as context} trees t & [exclude-initial?]]
  (when-let [{:keys [children period energy]} (first trees)]
    (into (if exclude-initial?
            []
            [(merge (drum-fn energy) {:at t})])
          (concat (realise-trees context children t true)
                  (realise-trees context (rest trees) (+ t period))))))
