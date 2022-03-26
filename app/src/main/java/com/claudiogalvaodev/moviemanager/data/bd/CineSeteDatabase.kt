package com.claudiogalvaodev.moviemanager.data.bd

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.claudiogalvaodev.moviemanager.data.bd.converter.Converters
import com.claudiogalvaodev.moviemanager.data.bd.dao.MoviesSavedDao
import com.claudiogalvaodev.moviemanager.data.bd.dao.MyListsDao
import com.claudiogalvaodev.moviemanager.data.bd.entity.MovieSaved
import com.claudiogalvaodev.moviemanager.data.bd.entity.MyList

@Database(entities = [MyList::class, MovieSaved::class], version = 4, exportSchema = true)
@TypeConverters(Converters::class)
abstract class CineSeteDatabase: RoomDatabase() {

    abstract val myListsDao: MyListsDao
    abstract val moviesSavedDao: MoviesSavedDao

    companion object {
        private lateinit var instance: CineSeteDatabase

        fun getInstance(
            context: Context,
        ) : CineSeteDatabase {
            if(Companion::instance.isInitialized) return instance

            instance = Room.databaseBuilder(context,
                CineSeteDatabase::class.java,
                "database")
                .createFromAsset("database/CineSete.db")
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                .build()

            return instance
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `OscarNomination` (`id` INTEGER NOT NULL,\n" +
                        "`itemId` INTEGER NOT NULL, `type` TEXT NOT NULL, `title` TEXT NOT NULL,\n" +
                        "`subtitle` TEXT, `imageUrl` TEXT NOT NULL,`releaseDate` TEXT,\n" +
                        "`leastOneMovieId` INTEGER, `categories` TEXT NOT NULL, `categoriesWinner` TEXT NOT NULL,\n" +
                        "PRIMARY KEY(`id` AUTOINCREMENT))")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE OscarNomination")
            }

        }
    }
}