package com.joedayz.firestore.persona

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.joedayz.firestore.R
import com.joedayz.firestore.persona.adapter.PersonaAdapter
import com.joedayz.firestore.persona.listener.PersonaListener
import com.joedayz.firestore.persona.model.Persona
import kotlinx.android.synthetic.main.activity_persona.*

class PersonaActivity : AppCompatActivity(), PersonaListener {

    lateinit var adapter: PersonaAdapter
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_persona)
        initVistas()
        //initMostrarListaPersona()
        initMostrarListaPersonaSnap()
        buttonAgregarPersona.setOnClickListener {
            initValidarPersona()
        }

    }

    private fun initMostrarListaPersonaSnap() {
        val docRef = db.collection("persona")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                mostrarMensaje("Listen failed " + e)
                return@addSnapshotListener
            }
            for (dc in snapshot!!.documentChanges) {
                val idPersona = dc.document.id
                val nombrePersona = dc.document.data.getValue("nombre") as String
                val apellidoPersona = dc.document.data.getValue("apellido") as String
                val persona = Persona(idPersona, nombrePersona, apellidoPersona)
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        adapter.agregarPersona(persona)
                        Log.d("EVENTOFIRESTORE", "ADDED: Se agrego un Registro")
                    }
                    DocumentChange.Type.MODIFIED -> {
                        adapter.actualizarPersona(persona)
                        Log.d("EVENTOFIRESTORE", "MODIFIED: Se modifico un Registro")
                    }
                    DocumentChange.Type.REMOVED -> {
                        adapter.eliminarPersona(persona)
                        Log.d("EVENTOFIRESTORE", "REMOVED: Se elimino un Registro")
                    }
                }

            }

        }
    }

    private fun initVistas() {
        adapter = PersonaAdapter(this)
        recicladorPersona.adapter = adapter
        recicladorPersona.layoutManager = LinearLayoutManager(this)
        recicladorPersona.setHasFixedSize(true)
    }

    private fun initMostrarListaPersona() {
        db.collection("persona")
            .get()
            .addOnSuccessListener { result ->
                val mutableListPersona = mutableListOf<Persona>()
                for (document in result) {
                    Log.d("PersonaActivity", "${document.id} => ${document.data}")
                    val nombrePersona = document.data.getValue("nombre") as String
                    val apellidoPersona = document.data.getValue("apellido") as String
                    Log.d("PersonaActivity", "nombrePersona " + nombrePersona)
                    //mutableListPersona.add(Persona(nombrePersona, apellidoPersona))
                }
                adapter.actualizarLista(mutableListPersona)
            }
            .addOnFailureListener { exception ->
                mostrarMensaje("Error getting documents." + exception)
            }
    }

    private fun initValidarPersona() {
        mostrarProgressBar()
        val nombrePersona = editNombrePersona.text.toString()
        if (nombrePersona.isEmpty()) {
            ocultarProgressBar()
            mostrarMensaje("Ingrese el nombre!!")
            return
        }
        val apellidoPersona = editApellidoPersona.text.toString()
        if (apellidoPersona.isEmpty()) {
            ocultarProgressBar()
            mostrarMensaje("Ingrese el apellido!!")
            return
        }

        val persona = Persona("", nombrePersona, apellidoPersona)
        db.collection("persona")
            .add(persona)
            .addOnSuccessListener { documentReference ->
                persona.idPersona = documentReference.id
                ocultarProgressBar()
                mostrarMensaje("Registro Correctamente!!")
            }
            .addOnFailureListener { e ->
                ocultarProgressBar()
                mostrarMensaje("Error adding document " + e)
            }

    }

    private fun mostrarProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun ocultarProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(applicationContext, mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun onClickEliminarPersona(persona: Persona) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Eliminar Persona")
        alertDialog.setMessage("¿Estas seguro de eliminar la persona?")
        alertDialog.setPositiveButton("Aceptar") { dialogInterface, which ->
            mostrarProgressBar()
            initEliminar(persona)
        }
        alertDialog.setNegativeButton("Cancelar") { dialogInterface, which ->
            dialogInterface.dismiss()

        }
        val alertDialogBuild: AlertDialog = alertDialog.create()
        alertDialogBuild.setCancelable(false)
        alertDialogBuild.show()
    }

    private fun initEliminar(persona: Persona) {
        db.collection("persona")
            .document(persona.idPersona)
            .delete()
            .addOnSuccessListener {
                ocultarProgressBar()
                mostrarMensaje("Se elimino correctamente!!")
            }
            .addOnFailureListener {
                ocultarProgressBar()
                mostrarMensaje("Error al eliminar persona " + it.localizedMessage)
            }
    }

    override fun onClickActualizarPersona(persona: Persona) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_editar_persona)
        val editNombre = dialog.findViewById(R.id.editNombrePersonaDiag) as EditText
        val editApellido = dialog.findViewById(R.id.editApellidoPersonaDiag) as EditText
        val buttonEditar = dialog.findViewById(R.id.buttonEditarDiag) as Button
        val buttonCancelar = dialog.findViewById(R.id.buttonCancelarDiag) as Button
        editNombre.setText(persona.nombre)
        editApellido.setText(persona.apellido)
        buttonEditar.setOnClickListener {
            mostrarProgressBar()
            val nombreActualizar = editNombre.text.toString()
            val apellidoActualizar = editApellido.text.toString()
            val personaActualizar = Persona(persona.idPersona, nombreActualizar, apellidoActualizar)
            initEditarPersona(personaActualizar, dialog)
        }
        buttonCancelar.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun initEditarPersona(persona: Persona, dialog: Dialog) {
        db.collection("persona")
            .document(persona.idPersona)
            .set(persona)
            .addOnSuccessListener {
                ocultarProgressBar()
                dialog.dismiss()
                mostrarMensaje("Actualizo correctamente ")
            }
            .addOnFailureListener {
                ocultarProgressBar()
                mostrarMensaje("Error al actualizar Persona" + it.localizedMessage)
            }
    }
}