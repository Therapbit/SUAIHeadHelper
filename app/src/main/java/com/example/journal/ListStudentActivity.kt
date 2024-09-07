package com.example.journal

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.journal.databinding.AddStudentBinding
import com.example.journal.databinding.ListStudentBinding

class ListStudentActivity : AppCompatActivity() {
    private lateinit var binding: ListStudentBinding

    private fun openActivity(targetActivity: Class<*>) {
        val intent = Intent(this, targetActivity)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ListStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = MainDb.getDb(this)

        val studentAdapter = ListStudentAdapter(emptyList())
        binding.recyclerViewStudents.apply {
            layoutManager = LinearLayoutManager(this@ListStudentActivity)
            adapter = studentAdapter
        }

        // Установка наблюдателя
        db.getDao().selectStudents().asLiveData().observe(this) { list ->
            studentAdapter.updateList(list)
        }


        binding.editStudent.setOnClickListener {
            val intent = Intent(this, AddStudentActivity::class.java)
            startActivity(intent)
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