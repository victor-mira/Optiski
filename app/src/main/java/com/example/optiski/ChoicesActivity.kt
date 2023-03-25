package com.example.optiski

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog

class ChoicesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choices)
        supportActionBar?.hide()

        val buttonResult = findViewById<ImageButton>(R.id.imageButton)
        val stations = resources.getStringArray(R.array.Stations)
        val spinner = findViewById<Spinner>(R.id.choix_station)
        val buttonHelp = findViewById<ImageButton>(R.id.helpButton)

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

        //Time picker
        val hourpicker = findViewById<NumberPicker>(R.id.hourpicker)
        val minutepicker = findViewById<NumberPicker>(R.id.minutepicker)
        hourpicker.minValue = 0
        hourpicker.maxValue = 4
        minutepicker.displayedValues = arrayOf("0","15","30","45")
        minutepicker.minValue = 0
        minutepicker.maxValue = 3

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
    }
}