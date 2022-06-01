(ns day-two.core
  (:require [clojure.java.io :as io])
  (:gen-class))

(defn forward [command sub] (assoc sub "horizontal" (+ (get sub "horizontal") (get command "value"))))
(defn up [command sub] (assoc sub "depth" (+ (get sub "depth") (get command "value"))))
(defn down [command sub] (assoc sub "depth" (- (get sub "depth") (get command "value"))))
(def funcas {"forward" forward "up" up "down" down})

(defn forward-v2 [command sub]
  (let [sub1 (assoc sub "horizontal" (+ (get sub "horizontal") (get command "value")))
        sub2 (assoc sub1 "depth" (+ (get sub1 "depth") (* (get sub1 "aim") (get command "value"))))]
    sub2))
(defn up-v2 [command sub] (assoc sub "aim" (- (get sub "aim") (get command "value"))))
(defn down-v2 [command sub] (assoc sub "aim" (+ (get sub "aim") (get command "value"))))
(def funcas-v2 {"forward" forward-v2 "up" up-v2 "down" down-v2})

(defn get-empty-sub [] {"depth" 0 "horizontal" 0 "aim" 0})
(defn sub-step-single [command sub funcas-map]
  (let [fwfunc (get funcas-map "forward")
        upfunc (get funcas-map "up")
        dwfunc (get funcas-map "down")]
  (case (get command "command")
    "forward" (fwfunc command sub)
    "up" (upfunc command sub)
    "down" (dwfunc command sub))))

(defn split-input [streeng] (re-seq #"(\S+)\s(\d+)" streeng))
(defn regex->command [split-vec] {"command" (nth split-vec 1) "value" (Integer/parseInt (nth split-vec 2))})
(defn into-commands [streengs]
  (->> streengs
       (map regex->command)
       (into [])))

(defn run-sub [commands sub-funcas]
  (loop [sub (get-empty-sub)
         index 0]
    (if (< index (count commands))
      (recur (sub-step-single (nth commands index) sub sub-funcas) (inc index))
      (* (Math/abs (get sub "depth")) (get sub "horizontal")))))
(defn part-one [commands] (run-sub commands funcas))
(defn part-two [commands] (run-sub commands funcas-v2))

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
