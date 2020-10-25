(ns tv.show
  (:require [clojure.string :as string]
            [tv.moment :as moment]))

;;; Helpers

(def trim (comp string/trim-newline string/trim))

(def regex #"(\W+)\s*e.\s*$")

(defn has-suffix? [s]
  (some? (re-find regex s)))

(defn strip-suffix [s]
  (-> (string/replace s regex "$1")
      (trim)))

(defn same? [a b]
  (let [f (comp trim string/lower-case)]
    (apply = (map f [a b]))))

(def unique? (complement same?))

;;; Protocol

(defprotocol TVShowProtocol
  (description [this] "Returns the normalized description of the given show or nil")
  (react-key [this] "Returns a unique React render key for the given show")
  (start-time [this] "Returns the start time of the given show")
  (status [this] "Returns the transmission status of the given show")
  (subtitle [this] "Returns the subtitle of the given show or nil")
  (title [this] "Returns the title of the given show"))

(defrecord TVShow [description live original-title start-time title]
  TVShowProtocol
  (description [_]
    (when-not (string/blank? description)
      (strip-suffix description)))
  (react-key [_]
    (str "ruv/" (moment/timestamp start-time)))
  (start-time [_]
    (moment/time-string start-time))
  (status [_]
    (cond
      (true? live) :live
      (has-suffix? description) :repeat
      :else nil))
  (subtitle [_]
    (when (unique? title original-title)
      (trim original-title)))
  (title [_]
    (trim title)))
