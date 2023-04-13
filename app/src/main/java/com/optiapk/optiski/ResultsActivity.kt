package com.optiapk.optiski

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.google.gson.Gson
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
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

        /**----Creation de la liste des pistes------**/
        var jsonString :String = ""
        try {
            jsonString = this.assets.open("pistes.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
        val gson = Gson()
        var listPisteType = object : TypeToken<List<Station>>() {}.type

        val stations: List<Station> = gson.fromJson(jsonString, listPisteType)
        val pistes_shuffled = stations[0].pistes.shuffled()
        val pistes_sublist = pistes_shuffled.subList(0, abs(Random.nextInt()%(stations[0].pistes.size-1)) +1)

        /**----Results Adapter----**/
        val resultsAdapter = ResultsAdpater(pistes_sublist)
        val viewPager2 = findViewById<ViewPager2>(R.id.trackpad)
        viewPager2.adapter=resultsAdapter
        viewPager2.clipToPadding=false
        viewPager2.clipChildren=false
        viewPager2.offscreenPageLimit=2
        viewPager2.get(0).overScrollMode= View.OVER_SCROLL_NEVER

        /**----Set Button Listener----**/
        val buttonNext = findViewById<Button>(R.id.buttonTrackListNext)
        val buttonPrevious = findViewById<Button>(R.id.buttonTrackListPrevious)
        buttonNext.setOnClickListener{
            if (viewPager2.currentItem < pistes_sublist.size-1) {
                viewPager2.currentItem++
                buttonPrevious.isEnabled=true
            }
            if (viewPager2.currentItem == pistes_sublist.size-1) {
                buttonNext.isEnabled=false
            }
        }
        findViewById<Button>(R.id.buttonTrackListPrevious).setOnClickListener{
            if (viewPager2.currentItem > 0) {
                viewPager2.currentItem--
                buttonNext.isEnabled=true
            }
            if (viewPager2.currentItem == 0) {
                buttonPrevious.isEnabled=false
            }
        }
        /*----Affichage de la liste des pistes------*/
        /*pistes_sublist.forEach{ piste ->
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
        }*/
    }
}