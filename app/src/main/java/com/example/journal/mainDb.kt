package com.example.journal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database
    (entities =
    [Group::class,
    Student::class,
    Discipline::class,
    Attendance::class,
    Schedule::class],
    version = 1,exportSchema = false)

abstract class MainDb : RoomDatabase() {
    abstract fun getDao(): DeviceDao
    companion object{

        fun getDb(context: Context): MainDb{
            return Room.databaseBuilder(
                context.applicationContext,
                MainDb::class.java,
                "journal2.db"
            ).build()
        }
    }
}

