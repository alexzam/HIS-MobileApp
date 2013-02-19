package az.his.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;
import az.his.android.hisapi.ApiListener;
import az.his.android.hisapi.ApiProvider;

public class StartActivity extends Activity implements ApiListener{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String url = sharedPref.getString("str_url", "http://ya.ru");
//        String url = sharedPref.getString("str_url", "http://192.168.1.3/his");

        ((TextView) findViewById(R.id.txtStatus)).setText("Checking " + url);

        ApiProvider.setUrl(url);
        ApiProvider.checkServer(this, this);
    }

    @Override
    public void handleApiResult(Object result) {
        if(Boolean.FALSE.equals(result)){
            ((TextView) findViewById(R.id.txtStatus)).setText("Unavailable");
        } else {
            ((TextView) findViewById(R.id.txtStatus)).setText("Available");
        }
    }
}
