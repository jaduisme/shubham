package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        BookCover::class,
        Manuscript::class,
        Chapter::class
    ],
    version = 2,
    exportSchema = false
)
abstract class KdpDatabase : RoomDatabase() {

    abstract fun kdpDao(): KdpDao

    companion object {
        @Volatile
        private var INSTANCE: KdpDatabase? = null

        fun getDatabase(context: Context): KdpDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KdpDatabase::class.java,
                    "kdp_bookmaker_database"
                )
                .fallbackToDestructiveMigration() // Quick reset during iterations if needed
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
