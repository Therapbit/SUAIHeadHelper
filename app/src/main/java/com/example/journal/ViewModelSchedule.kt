package com.example.journal

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class ScheduleWithDisciplineName(
    val schedule: Schedule,
    val disciplineName: String
)

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ScheduleRepository
    val allDisciplines: LiveData<List<String>>
    val groupedSchedulesWithDisciplines: MediatorLiveData<List<Pair<String, List<ScheduleWithDisciplineName>>>>

    init {
        val dao = MainDb.getDb(application).getDao()
        repository = ScheduleRepository(dao)
        allDisciplines = repository.getAllDisciplines()

        val schedulesLiveData = repository.getAllSchedules().flowOn(Dispatchers.IO).asLiveData()
        val disciplinesLiveData = repository.getAllDisciplineEntities().flowOn(Dispatchers.IO).asLiveData()

        groupedSchedulesWithDisciplines = MediatorLiveData<List<Pair<String, List<ScheduleWithDisciplineName>>>>()

        groupedSchedulesWithDisciplines.addSource(schedulesLiveData) { schedules ->
            val disciplines = disciplinesLiveData.value
            if (disciplines != null) {
                groupedSchedulesWithDisciplines.value = groupSchedulesWithDisciplinesByDate(schedules, disciplines)
            }
        }

        groupedSchedulesWithDisciplines.addSource(disciplinesLiveData) { disciplines ->
            val schedules = schedulesLiveData.value
            if (schedules != null) {
                groupedSchedulesWithDisciplines.value = groupSchedulesWithDisciplinesByDate(schedules, disciplines)
            }
        }
    }

    private fun groupSchedulesWithDisciplinesByDate(
        schedules: List<Schedule>,
        disciplines: List<Discipline>
    ): List<Pair<String, List<ScheduleWithDisciplineName>>> {
        val disciplineMap = disciplines.associateBy { it.ID_DISCIPLINE }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

        return schedules.groupBy { it.DATE_PAIR }
            .toSortedMap(compareBy { dateFormat.parse(it) })
            .map { (date, schedulesForDate) ->
                val schedulesWithDisciplineNameForDate = schedulesForDate.map { schedule ->
                    ScheduleWithDisciplineName(
                        schedule,
                        disciplineMap[schedule.ID_DISCIPLINE]?.NAME ?: "Unknown Discipline"
                    )
                }
                date to schedulesWithDisciplineNameForDate
            }
    }

    fun insert(schedule: Schedule) = viewModelScope.launch(Dispatchers.IO) {
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        val initialDate = dateFormat.parse(schedule.DATE_PAIR)!!
        val calendar = Calendar.getInstance()
        calendar.time = initialDate

        for (i in 0 until 9) {
            val newSchedule = schedule.copy(
                ID_PAIR = null,
                DATE_PAIR = dateFormat.format(calendar.time)
            )
            repository.insertSchedule(newSchedule)
            calendar.add(Calendar.DAY_OF_YEAR, 14)
        }
    }

    fun getDisciplineIdByName(name: String): LiveData<Int> {
        val result = MutableLiveData<Int>()
        viewModelScope.launch(Dispatchers.IO) {
            val disciplineId = repository.getDisciplineIdByName(name)
            result.postValue(disciplineId)
        }
        return result
    }
}

