(ns day-four.core
  (:gen-class))

(require '[clojure.java.io :as io]
         '[clojure.string :as s])

(defn split-input [streeng] (s/split streeng #"\s+"))
(defn str->int [streeng] (Integer/parseInt streeng))
(defn into-ints [streengs] (map str->int streengs))
(defn nth-last [index sequence] (nth sequence index))
(defn drop-empty-strings [coll] (into [] (filter #(false? (empty? %)) coll)))

(defn part-one [ints] 0)
(defn part-two [ints] 0)

(defn split-board [board]
  (->> board
       (map split-input)
       (map drop-empty-strings)
       (into [])))
(defn boards-from-lines [lines]
  (->> lines
       (partition 5 6)
       (map split-board)
       (into [])))
(defn game-from-lines [lines]
  (let [draw-sequence (s/split (nth lines 0) #"\,")
        boards (boards-from-lines (subvec lines 2))]
    {"draw" draw-sequence "boards" boards}))

(defn get-game [args]
  (->> (nth args 0)
       (slurp)
       (s/split-lines)
       (game-from-lines)))
(defn file-exists [filename] (.exists (io/as-file filename)))

(defn -main [& args]
  (if (and (= (count args) 1) (file-exists (nth args 0)))
    (let [game (get-game args)]
      (println "Part 1 result: " (part-one game))
      (println "Part 2 result: " (part-two game)))

    (println "Need exactly ONE argument: an existing file name.")))
