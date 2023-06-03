package com.example.unsplash.model.content_collections_data_classes

data class Photo(
    val id: String,
    val created_at: String,
    val updated_at: String,
    val width: Int,
    val height: Int,
    val color: String,
    val blur_hash: String,
    val likes: Int,
    val liked_by_user: Boolean,
    val description: String?,
    val user: User,
    val current_user_collections: List<Collection>,
    val urls: Urls,
    val links: Links
)

data class User(
    val id: String,
    val username: String,
    val name: String,
    val portfolio_url: String?,
    val bio: String?,
    val location: String?,
    val total_likes: Int,
    val total_photos: Int,
    val total_collections: Int,
    val instagram_username: String?,
    val twitter_username: String?,
    val profile_image: ProfileImage,
    val links: Links
)

data class Collection(
    val id: Int,
    val title: String,
    val published_at: String,
    val last_collected_at: String,
    val updated_at: String,
    val cover_photo: Photo?,
    val user: User?
)

data class Urls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String
)

data class Links(
    val self: String,
    val html: String?,
    val photos:String?,
    val likes:String?,
    val portfolio :String?
)

data class ProfileImage(
    val small: String,
    val medium: String,
    val large: String
)

