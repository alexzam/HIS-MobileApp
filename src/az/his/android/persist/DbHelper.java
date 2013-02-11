package az.his.android.persist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    public static final int VER = 1;
    private static final String FILENAME = "AzHis.db";

    public DbHelper(Context context) {
        super(context, FILENAME, null, VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ")
                .append(CategoryColumns.TABLE_NAME)
                .append(" (")
                .append(CategoryColumns._ID)
                .append(" INTEGER PRIMARY KEY, ")
                .append(CategoryColumns.NAME)
                .append(" TEXT, ")
                .append(CategoryColumns.FOREIGN_ID)
                .append(" INTEGER)");

        db.execSQL(sql.toString());

        ContentValues values = new ContentValues();
        values.put(CategoryColumns.NAME, "Test0");
        values.put(CategoryColumns.FOREIGN_ID, "1");
        db.insert(CategoryColumns.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(CategoryColumns.NAME, "Test1");
        values.put(CategoryColumns.FOREIGN_ID, "0");
        db.insert(CategoryColumns.TABLE_NAME, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    }

    public List<Category> getCats() {
        Cursor cursor = getCatsCursor();

        List<Category> ret = new ArrayList<Category>();
        while (cursor.moveToNext()) {
            Category category = new Category();
            category.setId(cursor.getInt(0));
            category.setName(cursor.getString(1));
            category.setForeignId(cursor.getInt(2));
            ret.add(category);
        }

        cursor.close();
        return ret;
    }

    public Cursor getCatsCursor() {
        SQLiteDatabase db = getReadableDatabase();

        String[] proj = new String[]{
                CategoryColumns.NAME,
                CategoryColumns._ID,
                CategoryColumns.FOREIGN_ID
        };

        return db.query(
                CategoryColumns.TABLE_NAME,
                proj,
                null, null, null, null,
                CategoryColumns.NAME
        );
    }
}
