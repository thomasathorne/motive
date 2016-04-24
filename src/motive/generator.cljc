(ns motive.generator)

;; A *generator* is a (usually non-deterministic) function
;;
;;  history -> state -> [next-event new-state]
;;
;; where history is a list of past events (beginning with the most
;; recent), and state can be any additional 'memory'.
;;
;; An event that is just a number is taken to represent a single midi
;; pitch. More complicated events will eventually exist, e.g.
;;
;;   [:chord #{35 39 42}]
;;   [:melody 60 62 64]
;;   [:phrase [60 60 67 67 69 69 67]
;;            [1  1  1  1  1  1  2]]
;;   [:time-box {:length 3000
;;               :events [... ... ...]}]

(defn state-reader
  "A generator that simply reads from its state."
  [& [history state]]
  [(first state) (rest state)])

(defn constant
  [x]
  (fn [& [history state]]
    [x state]))

(defn memoryless
  "Returns a memoryless generator for any simple distribution
  function."
  [dist-fn]
  (fn [& [history state]]
    [(dist-fn) state]))

(defn without-repeating
  "Generator middleware; produces a new generator that never repeats
  the same event twice in a row."
  [gen]
  (fn [& [history state]]
    (let [[event new-state] (gen history state)]
      (if (= event (first history))
        (recur [history state])
        [event new-state]))))

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
  [combine-fn & args]
  {:pre [(even? (count args))]}
  (let [pairs (partition 2 args)]
    (fn [& [history state]]
      (let [results (mapv (fn [[f gen] i]
                            (gen (map f history) (get state i)))
                          pairs (range))]
        [(apply combine-fn (mapv first results))
         (mapv second results)]))))
