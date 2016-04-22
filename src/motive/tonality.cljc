(ns motive.tonality)

;; There's a lot of options as to how to represent 'tonality'. This
;; first simple approach is essentially as a 'musical set'
;; (represented using a clj/cljs set of numbers). These are all
;; regarded modulo 12.

(def dorian #{0 2 3 5 7 9 10})
(def pentatonic #{0 2 4 7 9})
(def octatonic #{0 1 3 4 6 7 9 10})
(def whole-tone #{0 2 4 6 8 10})
(def diminished #{0 3 6 9})
(def augmented #{0 4 8})

(defn mode
  [mode-type tonic]
  (set (mapv #(mod (+ tonic %) 12) mode-type)))

(defn tonal?
  "Limited to whole number (ET12) notes."
  [tonality note]
  (tonality (mod note 12)))
