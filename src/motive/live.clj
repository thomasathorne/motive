(ns motive.live
  (:require [overtone.core :refer :all]))

(defmulti play :part)

(defn run-motive
  ([motive] (run-motive motive (now)))
  ([motive t & [history state]]
   (when-let [[{:keys [dur events] :as block} new-state] (motive history state)]
     (run! (fn [{:keys [at] :as e}] (play e (+ t (* 1000 at)))) events)
     (apply-by (+ (* 1000 dur) t)
               run-motive
               [motive (+ (* 1000 dur) t) (cons block history) new-state]))))
