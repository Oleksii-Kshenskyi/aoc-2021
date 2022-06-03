(ns day-four.core
  (:gen-class))

(require '[clojure.java.io :as io]
         '[clojure.string :as s])

(defn split-input [streeng] (s/split streeng #"\s+"))
(defn str->int [streeng] (Integer/parseInt streeng))
(defn drop-empty-strings [coll] (into [] (filter #(false? (empty? %)) coll)))
(defn init-unmarked [coll] (into [] (map #(vector % :unmarked) coll)))
(defn drop-by-index [coll index]
  (let [vec (into [] coll)]
    (concat (subvec vec 0 index) (subvec vec (inc index)))))
(defn reconstruct [boards]
  (->> boards
       (partition 2)
       (partition 5)
       (partition 5)))
(defn check-and-mark-single [flat-boards mark-number index]
  (let [flatvec (into [] flat-boards)]
  (if (= mark-number (nth flatvec index))
    (assoc flatvec (+ index 1) :marked)
    flat-boards)))
(defn mark-with [mark-number boards]
  (loop [flat (flatten boards)
         index 0]
    (if (= index (count flat))
      (reconstruct flat)
      (recur (check-and-mark-single flat mark-number index) (+ index 2)))))

(defn check-and-inc [sum pair]
  (if (= (nth pair 1) :unmarked)
    (+ sum (str->int (nth pair 0)))
    sum))
(defn unmarked-sum [board]
  (loop [flat-board (flatten board)
         sum 0]
    (if (empty? flat-board)
      sum
      (recur (drop 2 flat-board) (check-and-inc sum (take 2 flat-board))))))
(defn is-winning-row? [row]
  (every? #(= (nth % 1) :marked) row))
(defn any-winning-rows? [board]
  (some true? (map is-winning-row? board)))
(defn any-winning-cols? [board]
  (any-winning-rows? (apply map vector board)))
(defn is-winning-board? [board]
  (or (any-winning-rows? board) (any-winning-cols? board)))
(defn winning-board-exists [boards]
  (loop [current boards
         index 0]
    (cond
      (is-winning-board? (first current)) [index (first current)]
      (= (count current) 1) nil
      :else (recur (drop 1 current) (inc index)))))
(defn drop-all-winning-boards [boards]
  (loop [drop-us boards]
    (let [[win-index current-win] (winning-board-exists drop-us)]
      (if (= current-win nil)
        drop-us
        (recur (drop-by-index drop-us win-index))))))
(defn drop-boards-and-mark [mark-number boards]
  (mark-with mark-number (drop-all-winning-boards boards)))

(defn part-one [game]
  (loop [draw-seq (get game "draw")
         boards (get game "boards")
         last-dropped nil]
    (let [[_ winning-board] (winning-board-exists boards)]
    (if winning-board
      (* (unmarked-sum winning-board) (str->int last-dropped))
      (recur (drop 1 draw-seq) (mark-with (first draw-seq) boards) (first draw-seq))))))
  (defn part-two [game]
    (loop [draw-seq (get game "draw")
           boards (get game "boards")
           last-dropped nil]
      (let [[_ winning-board] (winning-board-exists boards)]
        (cond 
          (and winning-board (not= (count boards) 1)) (recur (drop 1 draw-seq) (drop-boards-and-mark (first draw-seq) boards) (first draw-seq))
          (or (empty? draw-seq) (and winning-board (= (count boards) 1))) (* (unmarked-sum winning-board) (str->int last-dropped))
          (not winning-board) (recur (drop 1 draw-seq) (mark-with (first draw-seq) boards) (first draw-seq))))))

(defn split-board [board]
  (->> board
       (map split-input)
       (map drop-empty-strings)
       (map init-unmarked)
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
