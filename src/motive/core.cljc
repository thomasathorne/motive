(ns motive.core)

(defn simulate
  ([n gen]
   (when (pos? n)
     (let [[e s] (gen)]
       (println (str s " ---- " e))
       (simulate (dec n) gen s))))
  ([n gen state]
   (when (pos? n)
     (let [[e s] (gen state)]
       (println (str s " ---- " e))
       (simulate (dec n) gen s)))))

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
