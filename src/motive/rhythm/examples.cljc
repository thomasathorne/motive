(ns motive.rhythm.examples
  (:require [motive.generator :as g]))

(def salsa (g/cycle [3 3 2]))

(def saleeb (g/cycle [4/3 4/3 4/3 3/2 3/2]))
