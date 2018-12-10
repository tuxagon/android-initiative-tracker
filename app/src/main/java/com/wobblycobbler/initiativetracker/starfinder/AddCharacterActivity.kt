package com.wobblycobbler.initiativetracker.starfinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.wobblycobbler.initiativetracker.R
import kotlinx.android.synthetic.main.activity_add_character.*

class AddCharacterActivity : AppCompatActivity() {

    private lateinit var characterNameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_character)
        characterNameEditText = edit_character_name
    }

    fun addCharacter(view: View) {
        val characterName = characterNameEditText.text.toString()
    }
}
