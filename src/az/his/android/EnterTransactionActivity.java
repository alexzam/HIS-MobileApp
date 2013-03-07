package az.his.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import az.his.android.persist.DbHelper;

public class EnterTransactionActivity extends Activity {

    private DbHelper dbHelper;
    private SharedPreferences sharedPref;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Boolean isInstalled = sharedPref.getBoolean("bool_installed", false);

        if (!isInstalled) {
            startActivity(new Intent(this, StartActivity.class));
        } else {
            dbHelper = new DbHelper(getApplicationContext());
            Cursor cursor = dbHelper.getCatsCursor();

            Toast.makeText(this, "Got from DB: " + cursor.getCount() + " categories", 500).show();

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    getApplicationContext(),
                    android.R.layout.simple_spinner_item,
                    cursor,
                    new String[]{"name"},
                    new int[]{android.R.id.text1});
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            Spinner spinner = (Spinner) findViewById(R.id.spnCategory);
            spinner.setAdapter(adapter);

            long trNum = dbHelper.getTransactionNum();
            ((TextView) findViewById(R.id.txtAddTrStatus)).setText("Transactions in DB: " + trNum);
        }
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
        }
        return super.onOptionsItemSelected(item);
    }
}
