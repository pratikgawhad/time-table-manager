package com.example.data.repository

import com.example.data.dao.LectureDao
import com.example.data.model.Lecture
import kotlinx.coroutines.flow.Flow

class LectureRepository(private val lectureDao: LectureDao) {

    val allLectures: Flow<List<Lecture>> = lectureDao.getAllLectures()

    fun getLecturesForDay(day: String): Flow<List<Lecture>> {
        return if (day.equals("All", ignoreCase = true)) {
            lectureDao.getAllLectures()
        } else {
            lectureDao.getLecturesForDay(day)
        }
    }

    suspend fun getEnabledLectures(): List<Lecture> = lectureDao.getEnabledLectures()

    suspend fun getLectureById(id: Long): Lecture? = lectureDao.getLectureById(id)

    suspend fun insertLecture(lecture: Lecture): Long = lectureDao.insertLecture(lecture)

    suspend fun updateLecture(lecture: Lecture) = lectureDao.updateLecture(lecture)

    suspend fun deleteLecture(lecture: Lecture) = lectureDao.deleteLecture(lecture)

    suspend fun deleteLectureById(id: Long) = lectureDao.deleteLectureById(id)
}
