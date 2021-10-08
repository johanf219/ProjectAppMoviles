package com.example.project


import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class Excel() {
    private lateinit var libro: Workbook
    private lateinit var hoja: Sheet
    private lateinit var fila: Row
    private lateinit var celda: Cell
    private lateinit var cellStyle: CellStyle

    fun crearLibro() {
        libro = HSSFWorkbook()
    }

    fun crearHoja(nombreHoja: String?) {
        hoja = libro.createSheet(nombreHoja)
    }

    fun crearFila(numeroFila: Int) {
        fila = hoja.createRow(numeroFila)
    }

    fun crearCellSytle(colorFondo: Short, colorTexto: Short, patron: Short, alineacion: Short) {
        cellStyle = libro.createCellStyle()
        cellStyle.setFillBackgroundColor(colorFondo)
        cellStyle.setFillForegroundColor(colorTexto)
        cellStyle.setFillPattern(patron)
        cellStyle.setAlignment(alineacion)
    }

    fun createCelda(valor: String?, numero: Int) {
        celda = fila.createCell(numero)
        celda.setCellValue(valor)
        celda.setCellStyle(cellStyle)
    }

    fun createCelda(valor: Double, numero: Int) {
        celda = fila.createCell(numero)
        celda.setCellValue(valor)
        celda.setCellStyle(cellStyle)
    }

    fun guardarLibro(fileName: String?, context: Context?): Uri {
        var isSuccess: Boolean
        val file = File(context?.getExternalFilesDir(null), fileName)
        var fileOutputStream: FileOutputStream? = null

        val fileUri: Uri = FileProvider.getUriForFile(context!!,
            "com.example.project.fileprovider", //(use your app signature + ".provider" )
            file);

        try {
            fileOutputStream = FileOutputStream(file)
            libro!!.write(fileOutputStream)
            Log.e(ContentValues.TAG, "Writing file$file")

            Toast.makeText( context, "Exito al crear el excel", Toast.LENGTH_LONG).show()
        } catch (e: FileNotFoundException) {
            Toast.makeText(context, "Fallo al crear el excel", Toast.LENGTH_LONG).show()
            Log.e(ContentValues.TAG, "Failed to save file due to Exception: ", e)
        } catch (e: IOException) {
            Log.e(ContentValues.TAG, "Error writing Exception: ", e)
        } finally {
            try {
                fileOutputStream?.close()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        return fileUri
    }
}