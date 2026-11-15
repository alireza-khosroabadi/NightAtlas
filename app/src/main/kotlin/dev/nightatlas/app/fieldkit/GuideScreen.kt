package dev.nightatlas.app.fieldkit

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import dev.nightatlas.app.navigation.isHttps

@Composable
internal fun GuideScreen(url: String, isOfflineGuide: Boolean, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        TextButton(onClick = onBack) { Text("Back") }
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.allowFileAccess = false
                    settings.allowContentAccess = false
                    webViewClient =
                        object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView,
                                request: WebResourceRequest,
                            ) = !isHttps(request.url.toString())
                        }
                    if (isOfflineGuide)
                        loadDataWithBaseURL(url, fieldGuide, "text/html", "UTF-8", null)
                    else if (isHttps(url)) loadUrl(url)
                }
            },
            onRelease = { it.destroy() },
        )
    }
}

private val fieldGuide =
    """
    <!doctype html>
    <html lang="en" dir="ltr"><meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <style>body{background:#101a23;color:#e4eaeb;font:19px/1.8 sans-serif;margin:1.5rem}h1{color:#f0c585}</style>
    <h1>A guide to clear nights</h1>
    <p>Move away from bright lights. Find the constellation with your eyes before looking through binoculars or a telescope.</p>
    <p>When you spot a constellation, log it in your journal.</p>
    </html>
"""
        .trimIndent()
