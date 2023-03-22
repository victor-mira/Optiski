package com.example.optiski

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ConnexionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connexion)
        supportActionBar?.hide()

        val buttonSignInGoogle = findViewById<Button>(R.id.signInGoogleButtonAlternate)
        val buttonNext = findViewById<Button>(R.id.nextButton)

        buttonSignInGoogle.setOnClickListener {
            val intent = Intent(this, ChoicesActivity::class.java)
            startActivity(intent)
        }

        buttonNext.setOnClickListener {
            val intent = Intent(this, ChoicesActivity::class.java)
            startActivity(intent)
        }

    }
}