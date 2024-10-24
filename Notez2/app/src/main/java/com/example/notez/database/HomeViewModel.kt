package com.example.notez.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel

// HomeViewModel.kt
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)

    // Fetch the branch data
    suspend fun getBranch(branchName: String): BranchEntity? {
        return database.branchDao().getBranch(branchName)
    }

    // Fetch years for the selected branch
    suspend fun getYears(branchName: String): List<YearEntity> {
        return database.yearDao().getYears(branchName)
    }

    // Fetch semesters for the selected year
    suspend fun getSemesters(yearId: Int): List<SemesterEntity> {
        return database.semesterDao().getSemesters(yearId)
    }

    // Fetch subjects for the selected semester
    suspend fun getSubjects(semesterId: Int): List<SubjectEntity> {
        return database.subjectDao().getSubjects(semesterId)
    }
}