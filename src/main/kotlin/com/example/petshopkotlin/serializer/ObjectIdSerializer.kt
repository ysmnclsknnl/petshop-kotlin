package com.example.petshopkotlin.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.bson.types.ObjectId
import java.io.IOException

class ObjectIdSerializer : JsonSerializer<ObjectId>() {
    @Throws(IOException::class)
    override fun serialize(objectId: ObjectId, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        jsonGenerator.writeString(objectId.toHexString())
    }
}
