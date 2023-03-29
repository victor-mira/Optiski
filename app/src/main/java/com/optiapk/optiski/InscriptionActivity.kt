package com.optiapk.optiski

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest


class InscriptionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription1)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        val buttonNext = findViewById<Button>(R.id.nextButtonInscription)
        val buttonGoogleSignIn = findViewById<Button>(R.id.signInGoogleButtonAlternateInscription)
        val editMail = findViewById<EditText>(R.id.enterEmailInscription)
        val editPassword = findViewById<EditText>(R.id.enterPasswordInscription)
        val editConfirmPassword = findViewById<EditText>(R.id.confirmPasswordInscription)
        var buttonInscription:Button
        var editPersonName:EditText

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        buttonGoogleSignIn.setOnClickListener {

            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, MainActivity.RC_SIGN_IN)
        }

        buttonNext.setOnClickListener {
            // TODO verif information completes sinon toast
            setContentView(R.layout.activity_inscription2)//

            buttonInscription = findViewById<Button>(R.id.inscriptionButton)
            editPersonName = findViewById<EditText>(R.id.editPersonName)

            buttonInscription.setOnClickListener {
                // TODO Verif password normes et confirmpassword
                inscriptionWithPassword(editMail.text.toString(), editPassword.text.toString(), editPersonName.text.toString())

            }

        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            updateUI(currentUser)
            // TODO check is user already registred
        }
    }

    fun inscriptionWithPassword(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
//                    updateUI(user)
                    if (auth.currentUser?.displayName != name) {

                        var profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        auth.currentUser?.updateProfile(profileUpdates)
                    }
                    val intent = Intent(this, ChoicesActivity::class.java)
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

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, MainActivity.RC_SIGN_IN)
        // Ui change
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // check if user already have a level or not
            setContentView(R.layout.activity_inscription2)//

            var buttonInscription:Button
            var editPersonName:EditText

            buttonInscription = findViewById<Button>(R.id.inscriptionButton)
            editPersonName = findViewById<EditText>(R.id.editPersonName)

            if (auth.currentUser?.displayName != null) {
                editPersonName.setText(auth.currentUser!!.displayName)
            }

            buttonInscription.setOnClickListener {
                if (auth.currentUser?.displayName != editPersonName.text.toString()) {

                    var profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(editPersonName.text.toString())
                        .build()
                    auth.currentUser?.updateProfile(profileUpdates)
                }
                val intent = Intent(this, ChoicesActivity::class.java)
                startActivity(intent)
            }
            // User is signed in
        } else {
            // No user is signed in
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == MainActivity.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(MainActivity.TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(MainActivity.TAG, "Google sign in failed", e)
            }
        }
    }

    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(MainActivity.TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(MainActivity.TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }
}