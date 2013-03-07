package az.his.android.persist;

import android.provider.BaseColumns;

public interface CategoryColumns extends BaseColumns {
    public static final String TABLE_NAME = "cats";

    public static final String NAME = "name";
    public static final String FOREIGN_ID = "foreign_id";
}
