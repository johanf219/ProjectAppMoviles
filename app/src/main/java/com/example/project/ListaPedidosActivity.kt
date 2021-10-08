package com.example.project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.CellStyle

class ListaPedidosActivity : AppCompatActivity() {

    private var excel = Excel()

    val orderDB = Firebase.database.getReference("order")
    var units: MutableList<Order> = mutableListOf()

    private val subjectEmail = "List of orders =) "
    private val bodyEmail = "Now you can view and metric the impact of sales"

    companion object{
        const val EMAIL_KEY = "email_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        orderDB.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var index = 0
                for (rest: DataSnapshot in snapshot.children) {
                    var value =  rest.getValue<Order>()
                    var order = Order(value?.client, value?.email, value?.cellphone, value?.delivery, value?.products, value?.address)
                    if (order != null) {
                        units.add(index, order)
                        index = index +1
                    }
                }

                crearExcel()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    private fun crearExcel() {
        excel.crearLibro();
        excel.crearHoja("first-report")
        excel.crearCellSytle(HSSFColor.BLACK.index, HSSFColor.GREEN.index, HSSFCellStyle.SOLID_FOREGROUND, CellStyle.ALIGN_CENTER)

        var numeroFila = 0
        excel.crearFila(numeroFila)
        numeroFila++

        excel.createCelda("client",0)
        excel.createCelda("email",1)
        excel.createCelda("cellphone",2)
        excel.createCelda("delivery",3)
        excel.createCelda("products",4)
        excel.createCelda("address",5)

        excel.crearCellSytle(HSSFColor.BLACK.index, HSSFColor.WHITE.index, HSSFCellStyle.SOLID_FOREGROUND, CellStyle.ALIGN_CENTER)

        for(unit in units) {
            Log.d("unitssssss:", unit.toString())
            excel.crearFila(numeroFila)
            numeroFila++
            excel.createCelda(unit.client,0)
            excel.createCelda(unit.email,1)
            excel.createCelda(unit.cellphone,2)
            excel.createCelda(unit.delivery,3)
            excel.createCelda(unit.products,4)
            excel.createCelda(unit.address,5)
        }

        var uri = excel.guardarLibro("list_of_orders.xls", this)

        val mIntent = Intent(Intent.ACTION_SEND)
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"

        val intent : Bundle = intent.extras!!
        val email = intent.getString(EMAIL_KEY)

        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subjectEmail)
        mIntent.putExtra(Intent.EXTRA_TEXT, bodyEmail)
        mIntent.putExtra(Intent.EXTRA_STREAM, uri);

        try {
            startActivity(Intent.createChooser(mIntent, "Choose email"))

        }catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }
}