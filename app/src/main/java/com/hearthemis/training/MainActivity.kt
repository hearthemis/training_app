package com.hearthemis.training

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.Toast
import android.widget.TextView
import android.content.Intent
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val buttonChronometer = findViewById<Button>(R.id.button_chronometer)
        buttonChronometer.text = "Accéder au chronomètre"
        val buttonResults = findViewById<Button>(R.id.button_results)
        buttonResults.text = "Accéder aux résultats"
        val buttonStats = findViewById<Button>(R.id.button_stats)
        buttonStats.text = "Accéder aux statistiques"
        val welcome = findViewById<TextView>(R.id.welcome)
        welcome.text = "Bienvenue sur votre application d'entraînement"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Ajout des listeners aux boutons
        //Bouton pour accéder au chronomètre
        buttonChronometer.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View): Unit {
                val intent = Intent(this@MainActivity, ChronometerActivity::class.java);
                startActivity(intent);
            }
        })
        //Bouton pour accéder aux résultats
        buttonResults.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View): Unit {
                val intent = Intent(this@MainActivity, Results::class.java);
                startActivity(intent);
            }
        })
        //Bouton pour accéder aux statistiques
        buttonStats.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View): Unit {
                val intent = Intent(this@MainActivity, Stats::class.java);
                startActivity(intent);
            }
        })
    }
}

