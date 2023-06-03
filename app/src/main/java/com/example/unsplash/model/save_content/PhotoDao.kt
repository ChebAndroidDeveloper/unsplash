package com.example.unsplash.model.save_content

import android.content.Context
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import com.example.unsplash.model.data_classes.Photos
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(photos: List<Photos>)

    @Query("SELECT * FROM photos")
    fun getAll(): List<Photos>
}
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun providePhotoDatabase(@ApplicationContext context: Context): PhotoDatabase {
        return Room.databaseBuilder(
            context,
            PhotoDatabase::class.java,
            "photo_database"
        ).build()
    }

    @Provides
    fun providePhotoDao(photoDatabase: PhotoDatabase): PhotoDao {
        return photoDatabase.photoDao()
    }
}
