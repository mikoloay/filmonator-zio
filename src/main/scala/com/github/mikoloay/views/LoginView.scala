package com.github.mikoloay.views


import scalatags.Text
import scalatags.Text.all.*
import com.github.mikoloay.views.htmx.HtmxAttributes


object LoginView:
    val fullBody = div(
        div(id := "message-container"),
        h2("Login"),
        form(
            HtmxAttributes.post("/login"),
            HtmxAttributes.target("#message-container"),
            HtmxAttributes.swap("innerHTML"),
            label(
                `for` := "username",
                "Username"
            ),
            input(
                `type` := "text",
                id := "username",
                name := "username",
                required
            ),
            br(),
            br(),
            label(
                `for` := "password",
                "Password"
            ),
            input(
                `type` := "text",
                id := "password",
                name := "password",
                required
            ),
            br(),
            br(),
            button(
                `type` := "submit",
                "Log in"
            )
        )
    )
