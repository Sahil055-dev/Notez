package com.example.notez.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// BranchDao.kt
@Dao
interface BranchDao {
    @Query("SELECT * FROM branches WHERE name = :branchName")
    suspend fun getBranch(branchName: String): BranchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranch(branch: BranchEntity)

}

// YearDao.kt
@Dao
interface YearDao {
    @Query("SELECT * FROM years WHERE branchName = :branchName")
    suspend fun getYears(branchName: String): List<YearEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertYear(year: YearEntity)
}

// SemesterDao.kt
@Dao
interface SemesterDao {
    @Query("SELECT * FROM semesters WHERE yearId = :yearId")
    suspend fun getSemesters(yearId: Int): List<SemesterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: SemesterEntity)
}

// SubjectDao.kt
@Dao
interface SubjectDao {

    // Fetch all subjects by semester ID
    @Query("SELECT * FROM subjects WHERE semesterId = :semesterId")
    suspend fun getSubjects(semesterId: Int): List<SubjectEntity>

    // Insert or replace a subject
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity)

    // Fetch a specific subject by its ID
    @Query("SELECT * FROM subjects WHERE id = :subjectId")
    suspend fun getSubjectById(subjectId: Int): SubjectEntity?
}


// CurriculumNoteDao.kt
@Dao
interface CurriculumNoteDao {
    @Query("SELECT * FROM notes WHERE moduleId = :moduleId")
    suspend fun getNotes(moduleId: Int): List<CurriculumNoteEntity>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Int): CurriculumNoteEntity? // Method to get a single note by its ID

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: CurriculumNoteEntity)
}

@Dao
interface ModuleDao {

    // Fetch all modules by subject ID
    @Query("SELECT * FROM modules WHERE subjectId = :subjectId")
    suspend fun getModulesForSubject(subjectId: Int): List<ModuleEntity>

    // Insert or replace a module
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(module: ModuleEntity)
}

// PyqDao.kt
@Dao
interface PastYearPaperDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPastYearPaper(pastYearPaper: PastYearPaperEntity)

    @Query("SELECT * FROM past_year_papers WHERE subjectId = :subjectId")
    suspend fun getPastYearPapersBySubject(subjectId: Int): List<PastYearPaperEntity>

    // Get a specific past year paper by its ID
    @Query("SELECT * FROM past_year_papers WHERE id = :paperId LIMIT 1")
    suspend fun getPastYearPaperById(paperId: Int): PastYearPaperEntity?
}

//UserUploadedNotes Dao
@Dao
interface UserUploadedNoteDao {
    @Query("SELECT * FROM user_uploaded_notes WHERE moduleId = :moduleId")
    suspend fun getUserUploadedNotes(moduleId: Int): List<UserUploadedNoteEntity>

    @Query("SELECT * FROM user_uploaded_notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Int): UserUploadedNoteEntity? // Method to get a single user-uploaded note by its ID

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserUploadedNote(note: UserUploadedNoteEntity)
}
