;; Copyright (C) 2022-2023 Magenta Health Inc. 
;; Authored by Carmen La <https://carmen.la/>.

;; This file is part of LookupLab.

;; LookupLab is free software: you can redistribute it and/or modify
;; it under the terms of the GNU Affero General Public License as
;; published by the Free Software Foundation, either version 3 of the
;; License, or (at your option) any later version.

;; LookupLab is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU Affero General Public License for more details.

;; You should have received a copy of the GNU Affero General Public License
;; along with LookupLab.  If not, see <https://www.gnu.org/licenses/>.

(ns build
  (:require [clojure.string :as string]
            [clojure.tools.build.api :as b]
            [deps-deploy.deps-deploy :as deploy]))

(def lib 'dfd/story-discovery)
(def main-cls (string/join "." (filter some? [(namespace lib) (name lib) "core"])))
(def version (format "0.0.1-SNAPSHOT"))
(def target-dir "target")
(def class-dir (str target-dir "/" "classes"))
(def uber-file (format "%s/%s-standalone.jar" target-dir (name lib)))
(def basis (b/create-basis {:project "deps.edn"}))

(defn clean
  "Delete the build target directory"
  [_]
  (println (str "Cleaning " target-dir))
  (b/delete {:path target-dir}))

(defn prep [_]
  (println "Writing Pom...")
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis basis
                :src-dirs ["src/clj"]})
  (b/copy-dir {:src-dirs ["src/clj" "resources" "env/prod/resources" "env/prod/clj"]
               :target-dir class-dir}))

(defn uber [_]
  (println "Compiling Clojure...")
  (b/compile-clj {:basis basis
                  :src-dirs ["src/clj" "env/prod/resources" "env/prod/clj"]
                  :class-dir class-dir})
  (println "Making uberjar...")
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :main main-cls
           :basis basis}))

(defn app [_]
  (do (clean nil) (prep nil) (uber nil)))


(defn ths [_]
  (println "generating search_data/dfd.ths file from search_data/synonyms.csv")
  (->> (string/split (slurp "search_data/synonyms.csv") (re-pattern (System/lineSeparator)))
       (rest)
       (map (fn [s] (clojure.string/split s (re-pattern ","))))
       (reduce (fn [res [keyword syn]]
                   (str res syn " : " keyword "\n"))
               "")
       (spit "search_data/dfd.ths")))

(defn syn [_]
  (println "generating search_data/dfd.syn file from search_data/synonyms.csv")
  (->> (string/split (slurp "search_data/synonyms.csv") (re-pattern (System/lineSeparator)))
       (rest)
       (map (fn [s] (first (clojure.string/split s (re-pattern ",")))))
       distinct
       (reduce (fn [res keyword]
                 (if (string/includes? (string/trim keyword) " ")
                   res
                   (str res keyword " " keyword "\n")))
               "")
       (spit "search_data/dfd.syn")))