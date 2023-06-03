package com.example.unsplash.model.data_classes

data class PhotoDetails(
    val id: String,
    val created_at: String,
    val updated_at: String,
    val width: Int,
    val height: Int,
    val color: String,
    val blur_hash: String?,
    val downloads: Int,
    val likes: Int,
    val liked_by_user: Boolean,
    val public_domain: Boolean,
    val description: String?,
    val exif: Exif?,
    val location: Location?,
    val tags: List<Tag>?,
    val current_user_collections: List<Collection>?,
    val urls: Urls,
    val links: Links,
    val user: User
)

data class Exif(
    val make: String?,
    val model: String?,
    val name: String?,
    val exposure_time: String?,
    val aperture: String?,
    val focal_length: String?,
    val iso: Int?
)

data class Location(
    val city: String?,
    val country: String?,
    val position: Position?
)

data class Position(
    val latitude: Double?,
    val longitude: Double?
)

data class Tag(
    val title: String
)

data class Collection(
    val id: Int,
    val title: String,
    val published_at: String,
    val last_collected_at: String,
    val updated_at: String,
    val cover_photo: PhotoDetails?,
    val user: User
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
    val download_location:String
)



