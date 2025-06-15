package com.example.gasolineraparcial2.presentacion.handlers

interface Handler {
    fun setNext(handler: Handler): Handler
    fun handle(request: CalculoRequest): Boolean
}
