package com.joedayz.firestore.persona.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joedayz.firestore.R
import com.joedayz.firestore.persona.adapter.holder.PersonaHolder
import com.joedayz.firestore.persona.listener.PersonaListener
import com.joedayz.firestore.persona.model.Persona

class PersonaAdapter(val listener: PersonaListener) : RecyclerView.Adapter<PersonaHolder>() {

    private var mutableList = mutableListOf<Persona>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonaHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_persona, parent, false)
        return PersonaHolder(view, listener)
    }

    override fun getItemCount(): Int {
        return mutableList.size
    }

    override fun onBindViewHolder(holder: PersonaHolder, position: Int) {
        val persona = mutableList.get(position)
        holder.bind(persona)
    }

    fun actualizarLista(mutableListPersona: MutableList<Persona>) {
        this.mutableList.clear()
        this.mutableList.addAll(mutableListPersona)
        notifyDataSetChanged()
    }

    fun agregarPersona(persona: Persona) {
        this.mutableList.add(persona)
        notifyDataSetChanged()
    }

    fun eliminarPersona(personaActual: Persona) {
        for (personaAntigua in mutableList) {
            if (personaActual.idPersona.equals(personaAntigua.idPersona)) {
                mutableList.remove(personaActual)
                notifyDataSetChanged()
                return
            }
        }
    }

    fun actualizarPersona(personaActual: Persona) {
        for (personaAntigua in mutableList) {
            if (personaActual.idPersona.equals(personaAntigua.idPersona)) {
                val obtenerPosicion = mutableList.indexOf(personaAntigua)
                mutableList.set(obtenerPosicion, personaActual)
                notifyDataSetChanged()
                return
            }
        }
    }
}

