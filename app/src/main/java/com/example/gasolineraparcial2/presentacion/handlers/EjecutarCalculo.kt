package com.example.gasolineraparcial2.presentacion.handlers

import android.widget.Toast
import com.example.gasolineraparcial2.negocio.NCalculo

class EjecutarCalculo : Handler {

    //  misma única instancia que gestiona el patrón Singleton.
    private val nCalculo = NCalculo.getInstance()

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

            //  Uso del objeto de negocio inicializado correctamente
            nCalculo.realizarCalculo(
                context = request.context,
                idSucursal = sucursalId,
                distanciaMetros = distancia,
                litrosDisponibles = litros,
                cantidadBombas = bombas
            )

            println("✅ Cálculo realizado y guardado en base de datos")
            return true
        } catch (e: Exception) {
            println("❌ Excepción en EjecutarCalculo: ${e.message}")
            Toast.makeText(request.context, "❌ Error al ejecutar cálculo", Toast.LENGTH_SHORT).show()
            return false
        }
    }
}