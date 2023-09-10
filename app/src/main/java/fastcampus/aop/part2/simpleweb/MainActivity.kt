package fastcampus.aop.part2.simpleweb

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private val refreshLayout : SwipeRefreshLayout by lazy {

        findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
    }

    private val webView : WebView by lazy {
        findViewById<WebView>(R.id.webView)
    }

    private val addressBar : EditText by lazy {
        findViewById<EditText>(R.id.addressBar)
    }

    private val goHomeButton : ImageButton by lazy {

        findViewById<ImageButton>(R.id.goHomeButton)

    }

    private val goBackButton : ImageButton by lazy {
        findViewById<ImageButton>(R.id.goBackButton)
    }

    private val goForwardButton : ImageButton by lazy {

        findViewById<ImageButton>(R.id.goForwardButton)
    }

    private val progressBar : ContentLoadingProgressBar by lazy {
        findViewById<ContentLoadingProgressBar>(R.id.progressBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        bindViews()
    }

    override fun onBackPressed() {
        if(webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        webView.apply {
            /** 외부에서 브라우저를 열지않고 앱 내에서 열기 위해서 */
            webViewClient = WebViewClient()

            webChromeClient = WebChromeClient()

            /** 안드로이드는 보안상 웹뷰에서 자바스크립트로 짜여진 코드를 default 로 막아놓았다. */
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_URL)
        }

    }

    private fun bindViews() {

        addressBar.setOnEditorActionListener { v, actionId, event ->

            if(actionId == EditorInfo.IME_ACTION_DONE){
                val loadingUrl = v.text.toString()
                /** 주소창 앞에 http 나 https 가 들어있다면  */
                if(URLUtil.isNetworkUrl(loadingUrl)) {

                    webView.loadUrl(loadingUrl)
                }
                /** 없다면 url 에 붙여준다. */
                else {
                    webView.loadUrl("http://${loadingUrl}")
                }

            }

            return@setOnEditorActionListener false
        }


        goBackButton.setOnClickListener {
            webView.goBack()
        }

        goForwardButton.setOnClickListener {

            webView.goForward()

        }

        goHomeButton.setOnClickListener {
            initViews()

        }

        refreshLayout.setOnRefreshListener {
            webView.reload()
        }

    }

    /** inner class 로 작성해야 위의 MainActivity 의 property 에 접근할 수 있다. */
    /** 즉 refreshLayout 에 접근이 가능하다는 말이다. */
    /** refresh 아이콘이 사라지는 시점을 제어하기 위해서 WebViewClient 의 onPageFinished 를 override 했다. */
    inner class WebViewClient: android.webkit.WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            refreshLayout.isRefreshing = false
            progressBar.hide()

            /** navigation history 에 따른 버튼 활성화화 */
            /** isEnabled 를 back 같은경우는 canGoBack() 으로 뒤에 history 가 있는지에 대한 여부를  */
            /** forward 같은 경우는 canGoForward() 로 앞에 history 가 있는지에 대한 여부를 boolean 으로 받는다. */
           goBackButton.isEnabled = webView.canGoBack()
           goForwardButton.isEnabled = webView.canGoForward()

           /** 주소창에 full url 주소를 셋팅되게 해놓는다. */
           addressBar.setText(url)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            progressBar.show()

        }
    }

    /** 현재 페이지에서 일어나는 알람등을 알려 주기 위한 콜백 인터페이스 */
    inner class WebChromeClient : android.webkit.WebChromeClient() {

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            progressBar.progress = newProgress

        }
    }

    companion object {

        const val DEFAULT_URL = "http://www.google.com"

    }

}