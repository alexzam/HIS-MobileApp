package az.his.android;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import az.his.android.persist.DbHelper;

public class MyActivity extends Activity {

    private DbHelper dbHelper;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        dbHelper = new DbHelper(getApplicationContext());
        Cursor cursor = dbHelper.getCatsCursor();

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getApplicationContext(),
                android.R.layout.simple_spinner_item,
                cursor,
                new String[]{"name"},
                new int[]{android.R.id.text1});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spnCategory);
        spinner.setAdapter(adapter);
    }
}
