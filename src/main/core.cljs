(ns main.core
  (:require [main.boids :as boids]))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (let [boids [(boids/->boid 0 0)
               (boids/->boid -10 -10)]]
    (js/console.log (clj->js (boids/run (first boids) boids))))
  (js/console.log "start"))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (js/console.log "init")
  (start))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))
