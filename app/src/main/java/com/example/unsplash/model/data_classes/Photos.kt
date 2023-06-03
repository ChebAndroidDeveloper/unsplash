package com.example.unsplash.model.data_classes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class Photos(
    @PrimaryKey val id: String,
    val createdAt: String?,
    val updatedAt: String?,
    val width: Int?,
    val height: Int?,
    val color: String?,
    val blurHash: String?,
    val likes: Int,
    val liked_by_user: Boolean,
    val description: String?,
    val user: User,
    val urls: Map<String, String>,
    val links: Map<String, String>
)

data class User(
    val id: String,
    val username: String,
    val name: String,
    val portfolioUrl: String?,
    val bio: String?,
    val location: String?,
    val totalLikes: Int,
    val totalPhotos: Int,
    val totalCollections: Int,
    val instagramUsername: String?,
    val twitterUsername: String?,
    val profileImage: Map<String, String>,
    val links: Map<String, String>
)