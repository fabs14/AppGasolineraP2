package com.example.gasolineraparcial2.presentacion.template

import android.content.Context
import android.widget.Toast

abstract class AccionSucursal(
    protected val context: Context
) {
    fun ejecutar(): Boolean {
        if (!validar()) {
            Toast.makeText(context, "Complete todos los campos correctamente", Toast.LENGTH_SHORT).show()
            return false
        }

        val ok = procesar()
        if (ok) {
            Toast.makeText(context, mensajeExito(), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, mensajeError(), Toast.LENGTH_SHORT).show()
        }
        return ok
    }

    abstract fun validar(): Boolean
    abstract fun procesar(): Boolean
    abstract fun mensajeExito(): String
    abstract fun mensajeError(): String
}
