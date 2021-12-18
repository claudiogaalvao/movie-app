package com.claudiogalvaodev.moviemanager.data.bd

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.claudiogalvaodev.moviemanager.data.bd.dao.MovieDao
import com.claudiogalvaodev.moviemanager.data.bd.entity.MovieEntity

@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class MoviesDatabase: RoomDatabase() {

    abstract val movieDao: MovieDao

    companion object {
        private lateinit var db: MoviesDatabase

        fun getInstance(context: Context) : MoviesDatabase {
            if(Companion::db.isInitialized) return db

            db = Room.databaseBuilder(context,
                MoviesDatabase::class.java,
                "movies")
                .allowMainThreadQueries()
                .build()

            return db
        }
    }
}