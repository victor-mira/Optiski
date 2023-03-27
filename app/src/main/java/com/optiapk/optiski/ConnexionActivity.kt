package com.optiapk.optiski

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ConnexionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connexion)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        val buttonSignInGoogle = findViewById<Button>(R.id.signInGoogleButtonAlternateInscription)
        val buttonNext = findViewById<Button>(R.id.nextButtonInscription)
        val email = findViewById<EditText>(R.id.enterEmailInscription)
        val password = findViewById<EditText>(R.id.enterPasswordInscription)

        buttonSignInGoogle.setOnClickListener {
            val intent = Intent(this, ChoicesActivity::class.java)
            startActivity(intent)
        }

        buttonNext.setOnClickListener {
            signInWithPassword(email.text.toString(), password.text.toString())
//            val intent = Intent(this, ChoicesActivity::class.java)
//            startActivity(intent)
        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            // UI change
        }
    }

    public fun signInWithPassword(email:String, password:String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }


}