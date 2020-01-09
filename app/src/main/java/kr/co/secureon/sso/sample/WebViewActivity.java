package kr.co.secureon.sso.sample;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sf.msso.MobileSsoAPI;
import com.sf.msso.SsoUtil;

import static kr.co.secureon.sso.sample.LoginActivity.PAGE_URL;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class WebViewActivity extends AppCompatActivity {
    WebView webView;
    EditText urlEditText;
    Button goBtn, refreshBtn, backBtn, homeBtn, stopBtn;
    String homePageUrl;
    String encToken, ssoToken;
    byte[] secId = null;
    //키보드 ime 모드 처리
    OnEditorActionListener urlEditTextListener = new OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            //event 처리
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                webView.loadUrl(urlEditText.getText().toString());
            }

            return false;
        }
    };
    OnClickListener goBtnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            webView.loadUrl(urlEditText.getText().toString());
        }
    };
    OnClickListener refreshBtnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            webView.reload();
        }
    };
    OnClickListener backBtnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            webView.goBack();
        }
    };
    OnClickListener homeBtnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            webView.loadUrl(homePageUrl);
        }
    };
    OnClickListener stopBtnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            webView.stopLoading();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT > 8) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        }

        //타이틀바 보이지 않도록 하기 setContentView 이전에 써야한다.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(Window.FEATURE_NO_TITLE, Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        //토큰 값 확인
        encToken = getIntent().getExtras().getString("ssoToken");
        secId = getIntent().getExtras().getByteArray("secId");

        if (encToken == null || "".equals(encToken)) {
            Toast.makeText(this, "SSO 토큰이 존재하지 않습니다.", Toast.LENGTH_LONG).show();

            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
        }

        MobileSsoAPI mobileSsoAPI = new MobileSsoAPI(getApplicationContext(), PAGE_URL);
        ssoToken = mobileSsoAPI.dec(encToken);

        urlEditText = findViewById(R.id.url_etext);
        goBtn = findViewById(R.id.go_btn);
        refreshBtn = findViewById(R.id.refresh_btn);
        backBtn = findViewById(R.id.back_btn);
        homeBtn = findViewById(R.id.home_btn);
        stopBtn = findViewById(R.id.stop_btn);
        webView = findViewById(R.id.ssoWebView);

        SsoUtil.ssoWebViewInit(webView);

        homePageUrl = urlEditText.getText().toString();
        homePageUrl += "?ssoToken=" + ssoToken;
        homePageUrl += "&secId=" + new String(secId);

        urlEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        urlEditText.setOnEditorActionListener(urlEditTextListener);
        urlEditText.setText(homePageUrl);
        goBtn.setOnClickListener(goBtnClick);
        refreshBtn.setOnClickListener(refreshBtnClick);
        backBtn.setOnClickListener(backBtnClick);
        homeBtn.setOnClickListener(homeBtnClick);
        stopBtn.setOnClickListener(stopBtnClick);

        goBtn.callOnClick();    //이동 버튼 클릭 이벤트
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}