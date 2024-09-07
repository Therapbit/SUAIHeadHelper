package com.example.journal

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao{
    @Insert
    fun insertGroup(item: Group)

    @Query ("SELECT GROUP_NUMBER FROM GROUPS LIMIT 1")
    fun selectGroupNumber(): Int

    @Query("SELECT COUNT(*) FROM GROUPS")
    fun getGroupCount(): Int


    @Query ("SELECT * FROM GROUPS")
    fun selectAllGroup(): Flow<List<Group>>
    @Insert
    fun insertStudent(item: Student)
    @Query ("SELECT * FROM STUDENTS ORDER BY SURNAME")
    fun selectStudents(): Flow<List<Student>>

    @Query ("DELETE FROM GROUPS")
    fun deleteAllGroup()

    @Delete
    fun deleteStudent(student: Student)

    @Query("""
    SELECT s.ID_PAIR, d.NAME, s.TYPE 
    FROM SCHEDULE s
    JOIN DISCIPLINES d ON s.ID_DISCIPLINE = d.ID_DISCIPLINE
    WHERE s.DATE_PAIR = :date
    """)
    fun getPair(date: String): List<PairWithDiscipline>

    @Query("SELECT * FROM ATTENDANCE")
    fun getAtt(): LiveData<List<Attendance>>

    @Query("SELECT * FROM ATTENDANCE")
    fun getAttDirect(): List<Attendance>

    @Query("UPDATE ATTENDANCE SET `Y/N` =:count WHERE ID_PAIR =:idOfPair AND ID_STUDENT =:idOfStudent")
    fun updateAttendance(idOfPair: Int, idOfStudent: Int, count: Int)


    @Query("SELECT * FROM STUDENTS")
    fun getStudentsDirect(): List<Student>

    @Query ("SELECT * FROM DISCIPLINES")
    fun selectDisciplines(): Flow<List<Discipline>>
    @Delete
    fun deleteDiscipline(discipline: Discipline)
    @Insert
    fun insertDiscipline(item: Discipline)
    @Query("SELECT ID_DISCIPLINE FROM DISCIPLINES WHERE NAME = :name")
    suspend fun getDisciplineIdByName(name: String): Int
    @Query("SELECT NAME FROM DISCIPLINES")
    fun selectAllDisciplineNames(): LiveData<List<String>>

    @Insert
    fun insertSchedule(item: Schedule)
    @Query("SELECT * FROM SCHEDULE")
    fun selectAllSchedules(): Flow<List<Schedule>>
    @Delete
    fun deleteSchedule(schedule: Schedule)

    @Query("""
        SELECT A.* 
        FROM ATTENDANCE A
        JOIN SCHEDULE S ON A.ID_PAIR = S.ID_PAIR
        WHERE S.ID_DISCIPLINE = :disciplineId 
        AND S.DATE_PAIR BETWEEN :dateFrom AND :dateTo
    """)
    suspend fun selectAttendanceByDisciplineAndDates(disciplineId: Int, dateFrom: String, dateTo: String): List<Attendance>


    @Query("""
        SELECT A.* 
        FROM ATTENDANCE A
        JOIN SCHEDULE S ON A.ID_PAIR = S.ID_PAIR
        WHERE S.DATE_PAIR BETWEEN :dateFrom AND :dateTo
    """)
    suspend fun selectAttendanceBetweenDates(dateFrom: String, dateTo: String): List<Attendance>

    @Query("SELECT * FROM ATTENDANCE WHERE ID_PAIR = :idOfPair AND ID_STUDENT = :idOfStudent LIMIT 1")
    suspend fun getAttendanceRecord(idOfPair: Int, idOfStudent: Int): Attendance?

    @Insert
    suspend fun insertAttendance(attendance: Attendance)

    @Query("SELECT * FROM ATTENDANCE")
    suspend fun selectAllAttendance(): List<Attendance>

    @Query("SELECT * FROM ATTENDANCE WHERE ID_PAIR IN (SELECT ID_PAIR FROM SCHEDULE WHERE ID_DISCIPLINE = :disciplineId)")
    suspend fun selectAttendanceByDiscipline(disciplineId:Int): List<Attendance>

    @Query ("DELETE FROM DISCIPLINES")
    fun deleteAllDisciplines()

    @Query ("DELETE FROM SCHEDULE")
    fun deleteAllSchedule()

    @Query ("DELETE FROM ATTENDANCE")
    fun deleteAttendance()

}