package kr.co.secureon.sso.sample;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
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

import com.sf.msso.MobileSsoAPI;
import com.sf.msso.SsoUtil;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class WebViewActivity extends Activity {
	WebView webView;
	EditText urlEditText;
	Button goBtn, refreshBtn, backBtn, homeBtn, stopBtn, loginActivityBtn;
	String homePageUrl;
	String encToken, ssoToken;
	String secIdFlag;
	byte[] secId = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(Build.VERSION.SDK_INT > 8) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		}
		
		//타이틀바 보이지 않도록 하기 setContentView 이전에 써야한다.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(Window.FEATURE_NO_TITLE, Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		
		//20141128 add smoh - securityID 사용 유무
		secIdFlag = this.getResources().getString(R.string.SEC_ID_FLAG);
		
		//토큰 값 확인
		encToken = getIntent().getExtras().getString("ssoToken");
		
		//20141128 modify smoh - for secIdFlag 체크
		if("TRUE".equalsIgnoreCase(secIdFlag)) {
			secId = getIntent().getExtras().getByteArray("secId");
			Log.d("smoh", getClass().getSimpleName() + ".secId : " + new String(secId));
		}
		
		if(encToken == null || "".equals(encToken)) {
			Log.d("smoh", getClass().getSimpleName() + ".ssoToken is null.");
			Toast.makeText(this, "SSO 토큰이 존재하지 않습니다.", Toast.LENGTH_LONG).show();
			
			Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(loginIntent);
		}
		
		Log.d("smoh", getClass().getSimpleName() + ".encToken : " + encToken);
		MobileSsoAPI mobileSsoAPI = new MobileSsoAPI(getApplicationContext(), getString(R.string.exp_page_url));
		ssoToken = mobileSsoAPI.dec(encToken);
		Log.d("smoh", getClass().getSimpleName() + ".ssoToken : " + ssoToken);
		
		urlEditText = (EditText)findViewById(R.id.url_etext);
		goBtn = (Button)findViewById(R.id.go_btn);
		refreshBtn = (Button)findViewById(R.id.refresh_btn);
		backBtn = (Button) findViewById(R.id.back_btn);
		homeBtn = (Button)findViewById(R.id.home_btn);
		stopBtn = (Button)findViewById(R.id.stop_btn);
		loginActivityBtn = (Button)findViewById(R.id.login_activity_btn);
		webView = (WebView)findViewById(R.id.ssoWebView);
		
		SsoUtil.ssoWebViewInit(webView);
		
		homePageUrl = urlEditText.getText().toString();
		homePageUrl += "?ssoToken=" + ssoToken;
		
		//20141128 modify smoh - for secIdFlag 체크
		if("TRUE".equalsIgnoreCase(secIdFlag)) {
			homePageUrl += "&secId=" + new String(secId);
		}
		
		urlEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
		
		urlEditText.setOnEditorActionListener(urlEditTextListener);
		urlEditText.setText(homePageUrl);
		goBtn.setOnClickListener(goBtnClick);
		refreshBtn.setOnClickListener(refreshBtnClick);
		backBtn.setOnClickListener(backBtnClick);
		homeBtn.setOnClickListener(homeBtnClick);
		stopBtn.setOnClickListener(stopBtnClick);
		loginActivityBtn.setOnClickListener(loginActivityBtnClick);
		
		goBtn.callOnClick();	//이동 버튼 클릭 이벤트
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	//키보드 ime 모드 처리
	OnEditorActionListener urlEditTextListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			//event 처리
			if(actionId == EditorInfo.IME_ACTION_DONE) {
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
	
	OnClickListener homeBtnClick = new OnClickListener(){
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
	
	OnClickListener loginActivityBtnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
			loginIntent.putExtra("ssoToken", ssoToken);
			//20141128 modify smoh - for secIdFlag
			if("TRUE".equalsIgnoreCase(secIdFlag)) {
				loginIntent.putExtra("secId", secId);
			}
			startActivity(loginIntent);
		}
	};
}