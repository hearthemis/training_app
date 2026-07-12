package com.hearthemis.training

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.view.View
import android.widget.Toast
import android.os.SystemClock
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.AdapterView

class ChronometerActivity : AppCompatActivity() {

    // Définition des modes possibles
    enum class TrainingMode { FREE,STANDARD, THREE_SEVEN, PRECISION }
    private var currentMode = TrainingMode.FREE

    private var standardLimit: Long = 150

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chronometer)

        val metre = findViewById<android.widget.Chronometer>(R.id.c_meter)
        val btnStartStop = findViewById<Button>(R.id.btn)
        val rootLayout = findViewById<View>(R.id.main) // Changement de la couleur de fond

        val button37 = findViewById<Button>(R.id.button_37)
        val buttonStandard = findViewById<Button>(R.id.button_standard)
        val buttonPrecision = findViewById<Button>(R.id.button_precision)
        val buttonReset = findViewById<Button>(R.id.button_reset)

        // --- CONFIGURATION DU SPINNER ---
        val standardSpinner = findViewById<Spinner>(R.id.standard_spinner)
        val durations = arrayOf("150 secondes", "20 secondes", "10 secondes")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, durations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        standardSpinner.adapter = adapter

        // Écouteur pour le Spinner
        standardSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // On extrait le nombre de la chaîne (ex: "150 secondes" -> 150)
                standardLimit = durations[position].split(" ")[0].toLong()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        var pauseOffset: Long = 0
        var isWorking = false

        // --- GESTION DES MODES ---
        buttonStandard.setOnClickListener {
            currentMode = TrainingMode.STANDARD
            Toast.makeText(this, "Standard ($standardLimit s) activé", Toast.LENGTH_SHORT).show()
        }


        // --- GESTION DES MODES ---

//        buttonStandard.setOnClickListener {
//            currentMode = TrainingMode.STANDARD
//            Toast.makeText(this, "Mode Standard activé", Toast.LENGTH_SHORT).show()
//            rootLayout.setBackgroundColor(getColor(android.R.color.white))
//        }

        button37.setOnClickListener {
            currentMode = TrainingMode.THREE_SEVEN
            Toast.makeText(this, "Mode 3/7 activé", Toast.LENGTH_SHORT).show()
        }

        buttonPrecision.setOnClickListener {
            currentMode = TrainingMode.PRECISION
            Toast.makeText(this, "Mode Précision activé", Toast.LENGTH_SHORT).show()
        }

        // --- LOGIQUE DU CHRONOMÈTRE ---

        metre.setOnChronometerTickListener { chrono ->
            val elapsedMillis = SystemClock.elapsedRealtime() - chrono.base
            val elapsedSeconds = elapsedMillis / 1000

            when (currentMode) {
                TrainingMode.THREE_SEVEN -> {
                    // Phase 1 : Préparation (0 à 60s)
                    if (elapsedSeconds < 60) {
                        rootLayout.setBackgroundColor(getColor(android.R.color.white))
                    }
                    // Phase 2 : Les 5 cycles (de 60s à 110s)
                    else if (elapsedSeconds < 110) {
                        val secondsInWorkout = elapsedSeconds - 60
                        val cycleTime = secondsInWorkout % 10 // Donne un chiffre entre 0 et 9

                        if (cycleTime < 7) {
                            // 7 secondes d'attente avant le tir
                            rootLayout.setBackgroundColor(getColor(android.R.color.holo_red_light))
                        } else {
                            // 3 secondes de tir
                            rootLayout.setBackgroundColor(getColor(android.R.color.holo_green_light))
                        }
                    }
                    // Phase 3 : Fin des 5 cycles
                    else {
                        rootLayout.setBackgroundColor(getColor(android.R.color.darker_gray))
                        metre.stop() // Arrête le chrono à la fin
                    }
                }

                TrainingMode.PRECISION -> {
                    // 1 min de préparation (60s) puis 5 min de tir (300s) = total 360s
                    if (elapsedSeconds < 60) {
                        rootLayout.setBackgroundColor(getColor(android.R.color.white))
                    } else if (elapsedSeconds < 360) {
                        rootLayout.setBackgroundColor(getColor(android.R.color.holo_blue_bright))
                    } else {
                        rootLayout.setBackgroundColor(getColor(android.R.color.darker_gray))
                        metre.stop() // Arrête le chrono à la fin
                    }
                }
                TrainingMode.STANDARD -> {
                    // Règle Standard : 7s préparation (Rouge) + Temps de tir (Vert)
                    val prepTime = 7L
                    val totalTime = prepTime + standardLimit

                    if (elapsedSeconds < prepTime) {
                        rootLayout.setBackgroundColor(getColor(android.R.color.holo_red_light))
                    } else if (elapsedSeconds < totalTime) {
                        rootLayout.setBackgroundColor(getColor(android.R.color.holo_green_light))
                    } else {
                        rootLayout.setBackgroundColor(getColor(android.R.color.darker_gray))
                        metre.stop()
                        isWorking = false
                        btnStartStop.text = "START"
                    }
                }
                TrainingMode.FREE -> {
                    // Rien de spécial
                }
            }
        }

        // --- BOUTON START / STOP ---
        btnStartStop.setOnClickListener {
            if (!isWorking) {
                metre.base = SystemClock.elapsedRealtime() - pauseOffset
                metre.start()
                btnStartStop.text = "STOP"
                isWorking = true
            } else {
                pauseOffset = SystemClock.elapsedRealtime() - metre.base
                metre.stop()
                btnStartStop.text = "START"
                isWorking = false
            }
        }

        // --- BOUTON RESET ---
        buttonReset.setOnClickListener {
            metre.stop()
            metre.base = SystemClock.elapsedRealtime()
            pauseOffset = 0
            isWorking = false
            btnStartStop.text = "START"
            rootLayout.setBackgroundColor(getColor(android.R.color.white))
            currentMode = TrainingMode.FREE // Optionnel : repasser en mode libre au reset
        }
    }
}
