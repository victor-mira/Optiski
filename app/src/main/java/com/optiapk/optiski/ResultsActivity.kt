package com.optiapk.optiski

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.google.gson.Gson
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.optiapk.optiski.models.Piste
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

class ResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        supportActionBar?.hide()

        /**----Item in view----*/
        val buttonNext = findViewById<Button>(R.id.buttonTrackListNext)
        val buttonPrevious = findViewById<Button>(R.id.buttonTrackListPrevious)

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

        /**----Load miniMap of station----**/
        Glide.with(this)
            .load(stations[0].map)
            .into(findViewById(R.id.miniPiste))

        /**----Results Adapter----**/
        val resultsAdapter = ResultsAdpater(pistes_sublist)
        val viewPager2 = findViewById<ViewPager2>(R.id.trackpad)
        viewPager2.adapter=resultsAdapter
        viewPager2.clipToPadding=false
        viewPager2.clipChildren=false
        viewPager2.offscreenPageLimit=2
        viewPager2.get(0).overScrollMode= View.OVER_SCROLL_NEVER

        /**----Pop-up de fin de session----**/
        val popup = Dialog(this)
        popup?.setContentView(R.layout.layout_dialog)

        // Initialisation des vues de la popup
        val messageView = popup?.findViewById<TextView>(R.id.dialod_text)
        val cancelButton = popup?.findViewById<Button>(R.id.cancel_button)
        val validateButton = popup?.findViewById<Button>(R.id.validate_button)
        //Ajouter le spinner
        val spinner = popup?.findViewById<ProgressBar>(R.id.dialog_spinner)
        var timer :CountDownTimer? = null
        val timerTime = 10
        var timeRemaining: Double = timerTime.toDouble()
        val timerInterval = 0.1

        // Initialisation du texte affiché dans la popup
        messageView?.setText(R.string.fin_tracks)
        // Lancement du compteur de temps
        spinner?.visibility = View.VISIBLE
        timer = object : CountDownTimer((timeRemaining * 1000.0).toLong(), (timerInterval*1000.0).toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining-=timerInterval
                // Mise à jour du texte affiché dans la popup
                spinner?.progress = abs(((timeRemaining-timerTime.toDouble())*10).toInt())
            }

            override fun onFinish() {
                // Action à effectuer lorsque le temps est écoulé
                // Fermeture de la popup
                popup?.dismiss()
                buttonNext.isEnabled=true
            }
        }

        // Initialisation des boutons
        cancelButton?.setOnClickListener {
            // Action à effectuer lorsque l'utilisateur appuie sur le bouton Annuler
            popup?.dismiss()
            timer?.cancel()
            buttonNext.isEnabled=true
        }
        validateButton?.setOnClickListener {
            val intent = Intent(this, Feedback::class.java)
            startActivity(intent)
        }

        /**----Set Button Listener----**/
        buttonNext.setOnClickListener{
            if (viewPager2.currentItem < pistes_sublist.size-1) {
                viewPager2.currentItem++
                buttonPrevious.isEnabled=true
            } else {
                // Affichage de la popup
                buttonNext.isEnabled=false
                timeRemaining = timerTime.toDouble()
                timer.start()
                popup?.show()
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