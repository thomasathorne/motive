(ns motive.examples
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
