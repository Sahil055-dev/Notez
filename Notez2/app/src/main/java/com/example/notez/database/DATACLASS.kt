package com.example.notez.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// BranchEntity
@Entity(tableName = "branches")
data class BranchEntity(
    @PrimaryKey val name: String // E.g. "Computer Engineering", "Civil Engineering"
)

// YearEntity
@Entity(
    tableName = "years",
    foreignKeys = [ForeignKey(
        entity = BranchEntity::class,
        parentColumns = ["name"],
        childColumns = ["branchName"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class YearEntity(
    @PrimaryKey
    val id: Int,
    val branchName: String, // Foreign key from BranchEntity
    val yearNumber: Int     // E.g. 1, 2, 3, 4
)

// SemesterEntity
@Entity(
    tableName = "semesters",
    foreignKeys = [ForeignKey(
        entity = YearEntity::class,
        parentColumns = ["id"],
        childColumns = ["yearId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SemesterEntity(
    @PrimaryKey
    val id: Int,
    val yearId: Int,          // Foreign key from YearEntity
    val semesterNumber: Int   // E.g. 1, 2
)

// SubjectEntity
@Entity(
    tableName = "subjects",
    foreignKeys = [ForeignKey(
        entity = SemesterEntity::class,
        parentColumns = ["id"],
        childColumns = ["semesterId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SubjectEntity(
    @PrimaryKey
    val id: Int,
    val semesterId: Int,    // Foreign key from SemesterEntity
    val name: String,       // E.g. "Physics-I", "Maths-I"
    val description: String // Subject description
)

// ModuleEntity
@Entity(
    tableName = "modules",
    foreignKeys = [ForeignKey(
        entity = SubjectEntity::class,
        parentColumns = ["id"],
        childColumns = ["subjectId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ModuleEntity(
    @PrimaryKey
    val id: Int,
    val subjectId: Int,     // Foreign key from SubjectEntity
    val moduleName: String  // E.g. "Module 1", "Module 2"
)

// CurriculumNoteEntity
@Entity(
    tableName = "notes",
    foreignKeys = [ForeignKey(
        entity = ModuleEntity::class,
        parentColumns = ["id"],
        childColumns = ["moduleId"],
        onDelete = ForeignKey.CASCADE
    )
    ]
)
data class CurriculumNoteEntity(
    @PrimaryKey val id: Int,
    val moduleId: Int,      // Foreign key from ModuleEntity
    val title: String,      // Note title
    val contentUrl: String  // URL to the note content
)

@Entity(
    tableName = "past_year_papers",
    foreignKeys = [ForeignKey(
        entity = SubjectEntity::class,
        parentColumns = ["id"],
        childColumns = ["subjectId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class PastYearPaperEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val subjectId: Int,    // Foreign key to the Subject
    val year: Int,         // Year of the exam (e.g., 2023)
    val month: String,     // Month of the exam (e.g., "May", "December")
    val pdfUrl: String     // URL or file path to the PDF document
)

//User Uploaded Notes
@Entity(
    tableName = "user_uploaded_notes",
    foreignKeys = [ForeignKey(
        entity = ModuleEntity::class,
        parentColumns = ["id"],
        childColumns = ["moduleId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class UserUploadedNoteEntity(
    @PrimaryKey val id: Int,
    val moduleId: Int,      // Foreign key from ModuleEntity
    val title: String,      // Note title
    val contentUrl: String  // URL to the note content
)



