(ns day-six.core
  (:gen-class))

(require '[clojure.java.io :as io]
         '[clojure.string :as str])

(defn split-input [streeng] (str/split streeng #"\,"))
(defn str->int [streeng] (Integer/parseInt streeng))
(defn into-ints [streengs] (->> streengs (map str->int) (into [])))
(defn dec-vec-at [vec index] (assoc vec index (dec (nth vec index))))

(defn spawn-or-leave [fish index]
  (let [fishvec (into [] fish)]
    (if (= (nth fishvec index) -1)
      (conj (assoc fishvec index 6) 8)
      fishvec)))
(defn single-simulate-spawns [fish]
  (loop [index 0
         result fish]
    (if (= index (count fish))
           result
           (recur (inc index) (spawn-or-leave result index)))))
(defn simulate-single-day [fish]
  (let [stepped-fish (map dec fish)]
    (single-simulate-spawns stepped-fish)))

(defn part-one [fish]
  (count (nth (take 81 (iterate simulate-single-day fish)) 80)))
(defn part-two [fish] 0)

(defn get-fish [args]
  (->> (nth args 0)
       (slurp)
       (split-input)
       (into-ints)))
(defn file-exists [filename] (.exists (io/as-file filename)))

(defn -main [& args]
  (if (and (= (count args) 1) (file-exists (nth args 0)))
    (let [fish (get-fish args)]
      (println "Part 1 result: " (part-one fish))
      (println "Part 2 result: " (part-two fish)))

    (println "Need exactly ONE argument: an existing file name.")))
