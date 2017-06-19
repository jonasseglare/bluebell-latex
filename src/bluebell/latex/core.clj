(ns bluebell.latex.core
  (:require [clojure.spec :as spec]))

(defn prefixed [p sp]
  (spec/cat :prefix (partial = p)
            :value sp))

(def reserved [:opt    ;; Optional arg prefix
               :arg    ;; Extra arg prefix
               :cat    ;; Used to concatenate forms
               :lower  ;; Lower, that is _
               :upper  ;; Upper, that is ^
               ])

(defn reserved? [x]
  (contains? reserved x))

(def not-reserved? (complement reserved?))

(spec/def ::identifier (spec/and keyword?
                                 not-reserved?))

(spec/def ::string string?)
(spec/def ::opt-arg-map map?)

(spec/def ::opt-arg (spec/or :map ::opt-arg-map
                             :string ::string))

(spec/def ::cat (spec/spec (spec/cat :prefix (partial = :cat)
                                     :forms ::forms)))

(spec/def ::form (spec/or :command ::command
                          :string ::string
                          :cat ::cat))

(spec/def ::forms (spec/* ::form))
(spec/def ::rest-forms (spec/* (prefixed :arg ::form)))

(spec/def ::args (spec/cat :first ::forms
                           :rest ::rest-forms))

(spec/def ::command-setting (spec/or :opt-arg (prefixed :opt ::opt-arg)
                                     :lower (prefixed :lower ::form)
                                     :upper (prefixed :upper ::form)))

(spec/def ::command-settings (spec/* ::command-setting))

(spec/def ::command (spec/cat :name ::identifier
                              :settings ::command-settings
                              :args ::args))
