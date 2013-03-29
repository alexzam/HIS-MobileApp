package az.his.android.persist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    private static final int VER = 1;
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

        sql = new StringBuilder();
        sql.append("CREATE TABLE ")
                .append(TransactColumns.TABLE_NAME)
                .append(" (")
                .append(TransactColumns._ID)
                .append(" INTEGER PRIMARY KEY, ")
                .append(TransactColumns.AMOUNT)
                .append(" INTEGER, ")
                .append(TransactColumns.CAT_ID)
                .append(" INTEGER, ")
                .append(TransactColumns.STAMP)
                .append(" INTEGER)");

        db.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
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

    public void replaceCats(SparseArray<String> cats) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(CategoryColumns.TABLE_NAME, "1=1", new String[]{});

            for (int i = 0; i < cats.size(); i++) {
                ContentValues values = new ContentValues();
                values.put(CategoryColumns.NAME, cats.valueAt(i));
                values.put(CategoryColumns.FOREIGN_ID, cats.keyAt(i));
                db.insert(CategoryColumns.TABLE_NAME, null, values);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void addTransaction(Integer amount, Integer catId) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(TransactColumns.AMOUNT, amount);
            values.put(TransactColumns.CAT_ID, catId);
            values.put(TransactColumns.STAMP, (new Date()).getTime());
            db.insert(TransactColumns.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.close();
    }

    public long getTransactionNum() {
        SQLiteDatabase db = getReadableDatabase();
        long num = db.compileStatement("SELECT COUNT(*) FROM " + TransactColumns.TABLE_NAME).simpleQueryForLong();
        db.close();

        return num;
    }

    public List<Transaction> getTransactions() {
        SQLiteDatabase db = getReadableDatabase();

        String[] proj = new String[]{
                TransactColumns.STAMP,
                TransactColumns.CAT_ID,
                TransactColumns.AMOUNT
        };

        Cursor cursor = db.query(
                TransactColumns.TABLE_NAME,
                proj,
                null, null, null, null,
                null
        );

        List<Transaction> ret = new ArrayList<Transaction>();
        while (cursor.moveToNext()) {
            Transaction tr = new Transaction(cursor.getInt(2), cursor.getInt(1), cursor.getLong(0));
            ret.add(tr);
        }

        cursor.close();
        return ret;
    }

    public void cleanTransactions() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TransactColumns.TABLE_NAME, "1=1", null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}
