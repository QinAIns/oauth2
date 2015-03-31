(ns oauth2.db.core
  (:require
    [yesql.core :refer [defqueries]]))

(def db-spec
  {:subprotocol "postgresql"
   :subname "//localhost/oauth2"
   :user "postgres"
   :password "postgres"})

(defqueries "sql/queries.sql" {:connection db-spec})
