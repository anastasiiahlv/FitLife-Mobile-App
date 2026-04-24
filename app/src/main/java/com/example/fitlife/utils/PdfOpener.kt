package com.example.fitlife.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object PdfOpener {

    fun openPdfFromAssets(
        context: Context,
        assetFileName: String,
        outputFileName: String = assetFileName
    ): Boolean {
        return try {
            val outputFile = File(context.cacheDir, outputFileName)

            if (!outputFile.exists()) {
                context.assets.open(assetFileName).use { input ->
                    outputFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                outputFile
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            }

            context.startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        } catch (_: Exception) {
            false
        }
    }
}