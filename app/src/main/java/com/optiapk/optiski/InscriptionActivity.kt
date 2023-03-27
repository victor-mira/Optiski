package com.optiapk.optiski

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class InscriptionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription1)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        val buttonNext = findViewById<Button>(R.id.nextButtonInscription)
        val EditMail = findViewById<EditText>(R.id.enterEmailInscription)
        val EditPassword = findViewById<EditText>(R.id.enterPasswordInscription)
        val EditConfirmPassword = findViewById<EditText>(R.id.confirmPasswordInscription)
        val buttonInscription = findViewById<Button>(R.id.inscriptionButton)

        buttonNext.setOnClickListener {
            setContentView(R.layout.activity_inscription2)
//            val intent = Intent(this, ConnexionActivity::class.java)
//            startActivity(intent)
        }


    }

    public fun incriptionWithPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
            }
    }

}