package az.his.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import az.his.android.hisapi.ApiListener;
import az.his.android.hisapi.ApiProvider;

public class StartActivity extends Activity implements ApiListener {
    private int step = 0;
    private String url;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean isInstalled = sharedPref.getBoolean("bool_installed", false);

        if (!isInstalled) {
            url = sharedPref.getString("str_url", "http://192.168.1.3/his");

            if (!ApiProvider.isNetworkReady(this)) {
                // Network is needed for first run
                setStatus("Network connection is needed for first launch.");
                disableProgress();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    finish();
                }
                finish();
            }

            checkServer();
        }
    }

    @Override
    public void handleApiResult(Object result) {
        if (step == 0) {
            if (Boolean.FALSE.equals(result)) {
                setStatus("Unavailable. Please change URL if needed.");
                disableProgress();
                enableUrlField();
            } else {
                setStatus("Available");
            }
        }
    }

    private void checkServer() {
        ((TextView) findViewById(R.id.txtStatus)).setText("Checking " + url);
        ApiProvider.setUrl(url);
        ApiProvider.checkServer(this, this);
    }

    public void onBtSubmitUrl(View view){
        disableUrlField();
        url = ((EditText)findViewById(R.id.etUrl)).getText().toString();
        checkServer();
    }

    private void enableUrlField() {
        ((EditText) findViewById(R.id.etUrl)).setText(url);
        findViewById(R.id.layUrl).setVisibility(View.VISIBLE);
    }

    private void disableUrlField() {
        findViewById(R.id.layUrl).setVisibility(View.GONE);
    }

    private void disableProgress() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    private void setStatus(String text) {
        ((TextView) findViewById(R.id.txtStatus)).setText(text);
    }
}
