(ns motive.core)

(defn simulate
  ([n gen] (simulate n gen nil))
  ([n gen state]
   (take n (generate (fn [state]
                       (let [[x ns] (gen state)]
                         [[x ns] ns]))
                     state))))

(defn generate
  "A lazy sequence of generated events."
  ([gen]
   (lazy-seq
    (let [[e s] (gen)]
      (when e
        (cons e (generate gen s))))))
  ([gen state]
   (lazy-seq
    (let [[e ns] (gen state)]
      (when e
        (cons e (generate gen ns)))))))
