(ns melody-demo
  (:require [overtone.core :refer :all]
            [motive.core :refer :all]
            [motive.generator :as g]
            [motive.tonality :as t]
            [motive.letter-names :as ln]
            [motive.math :as m]
            [motive.melody :as mel]
            [motive.rhythm :as rh]
            [motive.block :as bl]
            [motive.live :as l]))

(def poison-frog
  (g/memoryless #(m/uniform 50 51)))

(def dark-wizard
  (g/without-repeating
   (g/memoryless #(m/uniform 48 85))))

(def dire-wolf
  (g/without-repeating
   (mel/with-tonality-predicate (t/mode t/pentatonic ln/d-flat)
     (g/memoryless #(m/uniform 48 65)))))

(def wolf
  (g/without-repeating
   (mel/with-tonality-predicate (t/mode t/pentatonic ln/c)
     (g/memoryless #(m/uniform 48 65)))))

(def were-boomerang
  (g/looping
   (g/constant 60)
   (g/memoryless #(m/uniform 45 75))))

(def fire-crab
  (mel/with-bounding-distribution
    mel/middle-of-keyboard 4
    (mel/step-gen 60 [11 13 -11 -13])))

(def egg-of-death
  (g/markov-chain
   60
   (m/transition-fn
    [60 61 62 63 64]
    [[0  2  8  13 15]
     [7  0  2  9  17]
     [15 1  0  1  13]
     [8  30 3  0  2]
     [7  21 34 1  0]])))

(def saleeb-rhythm
  (g/cycle [4/3 4/3 4/3 3/2 3/2]))

(definst voice
  [freq 170 vol 0.4 gate 1 dur 10000]
  (let [env (env-gen (adsr 0.4 0.2 1 0.1)
                     (* gate (line:kr 1 0 dur))
                     1 0 1 FREE)
        note (* env
                (saw (* freq (+ 1 (* 0.01 (sin-osc:kr 5))))))]
    (+ (lpf note 200)
       (* 0.7 (bpf note 450 0.06))
       (* 0.5 (bpf note 1800 0.03))
       (* 0.8 (bpf note 3400 0.02))
       (* 0.01 note))))

(defmethod l/play :voice
  [{:keys [pitch dur]} t]
  (at t (voice (midi->hz pitch) :dur dur)))

(comment

  (l/run-motives (bl/with-tempo 150
                   (rh/block :voice saleeb-rhythm wolf))
                 (bl/with-tempo 150
                   (mel/block :voice (g/cycle [36 43 36 43 36 43 31]))))

  (stop)

  )
