(ns bluebell.latex.core
  (:require [clojure.spec :as spec]))

(def reserved [:opt :arg :begin])

(defn reserved? [x]
  (contains? reserved x))

(def not-reserved? (complement reserved?))

(spec/def ::identifier (spec/and keyword?
                                 not-reserved?))

(spec/def ::string string?)
(spec/def ::opt-arg-map map?)

(spec/def ::opt-arg (spec/or :map ::opt-arg-map
                             :string ::string))

(spec/def ::opt-args (spec/* (spec/cat :opt-tag (partial = :opt)
                                       :opt-arg ::opt-arg)))

(spec/def ::form (spec/or :command ::command
                          :string ::string))

(spec/def ::forms (spec/* ::form))
(spec/def ::rest-forms (spec/* (spec/cat :arg-tag (partial = :arg)
                                         :form ::form)))

(spec/def ::args (spec/cat :first ::forms
                           :rest ::rest-forms))

(spec/def ::command (spec/cat :name ::identifier
                              :opt-args ::opt-args
                              :args ::args))
