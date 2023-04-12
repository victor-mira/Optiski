package com.optiapk.optiski

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.optiapk.optiski.models.Piste
import org.w3c.dom.Text

class Feedback : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        val nbrPistes = (1..5).random()
        val duree = (30..90).random()

        val textFinDescription: TextView = findViewById(R.id.textFinDescription)
        textFinDescription.text = " Vous avez parcouru " + nbrPistes.toString() + " pistes en " + duree.toString() +" minutes "
    }

}