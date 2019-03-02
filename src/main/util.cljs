(ns main.util)

(defn dist [[x1 y1] [x2 y2]]
  (let [dx (- x1 x2)
        dy (- y1 y2)]
    (Math/sqrt
      (+
       (* dx dx)
       (* dy dy)))))

(defn add [[x1 y1] [x2 y2]]
  [(+ x1 x2) (+ y1 y2)])

(defn sub [[x1 y1] [x2 y2]]
  [(- x1 x2) (- y1 y2)])

(defn mult [[x y] n]
  [(* x n) (* y n)])

(defn div [[x y] n]
  [(/ x n) (/ y n)])

(defn dot [[x1 y1] [x2 y2]]
  (+
    (* x1 x2)
    (* y1 y2)))

(defn mag-sq [xy]
  (dot xy xy))

(defn mag [xy]
  (Math/sqrt (mag-sq xy)))

(defn normalize [xy]
  (let [m (mag xy)]
    (if (and (not= m 0) (not= m 1))
      (div xy m)
      xy)))

(defn limit [xy max]
  (if (> (mag-sq xy) (* max max))
    (-> xy normalize (mult max))
    xy))
