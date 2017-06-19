(ns bluebell.latex.core-test
  (:require [bluebell.latex.core :refer :all :as latex]
            [clojure.spec :as spec]
            [clojure.test :refer :all]))

(deftest spec-test
  (is (spec/valid? ::latex/command-setting [:opt "asdffasd"]))
  (is (spec/valid? ::latex/command-setting [:lower "asdf"]))
  (is (spec/valid? ::latex/command-settings [:opt {:rulle 3} :opt "kattskit"]))
  (is (spec/valid? ::latex/command [:sum :lower "a = 3" :upper "9" "a^2"]))
  (is (spec/valid? ::latex/command [:frac  "3" :arg "4"])))
