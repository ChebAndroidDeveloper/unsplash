package com.example.unsplash.model.get_content

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unsplash.model.content_collections_data_classes.Photo
import javax.inject.Inject

class CollectionContentListPagingSource@Inject constructor(
    private val api: UnsplashApi,
    private val authorization: String,
    private val collectionId: String
) : PagingSource<Int, Photo>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        return try {
            val page = params.key ?: 1
            val response = api.getCollectionContent(
                id = collectionId,
                page = page,
                perPage = params.loadSize,
                authorization = authorization
            )

            if (response.isSuccessful) {
                val data: List<Photo> = response.body() ?: emptyList()
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