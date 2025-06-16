package com.example.gasolineraparcial2.presentacion.handlers

import android.location.Location
import android.widget.Toast

class ValidarDibujo : Handler {
    private var next: Handler? = null

    override fun setNext(handler: Handler): Handler {
        this.next = handler
        return handler
    }

    override fun handle(request: CalculoRequest): Boolean {
        val puntos = request.puntosFila

        if (puntos == null || puntos.size < 2) {
            Toast.makeText(request.context, "Debe dibujar la fila con al menos dos puntos", Toast.LENGTH_SHORT).show()
            return false
        }

        var totalDistancia = 0.0
        for (i in 0 until puntos.size - 1) {
            val inicio = puntos[i]
            val fin = puntos[i + 1]
            val results = FloatArray(1)
            Location.distanceBetween(
                inicio.latitude, inicio.longitude,
                fin.latitude, fin.longitude,
                results
            )
            totalDistancia += results[0]
        }

        request.distanciaMetrosCalculada = totalDistancia
        println("✅ ValidarDibujo OK")

        // ✅ Si ya no hay next, este es el último paso de la cadena
        return next?.handle(request) ?: true
    }
}
