package com.example.unsplash.model.get_content

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unsplash.model.data_classes_for_search_result.Photo
import javax.inject.Inject

class SearchPhotoPagingSourceByItId @Inject constructor(
    private val api: UnsplashApi,
    private val query: String,
    private val authorization: String
) : PagingSource<Int, Photo>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        return try {
            val page = params.key ?: 1
            val response = api.searchPhotos(
                query = query,
                page = page,
                perPage = params.loadSize,
                authorization = authorization
            )

            if (response.isSuccessful) {
                val data: List<Photo> = response.body()?.results ?: emptyList()
                val nextPage = page + 1
                LoadResult.Page(data, prevKey = null, nextKey = nextPage)
            } else {
                LoadResult.Error(Exception("Failed to load data"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }


}
