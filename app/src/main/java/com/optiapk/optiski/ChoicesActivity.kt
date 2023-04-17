package com.optiapk.optiski

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.optiapk.optiski.models.Piste
import com.optiapk.optiski.models.PisteFinal
import java.io.IOException
import kotlin.collections.ArrayList


class ChoicesActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choices)
        supportActionBar?.hide()

        val sharedPref = getSharedPreferences(
            "Infos", Context.MODE_PRIVATE)
        val myEdit = sharedPref.edit()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        // [END config_signin]

        auth = FirebaseAuth.getInstance()


        val buttonResult = findViewById<ImageButton>(R.id.imageButton)
        val signoutButton = findViewById<ImageButton>(R.id.signOutGoogleButton)
        val stations = resources.getStringArray(R.array.Stations)
        val spinner = findViewById<Spinner>(R.id.choix_station)
        val buttonHelp = findViewById<ImageButton>(R.id.helpButton)

        //Time picker
        val hourpicker = findViewById<NumberPicker>(R.id.hourpicker)
        val minutepicker = findViewById<NumberPicker>(R.id.minutepicker)
        hourpicker.minValue = 0
        hourpicker.maxValue = 4
        minutepicker.displayedValues = arrayOf("0","15","30","45")
        minutepicker.minValue = 0
        minutepicker.maxValue = 3

        /*val builderAlert = AlertDialog.Builder(this)
        builderAlert.setTitle(R.string.position_alert_title)
        builderAlert.setMessage(R.string.position_alert_msg)
        builderAlert.setPositiveButton(R.string.ok) {dialog, which ->
            Toast.makeText(applicationContext, R.string.position_accept, Toast.LENGTH_SHORT).show()
        }
        builderAlert.setNegativeButton(R.string.reject) {dialog, which ->
            Toast.makeText(applicationContext, R.string.position_denied, Toast.LENGTH_SHORT).show()
        }
        builderAlert.show()*/

        /**----Creation de la liste des pistes------**/
        /*var jsonString :String = ""
        try {
            jsonString = this.assets.open("pistes.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
        val gson = Gson()
        val listPisteType = object : TypeToken<List<Station>>() {}.type

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
                .also{stations[stations.indexOf(title)]=stations[0]}*/

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
                    myEdit.putString("station", stations[position])
                    myEdit.apply()
                }

                    /*Toast.makeText(
                        this@ChoicesActivity,
                        getString(R.string.selected_item) + " " +
                                "" + stations[position], Toast.LENGTH_SHORT
                    )*/

               override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }

        buttonResult.setOnClickListener {
            val timeLeft = hourpicker.value * 60 + minutepicker.value * 15

            if (timeLeft == 0) {
                val builderAlert = AlertDialog.Builder(this)
                builderAlert.setTitle("Temps non réglementaire")
                builderAlert.setMessage("Le temps que vous avez saisi n'est pas réglementaire")
                builderAlert.setCancelable(false)
                builderAlert.setPositiveButton(R.string.ok) {dialog, which ->
                    dialog.cancel()
                }
                builderAlert.show()
            } else {
                val intent = Intent(this, ResultsActivity::class.java)
                val pos = 0
                val niveau = 1
                val stationChoisie = sharedPref.getString("station", null)
                val index = stations.indexOf(stationChoisie)

                val distanceParcourue = 0

                var jsonString = ""
                try {
                    jsonString =
                        this.assets.open("pistes.json").bufferedReader().use { it.readText() }
                } catch (ioException: IOException) {
                    ioException.printStackTrace()
                }
                val gson = Gson()
                val listPisteType = object : TypeToken<List<Station>>() {}.type


                val stations: List<Station> = gson.fromJson(jsonString, listPisteType)
                val pisteShuffled = stations[index].pistes
                val lifts = stations[index].lifts

                val pistesFinal = fillList(niveau, pisteShuffled)
                for (element in pistesFinal) {
                    println("$element")
                }
                val pisteSublist = algoChoixPistes(timeLeft, niveau, pistesFinal, lifts)


                val pisteSublistName = ArrayList<String>()
                val pisteSublistDifficulty = ArrayList<Int>()
                val pisteSublistTime = ArrayList<Int>()

                for (element in pisteSublist) {
                    pisteSublistName.add(element.number)
                    pisteSublistDifficulty.add(element.difficulty)
                    pisteSublistTime.add(element.time)
                }


                val jsonName = gson.toJson(pisteSublistName)
                val jsonDifficulty = gson.toJson(pisteSublistDifficulty)
                val jsonTime = gson.toJson(pisteSublistTime)//converting list to Json

                myEdit.putInt("position", pos)
                myEdit.putString("number", jsonName)
                myEdit.putString("difficulty", jsonDifficulty)
                myEdit.putString("time", jsonTime)
                myEdit.apply()




                startActivity(intent)
            }
        }
        buttonHelp.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Veuillez entrer une durée entre 30 minutes 4h45, par tranche de 15 minutes (Par exemple, 0:45 ou 2h15)")
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
    }

    private fun fillList(niveau: Int, Pistes: List<Piste>): MutableList<Piste> {
        val newList = mutableListOf<Piste>()
        for (element in Pistes) {
            if (element.difficulty <= niveau + 1) {
                newList += element
            }
        }
        return newList
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

    private fun algoChoixPistes(time : Int, niveau : Int, Pistes : List<Piste>, Lifts : List<Lift>) : MutableList<PisteFinal> {
        val newList = mutableListOf<PisteFinal>()
        var lift = 1
        var timeLeft = time
        var elementPiste : Piste


        while(timeLeft > 0) {

            do {
                    elementPiste = Pistes.random()

                } while(elementPiste.start_lift != lift)

            timeLeft -= Lifts[lift-1].time
            println("time left après lift : $timeLeft")
            timeLeft -= elementPiste.time[niveau]
            println("time left après piste : $timeLeft")


            newList += PisteFinal(Lifts[lift-1].number.toString(),Lifts[lift-1].difficulty,Lifts[lift-1].time)
            newList += PisteFinal(elementPiste.number, elementPiste.difficulty, elementPiste.time[niveau])

            lift = elementPiste.end_lift.random()




        }

        return newList
    }


}