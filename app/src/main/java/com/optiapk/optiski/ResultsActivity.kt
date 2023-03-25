package com.optiapk.optiski

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.google.gson.Gson
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.optiapk.optiski.models.Piste
import com.google.gson.reflect.TypeToken
import java.io.IOException
import kotlin.math.abs
import kotlin.random.Random

class ResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        supportActionBar?.hide()
        var jsonString :String = ""
        try {
            jsonString = this.assets.open("pistes.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
        val gson = Gson()
        var listPisteType = object : TypeToken<List<Piste>>() {}.type

        val pistes: List<Piste> = gson.fromJson(jsonString, listPisteType)
        val pistes_shuffled = pistes.shuffled()
        val pistes_sublist = pistes_shuffled.subList(0, abs(Random.nextInt()%(pistes.size-1)) +1)
        pistes_sublist.forEach{ piste ->
            val lineBorder = LinearLayout(this)
            val tv_piste = TextView(lineBorder.context)
            tv_piste.textSize = 25f
            tv_piste.text = piste.number
            val bitmap = Bitmap.createBitmap(200, 20, Bitmap.Config.ARGB_8888)
            val canvas= Canvas(bitmap)
            when(piste.difficulty) {
                1 -> canvas.drawColor(Color.GREEN)
                2-> canvas.drawColor(Color.BLUE)
                3-> canvas.drawColor(Color.BLACK)
            }
            val img_view = ImageView(lineBorder.context)
            img_view.setImageBitmap(bitmap)
            lineBorder.addView(tv_piste)
            lineBorder.addView(img_view)
            findViewById<LinearLayout>(R.id.scrollLayoutResult).addView(lineBorder)
            Log.i("data", piste.number)
        }
    }
}