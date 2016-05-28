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
  [gen]
  (fn [& [{:keys [previous state]} :as s]]
    (let [[event new-state] (gen state)]
      (if (and previous (= event previous))
        (recur s)
        [event {:previous event :state new-state}]))))

(defn choosing
  [& gens]
  (fn [& [state]]
    (let [i      (rand-int (count gens))
          [ch s] ((nth gens i) (nth state i))]
      [ch (cond
            state (assoc state i s)
            s     (assoc (vec (repeat (count gens) nil)) i s))])))

(defn looping
  [& gens]
  (fn [& [state]]
    (let [state                  (or state {:index 0 :states (vec (repeat (count gens) nil))})
          {:keys [index states]} state
          [e s]                  ((nth gens index) (nth states index))]
      [e (-> state
             (assoc-in [:states index] s)
             (assoc :index (if (= index (dec (count gens))) 0 (inc index))))])))

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

(defn blocking
  [pred gen]
  (fn [& [state]]
    (loop []
      (let [[x ns] (gen state)]
        (if (pred x)
          [x ns]
          (recur))))))

(defn markov-chain
  [init f]
  (fn [& [{:keys [previous]}]]
    (if (nil? previous)
      [init {:previous init}]
      (let [x (f previous)]
        [x {:previous x}]))))
