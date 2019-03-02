(ns main.core
  (:require [main.boids :as boids]
            [main.util :refer [heading]]))

(defn initial-birds [] (repeatedly 50 #(boids/->boid 320 240)))
(defonce birds (atom []))

(def canvas (.getElementById js/document "boids"))
(def ctx (.getContext canvas "2d"))

(defn draw-dot [{:keys [position velocity] :as boid} color]
  (let [[x y] position
        angle (heading velocity)]
    (.drawPolygon js/window ctx x y 3 6 2 color color angle)))

(defn draw-dots [colls]
  (.clearRect ctx 0 0 (.-width canvas) (.-height canvas))
  (let [num-generations  5
        last-generations (->> (reverse colls)
                              (take-nth 4)
                              (take num-generations))]
    (doseq [[idx coll] (map-indexed vector last-generations)]
      (doseq [bird coll]
        (draw-dot bird (str "rgba(0,0,0," (/ 1.0 (if (> idx 1)
                                                   (* idx 5)
                                                   idx)) ")"))))))

(defn advance-boids []
  (let [boids (last @birds)]
    (swap! birds conj (map #(boids/run % boids (.-height canvas) (.-width canvas)) boids))))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (js/console.log "start"))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (reset! birds [(initial-birds)])
  (js/setInterval advance-boids 20)
  (set! (.-onclick canvas) advance-boids)
  (add-watch birds :mutator
             (fn [_ ref old new]
               (draw-dots new)))
  (js/console.log "init")
  (start))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))

(comment (js/alert "asd")
         (.getElementById js/document "boids")
         (draw-dot {:x 100 :y 100})
         (.drawPolygon js/window ctx 100 100 3 30 2 "#ffffff" 80)
         (draw-dots [{:x 10 :y 10} {:x 100 :y 100}])
         (swap! birds #(map (fn [val] (update val :rads inc)) %)))
