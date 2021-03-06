package az.his.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import az.his.android.hisapi.ApiListener;
import az.his.android.hisapi.ApiProvider;
import az.his.android.persist.CategoryColumns;
import az.his.android.persist.DbHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class EnterTransactionActivity extends Activity implements ApiListener {

    private DbHelper dbHelper;
    private SharedPreferences sharedPref;
    private SimpleDateFormat statusDateFormat = new SimpleDateFormat("HH:mm E dd MMM");

    private Handler guiHandler = new Handler();
    private String status;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        SyncService.checkState(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Boolean isInstalled = sharedPref.getBoolean("bool_installed", false);

        if (!isInstalled) {
            startActivity(new Intent(this, StartActivity.class));
        } else {
            startService(new Intent(this, SyncService.class));

            dbHelper = new DbHelper(getApplicationContext());
            ApiProvider.setUrl(sharedPref.getString("str_url", null));

            Cursor cursor = dbHelper.getCatsCursor();
            startManagingCursor(cursor);

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    getApplicationContext(),
                    android.R.layout.simple_spinner_item,
                    cursor,
                    new String[]{"name"},
                    new int[]{android.R.id.text1});
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            Spinner spinner = (Spinner) findViewById(R.id.spnCategory);
            spinner.setAdapter(adapter);

            updateStatus();

            // Update status again in a couple of seconds when service is (probably) started.
            (new Timer()).schedule(new TimerTask() {
                @Override
                public void run() {
                    updateStatus();
                }
            }, 2000);
        }
    }

    private void updateStatus() {
        long trNum = dbHelper.getTransactionNum();
        StringBuilder statusStr = new StringBuilder();

        Date time = SyncService.getScheduledTime();

        if (time == null) {
            statusStr.append(getString(R.string.main_msg_notsched));
        } else {
            statusStr.append(getString(R.string.main_msg_sched, statusDateFormat.format(time)));
        }

        statusStr.append("\n");
        statusStr.append(getString(R.string.main_msg_transnum, trNum));

        status = statusStr.toString();
        guiHandler.post(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.txtAddTrStatus)).setText(status);
            }
        });
    }

    public void onBtSubmit(@SuppressWarnings("UnusedParameters") View view) {
        int amount = Integer.parseInt(((EditText) findViewById(R.id.etAmount)).getText().toString());
        Cursor item = (Cursor) ((Spinner) findViewById(R.id.spnCategory)).getSelectedItem();
        int catId = item.getInt(item.getColumnIndex(CategoryColumns.FOREIGN_ID));
        dbHelper.addTransaction(amount, catId);

        Toast.makeText(this, getString(R.string.main_msg_transadded), 1000).show();
        ((EditText) findViewById(R.id.etAmount)).setText("0");
        updateStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_sync:
                ApiProvider.postTransactions(this, this, sharedPref.getInt("int_userid", -1), dbHelper.getTransactions(), true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleApiResult(Object result) {
        if (result == Boolean.TRUE) {
            Toast.makeText(this, getString(R.string.main_msg_transsubmitted), 1000).show();
            dbHelper.cleanTransactions();
            updateStatus();
        }
    }
}
