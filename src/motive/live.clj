(ns motive.live
  (:require [overtone.core :refer :all]))

(defmulti play :part)

(defn run-motive
  ([gen] (run-motive gen (now)))
  ([gen t & [state]]
   (when-let [[{:keys [dur events] :as block} new-state] (gen state)]
     (run! (fn [{:keys [at] :as e}] (play e (+ t (* 1000 at)))) events)
     (apply-by (+ (* 1000 dur) t)
               run-motive
               [gen (+ (* 1000 dur) t) new-state]))))

(defn run-motives
  [& gens]
  (let [t (now)]
    (run! #(run-motive % t) gens)))
