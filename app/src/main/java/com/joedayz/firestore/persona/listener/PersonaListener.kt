package com.joedayz.firestore.persona.listener

import com.joedayz.firestore.persona.model.Persona

interface PersonaListener {

    fun onClickEliminarPersona(persona: Persona)
    fun onClickActualizarPersona(persona: Persona)

}