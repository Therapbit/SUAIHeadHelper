package com.example.journal

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.journal.databinding.ActivityAddGroupBinding
import com.example.journal.databinding.AddStudentBinding

class AddStudentActivity : AppCompatActivity() {
    private lateinit var binding: AddStudentBinding
    private lateinit var builder: AlertDialog.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = MainDb.getDb(this)


        binding = AddStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val studentAdapter = StudentAdapter(emptyList())
        binding.recyclerViewStudents.apply {
            layoutManager = LinearLayoutManager(this@AddStudentActivity)
            adapter = studentAdapter
        }

        // Установка наблюдателя
        db.getDao().selectStudents().asLiveData().observe(this) { list ->
            studentAdapter.updateList(list)
        }

        studentAdapter.setOnItemClickListener(object : StudentAdapter.OnItemClickListener {
            override fun onDeleteClick(position: Int) {
                val studentToDelete = studentAdapter.getItem(position)
                // Ваш код для удаления студента из базы данных
                Thread {
                    db.getDao().deleteStudent(studentToDelete)
                }.start()
            }
        })

        builder = AlertDialog.Builder(this)

        binding.addStudent.setOnClickListener{
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_student, null)
            val editTextSurnameStudent = dialogView.findViewById<EditText>(R.id.surnameStudent)
            val editTextNameStudent = dialogView.findViewById<EditText>(R.id.nameStudent)
            val editTextPatronymicStudent = dialogView.findViewById<EditText>(R.id.patronymicStudent)

            builder.setView(dialogView)
                .setTitle("Добавьте студента")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialogInterface, it ->


                    Thread {
                        val surnameStudent = editTextSurnameStudent.text.toString()
                        val nameStudent = editTextNameStudent.text.toString()
                        val patronymicStudent = editTextPatronymicStudent.text.toString()
                        val groupNumber = db.getDao().selectGroupNumber()
                        val student = Student(null, groupNumber, nameStudent, surnameStudent, patronymicStudent)
                        db.getDao().insertStudent(student)
                    }.start()

                    dialogInterface.dismiss()
                }
                .setNegativeButton("No") { dialogInterface, it ->
                    dialogInterface.cancel()
                }
                .show()
        }

        binding.editStudent.setOnClickListener {
            val intent = Intent(this, ListStudentActivity::class.java)
            startActivity(intent)
        }
    }
}