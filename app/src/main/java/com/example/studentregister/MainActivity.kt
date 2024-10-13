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
import com.example.studentregister.db.Student
import com.example.studentregister.db.StudentDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var clearButton: Button

    private lateinit var viewModel: StudentViewModel
    private lateinit var studentRecyclerView: RecyclerView
    private lateinit var adapter: StudentRecyclerViewAdapter

    private lateinit var selectedStudent: Student
    private var isListItemClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        nameEditText = findViewById(R.id.etName)
        emailEditText = findViewById(R.id.etEmail)
        saveButton = findViewById(R.id.btnSave)
        clearButton = findViewById(R.id.btnClear)
        studentRecyclerView = findViewById(R.id.rvStudents)

        val dao = StudentDatabase.getInstance(application).studentDao()
        val factory = StudentViewModelFactory(dao)
        viewModel = ViewModelProvider(this, factory).get(StudentViewModel::class.java)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        saveButton.setOnClickListener {
            if (isListItemClicked) {
                updateStudent()
                clearInput()
            } else {
                saveStudentData()
                clearInput()
            }

        }

        clearButton.setOnClickListener {
            if (isListItemClicked) {
                deleteStudent()
                clearInput()
            } else {
                clearInput()
            }
        }

        initiateRecyclerView()
    }

    private fun initiateRecyclerView() {
        studentRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StudentRecyclerViewAdapter { student: Student ->
            clickListener(student)
        }
        studentRecyclerView.adapter = adapter
        displayStudentsList()
    }

    private fun displayStudentsList() {
        viewModel.students.observe(this) {
            adapter.setStudentsList(it)
            adapter.notifyDataSetChanged()
        }
    }

    private fun clickListener(student: Student) {
        selectedStudent = student
        saveButton.setText("Update")
        clearButton.setText("Delete")
        isListItemClicked = true
        nameEditText.setText(selectedStudent.name)
        emailEditText.setText(selectedStudent.email)
    }

    private fun updateStudent() {
        viewModel.updateStudent(
            Student(
                selectedStudent.id,
                nameEditText.text.toString(),
                emailEditText.text.toString()
            )
        )

        resetInput()
    }

    private fun deleteStudent() {
        viewModel.deleteStudent(
            Student(
                selectedStudent.id,
                nameEditText.text.toString(),
                emailEditText.text.toString()
            )
        )

        resetInput()
    }

    private fun resetInput() {
        saveButton.setText("Save")
        clearButton.setText("Clear")
        isListItemClicked = false
    }

    private fun saveStudentData() {
        val name = nameEditText.text.toString()
        val email = emailEditText.text.toString()
        if (!name.isNullOrEmpty() && !email.isNullOrEmpty()) {
            viewModel.insertStudent(
                Student(
                    0,
                    nameEditText.text.toString(),
                    emailEditText.text.toString()
                )
            )
        }
    }

    private fun clearInput() {
        nameEditText.setText("")
        emailEditText.setText("")
    }
}