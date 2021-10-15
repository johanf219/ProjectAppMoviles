package com.example.project

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.project.databinding.ActivityCrearOrdenBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class CrearOrdenActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityCrearOrdenBinding

    private val childName = "order"

    val productDB = Firebase.database.getReference("product")
    var productsList: MutableList<Product> = mutableListOf()
    var products: MutableList<String> = mutableListOf()
    var units: MutableList<String> = mutableListOf()

    companion object{
        const val EMAIL_KEY = "email_key"
        const val CLIENT_KEY = "client_key"
        const val ADDRESS_KEY = "address_key"
        const val CELLPHONE_KEY = "cellphone_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityCrearOrdenBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        productDB.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var i = 0
                for (rest:DataSnapshot in snapshot.children) {
                    var value = rest.getValue<Product>()
                    if (value != null) {
                        productsList.add(i, value)
                        products.add(i, value.name.toString())
                        i += 1
                    }
                }

                setItems()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        viewBinding.coBtnBuy.setOnClickListener{
           createOrder()
        }
    }

    private fun setItems () {
        var spinnerProducts =  viewBinding.coSpProducts;
        var adapterProducts = ArrayAdapter(this, R.layout.simple_spinner_item, products);
        adapterProducts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerProducts.setAdapter(adapterProducts)

        viewBinding.coSpProducts.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                viewBinding.coPtUnit.setText(productsList[position].unit)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun createOrder() {
        val unit = viewBinding.coPtUnits.text.toString()
        if (unit == "") {
            Toast.makeText(this, "missing units", Toast.LENGTH_SHORT).show()
            return
        }

        val db = Firebase.database.reference

        val intent : Bundle = intent.extras!!
        val email = intent.getString(EMAIL_KEY)
        val client = intent.getString(CLIENT_KEY)
        val address = intent.getString(ADDRESS_KEY)
        val cellphone = intent.getString(CELLPHONE_KEY)
        val units = viewBinding.coPtUnit.text.toString()
        val delivery = viewBinding.coSpChoose.selectedItem.toString()
        var product = ""

        for(i in 1 until unit.toInt()) {
            product += ", 1 $units" + viewBinding.coSpProducts.selectedItem.toString()
        }

        val order = Order(client, email, cellphone, delivery, product, address)

        val query = db.child(childName).push()
        query.setValue(order)

        Toast.makeText(this, "Order created successfully", Toast.LENGTH_SHORT).show()
    }
}