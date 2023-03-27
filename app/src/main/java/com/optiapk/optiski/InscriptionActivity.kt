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
        val editMail = findViewById<EditText>(R.id.enterEmailInscription)
        val editPassword = findViewById<EditText>(R.id.enterPasswordInscription)
        val editConfirmPassword = findViewById<EditText>(R.id.confirmPasswordInscription)
        var buttonInscription:Button

        buttonNext.setOnClickListener {
            // TODO verif information completes sinon toast
            setContentView(R.layout.activity_inscription2)//

            buttonInscription = findViewById<Button>(R.id.inscriptionButton)

            buttonInscription.setOnClickListener {
                // TODO Verif password normes et confirmpassword
                inscriptionWithPassword(editMail.text.toString(), editPassword.text.toString())
            }
//            val intent = Intent(this, ConnexionActivity::class.java)
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

    fun inscriptionWithPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
//                    updateUI(user)
                    val intent = Intent(this, ConnexionActivity::class.java)
                    startActivity(intent)
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