package com.example.gasolineraparcial2.presentacion.handlers

import android.widget.Toast

class ValidarLitros : Handler {
    private var next: Handler? = null

    override fun setNext(handler: Handler): Handler {
        this.next = handler
        return handler
    }

    override fun handle(request: CalculoRequest): Boolean {
        if (request.litrosDisponibles == null || request.litrosDisponibles <= 0) {
            Toast.makeText(request.context, "Debe ingresar los litros disponibles", Toast.LENGTH_SHORT).show()
            return false
        }
        println("✅ ValidarLitros OK")
        return next?.handle(request) ?: true
    }


}
