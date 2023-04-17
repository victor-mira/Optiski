package com.optiapk.optiski



import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.optiapk.optiski.models.PisteFinal



class ResultsActivity : AppCompatActivity() {

    private val CHANNEL_ID = "channel1"
    val ACTIONPREVOIUS = "actionprevious"
    val ACTIONNEXT = "actionnext"

    private lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    //lateinit var intent1 : Intent
    lateinit var pisteSublistName : ArrayList<String>
    lateinit var pisteSublistDifficulty : ArrayList<String>
    lateinit var pisteSublistTime : ArrayList<String>

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasNotificationPermissionGranted = isGranted
            if (!isGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                            showNotificationPermissionRationale()
                        } else {
                            showSettingDialog()
                        }
                    }
                }
            }
        }

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Notification Permission")
            .setMessage("Notification permission is required, Please allow notification permission from setting")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showNotificationPermissionRationale() {

        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Alert")
            .setMessage("Notification permission is required, to show notification")
            .setPositiveButton("Ok") { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private var hasNotificationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        supportActionBar?.hide()

        val sh = getSharedPreferences(
            "Infos", Context.MODE_PRIVATE
        )
        var position = sh.getInt("position", 0)
        val myEdit = sh.edit()






        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val images = IntArray(3)
        images[0] = R.drawable.debutant
        images[1] = R.drawable.intermediaire
        images[2] = R.drawable.avance



        val gson = Gson()
        val jsonName = sh.getString("number", null)
        val jsonDifficulty = sh.getString("difficulty", null)
        val jsonTime = sh.getString("time", null)
        val type = object : TypeToken<ArrayList<String>>() {}.type//converting the json to list
        pisteSublistName = gson.fromJson(jsonName, type)//returning the list
        pisteSublistDifficulty = gson.fromJson(jsonDifficulty, type)
        pisteSublistTime = gson.fromJson(jsonTime, type)
        val pisteSublist: MutableList<PisteFinal>
        pisteSublist = FillList(pisteSublistName, pisteSublistDifficulty, pisteSublistTime)


        if (Build.VERSION.SDK_INT >= 33) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            hasNotificationPermissionGranted = true
        }






        /**----Creation de la liste des pistes------**/

        /*var jsonString :String = ""
        try {
            jsonString = this.assets.open("pistes.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
        var listPisteType = object : TypeToken<List<Station>>() {}.type

        val stations: List<Station> = gson.fromJson(jsonString, listPisteType)
        val pistes_shuffled = stations[0].pistes.shuffled()
        val pistes_sublist = pistes_shuffled.subList(0, abs(Random.nextInt()%(stations[0].pistes.size-1)) +1)*/


        /**----Results Adapter----**/
        val resultsAdapter = ResultsAdpater(pisteSublist)
        val viewPager2 = findViewById<ViewPager2>(R.id.trackpad)
        viewPager2.adapter = resultsAdapter
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit = 2
        viewPager2[0].overScrollMode = View.OVER_SCROLL_NEVER
        viewPager2.setCurrentItem(position, false)

        /**----Set Button Listener----**/


        val buttonNext = findViewById<Button>(R.id.buttonTrackListNext)
        val buttonPrevious = findViewById<Button>(R.id.buttonTrackListPrevious)

        if (viewPager2.currentItem == 0) {
            buttonPrevious.isEnabled = false
        }
        if (viewPager2.currentItem == pisteSublist.size -1){
            buttonNext.isEnabled = false
        }


        buttonNext.setOnClickListener {
            if (viewPager2.currentItem < pisteSublist.size - 1) {
                viewPager2.currentItem++
                buttonPrevious.isEnabled = true
                position++
                myEdit.putInt("position", position)
                myEdit.apply()
                ShowNotification(position, images, pisteSublist)

                if (viewPager2.currentItem == pisteSublist.size - 1) {
                    buttonNext.isEnabled = false
                }
            }
        }

            buttonPrevious.setOnClickListener {
                if (viewPager2.currentItem > 0) {
                    viewPager2.currentItem--
                    buttonNext.isEnabled = true
                    position--
                    myEdit.putInt("position", position)
                    myEdit.apply()
                    ShowNotification(position, images, pisteSublist)
                }
                if (viewPager2.currentItem == 0) {
                buttonPrevious.isEnabled=false
                }
            }
        ShowNotification(position, images, pisteSublist)

    }




    /*----Affichage de la liste des pistes------*/
        /*pistes_sublist.forEach{ piste ->
            val lineBorder = LinearLayout(this)
            val tv_piste = TextView(lineBorder.context)
            tv_piste.textSize = 25f
            tv_piste.text = piste.number
            val bitmap = Bitmap.createBitmap(200, 20, Bitmap.Config.ARGB_8888)
            val canvas= Canvas(bitmap)
            when(piste.difficulty) {
                1 -> canvas.drawColor(Color.GREEN)
                2-> canvas.drawColor(Color.BLUE)
                3-> canvas.drawColor(Color.BLACK)
            }
            val img_view = ImageView(lineBorder.context)
            img_view.setImageBitmap(bitmap)
            lineBorder.addView(tv_piste)
            lineBorder.addView(img_view)
            findViewById<LinearLayout>(R.id.scrollLayoutResult).addView(lineBorder)
            Log.i("data", piste.number)
        }*/






    fun FillList(Name : List<String>, Difficulty : List<String>, Time : List<String>) : MutableList<PisteFinal> {
        val newList = mutableListOf<PisteFinal>()
        for(i in Name.indices)
            run {
                newList += PisteFinal(Name[i], Difficulty[i].toInt(), Time[i].toInt())
            }

        return newList
    }

    fun ShowNotification(position : Int, images : IntArray, pisteSublist : MutableList<PisteFinal>) {
        val intentReturnApp = Intent(this, ResultsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntentReturnApp: PendingIntent =
            PendingIntent.getActivity(this, 0, intentReturnApp, PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(CHANNEL_ID, "Optiski" , NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)


            builder = Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_downhill_skiing)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, images[pisteSublist[position].difficulty - 1]))
                .setContentTitle(pisteSublist[position].number)
                .setContentText("Temps estimé pour la piste : ${pisteSublist[position]}")
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntentReturnApp)
                .setAutoCancel(true)
            //.addAction(R.drawable.clock, "Previous", pendingIntentPrevious)
        }
        notificationManager.notify(1234, builder.build())

    }




}




