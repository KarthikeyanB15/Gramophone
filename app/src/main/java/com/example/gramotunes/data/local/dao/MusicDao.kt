package com.example.gramotunes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gramotunes.data.local.entity.MusicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(music: List<MusicEntity>)

    @Query("SELECT * FROM music ORDER BY title ASC")
    fun getAllMusic() : Flow<List<MusicEntity>>

    @Query("SELECT COUNT(*) FROM music")
    suspend fun count(): Int

}