package com.cailloutr.room

import io.ktor.websocket.*

data class Members(
    val username: String,
    val sessionId: String,
    val socket: WebSocketSession
)
