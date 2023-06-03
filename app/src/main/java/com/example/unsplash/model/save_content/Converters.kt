package com.example.unsplash.model.save_content

import androidx.room.TypeConverter
import com.example.unsplash.model.data_classes.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromUrlsMap(value: Map<String, String>): String {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toUrlsMap(value: String): Map<String, String> {
        val gson = Gson()
        val type = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromUser(user: User): String {
        val gson = Gson()
        return gson.toJson(user)
    }

    @TypeConverter
    fun toUser(json: String): User {
        val gson = Gson()
        return gson.fromJson(json, User::class.java)
    }
}
