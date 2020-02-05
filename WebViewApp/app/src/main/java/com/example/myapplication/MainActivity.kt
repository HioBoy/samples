package com.example.myapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.DownloadListener
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        val TAG = MainActivity.javaClass.name

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    private val webClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            try {
                val url:String = request?.url.toString()
                if (url.startsWith("http") || url.startsWith("https")) {
                    Log.d(TAG, "xxxxxxxxxxxxxxxxxxxxxxxxx")
                    return super.shouldOverrideUrlLoading(view, request)
                }
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                return true
            } catch (e:Exception) {
                return false
            }

        }

    }
    private val dwonloadListener = object : DownloadListener {
        override fun onDownloadStart(
            url: String?,
            userAgent: String?,
            contentDisposition: String?,
            mimetype: String?,
            contentLength: Long
        ) {
            //To change body of created functions use File | Settings | File Templates.
            val info = "$url : $userAgent-------------------------"
            //Toast.makeText(this, info, Toast.LENGTH_LONG)
            Log.d(TAG, info)
            Log.e(TAG, info)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mywebView.goBack()
            return false
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example of a call to a native method
        val url = stringFromJNI()
        sample_text.setText(url)
        mywebView.settings.javaScriptEnabled = true
        mywebView.settings.domStorageEnabled = true
        mywebView.webViewClient = webClient
        mywebView.setDownloadListener(dwonloadListener)
        mywebView.loadUrl(url)

    }


    fun showText(v: View?) {
        var url = sample_text.text.toString()
        url = if (url.contains("http", true)) url else "http://$url"
        mywebView.loadUrl(url)
        mywebView.refreshDrawableState()

        Toast.makeText(this, url, Toast.LENGTH_LONG).show()
    }


}

