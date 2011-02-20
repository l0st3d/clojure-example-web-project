(ns infrastructure.db
  (:import [com.mchange.v2.c3p0 ComboPooledDataSource])
  (:require [clojure.contrib.logging :as log]))

(defn- create-pool-datasource [username password hostname database-name]
  (doto (new ComboPooledDataSource)
    (.setDriverClass "com.mysql.jdbc.Driver")
    (.setUser username)
    (.setPassword password)
    (.setIdleConnectionTestPeriod 3600)
    (.setMinPoolSize 1)
    (.setMaxPoolSize 25)
    (.setMaxIdleTimeExcessConnections 1800)
    (.setInitialPoolSize 1)
    (.setPreferredTestQuery "select 'testing database connection'")
    (.setMaxStatementsPerConnection 250)
    (.setJdbcUrl (str "jdbc:mysql://" hostname "/" database-name "?useServerPrepStmts=true&characterEncoding=UTF-8"))))

(defn create-db-spec [user password database]
  {:datasource (create-pool-datasource user password "localhost" database)})

(def spec (create-db-spec "root" "" "clj_ex"))
