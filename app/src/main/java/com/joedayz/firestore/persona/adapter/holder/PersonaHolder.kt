package com.joedayz.firestore.persona.adapter.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.joedayz.firestore.persona.listener.PersonaListener
import com.joedayz.firestore.persona.model.Persona
import kotlinx.android.synthetic.main.item_persona.view.*

class PersonaHolder(itemView: View, var personaListener: PersonaListener) :
    RecyclerView.ViewHolder(itemView) {

    fun bind(persona: Persona) {
        itemView.textViewPersona.text = persona.nombre + " " + persona.apellido
        itemView.buttonEditar.setOnClickListener {
            personaListener.onClickActualizarPersona(persona)
        }
        itemView.buttonEliminar.setOnClickListener {
            personaListener.onClickEliminarPersona(persona)
        }
    }

}