package com.optiapk.optiski

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.optiapk.optiski.models.Piste
import com.optiapk.optiski.models.PisteFinal
import java.io.IOException
import kotlin.collections.ArrayList


class ChoicesActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var firestore: FirebaseFirestore


    var context: Context? = null
    var intent1: Intent? = null
    var locationManager: LocationManager? = null
    var gpsStatus = false

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
        firestore = FirebaseFirestore.getInstance()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)



        context = applicationContext

        CheckGpsStatus()


        val buttonResult = findViewById<ImageButton>(R.id.imageButton)
        val signoutButton = findViewById<ImageButton>(R.id.signOutGoogleButton)
        val stations = resources.getStringArray(R.array.Stations)
        val spinner = findViewById<Spinner>(R.id.choix_station)
        val buttonHelp = findViewById<ImageButton>(R.id.helpButton)
        var coords: DoubleArray? = fetchLocation()

        println("${coords?.get(0)}, ${coords?.get(1)}")


        val imageLevel = findViewById<ImageView>(R.id.imageSkier)
        val textName = findViewById<TextView>(R.id.textNomAccount)
        val textLevel = findViewById<TextView>(R.id.textLevel)

        //Time picker
        val hourpicker = findViewById<NumberPicker>(R.id.hourpicker)
        val minutepicker = findViewById<NumberPicker>(R.id.minutepicker)
        hourpicker.minValue = 0
        hourpicker.maxValue = 4
        hourpicker.value = 1
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

        /*val builderAlert = AlertDialog.Builder(this)
        builderAlert.setTitle(R.string.position_alert_title)
        builderAlert.setMessage(R.string.position_alert_msg)
        builderAlert.setPositiveButton(R.string.ok) {dialog, which ->
            //getLocation()
            Toast.makeText(applicationContext, R.string.position_accept, Toast.LENGTH_SHORT).show()
        }
        builderAlert.setNegativeButton(R.string.reject) {dialog, which ->
            Toast.makeText(applicationContext, R.string.position_denied, Toast.LENGTH_SHORT).show()
        }
        builderAlert.show()*/

        /**----Creation de la liste des pistes------**/
        var jsonString :String = ""
        try {
            jsonString = this.assets.open("pistes.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
        val gson = Gson()
        val listPisteType = object : TypeToken<List<Station>>() {}.type

        //getLocation()


        val stations_json: List<Station> = gson.fromJson(jsonString, listPisteType)

        var title = ""

        if (coords != null) {

            stations_json.forEach { station ->
                if ((station.coords[0] > (coords[0] - 0.01)) && (station.coords[0] < (coords[0] + 0.01))
                    && (station.coords[1] > (coords[1] - 0.01)) && (station.coords[1] < (coords[1] + 0.01))
                ) {
                    title = station.title
                }
            }

            if (title != "")
                stations[0] = stations[stations.indexOf(title)]
                    .also { stations[stations.indexOf(title)] = stations[0] }
        }

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

//                    println("Station : ${stations[position]}")
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
                val niveau : Int
                if (textLevel.text == "Débutant") {
                    niveau = 0
                } else if (textLevel.text == "Intermédiaire") {
                    niveau = 1
                } else {
                    niveau = 2
                }
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
                myEdit.putString("map", stations[index].map)
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

            val builder = AlertDialog.Builder(this)
            builder.setMessage("Si vous voulez modifier votre profile, alors cliquez sur l'image représentant votre niveau")
            builder.setTitle("Aide profil")
            builder.setCancelable(true)
            builder.setPositiveButton("Compris!") {
                    dialog, which -> dialog.cancel()
            }

            val alertDialog = builder.create()
            alertDialog.show()

            /*auth = FirebaseAuth.getInstance()
            auth.signOut()
            googleSignInClient.signOut().addOnCompleteListener(this,
                OnCompleteListener<Void?> { updateUI(null) })*/
        }


        imageLevel.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val coords : DoubleArray? = fetchLocation()
        val stations = resources.getStringArray(R.array.Stations)
        val spinner = findViewById<Spinner>(R.id.choix_station)

        var jsonString :String = ""
        try {
            jsonString = this.assets.open("pistes.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
        val gson = Gson()
        val listPisteType = object : TypeToken<List<Station>>() {}.type

        //getLocation()


        val stations_json: List<Station> = gson.fromJson(jsonString, listPisteType)

        var title = ""

        if (coords != null) {

            stations_json.forEach { station ->
                if ((station.coords[0] > (coords[0] - 0.01)) && (station.coords[0] < (coords[0] + 0.01))
                    && (station.coords[1] > (coords[1] - 0.01)) && (station.coords[1] < (coords[1] + 0.01))
                ) {
                    title = station.title
                }
            }

            if (title != "")
                stations[0] = stations[stations.indexOf(title)]
                    .also { stations[stations.indexOf(title)] = stations[0] }
        }

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
                    //myEdit.putString("station", stations[position])
                    //myEdit.apply()
                }

                /*Toast.makeText(
                    this@ChoicesActivity,
                    getString(R.string.selected_item) + " " +
                            "" + stations[position], Toast.LENGTH_SHORT
                )*/

                override fun onNothingSelected(parent: AdapterView<*>) {

//                    println("Station : ${stations[position]}")
                }

            }
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



    fun CheckGpsStatus() {
        locationManager = context!!.getSystemService(LOCATION_SERVICE) as LocationManager
        assert(locationManager != null)
        gpsStatus = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!gpsStatus) {
            val builderAlert = AlertDialog.Builder(this)
            builderAlert.setTitle("GPS is Disabled")
            builderAlert.setMessage(R.string.position_alert_msg)
            builderAlert.setPositiveButton(R.string.ok) {dialog, which ->
                intent1 = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent1)
            }
            builderAlert.setNegativeButton(R.string.reject) {dialog, which ->
                Toast.makeText(applicationContext, R.string.position_denied, Toast.LENGTH_SHORT).show()
            }
            builderAlert.show()
        }
    }
    
    fun fetchLocation(): DoubleArray? {
        val latlon = DoubleArray(2)
        // Get the location manager
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val bestProvider = locationManager.getBestProvider(criteria, false)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }

        return try {
            val location = locationManager.getLastKnownLocation(bestProvider!!)
            val lat: Double
            val lon: Double
            lat = location!!.latitude
            lon = location.longitude
            latlon[0] = lat
            latlon[1] = lon
            latlon
        } catch (e: NullPointerException) {
            e.printStackTrace()
            null
        }
    }
}