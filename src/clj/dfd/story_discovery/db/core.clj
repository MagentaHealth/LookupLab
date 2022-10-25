(ns dfd.story-discovery.db.core
  (:require
    [kit.edge.db.postgres :refer [->pgobject]]
    [next.jdbc.prepare :as prepare])
  (:import
    [clojure.lang IPersistentVector]
    [java.sql PreparedStatement]))


(extend-protocol prepare/SettableParameter
  IPersistentVector
  (set-parameter [v ^PreparedStatement stmt ^long idx]
    (let [conn      (.getConnection stmt)
          meta      (.getParameterMetaData stmt)
          type-name (.getParameterTypeName meta idx)]
      (if-let [elem-type (when (= (first type-name) \_) (apply str (rest type-name)))]
        (.setObject stmt idx (.createArrayOf conn elem-type (to-array v)))
        (.setObject stmt idx (->pgobject v))))))
