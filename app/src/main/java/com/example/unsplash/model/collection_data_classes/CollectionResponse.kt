package com.example.unsplash.model.collection_data_classes

import com.example.unsplash.model.data_classes_for_search_result.ProfileImage

data class CollectionResponse(
    val id: String,
    val title: String,
    val description: String,
    val published_at: String,
    val last_collected_at: String,
    val updated_at: String,
    val total_photos: Int,
    val private: Boolean,
    val share_key: String,
    val cover_photo: CoverPhoto,
    val user: User
)

data class CoverPhoto(
    val id: String,
    val width: Int,
    val height: Int,
    val color: String,
    val blur_hash: String,
    val likes: Int,
    val liked_by_user: Boolean,
    val description: String?,
    val user: User,
    val urls: Urls,
    val links: Links
)

data class User(
    val id: String,
    val updated_at: String?,
    val username: String,
    val name: String?,
    val portfolio_url: String?,
    val bio: String?,
    val location: String?,
    val total_likes: Int?,
    val total_photos: Int?,
    val total_collections: Int?,
    val profile_image: ProfileImage?,
    val links: Links
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