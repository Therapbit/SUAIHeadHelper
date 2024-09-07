package com.example.journal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "GROUPS"
)
data class Group(
    @PrimaryKey
    val GROUP_NUMBER: Int,
    @ColumnInfo(name = "AMOUNT_STUDENTS")
    var AMOUNT_STUDENTS: Int
)

@Entity(
    tableName = "STUDENTS",
    foreignKeys = [ForeignKey(
        entity = Group::class,
        parentColumns = ["GROUP_NUMBER"],
        childColumns = ["GROUP_NUMBER"]
    )],
    indices = [Index(value = ["GROUP_NUMBER"])]
)
data class Student(
    @PrimaryKey(autoGenerate = true)
    val ID_STUDENT: Int?,
    @ColumnInfo(name = "GROUP_NUMBER")
    val GROUP_NUMBER: Int,
    @ColumnInfo(name = "NAME")
    var NAME: String,
    @ColumnInfo(name = "SURNAME")
    var SURNAME: String,
    @ColumnInfo(name = "PATRONYMIC")
    var PATRONYMIC: String
)

@Entity(
    tableName = "DISCIPLINES",
    indices = [Index(value = ["GROUP_NUMBER"])]
)
data class Discipline(
    @PrimaryKey(autoGenerate = true)
    val ID_DISCIPLINE: Int? = null,
    @ColumnInfo(name = "NAME")
    var NAME: String,
    @ColumnInfo(name = "GROUP_NUMBER")
    val GROUP_NUMBER: Int
)

@Entity(
    tableName = "SCHEDULE",
    foreignKeys = [ForeignKey(
        entity = Discipline::class,
        parentColumns = ["ID_DISCIPLINE"],
        childColumns = ["ID_DISCIPLINE"]
    )],
    indices = [Index(value = ["ID_DISCIPLINE"])]
)
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val ID_PAIR: Int? = null,
    @ColumnInfo(name = "ID_DISCIPLINE")
    val ID_DISCIPLINE: Int,
    @ColumnInfo(name = "DATE_PAIR")
    var DATE_PAIR: String,
    @ColumnInfo(name = "TIME_PAIR")
    var TIME_PAIR: String,
    @ColumnInfo(name = "TYPE")
    var TYPE: String
)

@Entity(
    tableName = "ATTENDANCE",
    primaryKeys = ["ID_STUDENT", "ID_PAIR"],
    foreignKeys = [
        ForeignKey(
            entity = Schedule::class,
            parentColumns = ["ID_PAIR"],
            childColumns = ["ID_PAIR"]
        ),
        ForeignKey(
            entity = Student::class,
            parentColumns = ["ID_STUDENT"],
            childColumns = ["ID_STUDENT"]
        )
    ],
    indices = [Index(value = ["ID_PAIR"]), Index(value = ["ID_STUDENT"])]
)
data class Attendance(
    @ColumnInfo(name = "ID_STUDENT")
    val ID_STUDENT: Int,
    @ColumnInfo(name = "ID_PAIR")
    val ID_PAIR: Int,
    @ColumnInfo(name = "Y/N")
    var YESORNO: Int
)