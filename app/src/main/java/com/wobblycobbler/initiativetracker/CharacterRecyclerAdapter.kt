package com.wobblycobbler.initiativetracker

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wobblycobbler.initiativetracker.db.entities.Character
import kotlinx.android.synthetic.main.character_list_item.view.*

class CharacterRecyclerAdapter(private val characters: ArrayList<Character>) :
    RecyclerView.Adapter<CharacterRecyclerAdapter.CharacterHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterRecyclerAdapter.CharacterHolder {
        val inflatedView = parent.inflate(R.layout.character_list_item, false)
        return CharacterHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: CharacterRecyclerAdapter.CharacterHolder, position: Int) {
        val itemCharacter = characters[position]
        holder.bindCharacter(itemCharacter)
    }

    override fun getItemCount() = characters.size

    class CharacterHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var character: Character? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Log.d("CharacterRecyclerView", "CLICK!")
        }

        fun bindCharacter(character: Character) {
            this.character = character
            view.characterName.text = character.name
        }
    }
}