package com.example.unsplash.model.save_content

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.unsplash.model.data_classes.Photos


@Database(entities = [Photos::class], version = 1)
@TypeConverters(Converters::class)
abstract class PhotoDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
}
