(ns main.boids
  (:require [main.util :as util]))

(def MAX_SPEED 2)
(def MAX_FORCE 0.03)
(def BOID_SIZE 2)

(defn ->boid [x y]
  (let [angle (rand Math/PI)]
    {:position [x y]
     :angle angle
     :velocity [(Math/sin angle) (Math/cos angle)]
     :acceleration [0 0]}))

(defn separate [boid boids]
  (let [desired-separation 25.0
        [steer count] (reduce (fn [[steer count] b]
                                (let [d (util/dist (:position boid)
                                                   (:position b))]
                                  (if (and (> d 0)
                                           (< d desired-separation))
                                    (let [diff (-> (util/sub (:position boid)
                                                             (:position b))
                                                   util/normalize
                                                   (util/div d))
                                          steer' (util/add steer diff)
                                          count' (inc count)]
                                      [steer' count'])
                                    [steer count])))
                              [[0 0] 0]
                              boids)
        steer' (if (> count 0)
                 (util/div steer count)
                 steer)]
    (if (> (util/mag steer') 0)
      (-> steer'
          util/normalize
          (util/mult MAX_SPEED)
          (util/sub (:velocity boid))
          (util/limit MAX_FORCE))
      steer')))

(defn align [boid boids]
  (let [neighbordist 50
        [sum count] (reduce (fn [[sum count] b]
                              (let [d (util/dist (:position boid)
                                                 (:position b))]
                                (if (and (> d 0)
                                         (< d neighbordist))
                                  [(util/add sum (:velocity b)) (inc count)]
                                  [sum count])))
                            [[0 0] 0]
                            boids)
        ]
    (if (> count 0)
      (-> sum
          (util/div count)
          util/normalize
          (util/mult MAX_SPEED)
          (util/sub (:velocity boid))
          (util/limit MAX_FORCE)
          )
      [0 0])
    )
)

(defn seek [target boid]
  (let [
        desired (-> target
                    (util/sub (:position boid))
                    util/normalize
                    (util/mult MAX_SPEED)
                    )
        steer (-> desired
                  (util/sub (:velocity boid))
                  (util/limit MAX_FORCE)
                  )
        ]
    steer
    )
  )

(defn cohesion [boid boids]
  (let [neighbordist 50
        [sum count] (reduce (fn [[sum count] b]
                              (let [d (util/dist (:position boid)
                                                 (:position b))]
                                (if (and (> d 0)
                                         (< d neighbordist))
                                  [(util/add sum (:position b)) (inc count)]
                                  [sum count])))
                            [[0 0] 0]
                            boids)]
    (if (> count 0)
      (-> sum
          (util/div count)
          (seek boid)
          )
      [0 0])))

(defn apply-force [boid force]
  (update boid :acceleration util/add force))

(defn flock [boid boids]
  (let [sep (separate boid boids)
        alg (align boid boids)
        cohc (cohesion boid boids)
        sep' (util/mult sep 1.5)
        alg' (util/mult alg 1.0)
        cohc' (util/mult cohc 1.0)
        ]
    (-> boid
        (apply-force sep')
        (apply-force alg')
        (apply-force cohc')
        )))

(defn update-boid [boid]
  (->
   boid
   (update :velocity util/add (:acceleration boid))
   (update :velocity util/limit MAX_SPEED)
   (update :position util/add (:velocity boid))
   (update :acceleration util/mult 0)))

(defn wrap [boid max-height max-width]
  (let [{:keys [position]} boid
        [x y] position]
    (cond-> boid
      (< x (- BOID_SIZE)) (assoc-in [:position 0] (+ max-width BOID_SIZE))
      (< y (- BOID_SIZE)) (assoc-in [:position 1] (+ max-height BOID_SIZE))
      (> x (+ max-width BOID_SIZE)) (assoc-in [:position 0] (- BOID_SIZE))
      (> y (+ max-height BOID_SIZE)) (assoc-in [:position 1] (- BOID_SIZE))
      )))

(defn run [boid boids max-height max-width]
  (-> boid
      (flock boids)
      update-boid
      (wrap max-height max-width)))
