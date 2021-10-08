package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.project.databinding.ActivitySigninBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SigninActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivitySigninBinding
    private lateinit var dbSnapShot: DataSnapshot
    private val childName = "user"
    private val defaultRole = "admin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val db = Firebase.database.reference

        db.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dbSnapShot = snapshot
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        viewBinding.signinBtnSignin.setOnClickListener{
            if (!this.checker()) {
                Toast.makeText(this, "missing inputs", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val username = viewBinding.signinPtUsername.text.toString()

            if (this.userAlreadyExists(username)) {
                Toast.makeText(this, "user already exists", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val user = User(viewBinding.signinPsPassword.text.toString(), defaultRole, "florezjohan2197@gmail.com")

            var query = db.child(childName).child(username)
            query.setValue(user)

            Toast.makeText(this, "sign in successful", Toast.LENGTH_LONG).show()

            cleaner()

            var intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cleaner() {
        viewBinding.signinPsPassword.text.clear()
        viewBinding.signinPtUsername.text.clear()
    }

    private fun userAlreadyExists(username: String): Boolean {
        val query = dbSnapShot.child(childName).child(username)
        if (!query.exists()) {
            return false
        }

        return true
    }

    private fun checker(): Boolean {
        if (viewBinding.signinPsPassword.text.toString() == "") {
            return false
        }

        if (viewBinding.signinPtUsername.text.toString() == "") {
            return false
        }

        return true
    }
}