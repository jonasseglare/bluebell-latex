(ns bluebell.latex.core-test
  (:require [bluebell.latex.core :refer :all :as latex]
            [clojure.spec :as spec]
            [clojure.test :refer :all]))

(deftest id-test
  (is (spec/valid? ::latex/id (latex/id "mjao"))))

(deftest args-test
  (is (spec/valid? ::latex/arg [::latex/arg "bra va"]))
  (is (spec/valid? ::latex/opt [::latex/opt "mjaoe"]))
  (is (spec/valid? ::latex/args [[::latex/opt "asdfasd"]
                                 [::latex/arg "asdfsf"]])))

(deftest cmd-test
  (is (spec/valid? ::latex/command [:documentclass
                                    [::latex/opt "a4paper"]
                                    [::latex/arg "article"]]))
  (is (not (spec/valid? ::latex/command [::documentclass
                                         [::latex/opt "a4paper"]
                                         [::latex/arg "article"]]))))

(deftest block-test
  (is (spec/valid? ::latex/block [::latex/block :mjao
                                  "Ganska bra va"])))

(deftest compile-tests
  (is (= "[3]" (compile-opt (spec/conform ::latex/opt [::latex/opt 3]))))
  (is (= "{3}" (compile-arg "" (spec/conform ::latex/arg [::latex/arg 3]))))
  (is (= "\\newline{a}" (compile-form
                        (spec/conform
                         ::latex/form
                         [:newline [::latex/arg "a"]]))))
  (is (= "\\begin{center}\nKattskit\n\\end{center}"
         (compile-form (spec/conform ::latex/form [::latex/block :center "Kattskit"]))))
  (is (= "1 2 3 4" (compile-form (spec/conform ::latex/form [1 2 3 4]))))
  (is (= "1" (compile-form (spec/conform ::latex/form 1))))
  (is (= "abc" (compile-form (spec/conform ::latex/form "abc"))))
  (is (= (compile-form (spec/conform
                        ::latex/form [:sum [::latex/lower "n = 0"]
                                     [::latex/upper "m"]]))
         "\\sum_{n = 0}^{m}"))
  (is (= "adsf=9"
         (compile-map (spec/conform ::latex/map {"adsf" 9}))))
  (is (= (full-compile [:figure {:width 9 :height 3}])
         "\\figure[width=9,height=3]")))
