package com.example.studentregister

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentregister.databinding.ActivityMainBinding
import com.example.studentregister.db.Student
import com.example.studentregister.db.StudentDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: StudentViewModel
    private lateinit var adapter: StudentRecyclerViewAdapter
    private lateinit var binding: ActivityMainBinding

    private lateinit var selectedStudent: Student
    private var isListItemClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dao = StudentDatabase.getInstance(application).studentDao()
        val factory = StudentViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory).get(StudentViewModel::class.java)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.apply {
            btnSave.setOnClickListener {
                if (isListItemClicked) {
                    updateStudent()
                    clearInput()
                } else {
                    saveStudentData()
                    clearInput()
                }

            }

            btnClear.setOnClickListener {
                if (isListItemClicked) {
                    deleteStudent()
                    clearInput()
                } else {
                    clearInput()
                }
            }
        }
        initiateRecyclerView()
    }

    private fun initiateRecyclerView() {
        binding.rvStudents.layoutManager = LinearLayoutManager(this)
        adapter = StudentRecyclerViewAdapter { student: Student ->
            clickListener(student)
        }
        binding.rvStudents.adapter = adapter
        displayStudentsList()
    }

    private fun displayStudentsList() {
        viewModel.students.observe(this) {
            adapter.setStudentsList(it)
            adapter.notifyDataSetChanged()
        }
    }

    private fun clickListener(student: Student) {
        binding.apply {
            selectedStudent = student
            btnSave.setText("Update")
            btnClear.setText("Delete")
            isListItemClicked = true
            etName.setText(selectedStudent.name)
            etEmail.setText(selectedStudent.email)
        }
    }

    private fun updateStudent() {
        binding.apply {
            viewModel.updateStudent(
                Student(
                    selectedStudent.id,
                    etName.text.toString(),
                    etEmail.text.toString()
                )
            )
        }
        resetInput()
    }

    private fun deleteStudent() {
        binding.apply {
            viewModel.deleteStudent(
                Student(
                    selectedStudent.id,
                    etName.text.toString(),
                    etEmail.text.toString()
                )
            )
            resetInput()
        }
    }

    private fun resetInput() {
        binding.apply {
            btnSave.setText("Save")
            btnClear.setText("Clear")
            isListItemClicked = false
        }
    }

    private fun saveStudentData() {
        binding.apply {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            if (!name.isNullOrEmpty() && !email.isNullOrEmpty()) {
                viewModel.insertStudent(
                    Student(
                        0,
                        etName.text.toString(),
                        etEmail.text.toString()
                    )
                )
            }
        }
    }

    private fun clearInput() {
        binding.apply {
            etName.setText("")
            etEmail.setText("")
        }
    }
}