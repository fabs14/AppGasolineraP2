package com.example.gasolineraparcial2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gasolineraparcial2.presentacion.PCalculo
import com.example.gasolineraparcial2.presentacion.PSucursal
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Fragmento por defecto
        cargarFragmento(PSucursal())

        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_sucursal -> cargarFragmento(PSucursal())
                R.id.nav_calculo -> cargarFragmento(PCalculo())
                else -> false
            }
        }
    }

    private fun cargarFragmento(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
        return true
    }
}
