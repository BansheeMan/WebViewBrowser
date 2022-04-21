package com.example.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.webview.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            //it.ok.setOnClickListener(clickOk1)
            it.ok.setOnClickListener(clickOk2)
        }
    }
    //----------------------------------------------------------------------------------------------// 1)  binding.webview.loadData() не работате, должна открывать WebView
    // --------------- 2 СПОСОБ ----------------------------                                        // 2)  binding.webview.loadDataWithBaseURL аналог первого, работает
    @SuppressLint("SetJavaScriptEnabled")                                                    // 3)  binding.webview.loadUrl(urlText) открывает ссылку в Chrome, а не WebView
    private val clickOk2 = View.OnClickListener {
        Toast.makeText(this, "STAR 2", Toast.LENGTH_SHORT).show()                       // runOnUiThread {} - выполняет в главно потоке, в данном примере аналог Handler(Looper.getMainLooper()).post { }
        val urlText = binding.etTextUrl.text.toString()                                             //  в данном примере аналог Handler(Looper.getMainLooper()).post { }
        Thread {
            runOnUiThread { binding.webview.loadUrl(urlText) }
        }.start()
        Toast.makeText(this, "END 2", Toast.LENGTH_SHORT).show()
    }
//----------------------------------------------------------------------------------------------
    // --------------- 1 СПОСОБ ----------------------------
    @SuppressLint("SetJavaScriptEnabled")
    private val clickOk1 = View.OnClickListener {
        Toast.makeText(this, "START 1", Toast.LENGTH_SHORT).show()
        val urlText = binding.etTextUrl.text.toString()
        val uri = URL(urlText)
        val urlConnection: HttpsURLConnection =
            (uri.openConnection() as HttpsURLConnection).apply {
                connectTimeout = 1000
                readTimeout = 1000
            }

        Thread {
            val buffer = BufferedReader(InputStreamReader(urlConnection.inputStream))
            val result = getLinesAsOneBigString(buffer)

            Handler(Looper.getMainLooper()).post { // 2 способ
                binding.webview.settings.javaScriptEnabled = true
                binding.webview.loadDataWithBaseURL(
                    null,
                    result,
                    "text/html; utf-8",
                    "utf-8",
                    null
                )
            }
        }.start()


        Toast.makeText(this, "END 1", Toast.LENGTH_SHORT).show()
    }

    private fun getLinesAsOneBigString(bufferedReader: BufferedReader): String {
        return bufferedReader.lines().collect(Collectors.joining("\n"));
    }
}

