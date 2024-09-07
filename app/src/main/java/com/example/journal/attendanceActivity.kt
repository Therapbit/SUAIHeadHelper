package com.example.journal

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.CheckBox
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PairWithDiscipline(
    val ID_PAIR: Int,
    val NAME: String,
    val TYPE: String
)

class attendanceActivity : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout
    private lateinit var db: MainDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        tableLayout = findViewById(R.id.tableAttendance)
        db = MainDb.getDb(this)

        val date = intent.getStringExtra("DATE") ?: "20.08.24"

        val dateTextView = findViewById<TextView>(R.id.dateTextView)
        dateTextView.text = "Посещения $date"

        lifecycleScope.launch {
            val students = getStudents()
            val pairsWithDiscipline = getPairsWithDisciplineByDate(date)
            val attendance = getAttendance()

            withContext(Dispatchers.Main) {
                checkDataAndCreateTable(students, pairsWithDiscipline, attendance)
            }
        }
    }

    private suspend fun getStudents(): List<Student> = withContext(Dispatchers.IO) {
        db.getDao().getStudentsDirect()
    }

    private suspend fun getPairsWithDisciplineByDate(date: String): List<PairWithDiscipline> = withContext(Dispatchers.IO) {
        db.getDao().getPair(date)
    }

    private suspend fun getAttendance(): List<Attendance> = withContext(Dispatchers.IO) {
        db.getDao().getAttDirect()
    }

    private suspend fun createAttendanceRecordsIfNeeded(date: String, students: List<Student>, pairsWithDiscipline: List<PairWithDiscipline>) {
        withContext(Dispatchers.IO) {
            students.forEach { student ->
                pairsWithDiscipline.forEach { pair ->
                    val attendanceRecord = db.getDao().getAttendanceRecord(pair.ID_PAIR, student.ID_STUDENT!!)
                    if (attendanceRecord == null) {
                        db.getDao().insertAttendance(Attendance(
                            ID_STUDENT = student.ID_STUDENT!!,
                            ID_PAIR = pair.ID_PAIR,
                            YESORNO = 0
                        ))
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun checkDataAndCreateTable(students: List<Student>, pairsWithDiscipline: List<PairWithDiscipline>, attendance: List<Attendance>) {
        tableLayout.removeAllViews()

        val message = when {
            students.isEmpty() && pairsWithDiscipline.isEmpty() -> "Список группы и расписание отсутствуют"
            students.isEmpty() -> "Список группы отсутствует"
            pairsWithDiscipline.isEmpty() -> "Расписание отсутствует"
            else -> null
        }

        if (message != null) {
            val messageTextView = TextView(this).apply {
                text = message
                gravity = Gravity.CENTER
                textSize = 20f
                setTypeface(null, Typeface.NORMAL)
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT).apply {
                    gravity = Gravity.CENTER
                }
            }
            tableLayout.addView(messageTextView)
        } else {
            lifecycleScope.launch {
                createAttendanceRecordsIfNeeded("20.08.24", students, pairsWithDiscipline)
                val updatedAttendance = getAttendance()
                withContext(Dispatchers.Main) {
                    createTable(students, pairsWithDiscipline, updatedAttendance)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createTable(students: List<Student>, pairsWithDiscipline: List<PairWithDiscipline>, attendance: List<Attendance>) {
        tableLayout.removeAllViews()

        val headerRow = TableRow(this).apply {
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 16, 0, 16)
            }
        }

        val emptyCell = TextView(this).apply {
            text = ""
            gravity = Gravity.CENTER
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT).apply {
                width = 300
            }
        }
        headerRow.addView(emptyCell)

        pairsWithDiscipline.forEach { pair ->
            val pairHeader = TextView(this).apply {
                text = "${pair.NAME}\n(${pair.TYPE})"
                gravity = Gravity.CENTER
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT).apply {
                    width = 300
                }
            }
            headerRow.addView(pairHeader)
        }

        tableLayout.addView(headerRow)

        students.forEach { student ->
            val row = TableRow(this).apply {
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(0, 16, 0, 16)
                }
            }

            val studentCell = TextView(this).apply {
                val initials = "${student.SURNAME} ${student.NAME.firstOrNull()?.toUpperCase() ?: ""}.${student.PATRONYMIC.firstOrNull()?.toUpperCase()?.let { "$it." } ?: ""}"
                text = initials
                gravity = Gravity.CENTER
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT).apply {
                    width = 300
                    gravity = Gravity.CENTER_VERTICAL
                }
            }
            row.addView(studentCell)

            pairsWithDiscipline.forEach { pair ->
                val checkBox = CheckBox(this).apply {
                    val attRecord = attendance.find { it.ID_PAIR == pair.ID_PAIR && it.ID_STUDENT == student.ID_STUDENT }
                    isChecked = attRecord?.YESORNO == 1

                    layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT).apply {
                        gravity = Gravity.CENTER
                        width = 300
                    }

                    setOnCheckedChangeListener { _, isChecked ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            student.ID_STUDENT?.let {
                                db.getDao().updateAttendance(pair.ID_PAIR, it, if (isChecked) 1 else 0)
                            }
                        }
                    }
                }
                row.addView(checkBox)
            }
            tableLayout.addView(row)
        }
    }
}
