package com.cailloutr.room

import com.cailloutr.data.MessageDataSource
import com.cailloutr.data.model.Message
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource
) {

    /**
     * ConcurrentHashMap ensures that only one thread at a time is writing on the hashmap
     * */
    private val members = ConcurrentHashMap<String, Members>()

    fun onJoin(
        username: String,
        sessionId: String,
        socket: WebSocketSession
    ) {
        if (members.containsKey(username)) {
            throw MemberAlreadyExistsException()
        }

        members[username] = Members(
            username = username,
            sessionId = sessionId,
            socket = socket
        )
    }

    suspend fun sendMessage(
        sendUsername: String,
        message: String
    ) {
        members.values.forEach { member ->
            val messageEntity = Message(
                text = message,
                userName = sendUsername,
                timestamp = System.currentTimeMillis()
            )
            messageDataSource.insertMessage(messageEntity)

            val parsedMessage = Json.encodeToString(messageEntity)

            member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun getAllMessages(): List<Message> {
        return messageDataSource.getAllMessages()
    }

    suspend fun tryDisconnect(
        username: String
    ) {
        members[username]?.socket?.close()

        if (members.containsKey(username)) {
            members.remove(username)
        }
    }
}