package kr.co.secureon.sso.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sf.msso.MobileSsoAPI;
import com.sf.msso.SsoUtil;

import java.io.IOException;

import static kr.co.secureon.sso.sample.LoginActivity.PAGE_URL;

public class MainActivity extends AppCompatActivity {

    Button logoutBtn;
    Button webViewActivityBtn;
    Button listViewBtn;
    Button mobileSSOBAppBtn;

    Context context;
    MobileSsoAPI mobileSsoAPI;
    String securityIdFlag;    //secId 사용유무
    byte[] securityId;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        securityIdFlag = this.getResources().getString(R.string.SEC_ID_FLAG);
        if ("TRUE".equalsIgnoreCase(securityIdFlag)) {
            securityId = SsoUtil.getSecId(context);
        }

        mobileSsoAPI = new MobileSsoAPI(this, PAGE_URL);

        listViewBtn = findViewById(R.id.listViewBtn);
        webViewActivityBtn = findViewById(R.id.webViewActivityBtn);
        mobileSSOBAppBtn = findViewById(R.id.mobileSSOBAppBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        //mobileSSOB 호출 버튼 클릭 이벤트
        mobileSSOBAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otherIntent = getPackageManager().getLaunchIntentForPackage("com.softforum.mssosample2");
                if (otherIntent == null) {
                    Toast.makeText(context, "com.softforum.mssosample2 앱이 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                otherIntent.putExtra("ssoToken", mobileSsoAPI.getToken());
                startActivity(otherIntent);
            }
        });

        listViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListViewActivity.class);
                startActivity(intent);
            }
        });

        //로그아웃 버튼 클릭 이벤트
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        //웹뷰 버튼 클릭 이벤트
        webViewActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);

                //ssoToken 평문을 암호화하여 보낸다.
                String encSsoToken = mobileSsoAPI.enc(mobileSsoAPI.getToken());
                intent.putExtra("ssoToken", encSsoToken);
                //20141128 modify smoh - for secIdFlag 추가
                if ("TRUE".equalsIgnoreCase(securityIdFlag)) {
                    intent.putExtra("secId", securityId);
                }
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logout();
    }

    // 로그아웃
    private void logout() {

        if (mobileSsoAPI.getToken() == null || "".equals(mobileSsoAPI.getToken())) {
            Toast.makeText(context, "SSO 토큰이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
        } else {
            try {
                mobileSsoAPI.andrsso_unregUserSession(mobileSsoAPI.getToken(), "127.0.0.1");
                if (mobileSsoAPI.deleteToken() == 0) {
                    finish();
                }
            } catch (IOException e) {
                Log.d("smoh", e.getMessage());
            }
        }

    }
}
