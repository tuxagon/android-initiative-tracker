package com.wobblycobbler.initiativetracker.db.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.wobblycobbler.initiativetracker.db.entities.Character

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters")
    fun getAll(): List<Character>

    @Insert
    fun insertAll(vararg characters: Character)

    @Update
    fun updateAll(vararg characters: Character)

    @Delete
    fun delete(characters: Character)
}