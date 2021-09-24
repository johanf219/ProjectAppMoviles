package com.example.project

import android.R
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.project.databinding.ActivityActualizarProductoBinding
import com.google.firebase.database.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ActualizarProductoActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityActualizarProductoBinding
    private lateinit var stReference: StorageReference

    private var productImageUri: Uri?= null

    private val images_path = "product"
    private val productChildName = "product"

    val unitDB = Firebase.database.getReference("unit")
    var units: MutableList<String> = mutableListOf()

    private val productIV = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityActualizarProductoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        unitDB.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var i = 0
                for (rest:DataSnapshot in snapshot.children) {
                    var value = rest.value.toString()
                    units.add(i, value)
                }

                selectItems()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        val imageUri = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE.toString() +
                    "://" + resources.getResourcePackageName(com.example.project.R.mipmap.product)
                    + '/' + resources.getResourceTypeName(com.example.project.R.mipmap.product)
                    + '/' + resources.getResourceEntryName(com.example.project.R.mipmap.product)
        )

        viewBinding.apIvProduct.setImageURI(imageUri)
        productImageUri = imageUri

        viewBinding.apIvProduct.setOnClickListener{abrirGaleria(productIV)}

        stReference = FirebaseStorage.getInstance().reference

        viewBinding.apBtnPublish.setOnClickListener{
            if(!this.checker()) {
                Toast.makeText(this, "missing inputs", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val product_name = viewBinding.apPtName.text.toString()

            publishImage(product_name)
        }
    }

    private fun checker(): Boolean {
        if (viewBinding.apPtName.text.toString() == "") {
            return false
        }

        if (viewBinding.apPtPrecio.text.toString() == "") {
            return false
        }

        return true
    }

    fun selectItems () {
        var spinnermiles =  viewBinding.spinner;
        var adapter = ArrayAdapter(this, R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnermiles.setAdapter(adapter)
    }

    private fun abrirGaleria(identifier: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, identifier)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if( resultCode == Activity.RESULT_OK) {
            if (requestCode == productIV) {
                productImageUri = data?.data
                viewBinding.apIvProduct.setImageURI(productImageUri)
            }
        }
    }

    private fun publishImage(product_name: String) {
        viewBinding.progressBar.visibility = View.VISIBLE

        val storageReference = stReference.child(images_path).child(product_name)
        if (productImageUri == null) {
            Toast.makeText(this, "missing image", Toast.LENGTH_LONG).show()
            return
        }

        saveProduct(product_name)

        storageReference.putFile(productImageUri!!)
            .addOnProgressListener {
                val progress = (100 * it.bytesTransferred / it.totalByteCount).toDouble()
                viewBinding.progressBar.progress = progress.toInt()
            }
            .addOnCompleteListener {
                viewBinding.progressBar.visibility = View.INVISIBLE
            }
            .addOnSuccessListener {
                Toast.makeText(this, "image uploaded successfuly", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "something goes wrong, try again", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveProduct(product_name: String) {
        val db = Firebase.database.reference

        val query = db.child(productChildName).child(product_name)

        val path = images_path + product_name
        val price = viewBinding.apPtPrecio.text.toString()
        val unit = viewBinding.spinner.selectedItem.toString()

        val product = Product(
            product_name,
            price,
            unit,
            path
        )

        query.setValue(product)

        Toast.makeText(this, "product updated successfully", Toast.LENGTH_SHORT).show()
    }
}