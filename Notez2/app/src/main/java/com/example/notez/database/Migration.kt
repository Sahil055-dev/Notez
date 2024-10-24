package com.example.notez.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Example migration: Add a new column to an existing table
        // Modify the SQL statements according to your actual schema changes
        database.execSQL("ALTER TABLE my_table ADD COLUMN new_column INTEGER NOT NULL DEFAULT 0")
    }
}