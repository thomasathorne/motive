(ns motive.generator)

(defn seq-g
  "A generator that simply reads from its state."
  [& [coll]]
  [(first coll) (rest coll)])

(defn constant-g
  [x]
  (fn [& [state]]
    [x state]))

(defn memoryless
  "Returns a memoryless generator for any simple distribution
  function."
  [dist-fn]
  (fn [& [state]]
    [(dist-fn) state]))

(defn without-repeating
  "Generator middleware; produces a new generator that never repeats
  the same event twice in a row."
  [gf]
  (fn [& [{:keys [previous state]} :as s]]
    (let [[x new-state] (gf state)]
      (if (and previous (= x previous))
        (recur s)
        [x {:previous x :state new-state}]))))

(defn choosing
  "Generate from a randomly chosen generator function each time."
  [& gfs]
  (let [n (count gfs)]
    (fn [& [state]]
      (let [i      (rand-int n)
            [x s] ((nth gfs i) (nth state i))]
        [x (cond
             state (assoc state i s)
             s     (assoc (vec (repeat n nil)) i s))]))))

(defn looping
  "Generate from each generator function in order."
  [& gfs]
  (let [n (count gfs)]
   (fn [& [state]]
     (let [state                  (or state {:index 0 :states (vec (repeat n nil))})
           {:keys [index states]} state
           [x s]                  ((nth gfs index) (nth states index))]
       [x (-> state
              (assoc-in [:states index] s)
              (assoc :index (if (= index (dec n)) 0 (inc index))))]))))

(defn cycle
  [xs]
  (let [n (count xs)]
    (fn [& [state]]
      (let [state (or state 0)]
        [(get xs state) (if (= state (dec n)) 0 (inc state))]))))

(defn map-g
  [combine-fn & gens]
  (fn [& [state]]
    (let [results (mapv (fn [gen i] (gen (get state i))) gens (range))]
      [(apply combine-fn (mapv first results))
       (mapv second results)])))

(defn filter-g
  [pred gen]
  (fn [& [state]]
    (loop [s state]
      (let [[x ns] (gen s)]
        (if (pred x)
          [x ns]
          (recur ns))))))

(defn blocking-filter
  [pred gen]
  (fn [& [state]]
    (loop []
      (let [[x ns] (gen state)]
        (if (pred x)
          [x ns]
          (recur))))))

(defn transducing
  ([rf gf] (transducing rf identity gf))
  ([xform rf gf]
   (fn [& [state]]
     (let [xf (xform rf)]
       (loop [s     state
              accum (xf)]
         (let [[x ns]    (gf s)
               new-accum (xf accum x)]
           (if (reduced? new-accum)
             [(unreduced new-accum) ns]
             (recur ns new-accum))))))))

(defn partition-g
  [n gf]
  (transducing (take n) conj gf))

(defn markov-chain
  [init f]
  (fn [& [{:keys [previous]}]]
    (if (nil? previous)
      [init {:previous init}]
      (let [x (f previous)]
        [x {:previous x}]))))
