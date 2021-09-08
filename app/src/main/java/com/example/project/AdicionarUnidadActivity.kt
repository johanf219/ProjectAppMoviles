package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.project.databinding.ActivityAdicionarUnidadBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AdicionarUnidadActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityAdicionarUnidadBinding
    private lateinit var dbSnapShot: DataSnapshot
    private val childName = "unit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityAdicionarUnidadBinding.inflate(layoutInflater)
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

       viewBinding.auvBtAdd.setOnClickListener{
           val unit = viewBinding.auvPtUnidad.text.toString()
           if (unit == "") {
               Toast.makeText(this, "missing unit", Toast.LENGTH_LONG).show()
               return@setOnClickListener
           }

           val value = dbSnapShot.child(childName).child(unit)
           if (value.exists()) {
               Toast.makeText(this, "unit already exists", Toast.LENGTH_LONG).show()
               return@setOnClickListener
           }

           val query = db.child(childName)
           query.push().setValue(unit)

           Toast.makeText(this, "unit created successfully", Toast.LENGTH_LONG).show()

           cleaner()

           var intent = Intent(this, MenuActivity::class.java)
           startActivity(intent)
       }
    }

    private fun cleaner() {
        viewBinding.auvPtUnidad.text.clear()
    }
}