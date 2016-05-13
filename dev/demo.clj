(ns demo
  (:require [overtone.core :refer :all]
            [motive.live :as l]
            [motive.generator :as g]
            [motive.beat :as b]
            [motive.math :as m]))

(defonce server (boot-server))

(definst splat
  [vel 1 dur 0.1 vol1 1 freq1 400 spread1 0.1 vol2 1 freq2 1800 spread2 0.2]
  (let [env (env-gen:kr (perc 0.01 dur 1 -6) 1 1 0 1 FREE)
        sig (* env (white-noise))]
    (+ (* vel vol1 (/ 1 spread1) (bpf sig freq1 spread1))
       (* vel vol2 (/ 1 spread2) (bpf sig freq2 spread2)))))

(defn hit [t vol dur f1 f2]
  (let [[vol1 freq1 spread1] f1
        [vol2 freq2 spread2] f2]
    (at t (splat vol dur vol1 freq1 spread1 vol2 freq2 spread2))))

(defmethod l/play :bass   [{:keys [vel]} t] (hit t vel 0.1 [20 50 0.2] [0 1 1]))
(defmethod l/play :snare  [{:keys [vel]} t] (hit t vel 0.3 [1 800 0.5] [1 2000 0.5]))
(defmethod l/play :hat    [{:keys [vel]} t] (hit t vel 0.2 [1 2500 0.2] [0.6 4000 1]))
(defmethod l/play :snap   [{:keys [vel]} t] (hit t vel 0.04 [1 3200 0.2] [0.6 8000 1]))
(defmethod l/play :ploosh [{:keys [vel]} t] (hit t vel 0.6 [1 300 0.3] [0.6 2000 1]))

(def context
  {:drum-fn (fn [e]
              (cond
                (> e 30) {:vel (/ e 80) :part :bass}
                (> e 10) {:vel (/ e 80) :part :snare}
                (> e 7)  {:vel (/ e 80) :part :hat}
                (> e 4)  {:vel (/ e 80) :part :ploosh}
                :else    {:vel (/ e 80) :part :snap}))
   :shape-fn (constantly [1 1 1 1])
   :branching-energy 10
   :randomness 2})

(defn beat [d r]
  {:dur    d
   :events (b/realise-trees context [(b/beat-tree (assoc context :randomness r) d 100)] 0)})

(def beat-gen
  (g/memoryless (fn []
                  (let [r (m/exponential 6)]
                    (beat 3 r)))))

(comment (l/run-motive beat-gen)
         (stop)

         )

(definst foo
  [freq 170 vol 0.4 gate 1 dur 10000]
  (let [env (env-gen (adsr 0.4 0.2 1 0.1)
                     (* gate (line:kr 1 0 dur))
                     1 0 1 FREE)
        note (saw freq)]
    (* vol note)))

(defmethod l/play :foo
  [{:keys [pitch dur]} t]
  (at t (foo (midi->hz pitch) 0.01 :dur dur)))
