(ns tv.specs
  (:require [cljs.spec.alpha :as spec]
            [clojure.string :as string]
            [tv.moment :as moment]))

;;; General

(spec/def ::date-time moment/date-time?)
(spec/def ::non-empty-string (complement string/blank?))

;;; TV Shows

(spec/def ::description string?)
(spec/def ::live boolean?)
(spec/def ::original-title string?)
(spec/def ::start-time ::date-time)
(spec/def ::title ::non-empty-string)

(spec/def ::show
  (spec/keys :req-un [::live ::start-time ::title]
             :opt-un [::description ::original-title]))

(spec/def ::schedule (spec/coll-of ::show :distinct true :min-count 1))

;;; API

(defn validate-schedule [schedule]
  (if (spec/valid? ::schedule schedule)
    schedule
    (spec/explain ::schedule schedule)))
