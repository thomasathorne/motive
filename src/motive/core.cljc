(ns motive.core)

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
