package kr.co.secureon.sso.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sf.msso.MobileSsoAPI;
import com.sf.msso.SsoUtil;

public class ListViewActivity extends Activity {
	SampleVO sampleVO = new SampleVO();
	ListView listView;
	ArrayAdapter<String> arrayAdapter;
	
	String secIdFlag;	//secId 사용유무
	byte[] secId = null;
	MobileSsoAPI mobileSsoAPI;
	String[] arrayStr = new String[]{"putValue()", "getValue()", "getAllValues()", "userPwdInit()", "userModifyPwd()", 
		"userSearch()", "userView()", "getUserRoleList()", "getResourcePermission()", "getResourceList()", 
		"Login Activity", "WebView Activity"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if(android.os.Build.VERSION.SDK_INT > 8) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		}
		
		//타이틀바 보이지 않도록 하기 setContentView 이전에 써야한다.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(Window.FEATURE_NO_TITLE, Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view);
		
		secIdFlag = this.getResources().getString(R.string.SEC_ID_FLAG);
		
		if("TRUE".equalsIgnoreCase(secIdFlag)) {
			secId = SsoUtil.getSecId(getApplicationContext());
		}
		
		mobileSsoAPI = new MobileSsoAPI(getApplicationContext(), getString(R.string.exp_page_url));
		
		arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
		listView = (ListView)findViewById(R.id.listViewLayout);
		
		listView.setAdapter(arrayAdapter);
		listView.setOnItemClickListener(onClickListItem);
		
		for(int i=0;i< arrayStr.length;i++) {
			arrayAdapter.add(arrayStr[i]);
		}
		
//		arrayAdapter.add("putValue()");
//		arrayAdapter.add("getValue()");
//		arrayAdapter.add("getAllValues()");
//		arrayAdapter.add("userPwdInit()");
//		arrayAdapter.add("userModifyPwd()");
//		arrayAdapter.add("userSearch()");
//		arrayAdapter.add("userView()");
//		arrayAdapter.add("getUserRoleList()");
//		arrayAdapter.add("getResourcePermission()");
//		arrayAdapter.add("getResourceList()");
//		arrayAdapter.add("Login Activity");
	}
	
	//아이템 터치 이벤트
	private OnItemClickListener onClickListItem = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			TextView textView = (TextView) view;
			String selectList = textView.getText().toString();
			Log.d("smoh", "textView : " + textView.getText().toString());
			Log.d("smoh", "position : " + position);
//			Log.d("smoh", "id : " + id);
			
//			String[] arrayStr = new String[]{"putValue()", "getValue()", "getAllValues()", "userPwdInit()", "userModifyPwd()", 
//				"userSearch()", "userView()", "getUserRoleList()", "getResourcePermission()", "getResourceList()", "Login Activity"};
			
			if("Login Activity".equalsIgnoreCase(selectList)) {
				Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
				loginIntent.putExtra("ssoToken", mobileSsoAPI.getToken());
				
				if("TRUE".equalsIgnoreCase(secIdFlag)) {
					loginIntent.putExtra("secId", secId);
				}
				startActivity(loginIntent);
			} else if("WebView Activity".equalsIgnoreCase(selectList)) {
				Intent webViewIntent = new Intent(getApplicationContext(), WebViewActivity.class);
				//ssoToken 평문을 암호화하여 보낸다.
				String encSsoToken = mobileSsoAPI.enc(mobileSsoAPI.getToken());
				Log.d("smoh", getClass().getSimpleName() + ".encSsoToken : " + encSsoToken);
				webViewIntent.putExtra("ssoToken", encSsoToken);
				//20141128 modify smoh - for secIdFlag 추가
				if("TRUE".equalsIgnoreCase(secIdFlag)) {
					Log.d("smoh", getClass().getSimpleName() + ".secId : " + new String(sampleVO.getSecId()));
					webViewIntent.putExtra("secId", sampleVO.getSecId());
				}
				startActivity(webViewIntent);
			} else {
				Intent ssoApiTestIntent = new Intent(getApplicationContext(), SsoApiTestActivity.class);
				ssoApiTestIntent.putExtra("mode", selectList);
				startActivity(ssoApiTestIntent);
			}
		}
		
	};
}
