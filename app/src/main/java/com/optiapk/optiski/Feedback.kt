package com.optiapk.optiski

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.optiapk.optiski.models.Piste
import org.w3c.dom.Text
import java.util.ArrayList

class Feedback : AppCompatActivity() {

    lateinit var pisteSublistTime : ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        supportActionBar?.hide()

        val buttonProfile = findViewById<Button>(R.id.buttonProfile)
        val buttonHome = findViewById<Button>(R.id.buttonRetour)

        val gson = Gson()
        val type = object : TypeToken<ArrayList<String>>() {}.type
        val sharedPref = getSharedPreferences(
            "Infos", Context.MODE_PRIVATE)
        val jsonTime = sharedPref.getString("time", null)
        pisteSublistTime = gson.fromJson(jsonTime, type)

        val nbrPistes = (pisteSublistTime.size) / 2
        var duree = 0

        for (i in 0..pisteSublistTime.size - 1) {
            duree += pisteSublistTime[i].toInt()
        }


        val textFinDescription: TextView = findViewById(R.id.textFinDescription)
        textFinDescription.text = " Vous avez parcouru " + nbrPistes.toString() + " pistes en " + duree.toString() +" minutes "

        buttonProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        buttonHome.setOnClickListener {
            val intent = Intent(this, ChoicesActivity::class.java)
            startActivity(intent)
        }
    }

}