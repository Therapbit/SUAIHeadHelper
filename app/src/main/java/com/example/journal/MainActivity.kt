
package com.example.journal

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.example.journal.databinding.ActivityMainBinding
import androidx.lifecycle.ViewModelProvider

import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var builder: AlertDialog.Builder
    private lateinit var scheduleViewModel: ScheduleViewModel

    private fun openActivity(targetActivity: Class<*>) {
        val intent = Intent(this, targetActivity)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val typesOfPair = arrayOf("ЛР", "ПР", "Л")
        val numbersOfPair = arrayOf("1", "2", "3", "4", "5", "6")
        builder = AlertDialog.Builder(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scheduleViewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)

        binding.group.setOnClickListener {
            Thread{
                val db = MainDb.getDb(this)
                val count = db.getDao().getGroupCount()
                if (count == 0) {
                    openActivity(AddGroupActivity::class.java)
                } else {
                    openActivity(ListStudentActivity::class.java)
                }
            }.start()
        }


        binding.reset.setOnClickListener{
            Thread{
                val db = MainDb.getDb(this)
                db.getDao().deleteAttendance()
                db.getDao().deleteAllSchedule()
                db.getDao().deleteAllDisciplines()
            }.start()
        }

        binding.disciplines.setOnClickListener {
            openActivity(DisciplinesActivity::class.java)
        }

        binding.stats.setOnClickListener {
            openActivity(Stats::class.java)
        }


        binding.schedule.setOnClickListener {
            openActivity(SchedulePresentation::class.java)
        }
   }
}
