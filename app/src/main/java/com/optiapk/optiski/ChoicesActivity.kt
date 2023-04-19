package com.optiapk.optiski

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.util.*

class ChoicesActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choices)
        supportActionBar?.hide()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        // [END config_signin]

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        val buttonResult = findViewById<ImageButton>(R.id.imageButton)
        val signoutButton = findViewById<ImageButton>(R.id.signOutGoogleButton)
        var stations = resources.getStringArray(R.array.Stations)
        val spinner = findViewById<Spinner>(R.id.choix_station)
        val buttonHelp = findViewById<ImageButton>(R.id.helpButton)
        val imageLevel = findViewById<ImageView>(R.id.imageSkier)
        val textName = findViewById<TextView>(R.id.textNomAccount)
        val textLevel = findViewById<TextView>(R.id.textLevel)

        //Time picker
        val hourpicker = findViewById<NumberPicker>(R.id.hourpicker)
        val minutepicker = findViewById<NumberPicker>(R.id.minutepicker)
        hourpicker.minValue = 0
        hourpicker.maxValue = 4
        minutepicker.displayedValues = arrayOf("0","15","30","45")
        minutepicker.minValue = 0
        minutepicker.maxValue = 3

        val user = auth.currentUser
        val userRef = firestore.collection("users")

        user?.let {
            userRef.document(user.uid).get()
                .addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                    // Handle the successful retrieval of the document data
                    val name = documentSnapshot.getString("userName")
                    val level = documentSnapshot.getString("userLevel")
                    textName.text = name
                    textLevel.text = level
                    if (level == "Débutant") {
                        imageLevel.setImageResource(R.drawable.debutant)
                    } else if (level == "Intermédiaire") {
                        imageLevel.setImageResource(R.drawable.intermediaire)
                    } else if (level == "Avancé") {
                        imageLevel.setImageResource(R.drawable.avance)

                    }
                }
                .addOnFailureListener { e: Exception ->
                    // Handle the failure to retrieve the document data
                }
        }

        val builderAlert = AlertDialog.Builder(this)
        builderAlert.setTitle(R.string.position_alert_title)
        builderAlert.setMessage(R.string.position_alert_msg)
        builderAlert.setPositiveButton(R.string.ok) {dialog, which ->
            Toast.makeText(applicationContext, R.string.position_accept, Toast.LENGTH_SHORT).show()
        }
        builderAlert.setNegativeButton(R.string.reject) {dialog, which ->
            Toast.makeText(applicationContext, R.string.position_denied, Toast.LENGTH_SHORT).show()
        }
        builderAlert.show()

        /**----Creation de la liste des pistes------**/
        var jsonString :String = ""
        try {
            jsonString = this.assets.open("pistes.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
        val gson = Gson()
        var listPisteType = object : TypeToken<List<Station>>() {}.type

        val coords= doubleArrayOf(4.95,5.0)
        val stations_json: List<Station> = gson.fromJson(jsonString, listPisteType)
        var title = ""
        stations_json.forEach{station ->
            if (station.coords[0]>coords[0]-0.01 && station.coords[0]<coords[0]+0.01
                && station.coords[1]>coords[1]-0.01 && station.coords[1]<coords[1]+0.01) {
                title = station.title
            }
        }
        if(!title.equals(""))
            stations[0]=stations[stations.indexOf(title)]
                .also{stations[stations.indexOf(title)]=stations[0]}

        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, stations
            )
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    Toast.makeText(
                        this@ChoicesActivity,
                        getString(R.string.selected_item) + " " +
                                "" + stations[position], Toast.LENGTH_SHORT
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    Toast.makeText(
                        this@ChoicesActivity,
                        getString(R.string.choix_station), Toast.LENGTH_SHORT
                    )
                }
            }
        }

        buttonResult.setOnClickListener {
            val intent = Intent(this, ResultsActivity::class.java)
            startActivity(intent)
        }
        buttonHelp.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Veuillez entrer une durée entre 30 minutes et 3 heures, par tranche de 15 minutes (Par exemple, 0:45 ou 2h15)")
            builder.setTitle("Aide")
            builder.setCancelable(true)
            builder.setPositiveButton("Compris!") {
                dialog, which -> dialog.cancel()
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        signoutButton.setOnClickListener{
            auth = FirebaseAuth.getInstance()
            auth.signOut()
            googleSignInClient.signOut().addOnCompleteListener(this,
                OnCompleteListener<Void?> { updateUI(null) })
        }




        imageLevel.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }


    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {

            // User is signed in
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // No user is signed in
        }


    }
}