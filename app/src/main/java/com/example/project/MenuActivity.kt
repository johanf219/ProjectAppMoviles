package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.project.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.menuBtnCrearPuestoVenta.setOnClickListener{
            var intent = Intent(this, CrearPuestoVentaActivity::class.java)
            startActivity(intent)
        }

        viewBinding.menuBtnAdicionarUnidadVenta.setOnClickListener{
            var intent = Intent(this, AdicionarUnidadActivity::class.java)
            startActivity(intent)
        }
    }
}