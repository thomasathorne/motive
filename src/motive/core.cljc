(ns motive.core)

(defn generate
  "A lazy sequence of generated events."
  [gen & [history init]]
  (lazy-seq
   (let [[e s] (gen history init)]
     (cons e (generate gen (cons e history) s)))))
