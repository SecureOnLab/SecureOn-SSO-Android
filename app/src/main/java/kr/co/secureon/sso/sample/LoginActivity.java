package kr.co.secureon.sso.sample;

import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sf.msso.MobileSsoAPI;
import com.sf.msso.SsoUtil;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class LoginActivity extends Activity {
	SampleVO sampleVO = new SampleVO();
	
	LinearLayout loginLayout;
	Button entLoginBtn, logoutBtn, webViewActivityBtn, mobileSSOBAppBtn, stdLoginBtn, expLoginBtn, listViewBtn;
	
	TextView resultText;
	MobileSsoAPI mobileSsoAPI;
	String encSsoToken;
	String secIdFlag;	//secId 사용유무
	byte[] secId = null;
	EditText userIdEditText, userPwdEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("smoh", getClass().getSimpleName() + ".exp_page_url : " + String.valueOf(getString(R.string.exp_page_url)));
		sampleVO.setPageURL(getString(R.string.exp_page_url));
		sampleVO.setClientIp("127.0.0.1");
		
		if(Build.VERSION.SDK_INT > 8) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		}

		setContentView(R.layout.activity_login);
		
		//20141128 add smoh - securityID 사용 유무
		secIdFlag = this.getResources().getString(R.string.SEC_ID_FLAG);
		if("TRUE".equalsIgnoreCase(secIdFlag)) {
			secId = SsoUtil.getSecId(this.getApplicationContext());
			if(secId != null) {
				Log.d("smoh", getClass().getSimpleName() + ".secId : " + new String(secId));
				sampleVO.setSecId(secId);
			}
		}
		
		mobileSsoAPI = new MobileSsoAPI(this, sampleVO.getPageURL());
		
		loginLayout = (LinearLayout)findViewById(R.id.loginLayout);
		
		userIdEditText = (EditText)findViewById(R.id.userId);
		userPwdEditText = (EditText)findViewById(R.id.userPwd);
		entLoginBtn = (Button)findViewById(R.id.entLoginBtn);
		logoutBtn = (Button)findViewById(R.id.logoutBtn);
		stdLoginBtn = (Button) findViewById(R.id.stdLoginBtn);
		expLoginBtn = (Button)findViewById(R.id.expLoginBtn);
		webViewActivityBtn = (Button)findViewById(R.id.webViewActivityBtn);
		mobileSSOBAppBtn = (Button)findViewById(R.id.mobileSSOBAppBtn);
		listViewBtn = (Button)findViewById(R.id.listViewBtn);
		
		resultText = (TextView)findViewById(R.id.resultText);
		resultText.setEnabled(false);	//textview 수정 안되도록 수정
		logoutBtn.setVisibility(View.INVISIBLE);
		
		if(getIntent() != null) {
			//웹에서 온경우
			Uri uri = getIntent().getData();
			if(uri != null) {
				Log.d("smoh",  getClass().getSimpleName() + ".uri : " + uri.toString());
				String ssoToken = uri.getQueryParameter("ssoToken");
				
				if(ssoToken != null) {
					Log.d("smoh", getClass().getSimpleName() + ".ssoToken : " + uri.getQueryParameter("ssoToken"));
					sampleVO.setToken(uri.getQueryParameter("ssoToken"));
				}
				
			}
			
			//웹뷰 액티비티에서 온 경우 또는 다른 앱에서 온 경우
			if(getIntent().getStringExtra("ssoToken") != null) {
				Log.d("smoh", getClass().getSimpleName() + ".ssoToken : " + getIntent().getStringExtra("ssoToken"));
				
				if(!"".equals(getIntent().getStringExtra("ssoToken"))) {
					Log.d("smoh", getClass().getSimpleName() + " - ssoToken : " + getIntent().getStringExtra("ssoToken"));
					sampleVO.setToken(getIntent().getStringExtra("ssoToken"));
				}
				
				if(getIntent().getByteArrayExtra("secId") != null) {
					Log.d("smoh", getClass().getSimpleName() + ".secId1 : " + new String(getIntent().getByteArrayExtra("secId")));
					sampleVO.setSecId(getIntent().getByteArrayExtra("secId"));
				}
//				20141128 modify smoh
//				else {
//					Log.d("smoh", getClass().getSimpleName() + ".secId2 : " + new String(secId));
//					sampleVO.setSecId(secId);
//				}
			}
		}
		
		//networkCheck - 0 : wifi	1 : mobile	-1 not connect
		int networkCheck = SsoUtil.getConnectivityStatus(this);
		Log.d("smoh", getClass().getSimpleName() + ".networkCheck : " + networkCheck);
		
		if(networkCheck == -1) {
			Toast.makeText(this, "네트워크 연결 실패", Toast.LENGTH_LONG).show();
		} else {
			try {
				sampleVO.setTokenKey(mobileSsoAPI.andrsso_init());
				
				if(mobileSsoAPI.getLastHttpErrorCode() == 200) {
					sampleVO.setSsoErrorCode(mobileSsoAPI.getLastSSOErrorCode());
					if(sampleVO.getSsoErrorCode() >= 0) {
						if(sampleVO.getToken() == null || "".equals(sampleVO.getToken())) {
							resultText.setText("사용자 토큰이 없습니다. SSO 오류 코드 : " + sampleVO.getSsoErrorCode());
							mobileSsoAPI.deleteToken();
						} else {
							Log.d("smoh", getClass().getSimpleName() + ".ssoToken : " + sampleVO.getToken());
							Log.d("smoh", getClass().getSimpleName() + ".getClientIp : " + sampleVO.getClientIp());
							
							if("TRUE".equalsIgnoreCase(secIdFlag)) {
								Log.d("smoh", getClass().getSimpleName() + ".getSecId : " + new String(sampleVO.getSecId()));
							}
							
							String user = "";
							//20141128 modify smoh - secIdFlag 추가
							if("TRUE".equalsIgnoreCase(secIdFlag)) {
								user = mobileSsoAPI.andrsso_verifyToken(sampleVO.getToken(), sampleVO.getClientIp(), sampleVO.getSecId());
							} else {
								user = mobileSsoAPI.andrsso_verifyToken(sampleVO.getToken(), sampleVO.getClientIp(), null);
							}
							
							if(user == null) {
								Log.d("smoh", getClass().getSimpleName() + ".getLastSSOErrorCode : " + String.valueOf(mobileSsoAPI.getLastSSOErrorCode()));
								mobileSsoAPI.deleteToken();
							}
							
							if(mobileSsoAPI.getLastHttpErrorCode() == 200) {
								sampleVO.setSsoErrorCode(mobileSsoAPI.getLastSSOErrorCode());
								
								if(sampleVO.getSsoErrorCode() >= 0) {
									resultText.setText(user + "님이 로그인 하셨습니다..");
									if(mobileSsoAPI.getToken() == null || "".equals(mobileSsoAPI.getToken())) {
										mobileSsoAPI.setToken(user, sampleVO.getToken());
									}
									
									entLoginBtn.setVisibility(View.INVISIBLE);
									stdLoginBtn.setVisibility(View.INVISIBLE);
									expLoginBtn.setVisibility(View.INVISIBLE);
									logoutBtn.setVisibility(View.VISIBLE);
								} else {
									resultText.setText("SSO 에러 코드 : " + sampleVO.getSsoErrorCode());
								}
							} else {
								resultText.setText("HTTP 오류 코드 : " + mobileSsoAPI.getLastHttpErrorCode());
							}
						}
					}
				} else {
					resultText.setText("HTTP 오류 코드 : " + mobileSsoAPI.getLastHttpErrorCode());
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//화면 클릭 시 키패드 숨기기 이벤트
			loginLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					keyPadHide();
				}
			});
			
			//id 이벤트 에서 키 입력 리스너
			userIdEditText.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if(keyCode == KeyEvent.KEYCODE_ENTER && KeyEvent.ACTION_DOWN == event.getAction()) {
						userPwdEditText.requestFocus();
						return true;
					}
					return false;
				}
			});
			
			//pwd 이벤트에서 키 입력리스너
			userPwdEditText.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if(keyCode == KeyEvent.KEYCODE_ENTER && KeyEvent.ACTION_DOWN == event.getAction()) {
						entLoginBtn.callOnClick();
						return true;
					}
					return false;
				}
			});
			
			//로그인 버튼 클릭 이벤트
			entLoginBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						String userId, userPwd;
						userId = SsoUtil.checkNull(userIdEditText.getText().toString());
						userPwd = SsoUtil.checkNull(userPwdEditText.getText().toString());
						
						if("".equals(userId)) {
							Toast.makeText(getApplicationContext(), "사용자 아이디를 입력바랍니다.", Toast.LENGTH_SHORT).show();
							userIdEditText.requestFocus();	//아이디 입력란에 포커스 주기
							return;
						}
						
						if("".equals(userPwd)) {
							Toast.makeText(getApplicationContext(), "사용자 패스워드를 입력바랍니다.", Toast.LENGTH_SHORT).show();
							userPwdEditText.requestFocus();
							return;
						}
						
						sampleVO.setUserId(userId);
						sampleVO.setUserPwd(userPwd);
						
						Log.d("smoh", getClass().getSimpleName() + ".userId : " + sampleVO.getUserId());
						Log.d("smoh", getClass().getSimpleName() + ".userPwd : " + sampleVO.getUserPwd());
						Log.d("smoh", getClass().getSimpleName() + ".clientIP : " + sampleVO.getClientIp());
						
						//20141128 modify smoh - for secIdFlag 추가
						if("TRUE".equalsIgnoreCase(secIdFlag)) {
							Log.d("smoh", getClass().getSimpleName() + ".secId : " + new String(sampleVO.getSecId()));
							sampleVO.setToken(mobileSsoAPI.andrsso_authID(sampleVO.getUserId(), sampleVO.getUserPwd(), "true", sampleVO.getClientIp(), sampleVO.getSecId()));
						} else {
							Log.d("smoh", getClass().getSimpleName() + ".secIdFlag NO.");
							sampleVO.setToken(mobileSsoAPI.andrsso_authID(sampleVO.getUserId(), sampleVO.getUserPwd(), "true", sampleVO.getClientIp(), null));
						}
						
						if(mobileSsoAPI.getLastHttpErrorCode() == 200) {
							sampleVO.setSsoErrorCode(mobileSsoAPI.getLastSSOErrorCode());
							
							if(sampleVO.getSsoErrorCode() >= 0) {
								if(sampleVO.getToken() != null) {
									Log.d("smoh", getClass().getSimpleName() + ".token : " + sampleVO.getToken());
									//기존 DB에 값이 있나 확인 및 삭제
									mobileSsoAPI.deleteToken();
									mobileSsoAPI.setToken(sampleVO.getUserId().toString(), sampleVO.getToken());
									resultText.setText(sampleVO.getUserId().toString() + "님이 로그인 하였습니다.");
									entLoginBtn.setVisibility(View.INVISIBLE);
									stdLoginBtn.setVisibility(View.INVISIBLE);
									expLoginBtn.setVisibility(View.INVISIBLE);
									logoutBtn.setVisibility(View.VISIBLE);
								}
							} else {
								resultText.setText("SSO 에러 코드 : " + sampleVO.getSsoErrorCode());
							}
						} else {
							resultText.setText("HTTP 오류 코드 : " + mobileSsoAPI.getLastHttpErrorCode());
						}
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						keyPadHide();
						editTextClean();
					}
				}
			});
			
			//로그아웃 버튼 클릭 이벤트
			logoutBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mobileSsoAPI.getToken() == null || "".equals(mobileSsoAPI.getToken())) {
						Toast.makeText(getApplicationContext(), "SSO 토큰이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
					} else {
						try {
							mobileSsoAPI.andrsso_unregUserSession(mobileSsoAPI.getToken(), sampleVO.getClientIp());
							
							if(mobileSsoAPI.deleteToken() == 0) {
								resultText.setText("로그아웃 되었습니다.");
								entLoginBtn.setVisibility(View.VISIBLE);
								stdLoginBtn.setVisibility(View.VISIBLE);
								expLoginBtn.setVisibility(View.VISIBLE);
								logoutBtn.setVisibility(View.INVISIBLE);
							} else {
								Log.d("smoh", getClass().getSimpleName() + ".deleteToken : " + String.valueOf(mobileSsoAPI.deleteToken()));
								resultText.setText("로그아웃에 실패하였습니다.");
							}
						} catch (IllegalStateException e) {
							Log.d("smoh", e.getMessage());
						} catch (IOException e) {
							Log.d("smoh", e.getMessage());
						}
					}
				}
			});
			
			//웹뷰 버튼 클릭 이벤트
			webViewActivityBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent webViewIntent = new Intent(getApplicationContext(), WebViewActivity.class);
					
					//ssoToken 평문을 암호화하여 보낸다.
					encSsoToken = mobileSsoAPI.enc(mobileSsoAPI.getToken());
					Log.d("smoh", getClass().getSimpleName() + ".encSsoToken : " + encSsoToken);
					webViewIntent.putExtra("ssoToken", encSsoToken);
					//20141128 modify smoh - for secIdFlag 추가
					if("TRUE".equalsIgnoreCase(secIdFlag)) {
						Log.d("smoh", getClass().getSimpleName() + ".secId : " + new String(sampleVO.getSecId()));
						webViewIntent.putExtra("secId", sampleVO.getSecId());
					}
					startActivity(webViewIntent);
				}
			});
			
			//20141128 add smoh - for regUserSession() 추가
			//standard login 버튼 클릭 이벤트
			stdLoginBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						String userId = SsoUtil.checkNull(userIdEditText.getText().toString());
						
						if("".equals(userId)) {
							Toast.makeText(getApplicationContext(), "사용자 아이디를 입력바랍니다.", Toast.LENGTH_SHORT).show();
							userIdEditText.requestFocus();	//아이디 입력란에 포커스 주기
							return;
						}
						
						sampleVO.setUserId(userId);
						
						Log.d("smoh", getClass().getSimpleName() + ".userId : " + sampleVO.getUserId());
						Log.d("smoh", getClass().getSimpleName() + ".clientIP : " + sampleVO.getClientIp());
						
						if("TRUE".equalsIgnoreCase(secIdFlag)) {
							Log.d("smoh", getClass().getSimpleName() + ".secId : " + new String(sampleVO.getSecId()));
							sampleVO.setToken(mobileSsoAPI.andrsso_regUserSession(sampleVO.getUserId(), sampleVO.getClientIp(), "true", secId));
							
						}else {
							Log.d("smoh", getClass().getSimpleName() + ".secIdFlag NO.");
							sampleVO.setToken(mobileSsoAPI.andrsso_regUserSession(sampleVO.getUserId(), sampleVO.getClientIp(), "true", null));
						}
						
						if(mobileSsoAPI.getLastHttpErrorCode() == 200) {
							sampleVO.setSsoErrorCode(mobileSsoAPI.getLastSSOErrorCode());
							
							if(sampleVO.getSsoErrorCode() >= 0) {
								if(sampleVO.getToken() != null) {
									Log.d("smoh", getClass().getSimpleName() + ".token : " + sampleVO.getToken());
									//기존 DB에 값이 있나 확인 및 삭제
									mobileSsoAPI.deleteToken();
									mobileSsoAPI.setToken(sampleVO.getUserId().toString(), sampleVO.getToken());
									resultText.setText(sampleVO.getUserId().toString() + "님이 로그인 하였습니다.");
									entLoginBtn.setVisibility(View.INVISIBLE);
									stdLoginBtn.setVisibility(View.INVISIBLE);
									expLoginBtn.setVisibility(View.INVISIBLE);
									logoutBtn.setVisibility(View.VISIBLE);
								}
							} else {
								resultText.setText("SSO 에러 코드 : " + sampleVO.getSsoErrorCode());
							}
						} else {
							resultText.setText("HTTP 오류 코드 : " + mobileSsoAPI.getLastHttpErrorCode());
						}
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} finally {
						keyPadHide();
						editTextClean();
					}
				}
			});
			
			//expree login 버튼 클릭 이벤트
			expLoginBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String userId = SsoUtil.checkNull(userIdEditText.getText().toString());
					
					if("".equals(userId)) {
						Toast.makeText(getApplicationContext(), "사용자 아이디를 입력 바랍니다.", Toast.LENGTH_SHORT).show();
						userIdEditText.requestFocus();
						return;
					}
					
					sampleVO.setUserId(userId);
					
					if("TRUE".equalsIgnoreCase(secIdFlag)) {
						Log.d("smoh", getClass().getSimpleName() + ".secId : " + new String(sampleVO.getSecId()));
						sampleVO.setToken(mobileSsoAPI.andrsso_makeSimpleToken("3", userId, sampleVO.getClientIp(), secId));
					}else {
						Log.d("smoh", getClass().getSimpleName() + ".secIdFlag NO.");
						sampleVO.setToken(mobileSsoAPI.andrsso_makeSimpleToken("3", userId, sampleVO.getClientIp(), null));
					}
					
					if(mobileSsoAPI.getLastHttpErrorCode() == 200) {
						sampleVO.setSsoErrorCode(mobileSsoAPI.getLastSSOErrorCode());
						
						if(sampleVO.getSsoErrorCode() >= 0) {
							if(sampleVO.getToken() != null) {
								Log.d("smoh", getClass().getSimpleName() + ".token : " + sampleVO.getToken());
								//기존 DB에 값이 있나 확인 및 삭제
								mobileSsoAPI.deleteToken();
								mobileSsoAPI.setToken(sampleVO.getUserId().toString(), sampleVO.getToken());
								resultText.setText(sampleVO.getUserId().toString() + "님이 로그인 하였습니다.");
								entLoginBtn.setVisibility(View.INVISIBLE);
								stdLoginBtn.setVisibility(View.INVISIBLE);
								expLoginBtn.setVisibility(View.INVISIBLE);
								logoutBtn.setVisibility(View.VISIBLE);
							}
						} else {
							resultText.setText("SSO 에러 코드 : " + sampleVO.getSsoErrorCode());
						}
					} else {
						resultText.setText("HTTP 오류 코드 : " + mobileSsoAPI.getLastHttpErrorCode());
					}
				}
			});
			
			//mobileSSOB 호출 버튼 클릭 이벤트
			mobileSSOBAppBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent otherIntent = getPackageManager().getLaunchIntentForPackage("com.softforum.mssosample2");
					
					otherIntent.putExtra("ssoToken", mobileSsoAPI.getToken());
					startActivity(otherIntent);
				}
			});
			
			listViewBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent listViewIntent = new Intent(getApplicationContext(), ListViewActivity.class);
					startActivity(listViewIntent);
				}
			});
		}
	}
	
	private void keyPadHide() {
		//키패드 내리기
		InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		mInputMethodManager.hideSoftInputFromWindow(userIdEditText.getWindowToken(), 0);
		mInputMethodManager.hideSoftInputFromWindow(userPwdEditText.getWindowToken(), 0);
	}
	
	private void editTextClean() {
		userIdEditText.setText("");
		userPwdEditText.setText("");
	}
}