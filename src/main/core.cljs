(ns main.core
  (:require [main.boids :as boids]))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (let [boids [(boids/->boid 0 0)
               (boids/->boid 10 10)]]
    (js/console.log (clj->js (boids/run (first boids) boids 200 200))))
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

;;(def birds (atom [{:x 10 :y 10 :rads 3.3} {:x 100 :y 100 :rads 1.9}]))
(defn initial-birds [] (repeatedly 13
                                   #(hash-map :x (rand-int 640)
                                              :y (rand-int 480)
                                              :rads (rand-int 6))))
(def birds (atom []))

(def canvas (.getElementById js/document "boids"))
(def ctx (.getContext canvas "2d"))

(defn draw-dot [{:keys [x y rads]}]
  (.drawPolygon js/window ctx
                x y 3 5 ;; x y sides size
                10 "#000000" ;; stroke-width stroke-color
                "#000000" rads)) ;; fill-color rotation

(defn draw-dots [coll]
  (.clearRect ctx 0 0 (.-width canvas) (.-height canvas))
  (doseq [bird coll]
    (draw-dot bird)))

(add-watch birds :mutator
           (fn [_ ref old new]
             (draw-dots new)))

(reset! birds (initial-birds))

(js/setInterval #(reset! birds (initial-birds)) (/ 1000 60))

(comment (js/alert "asd")
         (.getElementById js/document "boids")
         (draw-dot {:x 100 :y 100})
         (.drawPolygon js/window ctx 100 100 3 30 2 "#ffffff" 80)
         (draw-dots [{:x 10 :y 10} {:x 100 :y 100}])
         (swap! birds #(map (fn [val] (update val :rads inc)) %)))
