(ns bluebell.latex.core
  (:require [clojure.spec :as spec]
            [bluebell.utils.defmultiple :as defmultiple]))

(defn prefixed [p sp]
  (spec/cat :prefix (partial = p)
            :value sp))

(defn str-sep [sep args]
  (if (empty? args) "" 
      (reduce #(str %1 sep %2) (map str args))))

(defn str-lines [& args]
  (str-sep "\n" args))

(defn str-space [& args]
  (str-sep " " args))

(spec/def ::id (spec/cat :prefix (partial = ::id)
                         :value string?))

(spec/def ::identifier (spec/or :keyword (spec/and
                                          keyword?
                                          #(not (qualified-keyword? %)))
                                :id ::id))

(defn id [x]
  [::id x])

(spec/def ::opt (prefixed ::opt ::opt-args))
(spec/def ::arg (prefixed ::arg ::forms))
(spec/def ::lower (prefixed ::lower ::forms))
(spec/def ::upper (prefixed ::upper ::forms))

(defmultiple/defmultiple identifier-to-str first
  (:keyword [[_ x]] (name x))
  (:id [[_ x]] (:value x)))

(spec/def ::string string?)

(spec/def ::arg-key (spec/or :keyword keyword?
                             :string string?))

(defmultiple/defmultiple arg-key-to-str first
  (:keyword [[_ x]] (name x))
  (:string [[_ x]] x))

(spec/def ::map (spec/map-of ::arg-key ::form))


(spec/def ::any-arg (spec/or :opt ::opt
                             :arg ::arg
                             :lower ::lower
                             :upper ::upper
                             :map ::map))

(spec/def ::opts (spec/* ::opt))

(spec/def ::number number?)

(spec/def ::compound (spec/coll-of ::form))

(spec/def ::block (prefixed ::block (spec/cat :pre-ops ::opts
                                              :name ::identifier
                                              :post-args ::args
                                              :body ::forms)))

(defn compile-block [x]
  (let [id (identifier-to-str (:name x))]
    (str "\\begin" (compile-args (:pre-opts x))
         "{" id
         "}" (compile-args (:post-args x))
         "\n" (compile-forms "\n" (:body x)) "\n"
         "\\end{" id "}")))

(spec/def ::form (spec/or :command ::command
                          :block ::block
                          :string ::string
                          :number ::number
                          :compound ::compound))

(spec/def ::forms (spec/* ::form))

(spec/def ::args (spec/* ::any-arg))

(spec/def ::command (spec/cat :name ::identifier
                              :args ::args))

(defn parse [x]
  (spec/conform ::form x))

(declare compile-form)

(defn to-assignment [[k v]]
  (str (arg-key-to-str (spec/conform ::arg-key  k))
       "=" (compile-form v)))

(defn compile-map [x]
  (str-sep "," (map to-assignment x)))

(defn compile-forms [sep forms]
  (str-sep sep (map compile-form forms)))

(defn compile-opt [v]
  (str "[" (compile-forms "," (:value v)) "]"))

(defn compile-arg [prefix v]
  (str prefix "{" (compile-forms "" (:value v)) "}"))

(defmultiple/defmultiple compile-any-arg first
  (:opt [[_ x]] (compile-opt x))
  (:arg [[_ x]] (compile-arg "" x))
  (:lower [[_ x]] (compile-arg "_" x))
  (:upper [[_ x]] (compile-arg "^" x))
  (:map [[_ x]] (str "[" (compile-map x) "]")))

(defn compile-args [v]
  (apply str (map compile-any-arg v)))

(defn compile-command [x]
  (str "\\" (identifier-to-str (:name x))
       (compile-args (:args x))))


(defn compile-compound [x]
  (apply str-space (map compile-form x)))

(defmultiple/defmultiple compile-form first
  (:command [[_ x]] (compile-command x))
  (:string [[_ x]] x)
  (:number [[_ x]] (str x))
  (:block [[_ x]] (compile-block (:value x)))
  (:compound [[_ x]] (compile-compound x))
  (:map [[_ x]] (compile-map x)))

(defn full-compile [x]
  (-> x
      parse
      compile-form))
