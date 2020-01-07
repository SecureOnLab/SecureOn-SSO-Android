package kr.co.secureon.sso.sample;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sf.msso.MobileSsoAPI;
import com.sf.msso.SsoUtil;

import static kr.co.secureon.sso.sample.LoginActivity.CLIENT_IP;
import static kr.co.secureon.sso.sample.LoginActivity.PAGE_URL;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class SsoApiTestActivity extends AppCompatActivity {
    LinearLayout ssoApiTestLayout, firstLayout, secondLayout, thirdLayout, fourthLayout;
    TextView firstText, secondText, resultText;
    EditText modeEditText, firstEditText, secondEditText;
    Button actionBtn;
    RadioButton oneRadioBtn, subRadioBtn;
    RadioButton trueRadioBtn, falseRadioBtn;

    MobileSsoAPI mobileSsoAPI;
    byte[] secId = null;
    String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT > 8) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_sso_api_test);

        secId = SsoUtil.getSecId(getApplicationContext());

        mobileSsoAPI = new MobileSsoAPI(getApplicationContext(), PAGE_URL);
        if (mobileSsoAPI.getToken() == null || "".equals(mobileSsoAPI.getToken())) {
            Toast.makeText(getApplicationContext(), "로그인 하여야 사용 가능합니다.", Toast.LENGTH_LONG).show();
            loginActivity();
        }

        ssoApiTestLayout = findViewById(R.id.ssoApiTestLayout);
        firstLayout = findViewById(R.id.firstLayout);
        secondLayout = findViewById(R.id.secondLayout);
        thirdLayout = findViewById(R.id.thirdLayout);
        fourthLayout = findViewById(R.id.fourthLayout);

        firstText = findViewById(R.id.firstText);
        secondText = findViewById(R.id.secondText);
        resultText = findViewById(R.id.resultText);

        modeEditText = findViewById(R.id.modeEditText);
        firstEditText = findViewById(R.id.firstEditText);
        secondEditText = findViewById(R.id.secondEditText);

        actionBtn = findViewById(R.id.actionBtn);

        oneRadioBtn = findViewById(R.id.oneRadioBtn);
        subRadioBtn = findViewById(R.id.subRadioBtn);

        trueRadioBtn = findViewById(R.id.trueRadioBtn);
        falseRadioBtn = findViewById(R.id.falseRadioBtn);

        if (getIntent() != null) {
            if (getIntent().getStringExtra("mode") != null) {
                mode = getIntent().getStringExtra("mode");

                setTitle(mode + " API 테스트");
            }
        } else {
            loginActivity();
        }

        ssoApiTestLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyPadHide();
            }
        });

        modeViewBranch();

        actionBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                modeActionBranch();
            }
        });

    }

    private void modeViewBranch() {
//		String[] arrayStr = new String[]{"putValue()", "getValue()", "getAllValues()", "userPwdInit()", "userModifyPwd()", 
//			"userSearch()", "userView()", "getUserRoleList()", "getResourcePermission()", "getResourceList()", "Login Activity"};
        modeEditText.setText(mode);

        if ("putValue()".equalsIgnoreCase(mode)) {
            thirdLayout.setVisibility(View.GONE);
            fourthLayout.setVisibility(View.GONE);
            firstText.setText("태그명");
            secondText.setText("태그값");
        } else if ("getValue()".equalsIgnoreCase(mode)) {
            thirdLayout.setVisibility(View.GONE);
            fourthLayout.setVisibility(View.GONE);
            firstText.setText("태그명");
            secondText.setText("index");
        } else if ("getAllValues()".equalsIgnoreCase(mode) || "userView()".equalsIgnoreCase(mode) || "getUserRoleList()".equalsIgnoreCase(mode)) {
            firstLayout.setVisibility(View.GONE);
            secondLayout.setVisibility(View.GONE);
            thirdLayout.setVisibility(View.GONE);
            fourthLayout.setVisibility(View.GONE);
        } else if ("userPwdInit()".equalsIgnoreCase(mode)) {
            thirdLayout.setVisibility(View.GONE);
            fourthLayout.setVisibility(View.GONE);
            firstText.setText("사용자 ID");
            secondText.setText("패스워드");
            secondEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else if ("userModifyPwd()".equalsIgnoreCase(mode)) {
            thirdLayout.setVisibility(View.GONE);
            fourthLayout.setVisibility(View.GONE);
            firstText.setText("기존 패스워드");
            secondText.setText("신규 패스워드");
            firstEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            secondEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else if ("userSearch()".equalsIgnoreCase(mode)) {
            secondLayout.setVisibility(View.GONE);
            thirdLayout.setVisibility(View.GONE);
            fourthLayout.setVisibility(View.GONE);
            firstText.setText("사용자 ID");
        } else if ("getResourcePermission()".equalsIgnoreCase(mode)) {
            secondLayout.setVisibility(View.GONE);
            thirdLayout.setVisibility(View.GONE);
            firstText.setText("SRDN");
        } else if ("getResourceList()".equalsIgnoreCase(mode)) {
            firstText.setText("BASE");
            secondText.setText("PERMISSION");
        }
    }

    private void modeActionBranch() {
        if ("putValue()".equalsIgnoreCase(mode)) {
            putValueAction();
        } else if ("getValue()".equalsIgnoreCase(mode)) {
            getValueAction();
        } else if ("getAllValues()".equalsIgnoreCase(mode)) {
            getAllValuesAction();
        } else if ("userPwdInit()".equalsIgnoreCase(mode)) {
            userPwdInitAction();
        } else if ("userModifyPwd()".equalsIgnoreCase(mode)) {
            userModifyPwdAction();
        } else if ("userSearch()".equalsIgnoreCase(mode)) {
            userSearchAction();
        } else if ("userView()".equalsIgnoreCase(mode)) {
            userViewAction();
        } else if ("getUserRoleList()".equalsIgnoreCase(mode)) {
            getUserRoleListAction();
        } else if ("getResourcePermission()".equalsIgnoreCase(mode)) {
            getResourcePermissionAction();
        } else if ("getResourceList()".equalsIgnoreCase(mode)) {
            getResourceListAction();
        }
    }

    //putValue()
    private void putValueAction() {
        String tagName, tagValue;
        tagName = SsoUtil.checkNull(firstEditText.getText().toString());
        tagValue = SsoUtil.checkNull(secondEditText.getText().toString());

        if (!checkEditText(tagName, firstEditText, "태그명"))
            return;
        if (!checkEditText(tagValue, secondEditText, "태그값"))
            return;

        String ret = mobileSsoAPI.andrsso_putValue(tagName, tagValue);

        resultText.setText("SSO PutValue 결과 : " + ret);
    }

    private void getValueAction() {
        String tagName, index;
        tagName = SsoUtil.checkNull(firstEditText.getText().toString());
        index = SsoUtil.checkNull(secondEditText.getText().toString());

        if (!checkEditText(tagName, firstEditText, "태그명"))
            return;

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

        String ret = mobileSsoAPI.andrsso_getAllValues(mobileSsoAPI.getToken(),  CLIENT_IP, secId);
        resultText.setText("SSO GetAllValues 결과 : " + ret);
    }

    private void userPwdInitAction() {
        String userId, userPwd;
        userId = SsoUtil.checkNull(firstEditText.getText().toString());
        userPwd = SsoUtil.checkNull(secondEditText.getText().toString());
        int ret = -1;

        if (!checkEditText(userId, firstEditText, "사용자 ID"))
            return;
        if (!checkEditText(userPwd, secondEditText, "사용자 패스워드"))
            return;

        ret = mobileSsoAPI.andrsso_userPwdInit(userId, userPwd, 0, CLIENT_IP);
        resultText.setText("SSO UserPasswordInit 결과 : " + ret);
    }

    private void userModifyPwdAction() {
        String currentPwd, newPwd;
        currentPwd = SsoUtil.checkNull(firstEditText.getText().toString());
        newPwd = SsoUtil.checkNull(secondEditText.getText().toString());
        int ret = -1;

        if (!checkEditText(currentPwd, firstEditText, "현재 패스워드"))
            return;
        if (!checkEditText(newPwd, secondEditText, "새로운 패스워드"))
            return;

        ret = mobileSsoAPI.andrsso_userModifyPwd(mobileSsoAPI.getToken(), currentPwd, newPwd, CLIENT_IP);
        resultText.setText("SSO UserModfiyPwd 결과 : " + ret);
    }

    private void userSearchAction() {
        String userId = SsoUtil.checkNull(firstEditText.getText().toString());
        int ret = -1;

        if (!checkEditText(userId, firstEditText, "사용자 ID"))
            return;

        ret = mobileSsoAPI.andrsso_userSearch(userId);
        resultText.setText("SSO userSearch 결과 : " + ret);
    }

    private void userViewAction() {
        String ret = mobileSsoAPI.andrsso_userView(mobileSsoAPI.getToken(), CLIENT_IP);
        resultText.setText("SSO userView 결과 : " + ret);
    }

    private void getUserRoleListAction() {
        String ret = "";

        ret = mobileSsoAPI.andrsso_getUserRoleList(mobileSsoAPI.getToken(), CLIENT_IP);
        resultText.setText("SSO getUserRoleList : " + ret);
    }

    private void getResourcePermissionAction() {
        String srdn = SsoUtil.checkNull(firstEditText.getText().toString());
        String roleSearch = "";
        String ret = "";

        if (!checkEditText(srdn, firstEditText, "SRDN"))
            return;

        if (trueRadioBtn.isChecked()) {
            roleSearch = trueRadioBtn.getText().toString();
        }

        if (falseRadioBtn.isChecked()) {
            roleSearch = falseRadioBtn.getText().toString();
        }

        if ("".equals(roleSearch)) {
            Toast.makeText(getApplicationContext(), "Role 검색여부를 선택하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        ret = mobileSsoAPI.andrsso_getResourcePermission(srdn, mobileSsoAPI.getToken(), CLIENT_IP, roleSearch);
        resultText.setText("SSO getResourcePermission 결과 : " + ret);
    }

    private void getResourceListAction() {
        String base = SsoUtil.checkNull(firstEditText.getText().toString());
        String permission = SsoUtil.checkNull(secondEditText.getText().toString());
        String scope = "";
        String roleSearch = "";
        String ret = "";

        if (!checkEditText(base, firstEditText, "BASE"))
            return;
        if (!checkEditText(permission, secondEditText, "Permission"))
            return;

        if (oneRadioBtn.isChecked())
            scope = oneRadioBtn.getText().toString();

        if (subRadioBtn.isChecked())
            scope = subRadioBtn.getText().toString();

        if (trueRadioBtn.isChecked())
            roleSearch = trueRadioBtn.getText().toString();

        if (falseRadioBtn.isChecked())
            roleSearch = falseRadioBtn.getText().toString();

        if ("".equals(scope)) {
            Toast.makeText(getApplicationContext(), "SCOPE를 설정하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("".equals(roleSearch)) {
            Toast.makeText(getApplicationContext(), "Role 검색여부를 선택하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        ret = mobileSsoAPI.andrsso_getResourceList(base, scope, mobileSsoAPI.getToken(), permission, CLIENT_IP, roleSearch);
        resultText.setText("SSO getResourceList 결과 : " + ret);
    }

    //login activity 호출 메서드
    private void loginActivity() {
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
    }

    private void keyPadHide() {
        //키패드 내리기
        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(firstEditText.getWindowToken(), 0);
        mInputMethodManager.hideSoftInputFromWindow(secondEditText.getWindowToken(), 0);
    }

    private boolean checkEditText(String editStr, EditText editText, String words) {
        if ("".equals(editStr)) {
            Toast.makeText(SsoApiTestActivity.this, words + "를 입력하세요", Toast.LENGTH_SHORT).show();
            editText.requestFocus();
            return false;
        } else {
            return true;
        }
    }
}
