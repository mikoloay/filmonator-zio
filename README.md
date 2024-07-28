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
* htmx (written as Scala using Scalatags) - for front end

### TODO

* Add tests of course :)
* Create a nice front page with htmx
* Document the code
* Replace Quill with Doobie (maybe in a new repo though)
* Add scripts to containerize the app and run it with docker-compose
* Add authentication (login or bearer tokens)
* Print more logs from the http server
* Feature ideas:
  * Fetch movie descriptions from the web and update the table
  * Add kafka integration
  * Add the possibility to load csv files from S3/Minio
