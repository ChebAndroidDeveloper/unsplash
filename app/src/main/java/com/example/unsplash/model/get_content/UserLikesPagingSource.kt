package com.example.unsplash.model.get_content

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unsplash.model.liks_user.Like

class UserLikesPagingSource(
    private val api: UnsplashApi,
    private val userName: String,
    private val authorization: String
) : PagingSource<Int, Like>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Like> {
        val page = params.key ?: 1
        Log.d("UserLikesPagingSource", "load called with page: $page")
        return try {
            val response = api.getUserLikes(
                username = userName,
                page = page,
                perPage = params.loadSize,
                authorization = authorization
            )
            val likes = response.body() ?: emptyList()
            Log.d("UserLikesPagingSource", "load received likes: $likes")
            Log.d("***", "load: $likes")
            LoadResult.Page(
                data = likes,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (likes.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            Log.e("UserLikesPagingSource", "load failed with exception: $e")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Like>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
