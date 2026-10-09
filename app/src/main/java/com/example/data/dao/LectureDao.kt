package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Lecture
import kotlinx.coroutines.flow.Flow

@Dao
interface LectureDao {

    @Query("SELECT * FROM lectures ORDER BY hour ASC, minute ASC")
    fun getAllLectures(): Flow<List<Lecture>>

    @Query("SELECT * FROM lectures WHERE day = :day OR day = 'Daily' ORDER BY hour ASC, minute ASC")
    fun getLecturesForDay(day: String): Flow<List<Lecture>>

    @Query("SELECT * FROM lectures WHERE enabled = 1")
    suspend fun getEnabledLectures(): List<Lecture>

    @Query("SELECT * FROM lectures WHERE id = :id LIMIT 1")
    suspend fun getLectureById(id: Long): Lecture?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLecture(lecture: Lecture): Long

    @Update
    suspend fun updateLecture(lecture: Lecture)

    @Delete
    suspend fun deleteLecture(lecture: Lecture)

    @Query("DELETE FROM lectures WHERE id = :id")
    suspend fun deleteLectureById(id: Long)
}
