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

(defn flock [boid boids]
  (let [sep (separate boid boids)]
    sep))

(defn update-boid [boid]
  boid)

(defn run [boid boids]
  (-> boid
      (flock boids)
      update-boid))
