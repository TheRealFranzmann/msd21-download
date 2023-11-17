package at.interactivecuriosity.imagedownload

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var downloadButton: Button
    private lateinit var deleteButton: Button
    private val imageUrl = "https://www.markusmaurer.at/fhj/eyecatcher.jpg" // URL des herunterzuladenden Bildes
    private val fileName = "downloadedImage.jpg"
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ImageDownloadService.DOWNLOAD_COMPLETE) {
                val fileName = intent.getStringExtra(ImageDownloadService.IMAGE_FILE_NAME)
                if (fileName != null) {
                    val file = File(getExternalFilesDir(null), fileName)
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    imageView.setImageBitmap(bitmap)
                    Toast.makeText(context, "Downloaded Picture", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        downloadButton = findViewById(R.id.downloadButton)
        deleteButton = findViewById(R.id.deleteButton)

        val filter = IntentFilter(ImageDownloadService.DOWNLOAD_COMPLETE)
        registerReceiver(receiver, filter)

        downloadButton.setOnClickListener {
            val downloadIntent = Intent(this, ImageDownloadService::class.java)
            downloadIntent.putExtra("URL", imageUrl)
            downloadIntent.putExtra("FILENAME", fileName)
            startService(downloadIntent)
        }

        deleteButton.setOnClickListener {
            deleteImage(fileName)
        }
    }



    private fun deleteImage(fileName: String) {
        val file = File(getExternalFilesDir(null), fileName)
        if (file.exists()) {
            file.delete()
            runOnUiThread {
                imageView.setImageBitmap(null)
                Toast.makeText(this, "Bild gelöscht", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister broadcast receiver
        unregisterReceiver(receiver)
    }
}
