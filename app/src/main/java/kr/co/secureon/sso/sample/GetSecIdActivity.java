package kr.co.secureon.sso.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sf.msso.SsoUtil;

public class GetSecIdActivity extends AppCompatActivity {
    private final String AUTH_ID_SAMPLE_PAGE = "http://192.168.1.236:8080/android/msso_auth_id_sample.jsp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        byte[] secId = SsoUtil.getSecId(this);
        String secIdStr = "";
        if (secId != null) {
            secIdStr = new String(secId);
        }

        Intent gIntent = null;

        if (!"".equals(secIdStr)) {
            gIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AUTH_ID_SAMPLE_PAGE + "?secId=" + secIdStr));
        } else {
            gIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AUTH_ID_SAMPLE_PAGE));
        }

        startActivity(gIntent);
    }

}
