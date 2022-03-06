package com.claudiogalvaodev.moviemanager.data.bd.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.claudiogalvaodev.moviemanager.data.bd.entity.OscarNomination
import kotlinx.coroutines.flow.Flow

@Dao
interface OscarNominationsDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun populate(oscarNominactions: List<OscarNomination>)

    @Query("SELECT * FROM OscarNomination")
    fun getAll(): Flow<List<OscarNomination>>
}