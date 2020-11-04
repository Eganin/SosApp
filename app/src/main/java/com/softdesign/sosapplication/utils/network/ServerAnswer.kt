package com.softdesign.sosapplication.utils.network

import com.beust.klaxon.Json

data class ServerAnswer(
        @Json(name = "server_answer")
        val server_answer: String
)