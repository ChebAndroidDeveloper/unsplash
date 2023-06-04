package com.example.unsplash.model.get_content

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unsplash.model.content_collections_data_classes.PhotoCollections
import javax.inject.Inject

class CollectionContentListPagingSource@Inject constructor(
    private val api: UnsplashApi,
    private val authorization: String,
    private val collectionId: String
) : PagingSource<Int, PhotoCollections>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoCollections> {
        return try {
            val page = params.key ?: 1
            val response = api.getCollectionContent(
                id = collectionId,
                page = page,
                perPage = params.loadSize,
                authorization = authorization
            )
            Log.d("PagingSource", "Response body: ${response.body()}") // Добавлено логирование

            if (response.isSuccessful) {
                val data: List<PhotoCollections> = response.body() ?: emptyList()
                Log.d("ListPagingSource", "load: $data")
                if (data.isEmpty()) {
                    // Возвращаем пустую страницу с null для prevKey и nextKey
                    LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
                } else {
                    val nextPage = page + 1
                    LoadResult.Page(data, prevKey = null, nextKey = nextPage)
                }
            } else {
                LoadResult.Error(Exception("Failed to load data"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PhotoCollections>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
