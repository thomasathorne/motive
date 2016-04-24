(ns motive.live
  (:require [overtone.core :refer :all]))

(defmulti perform :part)

(defn run-motive
  ([motive] (run-motive motive (now)))
  ([motive t & [history state]]
   (let [[{:keys [dur events] :as block} new-state] (motive history state)]
     (run! (fn [{:keys [at] :as e}] (perform e (+ t (* 1000 at)))) events)
     (apply-by (+ (* 1000 dur) t)
               run-motive
               [motive (+ (* 1000 dur) t) (cons block history) new-state]))))

;; demo purposes

(definst foo
  [freq 170 vol 0.4 gate 1 dur 10000]
  (let [env (env-gen (adsr 0.4 0.2 1 0.1)
                     (* gate (line:kr 1 0 dur))
                     1 0 1 FREE)
        note (saw freq)]
    (* vol note)))

(defmethod perform :foo
  [{:keys [pitch dur]} t]
  (at t (foo (midi->hz pitch) 0.1 :dur dur)))
