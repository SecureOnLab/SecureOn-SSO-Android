package kr.co.secureon.sso.sample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.sf.msso.SsoUtil;

public class GetSecIdActivity extends Activity {
	private final String AUTH_ID_SAMPLE_PAGE = "http://192.168.70.155:7080/m/android/msso_auth_id_sample.jsp";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("smoh", getClass().getSimpleName() + " start");
		
		byte[] secId = SsoUtil.getSecId(this.getApplicationContext());
		String secIdStr = "";
		if(secId != null) {
			Log.d("smoh", getClass().getSimpleName() + ".secId : " + new String(secId));
			secIdStr = new String(secId);
		}
		
		Intent gIntent = null;
		
		if(!"".equals(secIdStr)) {
			gIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AUTH_ID_SAMPLE_PAGE + "?secId=" + secIdStr));
		} else {
			gIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AUTH_ID_SAMPLE_PAGE));
		}
		
		startActivity(gIntent);
	}
	
}
