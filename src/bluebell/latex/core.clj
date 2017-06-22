(ns bluebell.latex.core
  (:require [clojure.spec :as spec]
            [bluebell.utils.defmultiple :as defmultiple]))

(defn prefixed [p sp]
  (spec/cat :prefix (partial = p)
            :value sp))

(def reserved [:opt    ;; Optional arg prefix
               :lower  ;; Lower, that is _
               :upper  ;; Upper, that is ^
               :body   ;; for the begin form
               ])

(defn str-sep [sep args]
  (reduce #(str %1 sep %2) (map str args)))

(defn str-lines [& args]
  (str-sep "\n" args))

(defn str-space [& args]
  (str-sep " " args))

(defn reserved? [x]
  (contains? reserved x))

(def not-reserved? (complement reserved?))

(spec/def ::id (spec/cat :prefix (partial = ::id)
                         :value string?))

(spec/def ::identifier (spec/or :keyword (spec/and keyword?
                                                   #(not (qualified-keyword? %))
                                                   not-reserved?)
                                :id ::id))

(defn id [x]
  [::id x])

(defmultiple/defmultiple identifier-to-str first
  (:keyword [[_ x]] (name x))
  (:id [[_ x]] (:value x)))

(spec/def ::string string?)

(spec/def ::arg-key (spec/or :keyword keyword?
                             :string string?))

(defmultiple/defmultiple arg-key-to-str first
  (:keyword [[_ x]] (name x))
  (:string [[_ x]] x))

(spec/def ::arg-value (spec/or :string string?
                               :number number?))

(defmultiple/defmultiple arg-value-to-str first
  (:string [[_ x]] x)
  (:number [[_ x]] (str x)))

(spec/def ::opt-arg-map (spec/map-of ::arg-key ::arg-value))
(spec/def ::number number?)


(spec/def ::opt-arg (spec/or :map ::opt-arg-map
                             :string ::string))

(spec/def ::compound (spec/coll-of ::form))

(spec/def ::form (spec/or :command ::command
                          :string ::string
                          :number ::number
                          :compound ::compound))

(spec/def ::forms (spec/* ::form))

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

(declare compile-form)

(defn comma [a b] (str a "," b))

(defn to-assignment [[k v]]
  (str (arg-key-to-str (spec/conform ::arg-key  k))
       "=" (arg-value-to-str  v)))

(defmultiple/defmultiple make-opt-arg first
  (:map [[_ x]] (str "[" (reduce comma (map to-assignment x)) "]"))
  (:string [[_ x]] (str "[" x "]")))

(defmultiple/defmultiple make-setting first
  (:opt-arg [[_ x]] (make-opt-arg (:value x)))
  (:lower [[_ x]] (str "_{" (compile-form (:value  x)) "}"))
  (:upper [[_ x]] (str "^{" (compile-form (:value  x)) "}")))

(defn make-optional-args [x]
  (apply str (map make-setting x)))

(defn to-arg [x]
  (str "{" (compile-form x) "}"))

(defn make-args [x]
  (apply str (map to-arg x)))

(defn make-body [name body]
  (if body
    (str "\n" (apply str-lines (map compile-form (:value body)))
         "\n\\end{" (compile-form name) "}")
    ""))

(defn compile-command [x]
  (str "\\" (identifier-to-str (:name x))
       (make-optional-args (:settings x))
       (make-args (:args  x))
       (make-body (first (:args x)) (:body x))))


(defn compile-compound [x]
  (apply str-space (map compile-form x)))

(defmultiple/defmultiple compile-form first
  (:command [[_ x]] (compile-command x))
  (:string [[_ x]] x)
  (:number [[_ x]] (str x))
  (:compound [[_ x]] (compile-compound x)))

(defn full-compile [x]
  (-> x
      parse
      compile-form))
