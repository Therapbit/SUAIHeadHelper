package com.example.journal

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val dao: DeviceDao) {
    fun getAllDisciplines(): LiveData<List<String>> {
        return dao.selectAllDisciplineNames()
    }

    suspend fun getDisciplineIdByName(name: String): Int {
        return dao.getDisciplineIdByName(name)
    }

    suspend fun insertSchedule(schedule: Schedule) {
        dao.insertSchedule(schedule)
    }

    fun getAllSchedules(): Flow<List<Schedule>> {
        return dao.selectAllSchedules()
    }

    fun getAllDisciplineEntities(): Flow<List<Discipline>> {
        return dao.selectDisciplines()
    }
}