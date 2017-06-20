(ns bluebell.latex.core
  (:require [clojure.spec :as spec]
            [bluebell.utils.defmultiple :as defmultiple]))

(defn prefixed [p sp]
  (spec/cat :prefix (partial = p)
            :value sp))

(def reserved [:opt    ;; Optional arg prefix
               :arg    ;; Extra arg prefix
               :cat    ;; Used to concatenate forms
               :lower  ;; Lower, that is _
               :upper  ;; Upper, that is ^
               :body   ;; for the begin form
               ])

(defn reserved? [x]
  (contains? reserved x))

(def not-reserved? (complement reserved?))

(spec/def ::identifier (spec/and keyword?
                                 not-reserved?))

(spec/def ::string string?)
(spec/def ::opt-arg-map map?)
(spec/def ::number number?)


(spec/def ::opt-arg (spec/or :map ::opt-arg-map
                             :string ::string))

(spec/def ::compound (spec/coll-of ::form))

(spec/def ::form (spec/or :command ::command
                          :string ::string
                          :number ::number
                          :compound ::compound))

(spec/def ::forms (spec/* ::form))
(spec/def ::rest-forms (spec/* (prefixed :arg ::form)))

(spec/def ::args ::forms)

(spec/def ::command-setting (spec/alt :opt-arg (prefixed :opt ::opt-arg)
                                      :lower (prefixed :lower ::form)
                                      :upper (prefixed :upper ::form)))

(spec/def ::command-settings (spec/* ::command-setting))

(spec/def ::command (spec/cat :name ::identifier
                              :settings ::command-settings
                              :args ::args
                              :body (spec/? (prefixed :body ::forms))))

(defn parse [x]
  (spec/conform ::form x))

(defn compile-command [x]
  x)

(defn compile-cat [x] x)

(defmultiple/defmultiple compile-form first
  (:command [[_ x]] (compile-command x))
  (:string [[_ x]] x)
  (:number [[_ x]] (str x))
  (:cat [[_ x]] (compile-cat x)))

(defn full-compile [x]
  (-> x
      parse
      compile-form))
