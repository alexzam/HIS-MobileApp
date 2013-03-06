package az.his.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import az.his.android.hisapi.ApiListener;
import az.his.android.hisapi.ApiProvider;
import az.his.android.persist.DbHelper;

import java.util.Map;

public class StartActivity extends Activity implements ApiListener {
    private int step = 0;
    private String url;
    private Map<String, Integer> users;
    private SharedPreferences sharedPref;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        url = sharedPref.getString("str_url", "http://192.168.1.3/his");

        if (!ApiProvider.isNetworkReady(this)) {
            // Network is needed for first run
            setStatus("Network connection is needed for first launch.");
            enableProgress(false);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                finish();
            }
            finish();
        }

        checkServer();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleApiResult(Object result) {
        if (step == 0) {
            // Check server result
            if (Boolean.FALSE.equals(result)) {
                setStatus("Unavailable. Please change URL if needed.");
                enableProgress(false);
                enableUrlField();
            } else {
                setStatus("Getting user list");
                step = 1;
                ApiProvider.getUsers(this, this);
            }
        } else if (step == 1) {
            // Fetch users result
            if (result == null) {
                setStatus("FAIL");
                enableProgress(false);
                return;
            }

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("str_url", url);
            editor.commit();

            setStatus("Select user name");

            users = (Map<String, Integer>) result;
            String[] userNames = new String[]{};
            userNames = users.keySet().toArray(userNames);

            Spinner spinner = (Spinner) findViewById(R.id.spnUsers);
            ArrayAdapter<CharSequence> adapter
                    = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, userNames);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            enableProgress(false);
            enableUsersField(true);
        } else if (step == 2) {
            // Fetch categories result
            if (result == null) {
                setStatus("FAIL");
                enableProgress(false);
                return;
            }

            DbHelper dbHelper = new DbHelper(getApplicationContext());
            dbHelper.replaceCats((Map<Integer, String>) result);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("bool_installed", true);
            editor.commit();

            finish();
        }
    }

    private void checkServer() {
        ((TextView) findViewById(R.id.txtStatus)).setText("Checking " + url);
        enableProgress(true);
        ApiProvider.setUrl(url);
        ApiProvider.checkServer(this, this);
    }

    public void onBtSubmitUrl(View view) {
        disableUrlField();
        url = ((EditText) findViewById(R.id.etUrl)).getText().toString();
        url = url.replaceFirst("/+$", "");
        checkServer();
    }

    public void onBtSubmitUser(View view) {
        String name = (String) ((Spinner) findViewById(R.id.spnUsers)).getSelectedItem();
        Integer id = users.get(name);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("str_username", name);
        editor.putInt("int_userid", id);
        editor.commit();

        enableUsersField(false);
        enableProgress(true);
        setStatus("Fetching categories...");
        step = 2;
        ApiProvider.getCategories(this, this, id);
    }

    private void enableUrlField() {
        ((EditText) findViewById(R.id.etUrl)).setText(url);
        findViewById(R.id.layUrl).setVisibility(View.VISIBLE);
    }

    private void disableUrlField() {
        findViewById(R.id.layUrl).setVisibility(View.GONE);
    }

    private void enableUsersField(boolean enable) {
        findViewById(R.id.layUsers).setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    private void enableProgress(boolean enable) {
        findViewById(R.id.progressBar).setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    private void setStatus(String text) {
        ((TextView) findViewById(R.id.txtStatus)).setText(text);
    }
}
