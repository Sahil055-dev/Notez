package com.example.notez.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        BranchEntity::class,
        YearEntity::class,
        SemesterEntity::class,
        SubjectEntity::class,
        ModuleEntity::class,
        CurriculumNoteEntity::class,
        PastYearPaperEntity::class,
        UserUploadedNoteEntity :: class

    ],
    version = 4
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun branchDao(): BranchDao
    abstract fun yearDao(): YearDao
    abstract fun semesterDao(): SemesterDao
    abstract fun subjectDao(): SubjectDao
    abstract fun moduleDao(): ModuleDao
    abstract fun curriculumNoteDao(): CurriculumNoteDao
    abstract fun pastYearPaperDao(): PastYearPaperDao
    abstract fun userUploadedNoteDao(): UserUploadedNoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Automatically drop and recreate tables on version change
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
