package com.codecoy.bahdjol.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.codecoy.bahdjol.Converter

@Database(entities = [Service::class, SubService::class], version = 1)
@TypeConverters(Converter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun servicesDao(): ServicesDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()


        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "ServicesDatabase.db").allowMainThreadQueries().build()
    }
}