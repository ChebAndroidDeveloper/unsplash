package com.example.unsplash.model.get_content

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unsplash.model.data_classes.Photos
import javax.inject.Inject

class DatabasePagingSource @Inject constructor (
    private val unsplashRepository: UnsplashRepository
) : PagingSource<Int, Photos>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photos> {
        return try {
            val photos = unsplashRepository.getPhotosFromDatabase()
            LoadResult.Page(
                data = photos,
                prevKey = null,
                nextKey = null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photos>): Int? {
        TODO("Not yet implemented")
    }
}
