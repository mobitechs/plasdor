package com.plasdor.app.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.plasdor.app.R

class WebViewFragment : Fragment() {


    lateinit var rootView: View
    private lateinit var webView: WebView
    var url=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_web_view, container, false)
        initView()
        return rootView
    }

    private fun initView() {
        url = arguments?.getString("url").toString()
        webView = rootView.findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let { view?.loadUrl(it) }
                return true
            }
        }
        webView.loadUrl(url)
    }


}