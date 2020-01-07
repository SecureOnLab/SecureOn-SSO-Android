package kr.co.secureon.sso.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sf.msso.MobileSsoAPI;
import com.sf.msso.SsoUtil;

import static kr.co.secureon.sso.sample.LoginActivity.PAGE_URL;

public class ListViewActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> arrayAdapter;

    String secIdFlag;    //secId 사용유무
    byte[] secId = null;
    MobileSsoAPI mobileSsoAPI;
    String[] arrayStr = new String[]{"putValue()", "getValue()", "getAllValues()", "userPwdInit()", "userModifyPwd()",
            "userSearch()", "userView()", "getUserRoleList()", "getResourcePermission()", "getResourceList()"};
    //아이템 터치 이벤트
    private OnItemClickListener onClickListItem = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView textView = (TextView) view;
            String selectList = textView.getText().toString();

            Intent ssoApiTestIntent = new Intent(getApplicationContext(), SsoApiTestActivity.class);
            ssoApiTestIntent.putExtra("mode", selectList);
            startActivity(ssoApiTestIntent);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        secId = SsoUtil.getSecId(this);
        mobileSsoAPI = new MobileSsoAPI(this, PAGE_URL);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView = findViewById(R.id.listViewLayout);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(onClickListItem);

        for (int i = 0; i < arrayStr.length; i++) {
            arrayAdapter.add(arrayStr[i]);
        }

    }
}
