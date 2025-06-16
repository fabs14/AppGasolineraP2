package com.example.gasolineraparcial2.presentacion.handlers

import android.widget.Toast

class ValidarSucursal : Handler {
    private var next: Handler? = null

    override fun setNext(handler: Handler): Handler {
        this.next = handler
        return handler
    }

    override fun handle(request: CalculoRequest): Boolean {
        return if (request.sucursalId != null && request.sucursalId > 0) {
            next?.handle(request) ?: true
        } else {
            Toast.makeText(request.context, "Debe seleccionar una sucursal", Toast.LENGTH_SHORT).show()
            println("❌ Error: Seleccione una sucursal válida")
            false
        }
    }
}
