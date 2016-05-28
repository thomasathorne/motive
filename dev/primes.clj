(ns primes
  (:require [overtone.core :refer :all]
            [motive.live :as l]
            [motive.block :as bl]
            [motive.generator :as g]))

(definst ping [freq 440 dur 0.3]
  (let [env (env-gen (perc 0.01 dur 1 -5) 1 1 0 1 FREE)]
    (* 0.2 env (saw freq))))

(defmethod l/play :ping
  [{:keys [freq]} t]
  (at t (ping freq)))

(defn chord-block
  [chord]
  {:dur 1
   :events (mapv (fn [freq]
                   {:at 0 :part :ping :freq freq})
                 chord)})

(def primes
  [2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73
   79 83 89 97 101 103 107 109 113 127 131 137 139 149 151 157
   163 167 173 179 181 191 193 197 199 211 223 227 229 233 239
   241 251])

(def factors
  (memoize
   (fn [n]
     (loop [ps primes]
       (cond
         (empty? ps)
         []

         (== 0 (rem n (first ps)))
         (conj (factors (quot n (first ps))) (first ps))

         :else
         (recur (rest ps)))))))

(defn minor-scale [base n]
  (let [octs   (quot n 7)
        degree (rem n 7)]
    (* base
       (Math/pow 2 octs)
       ({0 1
         1 (/ 9 8)
         2 (/ 32 27)
         3 (/ 4 3)
         4 (/ 3 2)
         5 (/ 128 81)
         6 (/ 16 9)} degree))))

(defn primes-chord
  [ps]
  (map (comp (partial minor-scale 100)
             (zipmap primes (range)))
       ps))

(defn gen
  [& [state]]
  (let [state (or state 1)]
    [(let [fs (factors state)]
       (chord-block (primes-chord (factors state))))
     (inc state)]))

(comment

  (l/run-motives (bl/with-tempo 500 gen))

  (stop)

  )
