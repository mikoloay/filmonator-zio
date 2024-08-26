package com.github.mikolololoay.views


import scalatags.Text
import scalatags.Text.all.*


object UnauthenticatedView:
    val fullBody: Text.TypedTag[String] =
        div(
            h1("Access denied!"),
            p("You need to be logged in to access this site."),
            a(
                href := "/login",
                "Go to Login"
            )
        )