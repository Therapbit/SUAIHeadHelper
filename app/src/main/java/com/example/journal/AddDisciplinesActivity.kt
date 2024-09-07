package com.example.journal

import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.journal.databinding.ActivityAddDisciplinesBinding

class AddDisciplinesActivity : AppCompatActivity()  {
private lateinit var binding: ActivityAddDisciplinesBinding
private lateinit var builder: AlertDialog.Builder
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val db = MainDb.getDb(this)

    binding = ActivityAddDisciplinesBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val disciplineAdapter = DisciplineAdapter(emptyList())
    binding.recyclerViewDisciplines.apply {
        layoutManager = LinearLayoutManager(this@AddDisciplinesActivity)
        adapter = disciplineAdapter
    }

    db.getDao().selectDisciplines().asLiveData().observe(this) { list ->
        disciplineAdapter.updateList(list)
    }

    disciplineAdapter.setOnItemClickListener(object : DisciplineAdapter.OnItemClickListener {
        override fun onDeleteClick(position: Int) {
            val disciplineToDelete = disciplineAdapter.getItem(position)
            Thread {
                db.getDao().deleteDiscipline(disciplineToDelete)
            }.start()
        }
    })

    builder = AlertDialog.Builder(this)

    binding.addDiscipline.setOnClickListener{
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_discipline, null)
        val editTextDiscipline = dialogView.findViewById<EditText>(R.id.addDiscipline)

        builder.setView(dialogView)
            .setTitle("Добавьте дисциплину")
            .setCancelable(true)
            .setPositiveButton("Далее") { dialogInterface, it ->


                Thread {
                    val disciplineName = editTextDiscipline.text.toString()
                    val groupNumber = db.getDao().selectGroupNumber()
                    val discipline = Discipline(null, disciplineName, groupNumber)
                    db.getDao().insertDiscipline(discipline)
                }.start()

                dialogInterface.dismiss()
            }
            .setNegativeButton("Отмена") { dialogInterface, it ->
                dialogInterface.cancel()
            }
            .show()
    }


}
}