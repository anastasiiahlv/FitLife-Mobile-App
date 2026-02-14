package com.example.fitlife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.fitlife.data.local.DatabaseProvider
import com.example.fitlife.data.seed.DatabaseSeeder
import com.example.fitlife.ui.theme.FitLifeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // 1️⃣ Отримуємо БД
        val db = DatabaseProvider.get(this)

        // 2️⃣ Seed JSON → Room (тільки якщо БД пуста)
        lifecycleScope.launch {
            DatabaseSeeder.seedIfNeeded(this@MainActivity, db)
        }

        // 3️⃣ UI
        setContent {
            FitLifeTheme {
                StartScreen()
            }
        }
    }
}

@Composable
fun StartScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "FitLife is running",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
