(ns tv.show
  (:require [clojure.string :as string]
            [tv.moment :as moment]))

(def trim (comp string/trim-newline string/trim))

(def re #"(\W+)\s*e.\s*$")

(defn has-suffix? [s]
  (some? (re-find re s)))

(defn strip-suffix [s]
  (string/replace s re "$1"))

(defn same? [a b]
  (let [f (comp trim string/lower-case)]
    (apply = (map f [a b]))))

(def unique? (complement same?))

(defn description [{:keys [description]}]
  (when-not (string/blank? description)
    (strip-suffix (trim description))))

(defn react-key [{:keys [startTime]}]
  (str "ruv/" (moment/timestamp startTime)))

(defn status [{:keys [description live]}]
  (cond
    (true? live) :live
    (has-suffix? description) :repeat
    :else nil))

(defn subtitle [{:keys [originalTitle title]}]
  (when (unique? title originalTitle)
    (trim originalTitle)))

(defn tv-show
  "Constructs a valid TV show from the given map"
  [{:keys [startTime title] :as m}]
  {::description (description m)
   ::react-key (react-key m)
   ::start-time (moment/moment startTime)
   ::status (status m)
   ::subtitle (subtitle m)
   ::title (trim title)})
