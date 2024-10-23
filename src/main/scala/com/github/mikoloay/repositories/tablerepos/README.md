# Table Repos
This folder contains all logic related to repositories for accessing data represented by models.

The most important here is the type parametrized TableRepo trait. It defines common, CRUD related logic
for all model repositories. All other classes in this folder are actually only implementations of this
trait. Together with their companion objects they form a Service Pattern (https://zio.dev/reference/service-pattern/).
