package fr.sdv.gamebacklog.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

object ImageDownloadUtils {

    suspend fun downloadAndSaveImage(
        context: Context,
        imageUrl: String,
        fileName: String = "game_${System.currentTimeMillis()}.jpg"
    ): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val imagesDir = File(context.filesDir, "game_images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }

            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val bitmap = BitmapFactory.decodeStream(connection.inputStream)

            val imageFile = File(imagesDir, fileName)
            FileOutputStream(imageFile).use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, output)
                output.flush()
            }

            imageFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
