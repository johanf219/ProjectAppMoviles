package com.example.project

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.project.databinding.ActivityCrearPuestoVentaBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CrearPuestoVentaActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityCrearPuestoVentaBinding
    private lateinit var dbSnapShot: DataSnapshot
    private val childName = "sale"

    private val subjectEmail = "Sale created successfully =) "
    private val bodyEmail = "Now you can add the products and metric the impact"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityCrearPuestoVentaBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val db = Firebase.database.reference

        db.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dbSnapShot = snapshot
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        viewBinding.cpvBtCreate.setOnClickListener{
            if (!this.checker()) {
                Toast.makeText(this, "missing inputs", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val name = viewBinding.cpvPtNombre.text.toString()
            val username = viewBinding.cpvPtUsername.text.toString()

            val pointName = name + username

            if (this.saleAlreadyExists(pointName)) {
                Toast.makeText(this, "sale already exists", Toast.LENGTH_LONG).show()
                cleaner()
                
                return@setOnClickListener
            }

            saveSale(pointName)
            sendEmail()
        }
    }

    private fun checker(): Boolean {
        if (viewBinding.cpvPtNombre.text.toString() == "") {
            return false
        }

        if (viewBinding.cpvPtUsername.text.toString() == "") {
            return false
        }

        if (viewBinding.cpvPsPassword.text.toString() == "") {
            return false
        }

        if (viewBinding.cpvPtCelular.text.toString() == "") {
            return false
        }

        if (viewBinding.cpvPtEmail.text.toString() == "") {
            return false
        }

        return true
    }

    private fun cleaner() {
        viewBinding.cpvPtNombre.text.clear()
        viewBinding.cpvPtUsername.text.clear()
        viewBinding.cpvPsPassword.text.clear()
        viewBinding.cpvPtCelular.text.clear()
        viewBinding.cpvPtEmail.text.clear()
    }

    private fun saleAlreadyExists(name: String): Boolean {
        val query = dbSnapShot.child(childName).child(name)
        if (!query.exists()) {
            return false
        }

        return true
    }

    private fun saveSale(pointName: String) {
        val db = Firebase.database.reference

        val sale = Sale(
            viewBinding.cpvPtNombre.text.toString(),
            viewBinding.cpvPtUsername.text.toString(),
            viewBinding.cpvPsPassword.text.toString(),
            viewBinding.cpvPtCelular.text.toString(),
            viewBinding.cpvPtEmail.text.toString()
        )

        val query = db.child(childName).child(pointName)
        query.setValue(sale)

        Toast.makeText(this, "Sale created successfully", Toast.LENGTH_SHORT).show()
    }

    private fun sendEmail() {
        val email = viewBinding.cpvPtEmail.text.toString()

        val mIntent = Intent(Intent.ACTION_SEND)

        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subjectEmail)
        mIntent.putExtra(Intent.EXTRA_TEXT, bodyEmail)

        try {
            startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }
}