package kr.co.secureon.sso.sample;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sf.msso.MobileSsoAPI;
import com.sf.msso.SsoUtil;

import static kr.co.secureon.sso.sample.LoginActivity.CLIENT_IP;
import static kr.co.secureon.sso.sample.LoginActivity.PAGE_URL;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class ApiTestActivity extends AppCompatActivity {

    LinearLayout apiTestLayout;
    RadioGroup radioGroup;
    RadioButton putValueRadioBtn;
    RadioButton getValueRadioBtn;
    RadioButton getAllValuesRadioBtn;
    RadioButton userPwdInitRadioBtn;
    RadioButton userModifyPwdRadioBtn;
    RadioButton userSearchRadioBtn;
    Button actionBtn;
    Button loginActivityBtn;
    TextView firstText;
    TextView secondText;
    TextView resultText;
    EditText firstEditText;
    EditText secondEditText;

    byte[] secId = null;
    MobileSsoAPI mobileSsoAPI;

    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT > 8) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_test);
        secId = SsoUtil.getSecId(this);

        mobileSsoAPI = new MobileSsoAPI(getApplicationContext(), PAGE_URL);
        if (mobileSsoAPI.getToken() == null || "".equals(mobileSsoAPI.getToken())) {
            Toast.makeText(getApplicationContext(), "로그인 하여야 사용 가능합니다.", Toast.LENGTH_LONG).show();
            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginIntent);
        }

        apiTestLayout = findViewById(R.id.apiTestLayout);

        radioGroup = findViewById(R.id.radioGroup1);
        putValueRadioBtn = findViewById(R.id.putValueRadioBtn);
        getValueRadioBtn = findViewById(R.id.getValueRadioBtn);
        getAllValuesRadioBtn = findViewById(R.id.getAllValuesRadioBtn);
        userPwdInitRadioBtn = findViewById(R.id.userPwdInitRadioBtn);
        userModifyPwdRadioBtn = findViewById(R.id.userModifyPwdRadioBtn);
        userSearchRadioBtn = findViewById(R.id.userSearchRadioBtn);

        actionBtn = findViewById(R.id.actionBtn);
        loginActivityBtn= findViewById(R.id.loginActivityBtn);

        firstText = findViewById(R.id.firstText);
        secondText = findViewById(R.id.secondText);

        firstEditText = findViewById(R.id.firstEditText);
        secondEditText = findViewById(R.id.secondEditText);

        resultText = findViewById(R.id.resultText);
        resultText.setEnabled(false);    //textview 수정 안되도록 수정

        apiTestLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyPadHide();
            }
        });

        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
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
                if (putValueRadioBtn.isChecked()) {
                    putValueAction();
                } else if (getValueRadioBtn.isChecked()) {
                    getValueAction();
                } else if (getAllValuesRadioBtn.isChecked()) {
                    getAllValuesAction();
                } else if (userPwdInitRadioBtn.isChecked()) {
                    userPwdInitAction();
                } else if (userModifyPwdRadioBtn.isChecked()) {
                    userModifyPwdAction();
                } else if (userSearchRadioBtn.isChecked()) {
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
                loginIntent.putExtra("secId", secId);
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

        resultText.setText("SSO PutValue 결과 : " + ret);
    }

    private void getValueAction() {
        String tagName = SsoUtil.checkNull(firstEditText.getText().toString());
        String index = SsoUtil.checkNull(secondEditText.getText().toString());

        checkEditText(tagName, firstEditText, "태그명");

        if ("".equals(index)) {
            index = "0";
        } else {
            if (!SsoUtil.isNumber(index)) {
                Toast.makeText(getApplicationContext(), "index에는 숫자만 입력가능합니다.", Toast.LENGTH_SHORT).show();
                secondEditText.setText("");
                secondEditText.requestFocus();
                return;
            }
        }

        String ret = mobileSsoAPI.andrsso_getValue(tagName, Integer.parseInt(index), mobileSsoAPI.getToken(), CLIENT_IP, secId);

        resultText.setText("SSO GetValue 결과 : " + ret);
    }

    private void getAllValuesAction() {
        String ret = mobileSsoAPI.andrsso_getAllValues(mobileSsoAPI.getToken(), CLIENT_IP, secId);
        resultText.setText("SSO GetAllValues 결과 : " + ret);
    }

    private void userPwdInitAction() {
        String userId, userPwd;
        userId = SsoUtil.checkNull(firstEditText.getText().toString());
        userPwd = SsoUtil.checkNull(secondEditText.getText().toString());
        int ret = -1;

        checkEditText(userId, firstEditText, "사용자 ID");
        checkEditText(userPwd, secondEditText, "사용자 패스워드");

        ret = mobileSsoAPI.andrsso_userPwdInit(userId, userPwd, 0, CLIENT_IP);
        resultText.setText("SSO UserPasswordInit 결과 : " + ret);
    }

    private void userModifyPwdAction() {
        String currentPwd, newPwd;
        currentPwd = SsoUtil.checkNull(firstEditText.getText().toString());
        newPwd = SsoUtil.checkNull(secondEditText.getText().toString());
        int ret = -1;

        checkEditText(currentPwd, firstEditText, "현재 패스워드");
        checkEditText(newPwd, secondEditText, "새로운 패스워드");

        ret = mobileSsoAPI.andrsso_userModifyPwd(mobileSsoAPI.getToken(), currentPwd, newPwd, CLIENT_IP);
        resultText.setText("SSO UserModfiyPwd 결과 : " + ret);
    }

    private void userSearchAction() {
        String userId = SsoUtil.checkNull(firstEditText.getText().toString());
        int ret = -1;

        checkEditText(userId, firstEditText, "사용자 ID");

        ret = mobileSsoAPI.andrsso_userSearch(userId);
        resultText.setText("SSO userSearch 결과 : " + ret);
    }

    private void keyPadHide() {
        //키패드 내리기
        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
        if ("".equals(editStr)) {
            Toast.makeText(getApplicationContext(), words + "를 입력하세요", Toast.LENGTH_SHORT).show();
            editText.requestFocus();
            return;
        }
    }
}
