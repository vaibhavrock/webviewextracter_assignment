package vaibhav.boxsample

import android.Manifest
import android.app.DownloadManager
import android.content.pm.PackageManager
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mWebView: WebView
    private lateinit var btnFetch: AppCompatButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnFetch = findViewById(R.id.btnFetch)
        val evUrl = findViewById<AppCompatEditText>(R.id.evUrl)
        mWebView = findViewById(R.id.webView)

        mWebView.getSettings().javaScriptEnabled = true
        mWebView.getSettings().setSupportZoom(false)
        mWebView.getSettings().domStorageEnabled = true
        mWebView.getSettings().setSupportMultipleWindows(true)
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)

        mWebView.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                //Toast.makeText(applicationContext, "onReceivedSslError", Toast.LENGTH_SHORT).show()
                Log.e("vaiiiiiiiiiiiiii", "urllllllllllllllllllll:$error")
                // super.onReceivedSslError(view, handler, error);
                handler.proceed()
            }

            override fun shouldOverrideUrlLoading(view: WebView, webResourceRequest: WebResourceRequest): Boolean {
               // Toast.makeText(applicationContext, "shouldOverrideUrlLoading", Toast.LENGTH_SHORT).show()
                val url = webResourceRequest.url.toString()
                Log.e("vaiiiiiiiiiiiiii", "urllllllllllllllllllll:$url")
                var value = true
                val extension = MimeTypeMap.getFileExtensionFromUrl(url)
                if (extension != null) {
                    val mime = MimeTypeMap.getSingleton()
                    val mimeType = mime.getMimeTypeFromExtension(extension)?.toLowerCase()
                    if (mimeType != null) {
                        if (mimeType.contains("video")
                                || extension.contains("mov")
                                || extension.contains("mp3")
                                || extension.contains("jpg")
                                || extension.contains("png")) {
                            val mdDownloadManager = this@MainActivity.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                            val request = DownloadManager.Request(Uri.parse(url))
                            val destinationFile = File(
                                    Environment.getExternalStorageDirectory(),
                                    getFileName(mimeType))
                            request.setDescription("Downloading via Your app name..")
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            request.setDestinationUri(Uri.fromFile(destinationFile))
                            mdDownloadManager.enqueue(request)
                            value = false
                        }
                    }
                    if (value) {
                        view.loadUrl(url)
                    }
                }
                return value
            }
        }

        mWebView.setDownloadListener { url: String?, userAgent: String?, contentDisposition: String?, mimeType: String?, contentLength: Long ->
            Toast.makeText(applicationContext, "setDownloadListener", Toast.LENGTH_SHORT).show()
            val request = DownloadManager.Request(Uri.parse(url))
            request.setMimeType(mimeType)
            val cookies = CookieManager.getInstance().getCookie(url)
            request.addRequestHeader("cookie", cookies)
            request.addRequestHeader("User-Agent", userAgent)
            request.setDescription("Downloading File...")
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                    url, contentDisposition, mimeType))
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            Toast.makeText(applicationContext, "Downloading File", Toast.LENGTH_LONG).show()
        }

        btnFetch.setOnClickListener(View.OnClickListener { v: View? ->
            val url = evUrl.text.toString()
            mWebView.loadUrl(url)
        })


        val permissionCheck = ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            //requesting permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        } else {
            Log.i("main activity", "Permission Granted")
        }

    }

    override fun onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    fun getFileName(extention: String): String {
        var filenameWithoutExtension = ""
        filenameWithoutExtension = System.currentTimeMillis().toString() + "." + extention
        return filenameWithoutExtension
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //you have the permission now.
            Log.i("main", "Permission Already Granted")
        }
    }
}