package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
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
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            // 正常返回false，其它返回true
            if (url.isNullOrBlank()) return false
            if (url?.startsWith("http", true) ||
                url?.startsWith("https", true) ||
                url?.startsWith("file", true)
            ) {
                view?.loadUrl(url)
                sample_text.setText(url)
                return false
            }
            //下面如果返回false webView中显示net:ERR_UNKNOWN_URL_SCHEME
            try {
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("$TAG shouldOverrideUrlLoading", e.printStackTrace().toString())
            }
            return true
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
            Log.d(TAG, info)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        /*
            mywebView.goBack();//跳到上个页面
            mywebView.goForward();//跳到下个页面
            mywebView.canGoBack();//是否可以跳到上一页(如果返回false,说明已经是第一页)
            mywebView.canGoForward();//是否可以跳到下一页(如果返回false,说明已经是最后一页)
        */
        if (keyCode == KeyEvent.KEYCODE_BACK && mywebView.canGoBack()) {
            mywebView.goBack()
            return false
        }

        return super.onKeyDown(keyCode, event)
    }

    interface JsCallback { public fun onJsCallback()}

    private val jsCallback = object : JsCallback{
        @JavascriptInterface
        override public fun onJsCallback() {
            Log.d("$TAG onJsCallback:","JavaScript调用Android啦")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example of a call to a native method
        val url = stringFromJNI()
        sample_text.setText(url)
        // 启动javascript
        mywebView.settings.javaScriptEnabled = true
        mywebView.addJavascriptInterface(jsCallback, "js")


        //设置页面自适应屏幕
        mywebView.settings.useWideViewPort = true
        mywebView.settings.loadWithOverviewMode = true

        mywebView.settings.setSupportZoom(true) //启用缩放功能
        mywebView.settings.builtInZoomControls = true //使用WebView内置的缩放功能
        //隐藏屏幕中的虚拟缩放按钮。Note:为true在某些系统版本中可能会导致应用出现意外崩溃。
        mywebView.settings.displayZoomControls = false

        // 缓存模式如下：
        // LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
        // LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
        // LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        // LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
        mywebView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK


        mywebView.settings.domStorageEnabled = true
        mywebView.settings.allowFileAccess = true
        mywebView.settings.databaseEnabled = true
        mywebView.webViewClient = webClient
        mywebView.setDownloadListener(dwonloadListener)

        //alert不能弹出的问题
        mywebView.webChromeClient = WebChromeClient()

        mywebView.loadUrl(url)

    }


    fun showText(v: View?) {

        var url = sample_text.text.toString()
        url = if (url.startsWith("http", true) ||
                url.startsWith("file", true)) url else "http://$url"
        mywebView.loadUrl(url)
        //mywebView.loadUrl("javascript:wave()");

        Toast.makeText(this, url, Toast.LENGTH_LONG).show()
    }

    fun execJS(v: View?) {
        var jsFunc = jsText.text.toString()
        if (!jsFunc.startsWith("javascript:", true))
            jsFunc = "javascript:$jsFunc"
        mywebView.loadUrl(jsFunc)
        Toast.makeText(this, jsFunc, Toast.LENGTH_LONG).show()
    }


}

