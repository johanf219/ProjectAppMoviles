package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.project.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMenuBinding

    companion object{
        const val EMAIL_KEY = "email_key"
    }

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

        viewBinding.menuBtnActualizarPuesto.setOnClickListener{
            var intent = Intent(this, PersonalizarPuestoActivity::class.java)
            startActivity(intent)
        }

        viewBinding.menuBtnActualizarProduct.setOnClickListener{
            var intent = Intent(this, ActualizarProductoActivity::class.java)
            startActivity(intent)
        }

        viewBinding.menuBtnVerListaPedidos.setOnClickListener{
            var ltintent = Intent(this, ListaPedidosActivity::class.java)

            val mintent : Bundle = intent.extras!!
            val email = mintent.getString(EMAIL_KEY).toString()

            ltintent.putExtra(ListaPedidosActivity.EMAIL_KEY, email)

            startActivity(ltintent)
        }
    }
}