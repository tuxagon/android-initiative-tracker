package com.wobblycobbler.initiativetracker.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wobblycobbler.initiativetracker.db.daos.CharacterDao
import com.wobblycobbler.initiativetracker.db.entities.Character

@Database(version = 1, entities = [Character::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    fun prepopulate(): AppDatabase {
        this.characterDao().insertAll(
            Character(name = "Barron Redheart"),
            Character(name = "Lorc Irontusk"),
            Character(name = "Gormlaith Kall"),
            Character(name = "Gelabrous Finn")
        )
        return this
    }

    abstract fun characterDao(): CharacterDao

    companion object {
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.inMemoryDatabaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java
                ).build().prepopulate()
            }
            return instance!!
        }
    }
}