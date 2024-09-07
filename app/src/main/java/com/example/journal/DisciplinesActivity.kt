package com.example.journal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.journal.databinding.ActivityDisciplinesBinding

class DisciplinesActivity : AppCompatActivity()  {
private lateinit var binding: ActivityDisciplinesBinding

    private fun openActivity(targetActivity: Class<*>) {
        val intent = Intent(this, targetActivity)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val db = MainDb.getDb(this)

    binding = ActivityDisciplinesBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val disciplineAdapter = DisciplineAdapterSecond(emptyList())
    binding.recyclerViewDisciplines.apply {
        layoutManager = LinearLayoutManager(this@DisciplinesActivity)
        adapter = disciplineAdapter
    }

    db.getDao().selectDisciplines().asLiveData().observe(this) { list ->
        disciplineAdapter.updateList(list)
    }
    binding.editDisciplines.setOnClickListener {
        val intent = Intent(this, AddDisciplinesActivity::class.java)
        startActivity(intent)
    }

    binding.group.setOnClickListener {
        openActivity(ListStudentActivity::class.java)
    }

    binding.stats.setOnClickListener {
        openActivity(Stats::class.java)
    }

    binding.schedule.setOnClickListener {
        openActivity(SchedulePresentation::class.java)
    }

}
}