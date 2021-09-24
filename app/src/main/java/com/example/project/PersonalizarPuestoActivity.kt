package com.example.project

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.example.project.databinding.ActivityPersonalizarPuestoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PersonalizarPuestoActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityPersonalizarPuestoBinding
    private lateinit var dbSnapShot: DataSnapshot
    private lateinit var stReference: StorageReference

    private var mImageSelecionadaUri: Uri?= null
    private var mImageSelecionadaUriWorker: Uri?=null

    private val images_path = "sale"
    private val childName = "sale"

    private val terrainIV = 1
    private val tenderIV = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityPersonalizarPuestoBinding.inflate(layoutInflater)
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

        viewBinding.pptIvTerreno.setOnClickListener{abrirGaleria(terrainIV)}
        viewBinding.ppIvTender.setOnClickListener{abrirGaleria(tenderIV)}

        val tenderImageUri = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE.toString() +
                    "://" + resources.getResourcePackageName(R.mipmap.avatar)
                    + '/' + resources.getResourceTypeName(R.mipmap.avatar)
                    + '/' + resources.getResourceEntryName(R.mipmap.avatar)
        )

        viewBinding.ppIvTender.setImageURI(tenderImageUri)
        mImageSelecionadaUriWorker = tenderImageUri

        val terrainImageUri = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE.toString() +
                    "://" + resources.getResourcePackageName(R.mipmap.terrain)
                    + '/' + resources.getResourceTypeName(R.mipmap.terrain)
                    + '/' + resources.getResourceEntryName(R.mipmap.terrain)
        )

        viewBinding.pptIvTerreno.setImageURI(terrainImageUri)
        mImageSelecionadaUri = terrainImageUri

        stReference = FirebaseStorage.getInstance().reference

        viewBinding.ppBtnUpdate.setOnClickListener{
            if(!this.checker()) {
                Toast.makeText(this, "missing inputs", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val sale_id = viewBinding.ppPtPuestoId.text.toString()
            val special_name = viewBinding.ppPtNombreEspecial.text.toString()

            if(!this.saleAlreadyExists(sale_id)) {
                Toast.makeText(this, "sale not exists", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            publishImage(sale_id, special_name)
        }
    }

    private fun checker(): Boolean {
        if (viewBinding.ppPtPuestoId.text.toString() == "") {
            return false
        }

        if (viewBinding.ppPtNombreEspecial.text.toString() == "") {
            return false
        }

        if (viewBinding.ppPtTender.text.toString() == "") {
            return false
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if( resultCode == Activity.RESULT_OK) {
            if (requestCode == terrainIV) {
                mImageSelecionadaUri = data?.data
                viewBinding.pptIvTerreno.setImageURI(mImageSelecionadaUri)
            }

            if (requestCode == tenderIV) {
                mImageSelecionadaUriWorker = data?.data
                viewBinding.ppIvTender.setImageURI(mImageSelecionadaUri)
            }
        }
    }

    private fun saleAlreadyExists(name: String): Boolean {
        val query = dbSnapShot.child(childName).child(name)
        if (!query.exists()) {
            return false
        }

        return true
    }

    private fun abrirGaleria(identifier: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, identifier)
    }

    private fun publishImage(sale_id: String, special_name: String) {
        val storageReference = stReference.child(images_path).child(sale_id).child("Terreno").child(special_name)
        if (mImageSelecionadaUri != null && mImageSelecionadaUriWorker!= null) {
            saveSale(sale_id, special_name)

            storageReference.putFile(mImageSelecionadaUri!!)
                .addOnSuccessListener {
                    Toast.makeText(this, "image uploaded successfuly", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "something goes wrong, try again", Toast.LENGTH_LONG).show()
                }

            val storageReference2 = stReference.child(images_path).child(sale_id).child("Trabajador").child(special_name)
            storageReference2.putFile(mImageSelecionadaUriWorker!!)
                .addOnSuccessListener {
                    Toast.makeText(this, "image uploaded successfuly", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "something goes wrong, try again", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun saveSale(sale_id: String, special_name: String) {
        val db = Firebase.database.reference

        val query = db.child(childName).child(sale_id).child(special_name)

        val terrain_path = images_path + "/Terreno/" + special_name
        val tender_path = images_path + "/Trabajador/" + special_name

        val special = SpecialSale(
            sale_id,
            special_name,
            viewBinding.ppPtTender.text.toString(),
            terrain_path,
            tender_path,
        )

        query.setValue(special)

        Toast.makeText(this, "Sale created successfully", Toast.LENGTH_SHORT).show()
    }
}