package kr.co.secureon.sso.sample;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sf.msso.SsoUtil;

public class WebViewTestActivity extends AppCompatActivity {

    WebView webView;
    Button webViewTestBtn;

    String token = "djaskldjsa890du30ejd2jd890jcsdosdfu89ewjd";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_test);

        final byte[] secId = SsoUtil.getSecId(this);

        webView = findViewById(R.id.webView);
        webViewTestBtn = findViewById(R.id.webViewTestBtn);

        WebSettings settings = webView.getSettings();
        // Javascript 사용하기
        settings.setJavaScriptEnabled(true);
        // ViewPort meta tag를 활성화 여부
        settings.setUseWideViewPort(true);
        // TextEncoding 이름 정의
        settings.setDefaultTextEncodingName("UTF-8");

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("file:///android_asset/WebViewTest.html");

    // 네이티브에서 -> 웹뷰로 token과 secid를 보낸다.
    webViewTestBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript(String.format("javascript:setToken('%s', '%s');", token, new String(secId)), null);
            } else {
                webView.loadUrl(String.format("javascript:setToken('%s', '%s');", token, new String(secId)));
            }
        }
    });

        // 웹뷰에서 네이티브로 token과 secId를 보낸다.
        webView.addJavascriptInterface(new WebViewInterface() {

            @JavascriptInterface
            @Override
            public void callNative(String token, String secId) {
                Log.d("WebViewInterface", token);
                Log.d("WebViewInterface", secId);
                webViewTestBtn.setText(String.format("{token: '%s', secId: '%s'}", token, secId));
            }
        }, "WebViewCallbackInterface");

    }

    private interface WebViewInterface{
        void callNative(String token, String secI);
    }
}

