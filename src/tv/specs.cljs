(ns tv.specs
  (:require [cljs.spec.alpha :as spec]
            [clojure.string :as string]
            [tv.moment :as moment]))

;;; General

(spec/def ::date-time moment/moment?)
(spec/def ::non-empty-string (complement string/blank?))

;;; TV Show

(spec/def :tv.show/description (spec/nilable string?)) ; any string or nil
(spec/def :tv.show/react-key ::non-empty-string) ; string which is known not to be empty
(spec/def :tv.show/start-time ::date-time) ; valid date-time
(spec/def :tv.show/status (spec/nilable #{:live :repeat})) ; keyword or nil
(spec/def :tv.show/subtitle (spec/nilable string?)) ; any string or nil
(spec/def :tv.show/title ::non-empty-string) ; string which is known not to be empty

(spec/def ::tv-show
  (spec/keys :req [:tv.show/description
                   :tv.show/react-key
                   :tv.show/start-time
                   :tv.show/status
                   :tv.show/subtitle
                   :tv.show/title]))

(spec/def ::tv-schedule
  (spec/coll-of ::tv-show :distinct true :min-count 1))

;;; Convenience API

(def explain-schedule (partial spec/explain ::tv-schedule))
(def valid-schedule? (partial spec/valid? ::tv-schedule))

(def explain-show (partial spec/explain ::tv-show))
(def valid-show? (partial spec/valid? ::tv-show))
