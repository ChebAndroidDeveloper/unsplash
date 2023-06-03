package com.example.unsplash.model.get_content

import com.example.unsplash.model.data_classes.Photos
import com.example.unsplash.model.save_content.PhotoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UnsplashRepository @Inject constructor(
    private val photoDao: PhotoDao
) {
    suspend fun getPhotosFromDatabase(): List<Photos> = withContext(Dispatchers.IO) {
        photoDao.getAll()
    }

    suspend fun savePhotosToDatabase(photos: List<Photos>) = withContext(Dispatchers.IO) {
        photoDao.insertAll(photos)
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {
    @Provides
    fun provideUnsplashRepository(
        photoDao: PhotoDao
    ): UnsplashRepository {
        return UnsplashRepository(photoDao)
    }
}
