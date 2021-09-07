package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.project.databinding.ActivityLoginBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityLoginBinding
    private lateinit var dbSnapShot: DataSnapshot
    private val childName = "user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
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

        viewBinding.loginBtnLogin.setOnClickListener{
            if (!this.checker()) {
                Toast.makeText(this, "missing inputs", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val username = viewBinding.loginPtUsername.text.toString()
            val password = viewBinding.loginPsPassword.text.toString()

            var query = dbSnapShot.child(childName).child(username)
            if (!query.exists()) {
                    Toast.makeText(this, "user not found", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
            }

            val savedPassword = query.getValue<User>()?.password
            if (password != savedPassword) {
                Toast.makeText(this, "wrong password", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            cleaner()

            var intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cleaner() {
        viewBinding.loginPsPassword.text.clear()
        viewBinding.loginPtUsername.text.clear()
    }

    private fun checker(): Boolean {
        if (viewBinding.loginPtUsername.text.toString() == "") {
            return false
        }

        if (viewBinding.loginPsPassword.text.toString() == "") {
            return false
        }

        return true
    }
}