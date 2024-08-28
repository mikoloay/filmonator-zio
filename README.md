# Filmonator

A fast and reliable cinema management system.

Filmonator is a side project created with the sole purpose of becoming a better Scala developer and a better developer in general. In the future I would like to implement the same application with other technologies and languages to have a straightforward comparison of code readability, performance and of how much pleasure I had when using them.

May this project also serve as another publicly available example of a full-stack Scala 3 application. They are scarce and it's always good to have them for reference.

Why a cinema management system? Well, movies are the best form of art and my another big passion along with programming, so I combine them whenever I can.

### Stack

Filmonator-zio is a Scala application built with the principles of functional programming in mind. It uses the following libraries and technologies:

* ZIO - the main framework to manage concurrency, error handling and side effects.
* Quill - to interact with the database
* tapir + ZIO HTTP - to define the http server, the endpoints, and to generate OpenAPI documentation
* htmx (written as Scala using Scalatags) - for front-end

### How to run it

The application requires local postgres and redis databases running. There is a docker-compose.yml in this folder which provides exactly those services. You can spin them up with the "docker compose up" command and then run the application normally using sbt.

When you start the application, first it will initialize the postgres db - it will create tables for all models and fill it with data from src/main/resources/initial_csvs/*.csv files. It might take a few minutes so please be patient :).

### Exploring the app

After you run the app you can go to http://localhost:8081/login to get access to other endpoints. Enter "admin" and "password" and log in. You will be redirected to the root endpoint presenting the results of an aggregation query joining data from all models. You can also visit the following endpoints:
* http://localhost:8081/movies
* http://localhost:8081/screenings
* http://localhost:8081/screening_rooms
* http://localhost:8081/tickets
* http://localhost:8081/transactions
They are all very similar and present rows from models' tables in a nice table.

You can also read the Open API specification of all API endpoints under the http://localhost:8081/api/docs endpoint. This API documentation is automatically generated by tapir.

### Structure

The application's source code has the following folders:

* models - all data entities are defined here as case classes.
* repositories - each model has its respective repository defining operations on a database.
* http - all endpoints and the http server are defined here. There are 3 types of endpoints here:
  * Api endpoints - REST API endpoints with CRUD operations on all models using the repositories.
  * UI endpoints - endpoints serving html code to clients'. UI endpoints are secured - you won't be able to view them until you successfully login in the /login endpoint ("admin", "password" are the correct credentials).
  * Auth endpoints - endpoints related to the login functionality and to validation of client's session. When someone logs in with correct credentials, a unique session id is generated, sent to Redis and returned to the client as a cookie. All UI endpoints first look for the session id from a client's cookie on Redis before they let someone in.
* views - front-end for all UI endpoints written with Scalatags and using htmx functionalities.
* utils - various utilities related to csv parsing, database initialization etc.

### TODO
* Replace Quill with Doobie (maybe in a new repo though).
* Add Kafka integration - randomly generate ticket transactions in Kafka and only then load them into postgres after some aggregations.
* Add a possibility to load csv files from S3/Minio.
