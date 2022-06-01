(ns day-two.core
  (:require [clojure.java.io :as io])
  (:gen-class))

(defn get-empty-sub [] {"depth" 0 "horizontal" 0})
(defn sub-step-single [command sub]
  (case (get command "command")
    "forward" (assoc sub "horizontal" (+ (get sub "horizontal") (get command "value")))
    "up" (assoc sub "depth" (+ (get sub "depth") (get command "value")))
    "down" (assoc sub "depth" (- (get sub "depth") (get command "value")))))
(defn split-input [streeng] (re-seq #"(\S+)\s(\d+)" streeng))
(defn regex->command [split-vec] {"command" (nth split-vec 1) "value" (Integer/parseInt (nth split-vec 2))})
(defn into-commands [streengs]
  (->> streengs
       (map regex->command)
       (into [])))

(defn part-one [commands]
  (loop [sub (get-empty-sub)
         index (- (count commands) 1)]
    (if (>= index 0)
      (recur (sub-step-single (nth commands index) sub) (dec index))
      (* (Math/abs (get sub "depth")) (get sub "horizontal")))))
(defn part-two [commands] 0)

(defn get-commands [args]
  (->> (nth args 0)
       (slurp)
       (split-input)
       (into-commands)))
(defn file-exists [filename] (.exists (io/as-file filename)))

(defn -main [& args]
  (if (and (= (count args) 1) (file-exists (nth args 0)))
    (let [commands (get-commands args)]
      (println "Part 1 result: " (part-one commands))
      (println "Part 2 result: " (part-two commands)))

    (println "Need exactly ONE argument: an existing file name.")))
