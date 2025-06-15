package com.example.gasolineraparcial2.presentacion.handlers

import com.example.gasolineraparcial2.negocio.NCalculo
import android.widget.Toast

class EjecutarCalculo : Handler {
    override fun setNext(handler: Handler): Handler = this

    override fun handle(request: CalculoRequest): Boolean {
        println("✅ EjecutarCalculo ejecutado")
        try {
            val sucursalId = request.sucursalId
            val litros = request.litrosDisponibles
            val bombas = request.bombas
            val distancia = request.distanciaMetrosCalculada

            if (sucursalId == null || litros == null || bombas == null || distancia == null) {
                println("❌ Datos faltantes en EjecutarCalculo")
                Toast.makeText(request.context, "❌ Faltan datos para el cálculo", Toast.LENGTH_SHORT).show()
                return false
            }

            val resultado = NCalculo.getInstance().realizarCalculo(
                context = request.context,
                idSucursal = sucursalId,
                distanciaMetros = distancia,
                litrosDisponibles = litros,
                cantidadBombas = bombas
            )

            val mensaje = """
                ✅ Cálculo realizado:
                Combustible disponible: ${if (resultado.combustibleDisponible) "Sí" else "No"}
                Tiempo estimado: ${resultado.tiempoEsperaMinutos} min
                Litros restantes: ${resultado.litrosRestantes}
                Fecha: ${resultado.fechaHora}
            """.trimIndent()

            request.txtResultado?.text = mensaje
            return true
        } catch (e: Exception) {
            println("❌ Excepción en EjecutarCalculo: ${e.message}")
            Toast.makeText(request.context, "❌ Error al ejecutar cálculo", Toast.LENGTH_SHORT).show()
            return false
        }
    }

}


