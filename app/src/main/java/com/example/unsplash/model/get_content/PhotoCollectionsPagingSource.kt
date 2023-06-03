package com.example.unsplash.model.get_content

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unsplash.model.collection_data_classes.CollectionResponse
import javax.inject.Inject

class PhotoCollectionsPagingSource @Inject constructor(
    private val api: UnsplashApi,
    private val authorization: String
) : PagingSource<Int, CollectionResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CollectionResponse> {
        Log.d("9999", "load called")
        return try {
            val page = params.key ?: 1
            val response = api.searchCollections(
                page = page,
                perPage = params.loadSize,
                authorization = authorization
            )

            if (response.isSuccessful) {
                Log.d("9999", "response is successful")
                val data: List<CollectionResponse> = response.body() ?: emptyList()
                Log.d("9999", data.size.toString())
                val nextPage = page + 1
                LoadResult.Page(data, prevKey = null, nextKey = nextPage)
            } else {
                Log.d("9999", "response is not successful")
                LoadResult.Error(Exception("Failed to load data"))
            }
        } catch (e: Exception) {
            Log.d("9999", "exception caught: ${e.message}")
            LoadResult.Error(e)
        }
    }


    override fun getRefreshKey(state: PagingState<Int, CollectionResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }


}