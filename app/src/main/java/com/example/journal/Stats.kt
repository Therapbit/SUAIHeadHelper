package com.example.journal

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.journal.databinding.ActivityStatsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale


class Stats : AppCompatActivity() {
    private lateinit var binding: ActivityStatsBinding
    private lateinit var builder: AlertDialog.Builder
    private lateinit var db: MainDb
    private lateinit var studentAdapterStats: StudentAdapterStats
    private lateinit var datePickerFrom: EditText
    private lateinit var datePickerTo: EditText


    private fun openActivity(targetActivity: Class<*>) {
        val intent = Intent(this, targetActivity)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        db = MainDb.getDb(this)

        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        datePickerFrom = binding.datePickerFrom
        datePickerTo = binding.datePickerTo

        studentAdapterStats = StudentAdapterStats(emptyList())
        binding.RecyclerViewStats.apply {
            layoutManager = LinearLayoutManager(this@Stats)
            adapter = studentAdapterStats
        }

        binding.disciplines.setOnClickListener {
            openActivity(DisciplinesActivity::class.java)
        }


        binding.schedule.setOnClickListener {
            openActivity(SchedulePresentation::class.java)
        }

        binding.group.setOnClickListener {
            openActivity(ListStudentActivity::class.java)
        }

        setupSpinner()
        observeStudents()
        setupDatePickers()
    }

    private fun setupSpinner() {
        db.getDao().selectAllDisciplineNames().observe(this) { disciplineNames ->
            val allDisciplines = listOf("Все") + disciplineNames
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, allDisciplines)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerDisciplines.adapter = adapter

            binding.spinnerDisciplines.onItemSelectedListener = object :AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedDiscipline = allDisciplines[position]
                    updateAttendanceData(selectedDiscipline)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Ничего не делать
                }
            }
        }
    }




    private fun setupDatePickers() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateAttendanceData(binding.spinnerDisciplines.selectedItem.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        datePickerFrom.addTextChangedListener(textWatcher)
        datePickerTo.addTextChangedListener(textWatcher)
    }

    private fun updateAttendanceData(discipline: String) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateFromText =if (datePickerFrom.text != null) datePickerFrom.text.toString() else "01012020"
        val dateToText = if (datePickerTo.text != null) datePickerTo.text.toString() else "01012050"

        lifecycleScope.launch {
            val attendanceMap = withContext(Dispatchers.IO) {
                val attendanceMap = mutableMapOf<Int, Triple<Int, Int, Int>>()
                if (discipline == "Все") {
                    val attendances = if (dateFromText.isNotEmpty() && dateToText.isNotEmpty()) {
                        try {
                            dateFormat.parse(dateFromText)
                            dateFormat.parse(dateToText)
                            db.getDao().selectAttendanceBetweenDates(dateFromText, dateToText)
                        } catch (e: Exception) {
                            emptyList() // or handle the exception as needed
                        }
                    } else {
                        db.getDao().selectAllAttendance()
                    }
                    attendances.forEach {
                        val current = attendanceMap[it.ID_STUDENT] ?: Triple(0, 0, 0)
                        attendanceMap[it.ID_STUDENT] = Triple(
                            current.first + 1,
                            current.second + it.YESORNO,
                            current.third + if(it.YESORNO == 0) 1 else 0
                        )
                    }
                } else {
                    val disciplineId = db.getDao().getDisciplineIdByName(discipline)
                    val attendances = if (dateFromText.isNotEmpty() && dateToText.isNotEmpty()) {
                        try {
                            dateFormat.parse(dateFromText)
                            dateFormat.parse(dateToText)
                            db.getDao().selectAttendanceByDisciplineAndDates(disciplineId, dateFromText, dateToText)
                        } catch (e: Exception) {
                            emptyList() // or handle the exception as needed
                        }
                    } else {
                        db.getDao().selectAttendanceByDiscipline(disciplineId)
                    }
                    attendances.forEach {
                        val current = attendanceMap[it.ID_STUDENT] ?: Triple(0, 0, 0)
                        attendanceMap[it.ID_STUDENT] = Triple(
                            current.first + 1,
                            current.second + it.YESORNO,
                            current.third + if (it.YESORNO == 0) 1 else 0
                        )
                    }
                }
                attendanceMap
            }

            // Обновляем адаптер в главном потоке
            withContext(Dispatchers.Main) {
                db.getDao().selectStudents().asLiveData().observe(this@Stats) { list ->
                    studentAdapterStats.updateList(list, attendanceMap)
                }
            }
        }
    }

    private fun observeStudents() {
        db.getDao().selectStudents().asLiveData().observe(this) { list ->
            val selectedItem = binding.spinnerDisciplines.selectedItem
            if (selectedItem != null) {
                updateAttendanceData(selectedItem.toString())
            } else {
                updateAttendanceData("Все")
            }
        }
    }
}