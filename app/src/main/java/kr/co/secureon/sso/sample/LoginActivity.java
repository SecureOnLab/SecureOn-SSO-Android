package kr.co.secureon.sso.sample;

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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sf.msso.MobileSsoAPI;
import com.sf.msso.SsoUtil;

import java.io.IOException;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";

    public static final String CLIENT_IP = "127.0.0.1";
    public static final String PAGE_URL = "http://192.168.1.236:8080/android/exp_mobilesso.jsp";
//    public static final String PAGE_URL = "http://192.168.2.2:8080/android/exp_mobilesso.jsp";

    ScrollView scrollView;
    EditText userIdEditText;
    EditText userPwdEditText;
    Button entLoginBtn;
    Button stdLoginBtn;
    Button expLoginBtn;
    TextView resultText;

    MobileSsoAPI mobileSsoAPI;
    byte[] securityId;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT > 8) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        }

        setContentView(R.layout.activity_login);

        securityId = SsoUtil.getSecId(this);
        mobileSsoAPI = new MobileSsoAPI(this, PAGE_URL);

        scrollView = findViewById(R.id.scrollView);

        userIdEditText = findViewById(R.id.userId);
        userPwdEditText = findViewById(R.id.userPwd);
        entLoginBtn = findViewById(R.id.entLoginBtn);
        stdLoginBtn = findViewById(R.id.stdLoginBtn);
        expLoginBtn = findViewById(R.id.expLoginBtn);

        resultText = findViewById(R.id.resultText);
        resultText.setEnabled(false);    //textview 수정 안되도록 수정

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);

                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


        if (getIntent() != null) {
            //웹에서 온경우
            Uri uri = getIntent().getData();
            if (uri != null) {
                String ssoToken = uri.getQueryParameter("ssoToken");
                if (ssoToken != null) {
                    token = uri.getQueryParameter("ssoToken");
                }
            }

            //웹뷰 액티비티에서 온 경우 또는 다른 앱에서 온 경우
            if (getIntent().getStringExtra("ssoToken") != null) {
                if (!"".equals(getIntent().getStringExtra("ssoToken"))) {
                    token = getIntent().getStringExtra("ssoToken");
                }
                if (getIntent().getByteArrayExtra("secId") != null) {
                    securityId = getIntent().getByteArrayExtra("secId");
                }
            }
        }

        //networkCheck - 0 : wifi	1 : mobile	-1 not connect
        int networkCheck = SsoUtil.getConnectivityStatus(this);

        if (networkCheck == -1) {
            Toast.makeText(this, "네트워크 연결 실패", Toast.LENGTH_LONG).show();
        } else {
            try {
                if (mobileSsoAPI.getLastHttpErrorCode() == 200) {
                    int errorCode = mobileSsoAPI.getLastSSOErrorCode();
                    if (errorCode >= 0) {
                        if (token == null || "".equals(token)) {
                            resultText.setText("사용자 토큰이 없습니다. SSO 오류 코드 : " + errorCode);
                            mobileSsoAPI.deleteToken();
                        } else {

                            String user = mobileSsoAPI.andrsso_verifyToken(token, CLIENT_IP, securityId);

                            if (user == null) {
                                mobileSsoAPI.deleteToken();
                            }

                            if (mobileSsoAPI.getLastHttpErrorCode() == 200) {
                                errorCode = mobileSsoAPI.getLastSSOErrorCode();

                                if (errorCode >= 0) {
                                    resultText.setText(user + "님이 로그인 하셨습니다..");
                                    if (mobileSsoAPI.getToken() == null || "".equals(mobileSsoAPI.getToken())) {
                                        mobileSsoAPI.setToken(user, token);
                                    }
                                } else {
                                    resultText.setText("SSO 에러 코드 : " + errorCode);
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

            //id 이벤트 에서 키 입력 리스너
            userIdEditText.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER && KeyEvent.ACTION_DOWN == event.getAction()) {
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
                    if (keyCode == KeyEvent.KEYCODE_ENTER && KeyEvent.ACTION_DOWN == event.getAction()) {
                        entLoginBtn.callOnClick();
                        return true;
                    }
                    return false;
                }
            });

            // 엔터프라이즈 로그인 버튼 클릭 이벤트
            entLoginBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String userId = userIdEditText.getText().toString();
                        String userPwd = userPwdEditText.getText().toString();

                        if ("".equals(userId)) {
                            Toast.makeText(getApplicationContext(), "사용자 아이디를 입력바랍니다.", Toast.LENGTH_SHORT).show();
                            userIdEditText.requestFocus();    //아이디 입력란에 포커스 주기
                            return;
                        }

                        if ("".equals(userPwd)) {
                            Toast.makeText(getApplicationContext(), "사용자 패스워드를 입력바랍니다.", Toast.LENGTH_SHORT).show();
                            userPwdEditText.requestFocus();
                            return;
                        }

                        token = mobileSsoAPI.andrsso_authID(userId, userPwd,"true", CLIENT_IP, securityId);

                        if (mobileSsoAPI.getLastHttpErrorCode() == 200) {
                            int ssoErrorCode = mobileSsoAPI.getLastSSOErrorCode();
                            if (ssoErrorCode >= 0) {

                                if (token == null) {
                                    return;
                                }
                                //기존 DB에 값이 있나 확인 및 삭제
                                mobileSsoAPI.deleteToken();
                                mobileSsoAPI.setToken(userId, token);
                                resultText.setText(userId + "님이 로그인 하였습니다.");

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                resultText.setText("SSO 에러 코드 : " + ssoErrorCode);
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
                    }
                }
            });

            //standard login 버튼 클릭 이벤트
            stdLoginBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String userId = SsoUtil.checkNull(userIdEditText.getText().toString());

                        if ("".equals(userId)) {
                            Toast.makeText(getApplicationContext(), "사용자 아이디를 입력바랍니다.", Toast.LENGTH_SHORT).show();
                            userIdEditText.requestFocus();    //아이디 입력란에 포커스 주기
                            return;
                        }

                        token = mobileSsoAPI.andrsso_regUserSession(userId, CLIENT_IP,"true", securityId);

                        if (mobileSsoAPI.getLastHttpErrorCode() == 200) {
                            int ssoErrorCode = mobileSsoAPI.getLastSSOErrorCode();
                            if (ssoErrorCode >= 0) {
                                if (token != null) {
                                    //기존 DB에 값이 있나 확인 및 삭제
                                    mobileSsoAPI.deleteToken();
                                    mobileSsoAPI.setToken(userId, token);
                                    resultText.setText(userId + "님이 로그인 하였습니다.");

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            } else {
                                resultText.setText("SSO 에러 코드 : " + ssoErrorCode);
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

                    if ("".equals(userId)) {
                        Toast.makeText(getApplicationContext(), "사용자 아이디를 입력 바랍니다.", Toast.LENGTH_SHORT).show();
                        userIdEditText.requestFocus();
                        return;
                    }

                    token = mobileSsoAPI.andrsso_makeSimpleToken("3", userId, CLIENT_IP, securityId);

                    if (mobileSsoAPI.getLastHttpErrorCode() == 200) {
                        int ssoErrorCode = mobileSsoAPI.getLastSSOErrorCode();
                        if (ssoErrorCode >= 0) {
                            if (token != null) {
                                //기존 DB에 값이 있나 확인 및 삭제
                                mobileSsoAPI.deleteToken();
                                mobileSsoAPI.setToken(userId, token);
                                resultText.setText(userId + "님이 로그인 하였습니다.");

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            resultText.setText("SSO 에러 코드 : " + ssoErrorCode);
                        }
                    } else {
                        resultText.setText("HTTP 오류 코드 : " + mobileSsoAPI.getLastHttpErrorCode());
                    }
                }
            });


        }
    }

    private void keyPadHide() {
        //키패드 내리기
        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(userIdEditText.getWindowToken(), 0);
        mInputMethodManager.hideSoftInputFromWindow(userPwdEditText.getWindowToken(), 0);
    }

    private void editTextClean() {
        userIdEditText.setText("");
        userPwdEditText.setText("");
    }

}