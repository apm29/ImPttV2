package com.imptt.v2.view.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import com.imptt.v2.R
import com.imptt.v2.core.struct.BaseFragment
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebChromeClient


class AgentWebViewFragment : BaseFragment() {

    override fun setupViewLayout(savedInstanceState: Bundle?): Int {
        return R.layout.fragment_agent_webview
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun setupViews(view: View, savedInstanceState: Bundle?) {
        val mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(
                view.findViewById(R.id.layoutWebView),
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
            )
            .useDefaultIndicator()
            .setWebChromeClient(object : WebChromeClient(){
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    setToolbarTitle(title)
                }
            })
            .createAgentWeb()
            .ready()
            .go("http://jwttest.ciih.net/#/cuttingEdgeNews")

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object :OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    if(mAgentWeb.webCreator.webView.canGoBack()){
                        mAgentWeb.webCreator.webView.goBack()
                    }else{
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        )
    }


}