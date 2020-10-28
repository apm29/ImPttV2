package com.imptt.v2.view.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.Toast
import com.imptt.v2.R
import com.imptt.v2.core.struct.BaseFragment
import com.imptt.v2.utils.navigate
import kotlinx.android.synthetic.main.fragment_web_view.*
import kotlinx.coroutines.delay
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class WebViewFragment : BaseFragment() {
    private val webViewClient: WebViewClient by lazy {
        object : WebViewClient() {

        }
    }

    override fun setupViewLayout(savedInstanceState: Bundle?): Int {
        return R.layout.fragment_web_view
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun setupViews(view: View, savedInstanceState: Bundle?) {
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.webViewClient = webViewClient
        WebView.setWebContentsDebuggingEnabled(true)
        web.addJavascriptInterface(object : Any() {
            @JavascriptInterface
            fun showToast(msg: String) {
                mHandler.post {
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                }
            }

            @JavascriptInterface
            fun add(a: Int, b: Int) {

            }

        }, "jsBridge")
        web.loadUrl(
            WebViewFragmentArgs.fromBundle(requireArguments()).url ?: "http://ebasetest.ciih.net"
        )
        buttonHome.setOnClickListener {
            navigate(R.id.mainFragment, null, true)
        }
    }


}