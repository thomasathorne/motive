(ns demo
  (:require [overtone.core :refer :all]
            [motive.live :as l]
            [motive.generator :as g]))

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

(def drum-kit
  {:bass   {:initialness 1   :pace 1 :energy 10 :rarity 1}
   :snare  {:initialness 0   :pace 3 :energy 5  :rarity 1}
   :hat    {:initialness 0   :pace 4 :energy 4  :rarity 1}
   :snap   {:initialness 0   :pace 8 :energy 2  :rarity 1}
   :ploosh {:initialness 0.5 :pace 1 :energy 3  :rarity 3}})

(defn trees->beat
  [trees t]
  (when-let [{:keys [period energy children]} (first trees)]
    {:dur (apply + (map :period trees))
     :events (into [{:at t :part :snare :vel (/ energy 60)}]
                   (concat (:events (trees->beat children t))
                           (:events (trees->beat (rest trees) (+ t period)))))}))

(defn beat []
  (trees->beat [(b/beat-tree {:randomness 4
                              :shape-dist #(rand-nth [[1 1] [1 1 1]])}
                             2 50)]
               0))

(comment (l/run-motive (g/memoryless beat))
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
