package com.example.coursework_1

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TASKS_TABLE =
            "CREATE	TABLE $ACHIEVEMENT_PROGRESS($COLUMN_ID INTEGER PRIMARY KEY,$COLUMN_TOPIC TEXT," +
                    "$CURRENT_PROGRESS INT)"
        db.execSQL(CREATE_TASKS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $ACHIEVEMENT_PROGRESS")
        onCreate(db)
    }

    fun isTableEmpty(): Boolean {
        return readableDatabase.rawQuery("SELECT EXISTS (SELECT 1 FROM $ACHIEVEMENT_PROGRESS LIMIT 1)", null)
            .use {
                it.moveToFirst() && it.getInt(0) == 0
            }
    }

    fun deleteTable() {
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $ACHIEVEMENT_PROGRESS")
    }

    fun addNewTopic(topicName: String) {
        val values = ContentValues()
        values.put(COLUMN_TOPIC, topicName)
        val db = this.writableDatabase
        db.insert(ACHIEVEMENT_PROGRESS, null, values)
    }

    fun updateProgress(topic: String, score: Int) {
        val values = ContentValues()
        values.put(CURRENT_PROGRESS, score)
        val db = this.writableDatabase
        db.update(
            ACHIEVEMENT_PROGRESS,
            values,
            "$COLUMN_ID	= ?",
            arrayOf((topic))
        )
    }

    companion object {
        private const val DATABASE_VERSION = 5
        private const val DATABASE_NAME = "achievements"
        private const val ACHIEVEMENT_PROGRESS = "Achievement_Progress"

        private const val COLUMN_ID = "_id"
        private const val COLUMN_TOPIC = "topic"
        private const val CURRENT_PROGRESS = "total"
    }




}