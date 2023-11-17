package at.interactivecuriosity.imagedownload

import android.app.IntentService
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class ImageDownloadService : IntentService("ImageDownloadService") {

    companion object {
        const val DOWNLOAD_COMPLETE = "download complete"
        const val IMAGE_FILE_NAME = "image file name"
    }
    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val url = intent.getStringExtra("URL")
            val fileName = intent.getStringExtra("FILENAME")
            downloadImage(url, fileName)
        }
    }

    private fun downloadImage(urlString: String?, fileName: String?) {
        try {
            if (urlString != null && fileName != null) {
                val url = URL(urlString)
                val connection = url.openConnection()
                connection.connect()
                val inputStream = connection.getInputStream()
                val file = File(getExternalFilesDir(null), fileName)
                FileOutputStream(file).use { output ->
                    inputStream.copyTo(output)
                }
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                Log.d("ImageDownloadService", "Image downloaded: $fileName")
                val downloadCompleteIntent = Intent(DOWNLOAD_COMPLETE)
                downloadCompleteIntent.putExtra(IMAGE_FILE_NAME, fileName)
                sendBroadcast(downloadCompleteIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ImageDownloadService", "Error downloading image: ${e.message}")
        }
    }
}