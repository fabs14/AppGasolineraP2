package com.example.gasolineraparcial2.presentacion.handlers

import android.content.Context
import com.google.android.gms.maps.model.LatLng

data class CalculoRequest(
    val context: Context,
    val sucursalId: Int?,
    val litrosDisponibles: Int?,
    val bombas: Int?,
    val puntosFila: List<LatLng>? = null,
    var distanciaMetrosCalculada: Double? = null
)