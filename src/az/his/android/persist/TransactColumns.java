package az.his.android.persist;

import android.provider.BaseColumns;

interface TransactColumns extends BaseColumns {
    public static final String TABLE_NAME = "transact";

    public static final String AMOUNT = "amount";
    public static final String CAT_ID = "cat_id";
    public static final String STAMP = "stamp";
}
