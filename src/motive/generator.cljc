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
  (let [vprev (volatile! nil)]
    (fn [& [state]]
      (let [[event new-state] (gen state)]
        (if (and @vprev (= event @vprev))
          (recur state)
          (do (vreset! vprev event)
              [event new-state]))))))

(defn choosing
  [& gens]
  (fn [& [history state]]
    (let [i      (rand-int (count gens))
          [ch s] ((nth gens i) history (nth state i))]
      [ch (cond
            state (assoc state i s)
            s     (assoc (vec (repeat (count gens) nil)) i s))])))

(defn looping
  [& gens]
  (fn [& [history state]]
    (let [state                  (or state {:index 0 :states (vec (repeat (count gens) nil))})
          {:keys [index states]} state
          [e s]                  ((nth gens index) history (nth states index))]
      [e (-> state
             (assoc-in [:states index] s)
             (assoc :index (if (= index (dec (count gens))) 0 (inc index))))])))

(defn cycle
  [xs]
  (let [n (count xs)]
    (fn [& [history state]]
      (let [state (or state 0)]
        [(get xs state) (if (= state (dec n)) 0 (inc state))]))))

(defn parallel
  [combine-fn & gens]
  (fn [& [history {:keys [histories states] :as state}]]
    (let [histories (or histories (repeat (count gens) ()))
          results   (mapv (fn [gen i]
                            (gen (get histories i) (get states i)))
                          gens (range))]
      [(apply combine-fn (mapv first results))
       {:histories (mapv cons (map first results) histories)
        :states    (mapv second results)}])))

(defn markov-chain
  [init f]
  (fn [& [history state]]
    (if (empty? history)
      [init state]
      [(f (first history)) state])))
