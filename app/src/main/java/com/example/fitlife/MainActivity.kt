package com.example.fitlife

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.fitlife.data.local.DatabaseProvider
import com.example.fitlife.data.seed.DatabaseSeeder
import com.example.fitlife.ui.navigation.FitLifeBottomBar
import com.example.fitlife.ui.navigation.FitLifeNavGraph
import com.example.fitlife.ui.theme.FitLifeTheme
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import androidx.compose.material3.MaterialTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = packageName

        val db = DatabaseProvider.get(this)
        lifecycleScope.launch {
            DatabaseSeeder.seedIfNeeded(this@MainActivity, db)
        }

        setContent {
            FitLifeTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = { FitLifeBottomBar(navController) }
                ) { innerPadding ->
                    FitLifeNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}