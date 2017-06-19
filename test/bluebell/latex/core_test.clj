(ns bluebell.latex.core-test
  (:require [bluebell.latex.core :refer :all :as latex]
            [clojure.spec :as spec]
            [clojure.test :refer :all]))

(deftest spec-test
  (is (spec/valid? ::latex/command-setting [:opt "asdffasd"])))
