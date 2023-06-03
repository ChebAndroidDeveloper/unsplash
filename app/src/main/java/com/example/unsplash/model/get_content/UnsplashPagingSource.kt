package com.example.unsplash.model.get_content

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.unsplash.model.data_classes.Photos
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class UnsplashPagingSource @Inject constructor(
    private val unsplashApi: UnsplashApi,
    private val unsplashRepository: UnsplashRepository,
    private val accessToken: String // Включаем параметр accessToken

) : PagingSource<Int, Photos>() {

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photos> {
        Log.d("---", "токен $accessToken")
        val page = params.key ?: 1
        val result = CompletableDeferred<LoadResult<Int, Photos>>()
        Log.d("---", "резулт $result")

        unsplashApi.getPhotoList(
            page = page,
            perPage = params.loadSize,
            authorization = "Bearer $accessToken"
        ).enqueue(object : Callback<List<Photos>> {
            override fun onResponse(call: Call<List<Photos>>, response: Response<List<Photos>>) {
                val photos = response.body() ?: emptyList()
                Log.d("---", "Получены данные из API: $photos")

                result.complete(
                    LoadResult.Page(
                        data = photos,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (photos.isEmpty()) null else page + 1
                    )
                )

                GlobalScope.launch {
                    unsplashRepository.savePhotosToDatabase(photos)
                    unsplashRepository.getPhotosFromDatabase()
                }
            }

            override fun onFailure(call: Call<List<Photos>>, t: Throwable) {
                result.complete(LoadResult.Error(t))
                Log.d("---", "Что-то пошломалась $t")
            }
        })
        return result.await()
    }

    override fun getRefreshKey(state: PagingState<Int, Photos>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}





