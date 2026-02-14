package com.example.fitlife.ui.screens.centers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CentersScreen(
    onOpenDetails: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("CentersScreen", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Open details for demo center #1",
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable { onOpenDetails("demo-center-1") }
        )
        Text(
            text = "Open details for demo center #2",
            modifier = Modifier
                .padding(top = 12.dp)
                .clickable { onOpenDetails("demo-center-2") }
        )
    }
}
