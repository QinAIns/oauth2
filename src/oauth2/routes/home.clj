(ns oauth2.routes.home
  (:require [oauth2.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [clojure.java.io :as io]))

(defn authorize
  ;;授权请求
  [response_type client_id redirect_uri scope state]
  {:status 302
   :headers {"Location" (str redirect_uri "?code=123&state=" state )}}
  )

(defn home-page []
  
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  
  (GET "/authorize" [response_type client_id redirect_uri scope state] (authorize response_type client_id redirect_uri scope state))
  
  (GET "/about" [] (about-page)))
