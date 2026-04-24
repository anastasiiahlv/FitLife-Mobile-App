package com.example.fitlife.ui.screens.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fitlife.R
import com.example.fitlife.utils.PdfOpener
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserGuideScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.guide_title)) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(stringResource(R.string.common_back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.guide_title),
                style = MaterialTheme.typography.headlineMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.guide_about_title),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = stringResource(R.string.guide_about_text),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.guide_features_title),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(stringResource(R.string.guide_feature_1))
                    Text(stringResource(R.string.guide_feature_2))
                    Text(stringResource(R.string.guide_feature_3))
                    Text(stringResource(R.string.guide_feature_4))
                    Text(stringResource(R.string.guide_feature_5))
                }
            }

            Button(
                onClick = {
                    val languageTag = AppCompatDelegate.getApplicationLocales().toLanguageTags()

                    val assetFileName = when {
                        languageTag.startsWith("uk") -> "user_guide_uk.pdf"
                        languageTag.startsWith("en") -> "user_guide_en.pdf"
                        Locale.getDefault().language == "uk" -> "user_guide_uk.pdf"
                        else -> "user_guide_en.pdf"
                    }

                    PdfOpener.openPdfFromAssets(
                        context = context,
                        assetFileName = assetFileName
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.guide_open_pdf))
            }
        }
    }
}