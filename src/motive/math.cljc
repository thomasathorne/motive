(ns motive.math)

(defn log [x]
  #?(:clj  (Math/log x)
     :cljs (.log js/Math x)))

(defn ceil [x]
  #?(:clj  (Math/ceil x)
     :cljs (.ceil js/Math x)))

(defn floor [x]
  #?(:clj  (Math/floor x)
     :cljs (.floor js/Math x)))

(defn abs [x]
  #?(:clj  (Math/abs x)
     :cljs (.abs js/Math x)))

(defn exp [x]
  #?(:clj  (Math/exp x)
     :cljs (.exp js/Math x)))

(defn pow [x y]
  #?(:clj  (Math/pow x y)
     :cljs (.pow js/Math x y)))

(defn uniform
  [a b]
  (+ a (rand-int (- (inc b) a))))

(defn exponential
  [mean]
  (let [x (rand)]
    (* mean (- (log x)))))

(defn poisson
  [rate]
  (loop [n         0
         remaining 1.0]
    (let [e (exponential (/ 1 (double rate)))]
      (if (<= e remaining)
        (recur (inc n) (- remaining e))
        n))))

(defn cdf
  [probs]
  (let [total (apply + probs)]
    (transduce (map #(/ % total 1.0))
               (completing (fn [accum next]
                             (let [prev (or (last accum) 0)]
                               (conj accum (+ next prev)))))
               []
               probs)))

(defn categorical
  [cdf-vec]
  (let [x (rand)]
    (count (take-while #(< % x) cdf-vec))))

(defn transition-fn
  [states trans-matrix]
  (let [cdfs         (mapv cdf trans-matrix)
        state->index (zipmap states (range))]
    (comp states
          categorical
          cdfs
          state->index)))

(defn normalize-vector
  [v]
  (let [total (apply + v)]
    (mapv #(/ % total 1.0) v)))

(defn polya-step
  [ns]
  (let [cdf-vec (cdf ns)
        draw    (categorical cdf-vec)]
    (update ns draw inc)))

(defn dirichlet
  [ns & [steps]]
  (let [steps  (or steps 100)
        ns     (nth (iterate polya-step ns) steps)
        gammas (mapv (fn [n]
                       (apply + (repeatedly (ceil n) #(exponential 1))))
                     ns)]
    (normalize-vector gammas)))
