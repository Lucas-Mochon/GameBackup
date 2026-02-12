package fr.sdv.gamebacklog.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageUtils {

    private const val IMAGES_DIR = "game_covers"

    fun getImagesDirectory(context: Context): File {
        val dir = File(context.filesDir, IMAGES_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun saveImageFromUri(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = generateImageFileName()
            val file = File(getImagesDirectory(context), fileName)

            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteImage(imagePath: String): Boolean {
        return try {
            val file = File(imagePath)
            file.exists() && file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun generateImageFileName(): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return "game_${timestamp}.jpg"
    }

    fun getImageUri(imagePath: String): Uri? {
        return try {
            Uri.fromFile(File(imagePath))
        } catch (e: Exception) {
            null
        }
    }
}

