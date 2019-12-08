package kr.co.secureon.sso.sample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.sf.msso.MobileSsoAPI;
import com.sf.msso.SsoUtil;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class ApiTestActivity extends Activity {
	SampleVO sampleVO = new SampleVO();
	LinearLayout apiTestLayout;
//	Button putValueBtn, getValueBtn, getAllValuesBtn, userPasswordInitBtn, userModifyPwdBtn, loginActivityBtn;
	RadioGroup radioGroup;
	RadioButton putValueRadioBtn, getValueRadioBtn, getAllValuesRadioBtn;
	RadioButton userPwdInitRadioBtn, userModifyPwdRadioBtn, userSearchRadioBtn;
	Button actionBtn, loginActivityBtn;
	TextView firstText, secondText, resultText;
	EditText firstEditText, secondEditText;
	String secIdFlag;	//secId 사용유무
	byte[] secId = null;
	MobileSsoAPI mobileSsoAPI;
	
	protected void onCreate(Bundle savedInstanceState) {
		if(Build.VERSION.SDK_INT > 8) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		}
		
		//타이틀바 보이지 않도록 하기 setContentView 이전에 써야한다.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(Window.FEATURE_NO_TITLE, Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_api_test);
		
		secIdFlag = this.getResources().getString(R.string.SEC_ID_FLAG);
		
		if("TRUE".equalsIgnoreCase(secIdFlag)) {
			secId = SsoUtil.getSecId(getApplicationContext());
		}
		
		mobileSsoAPI = new MobileSsoAPI(getApplicationContext(), getString(R.string.exp_page_url));
		Log.d("smoh",  "gettoken : " + mobileSsoAPI.getToken());
		if(mobileSsoAPI.getToken() == null || "".equals(mobileSsoAPI.getToken())) {
			Toast.makeText(getApplicationContext(), "로그인 하여야 사용 가능합니다.", Toast.LENGTH_LONG).show();
			Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(loginIntent);
		}
		
		apiTestLayout = (LinearLayout)findViewById(R.id.apiTestLayout);
		
		radioGroup = (RadioGroup)findViewById(R.id.radioGroup1);
		putValueRadioBtn = (RadioButton)findViewById(R.id.putValueRadioBtn);
		getValueRadioBtn = (RadioButton)findViewById(R.id.getValueRadioBtn);
		getAllValuesRadioBtn = (RadioButton)findViewById(R.id.getAllValuesRadioBtn);
		userPwdInitRadioBtn = (RadioButton)findViewById(R.id.userPwdInitRadioBtn);
		userModifyPwdRadioBtn = (RadioButton)findViewById(R.id.userModifyPwdRadioBtn);
		userSearchRadioBtn = (RadioButton)findViewById(R.id.userSearchRadioBtn);
		
		actionBtn = (Button)findViewById(R.id.actionBtn);
		loginActivityBtn = (Button)findViewById(R.id.loginActivityBtn);
		
		firstText = (TextView)findViewById(R.id.firstText);
		secondText = (TextView)findViewById(R.id.secondText);
		
		firstEditText = (EditText)findViewById(R.id.firstEditText);
		secondEditText = (EditText)findViewById(R.id.secondEditText);
		
		resultText = (TextView)findViewById(R.id.resultText);
		resultText.setEnabled(false);	//textview 수정 안되도록 수정
		
		apiTestLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				keyPadHide();
			}
		});
		
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				switch(checkedId) {
					case R.id.putValueRadioBtn:
						firstText.setText("태그명");
						secondText.setText("태그값");
						break;
					case R.id.getValueRadioBtn:
						firstText.setText("태그명");
						secondText.setText("index");
						break;
					case R.id.getAllValuesRadioBtn:
						firstText.setText("사용안함");
						secondText.setText("사용안함");
						break;
					case R.id.userPwdInitRadioBtn:
						firstText.setText("사용자 ID");
						secondText.setText("패스워드");
						secondEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
						break;
					case R.id.userModifyPwdRadioBtn:
						firstText.setText("기존 패스워드");
						secondText.setText("신규패스워드");
						firstEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
						secondEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
						break;
					case R.id.userSearchRadioBtn:
						firstText.setText("사용자 ID");
						secondText.setText("사용안함");
						break;
					default:
						break;
				}
				
			}
		});
		
		actionBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(putValueRadioBtn.isChecked()) {
					putValueAction();
				} else if(getValueRadioBtn.isChecked()) {
					getValueAction();
				} else if(getAllValuesRadioBtn.isChecked()) {
					getAllValuesAction();
				} else if(userPwdInitRadioBtn.isChecked()) {
					userPwdInitAction();
				} else if(userModifyPwdRadioBtn.isChecked()) {
					userModifyPwdAction();
				} else if(userSearchRadioBtn.isChecked()) {
					userSearchAction();
				} else {
					Toast.makeText(getApplicationContext(), "라디오버튼을 선택하세요", Toast.LENGTH_SHORT).show();
					return;
				}
				
				keyPadHide();
				editTextClean();
				radioBtnClean();
			}
		});
		
		loginActivityBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
				loginIntent.putExtra("ssoToken", mobileSsoAPI.getToken());
				//20141128 modify smoh - for secIdFlag
				if("TRUE".equalsIgnoreCase(secIdFlag)) {
					loginIntent.putExtra("secId", secId);
				}
				startActivity(loginIntent);
			}
			
		});
	}
	
	//putValue()
	private void putValueAction() {
		String tagName, tagValue;
		tagName = SsoUtil.checkNull(firstEditText.getText().toString());
		tagValue = SsoUtil.checkNull(secondEditText.getText().toString());
		
		checkEditText(tagName, firstEditText, "태그명");
		checkEditText(tagValue, secondEditText, "태그값");
		
		String ret = mobileSsoAPI.andrsso_putValue(tagName, tagValue);
		Log.d("smoh", "putValue result : " + ret);
		
		resultText.setText("SSO PutValue 결과 : " + ret);
	}
	
	private void getValueAction() {
		String tagName, index;
		tagName = SsoUtil.checkNull(firstEditText.getText().toString());
		index = SsoUtil.checkNull(secondEditText.getText().toString());
		
		checkEditText(tagName, firstEditText, "태그명");
		
		if("".equals(index)) {
			index = "0";
		} else {
			if(!SsoUtil.isNumber(index)) {
				Toast.makeText(getApplicationContext(), "index에는 숫자만 입력가능합니다.", Toast.LENGTH_SHORT).show();
				secondEditText.setText("");
				secondEditText.requestFocus();
				return;
			}
		}
		
		String ret = "";
		if("TRUE".equalsIgnoreCase(secIdFlag)) {
			ret = mobileSsoAPI.andrsso_getValue(tagName, Integer.parseInt(index), mobileSsoAPI.getToken(), sampleVO.getClientIp(), secId);
		}else {
			ret = mobileSsoAPI.andrsso_getValue(tagName, Integer.parseInt(index), mobileSsoAPI.getToken(), sampleVO.getClientIp(), null);
		}
		Log.d("smoh", "getValue result : " + ret);
		
		resultText.setText("SSO GetValue 결과 : " + ret);
	}
	
	private void getAllValuesAction() {
		String ret = "";
		
		if("TRUE".equalsIgnoreCase(secIdFlag)) {
			ret = mobileSsoAPI.andrsso_getAllValues(mobileSsoAPI.getToken(), sampleVO.getClientIp(), secId);
		} else {
			ret = mobileSsoAPI.andrsso_getAllValues(mobileSsoAPI.getToken(), sampleVO.getClientIp(), secId);
		}
		
		Log.d("smoh", "getAllValues : " + ret);
		
		resultText.setText("SSO GetAllValues 결과 : " + ret);
	}
	
	private void userPwdInitAction() {
		String userId, userPwd;
		userId = SsoUtil.checkNull(firstEditText.getText().toString());
		userPwd = SsoUtil.checkNull(secondEditText.getText().toString());
		int ret = -1;
		
		checkEditText(userId, firstEditText, "사용자 ID");
		checkEditText(userPwd, secondEditText, "사용자 패스워드");
		
		ret = mobileSsoAPI.andrsso_userPwdInit(userId, userPwd, 0, sampleVO.getClientIp());
		Log.d("smoh", "userPasswordInit ret : " + ret);
		resultText.setText("SSO UserPasswordInit 결과 : " + ret);
	}
	
	private void userModifyPwdAction() {
		String currentPwd, newPwd;
		currentPwd = SsoUtil.checkNull(firstEditText.getText().toString());
		newPwd = SsoUtil.checkNull(secondEditText.getText().toString());
		int ret = -1;
		
		checkEditText(currentPwd, firstEditText, "현재 패스워드");
		checkEditText(newPwd, secondEditText, "새로운 패스워드");
		
		ret = mobileSsoAPI.andrsso_userModifyPwd(mobileSsoAPI.getToken(), currentPwd, newPwd, sampleVO.getClientIp());
		Log.d("smoh", "userModifyPwd ret : " + ret);
		resultText.setText("SSO UserModfiyPwd 결과 : " + ret);
	}
	
	private void userSearchAction() {
		String userId = SsoUtil.checkNull(firstEditText.getText().toString());
		int ret = -1;
		
		checkEditText(userId, firstEditText, "사용자 ID");
		
		ret = mobileSsoAPI.andrsso_userSearch(userId);
		Log.d("smoh", "userSearch ret : " + ret);
		resultText.setText("SSO userSearch 결과 : " + ret);
	}
	
	private void keyPadHide() {
		//키패드 내리기
		InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		mInputMethodManager.hideSoftInputFromWindow(firstEditText.getWindowToken(), 0);
		mInputMethodManager.hideSoftInputFromWindow(secondEditText.getWindowToken(), 0);
	}
	
	private void editTextClean() {
		firstEditText.setText("");
		secondEditText.setText("");
		//평문으로 입력되도록 변경
		firstEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
		secondEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
	}
	
	private void radioBtnClean() {
		radioGroup.clearCheck();
	}
	
	private void checkEditText(String editStr, EditText editText, String words) {
		if("".equals(editStr)) {
			Toast.makeText(getApplicationContext(), words + "를 입력하세요", Toast.LENGTH_SHORT).show();
			editText.requestFocus();
			return;
		}
	}
}
