package com.optiapk.optiski


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.optiapk.optiski.models.User


class InscriptionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    lateinit var viewPager2: ViewPager2
    var viewPagerItemArrayList: ArrayList<ViewPagerItem>? = null
    private lateinit var niveauxExplicationsArray: Array<String>
    private lateinit var niveauxArray : Array<String>
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription1)
        supportActionBar?.hide()


        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val buttonNext = findViewById<Button>(R.id.nextButtonInscription)
        val buttonGoogleSignIn = findViewById<Button>(R.id.signInGoogleButtonAlternateInscription)
        val editMail = findViewById<TextInputEditText>(R.id.real_text_mail)
        val editPassword = findViewById<TextInputEditText>(R.id.real_text_password)
        val editConfirmPassword = findViewById<TextInputEditText>(R.id.real_text_confirm)
        var buttonInscription:Button
        var editPersonName:EditText
        var level:TextView


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        buttonGoogleSignIn.setOnClickListener {
            googleSignIn()
        }

        buttonNext.setOnClickListener {
            // TODO verif information completes sinon toast

            println("mdp : ${editPassword.text}, confirm : ${editConfirmPassword.text}")
            buttonInscription = findViewById<Button>(R.id.inscriptionButton)
            editPersonName = findViewById<EditText>(R.id.editPersonNameProfile)





            if (editPassword.text.toString() != editConfirmPassword.text.toString()
                || editPassword.text.toString() == ""
                || editMail.text.toString() == "") {

                val builderAlert = AlertDialog.Builder(this)
                builderAlert.setTitle("Erreur dans la saisie")
                builderAlert.setMessage("Adresse Mail et/ou mot de passe non conforme (oubli ou non semblables")
                builderAlert.setPositiveButton("Compris !") {dialog, which ->
                }

                builderAlert.show()

            } else {
                setContentView(R.layout.activity_inscription2)//

                buttonInscription = findViewById<Button>(R.id.inscriptionButton)
                var editPersonName = findViewById<TextInputEditText>(R.id.editPersonName)

                viewPager2 = findViewById(R.id.inscriptionViewPager)
                val images = intArrayOf(
                    R.drawable.debutant,
                    R.drawable.intermediaire,
                    R.drawable.avance
                )

                niveauxExplicationsArray = resources.getStringArray(R.array.niveaux_explications)
                niveauxArray = resources.getStringArray(R.array.niveaux)

                viewPagerItemArrayList = ArrayList()

                for (i in images.indices) {
                    val viewPagerItem = ViewPagerItem(
                        images[i],
                        niveauxArray.get(i),
                        niveauxExplicationsArray.get(i)
                    )
                    viewPagerItemArrayList!!.add(viewPagerItem)
                }
                val vpAdapter = VPAdapter(viewPagerItemArrayList!!)

                viewPager2.setAdapter(vpAdapter)

                viewPager2.setClipToPadding(false)

                viewPager2.setClipChildren(false)

                viewPager2.setOffscreenPageLimit(2)

                viewPager2.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER

                var tabLayout = findViewById<TabLayout>(R.id.tabLayout)
                TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                    //tab.text = niveauxArray[position].substringBefore(' ')
                }.attach()



            buttonInscription.setOnClickListener {
                // TODO Verif password normes et confirmpassword
                var level = niveauxArray[viewPager2.currentItem]

                inscriptionWithPassword(editMail.text.toString(), editPassword.text.toString(), editPersonName.text.toString(), level)

                }
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

    fun inscriptionWithPassword(email: String, password: String, name: String, level:String) {
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

                    val userRef = firestore.collection("users")
                    user?.let {
                        val newUser = User(it, level)
                        userRef.document(user.uid).set(newUser)
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

    @SuppressLint("MissingInflatedId")
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // check if user already have a level or not
            setContentView(R.layout.activity_inscription2)//

            var buttonInscription:Button
            var editPersonName:EditText

            buttonInscription = findViewById<Button>(R.id.inscriptionButton)
            editPersonName = findViewById<EditText>(R.id.editPersonNameProfile)




            viewPager2 = findViewById(R.id.inscriptionViewPager)
            val images = intArrayOf(
                R.drawable.debutant,
                R.drawable.intermediaire,
                R.drawable.avance)

            niveauxExplicationsArray = resources.getStringArray(R.array.niveaux_explications)
            niveauxArray = resources.getStringArray(R.array.niveaux)

            viewPagerItemArrayList = ArrayList()

            for (i in images.indices) {
                val viewPagerItem = ViewPagerItem(images[i], niveauxArray.get(i), niveauxExplicationsArray.get(i))
                viewPagerItemArrayList!!.add(viewPagerItem)
            }
            val vpAdapter = VPAdapter(viewPagerItemArrayList!!)

            viewPager2.setAdapter(vpAdapter)

            viewPager2.setClipToPadding(false)

            viewPager2.setClipChildren(false)

            viewPager2.setOffscreenPageLimit(2)

            viewPager2.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER

            var tabLayout = findViewById<TabLayout>(R.id.tabLayout)
            TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                //tab.text = niveauxArray[position].substringBefore(' ')
            }.attach()



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

                var levelValue = niveauxArray[viewPager2.currentItem]
                val userRef = firestore.collection("users")
                user?.let {
                    userRef.document(user.uid).update("userLevel", levelValue)
                    userRef.document(user.uid).update("userName", editPersonName.text.toString())
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

                    val userRef = firestore.collection("users")
                    user?.let {
                        val newUser = User(it, "undefined")
                        userRef.document(user.uid).set(newUser)
                    }

                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(MainActivity.TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }
}