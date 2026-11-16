package com.studentlifeos.app

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewAssetLoader.AssetsPathHandler
import android.webkit.ValueCallback
import com.google.firebase.auth.FirebaseAuth
class MainActivity : ComponentActivity() {
    private lateinit var webView: WebView
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    private val filePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        filePathCallback?.onReceiveValue(if (uri != null) arrayOf(uri) else null)
        filePathCallback = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAuth.getInstance().signInAnonymously()
            .addOnSuccessListener {
                android.util.Log.d("StudentLifeOS", "Anonymous login successful")
            }
            .addOnFailureListener { exception ->
                android.util.Log.e("StudentLifeOS", "Anonymous login failed", exception)
            }
        window.statusBarColor = android.graphics.Color.rgb(8, 11, 20)
        window.navigationBarColor = android.graphics.Color.rgb(8, 11, 20)

        webView = WebView(this)
        setContentView(webView)

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", AssetsPathHandler(this))
            .build()

        webView.settings.apply {
            javaScriptEnabled = true // Required by the existing Student Life OS interface.
            domStorageEnabled = true // Keeps the app's LocalStorage data between launches.
            allowFileAccess = false
            allowContentAccess = true // Needed for the JSON backup file picker.
            javaScriptCanOpenWindowsAutomatically = false
            setSupportMultipleWindows(false)
            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
            safeBrowsingEnabled = true
        }

        webView.webViewClient = object : WebViewClientCompat() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): android.webkit.WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url
                return if (uri.host == "appassets.androidplatform.net") {
                    false
                } else {
                    // Open external links (CDNs, documentation, etc.) in the browser.
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, uri))
                    } catch (_: Exception) {
                        Toast.makeText(this@MainActivity, "Can't open this link", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                callback: ValueCallback<Array<Uri>>,
                params: FileChooserParams?
            ): Boolean {
                filePathCallback?.onReceiveValue(null)
                filePathCallback = callback
                filePicker.launch("application/json")
                return true
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (::webView.isInitialized && webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })

        if (savedInstanceState == null) {
            webView.loadUrl("https://appassets.androidplatform.net/assets/index.html")
        } else {
            webView.restoreState(savedInstanceState)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        filePathCallback?.onReceiveValue(null)
        webView.destroy()
        super.onDestroy()
    }
}
