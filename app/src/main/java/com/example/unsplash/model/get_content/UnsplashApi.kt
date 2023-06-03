package com.example.unsplash.model.get_content


import com.example.unsplash.model.data_classes.LikeResponse
import com.example.unsplash.model.data_classes.Photos
import com.example.unsplash.model.data_classes.PhotoDetails
import com.example.unsplash.model.liks_user.Like
import com.example.unsplash.model.user_data_classes.Me

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashApi {
    @GET("/photos")
    fun getPhotoList(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Header("Authorization") authorization: String
    ): Call<List<Photos>>

    @GET("photos/{id}")
    suspend fun getPhotoDetailsById(
        @Path("id") id: String,
        @Header("Authorization") auth: String
    ): PhotoDetails

    @POST("photos/{id}/like")
    suspend fun likePhoto(
        @Path("id") id: String,
        @Header("Authorization") auth: String
    ): Response<LikeResponse>

    @DELETE("photos/{id}/like")
    suspend fun unlikePhoto(
        @Path("id") id: String,
        @Header("Authorization") auth: String
    ): Response<LikeResponse>


    @GET("/search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Header("Authorization") authorization: String
    ): Response<com.example.unsplash.model.data_classes_for_search_result.ApiResponse>


    @GET("/collections")
    suspend fun searchCollections(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Header("Authorization") authorization: String
    ): Response<List<com.example.unsplash.model.collection_data_classes.CollectionResponse>>

    @GET("/collections/{id}/photos")
    suspend fun getCollectionContent(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Header("Authorization") authorization: String
    ): Response<List<com.example.unsplash.model.content_collections_data_classes.Photo>>


    @GET("/me")
    suspend fun getInfoAboutMe(
        @Header("Authorization") authorization: String
    ): Response<Me>


    @GET("/users/{username}/likes")
    suspend fun getUserLikes(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Header("Authorization") authorization: String
    ): Response<List<Like>>


}







