(ns motive.melody
  (:require [motive.tonality :as t]
            [motive.math :as m]
            [motive.generator :as g]
            [com.rpl.specter :refer [FIRST select]]))

(defn with-tonality-predicate
  [tonality gen]
  (fn [& args]
    (loop [args args]
      (let [[e s] (apply gen args)]
        (if (t/tonal? tonality e)
          [e s]
          (recur args))))))

(defn with-interval-pred
  [pred? gen]
  (fn [& [{:keys [previous state] :as s}]]
    (let [[x ns] (gen state)]
      (if (and previous (not (pred? (- x previous))))
        (recur [s])
        [x {:previous x :state ns}]))))

(defn with-max-interval
  [max-i gen]
  (with-interval-pred (fn [i] (<= (m/abs i) max-i)) gen))

(defn with-min-interval
  [min-i gen]
  (with-interval-pred (fn [i] (>= (m/abs i) min-i)) gen))


(defn with-bounding-distribution
  "The bounding distribution `dist` is a function from notes
  to (non-normalized) probabilities. The argument `n` controls the
  number of samples taken---higher values give slower performance but
  a better approximation of the distribution."
  [dist n gen]
  (fn [& args]
    (let [samples (vec (repeatedly n #(apply gen args)))
          probs   (mapv (comp dist first) samples)
          y       (* (rand) (apply max probs))
          indices (keep-indexed (fn [i p]
                                  (when (< y p) i))
                                probs)]
      (nth samples (rand-nth indices)))))

(defn step-gen
  [initial intervals]
  (g/markov-chain initial
                  (fn [x] (+ x (rand-nth intervals)))))

(defn middle-of-keyboard
  [n]
  (m/exp (- (/ (m/pow (- n 60) 2) 200))))

(defn block
  [part melody]
  (g/map-g (fn [m] (when m {:dur 1
                            :events [{:at 0
                                      :part part
                                      :pitch m
                                      :dur 1}]}))
           melody))
