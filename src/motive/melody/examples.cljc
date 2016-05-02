(ns motive.melody.examples
  (:require [motive.generator :as g]
            [motive.tonality :as t]
            [motive.letter-names :as ln]
            [motive.math :as m]
            [motive.melody :as mel]))

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
