(ns bluebell.latex.core-test
  (:require [bluebell.latex.core :refer :all :as latex]
            [clojure.spec :as spec]
            [clojure.test :refer :all]))

(deftest spec-test
  (is (spec/valid? ::latex/command-setting [:opt "asdffasd"]))
  (is (spec/valid? ::latex/command-setting [:lower "asdf"]))
  (is (spec/valid? ::latex/command-settings [:opt {:rulle 3} :opt "kattskit"]))
  (is (spec/valid? ::latex/command [:sum :lower "a = 3" :upper "9" "a^2"]))
  (is (spec/valid? ::latex/command [:frac  "3" "4"]))
  (is (= (spec/conform ::latex/form ["asdf" "asdf"])
         [:compound [[:string "asdf"] [:string "asdf"]]]))
  (is (spec/valid? ::latex/command [:begin "document" :body "This is the title" "some more"])))

(deftest id-test
  (is (= "rulle" (identifier-to-str (spec/conform ::latex/identifier (id "rulle")))))
  (is (= "rulle" (identifier-to-str (spec/conform ::latex/identifier :rulle)))))

(deftest compiling
  (is (= "mjao" (full-compile "mjao"))) ; string
  (is (= "9" (full-compile 9))); number
  (is (= "abc9k" (full-compile ["abc" 9 ["k"]]))) ; Compound
  (is (= "\\rulle{a}" (full-compile [:rulle ["a"]])))
  (is (= "\\rulle_{n = 0}^{10}" (full-compile [:rulle :lower "n = 0" :upper "10"])))
  (is (= "\\rulle[package=mjao]"
         (full-compile [:rulle :opt {:package "mjao"}])))
  (is (= "\\rulle[mjao]" (full-compile [:rulle :opt "mjao"])))
  (is (= (full-compile [:begin "center" :body "Kattskit!"])
         "\\begin{center}\nKattskit!\n\\end{center}")))
