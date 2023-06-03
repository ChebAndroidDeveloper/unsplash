package com.example.unsplash.model.data_classes_for_search_result

data class ApiResponse(
    val total: Int,
    val total_pages: Int,
    val results: List<Photo>
)

data class Photo(
    val id: String,
    val created_at: String,
    val width: Int,
    val height: Int,
    val color: String,
    val blur_hash: String,
    val likes: Int,
    val liked_by_user: Boolean,
    val description: String?,
    val user: User,
    val current_user_collections: List<Any>,
    val urls: Urls,
    val links: Links
)

data class User(
    val id: String,
    val username: String,
    val name: String,
    val first_name: String,
    val last_name: String?,
    val instagram_username: String?,
    val twitter_username: String?,
    val portfolio_url: String?,
    val profile_image: ProfileImage,
    val links: UserLinks
)

data class ProfileImage(
    val small: String,
    val medium: String,
    val large: String
)

data class UserLinks(
    val self: String,
    val html: String,
    val photos: String,
    val likes: String
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
    val html: String,
    val download: String
)
