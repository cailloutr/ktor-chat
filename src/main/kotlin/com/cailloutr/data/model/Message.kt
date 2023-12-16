package com.cailloutr.data.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
class Message(
    val text: String,
    val userName: String,
    val timestamp: Long,
    @BsonId
    val id: String = ObjectId().toString()
)