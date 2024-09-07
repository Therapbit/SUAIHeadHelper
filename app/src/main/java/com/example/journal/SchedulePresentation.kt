package com.example.journal

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.journal.databinding.ActivityMainBinding
import com.example.journal.databinding.ActivitySchedulePresentationBinding
import com.example.journal.databinding.ListStudentBinding

class SchedulePresentation : AppCompatActivity() {

    private lateinit var scheduleAdapter: ScheduleAdapter
    private val viewModel: ScheduleViewModel by viewModels()
    lateinit var binding: ActivitySchedulePresentationBinding
    private lateinit var builder: AlertDialog.Builder
    private lateinit var scheduleViewModel: ScheduleViewModel

    private fun openActivity(targetActivity: Class<*>) {
        val intent = Intent(this, targetActivity)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySchedulePresentationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val typesOfPair = arrayOf("ЛР", "ПР", "Л")
        val numbersOfPair = arrayOf("1", "2", "3", "4", "5", "6")
        builder = AlertDialog.Builder(this)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        scheduleAdapter = ScheduleAdapter(emptyList())
        recyclerView.adapter = scheduleAdapter

        scheduleViewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)

        viewModel.groupedSchedulesWithDisciplines.observe(this, Observer { groupedSchedules ->
            scheduleAdapter.updateList(groupedSchedules)
        })

        binding.disciplines.setOnClickListener {
            openActivity(DisciplinesActivity::class.java)
        }

        binding.stats.setOnClickListener {
            openActivity(Stats::class.java)
        }
        binding.group.setOnClickListener {
            openActivity(ListStudentActivity::class.java)
        }

        binding.addSchedule.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_schedule, null)
            val editTextPairDate = dialogView.findViewById<EditText>(R.id.editTextDate)

            val editSpinnerType: Spinner = dialogView.findViewById(R.id.spinnerType)
            val adapterType = ArrayAdapter(this, android.R.layout.simple_spinner_item, typesOfPair)
            adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            editSpinnerType.adapter = adapterType

            val editSpinnerNumber: Spinner = dialogView.findViewById(R.id.spinnerNumber)
            val adapterNumber = ArrayAdapter(this, android.R.layout.simple_spinner_item, numbersOfPair)
            adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            editSpinnerNumber.adapter = adapterNumber

            val editSpinnerDiscipline: Spinner = dialogView.findViewById(R.id.spinnerDiscipline)

            scheduleViewModel.allDisciplines.observe(this, Observer { disciplineNames ->
                val adapterDiscipline = ArrayAdapter(this, android.R.layout.simple_spinner_item, disciplineNames)
                adapterDiscipline.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                editSpinnerDiscipline.adapter = adapterDiscipline
            })

            builder.setView(dialogView)
                .setTitle("Добавьте расписание")
                .setCancelable(true)
                .setPositiveButton("Ок") { dialogInterface, it ->
                    val typeOfPair = editSpinnerType.selectedItem.toString()
                    val numberOfPair = editSpinnerNumber.selectedItem.toString()
                    val pairDate = editTextPairDate.text.toString()
                    val disciplineName = editSpinnerDiscipline.selectedItem.toString()
                    scheduleViewModel.getDisciplineIdByName(disciplineName).observe(this, Observer { disciplineId ->
                        if (disciplineId != null) {
                            val schedule = Schedule(null, disciplineId, pairDate, numberOfPair, typeOfPair)
                            scheduleViewModel.insert(schedule)
                        }
                    })

                    dialogInterface.dismiss()
                }
                .setNegativeButton("Отмена") { dialogInterface, it ->
                    dialogInterface.cancel()
                }
                .show()
        }
        }
}
