package com.example.fitlife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.fitlife.data.local.DatabaseProvider
import com.example.fitlife.data.seed.DatabaseSeeder
import com.example.fitlife.ui.navigation.FitLifeBottomBar
import com.example.fitlife.ui.navigation.FitLifeNavGraph
import com.example.fitlife.ui.theme.FitLifeTheme
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.padding
import org.osmdroid.config.Configuration


class MainActivity : ComponentActivity() {

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
