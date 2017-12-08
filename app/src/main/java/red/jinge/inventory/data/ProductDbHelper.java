package red.jinge.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Seeyon on 2017-12-8.
 */

public class ProductDbHelper extends SQLiteOpenHelper {
    // database version
    public static final int DATABASE_VERSION = 1;

    // database name
    public static final String DATABASE_NAME = "products.db";

    // create table statement
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + "("
                    + ProductContract.ProductEntry._ID          + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ProductContract.ProductEntry.COLUMN_NAME  + " TEXT NOT NULL, "
                    + ProductContract.ProductEntry.COLUMN_COUNT + " INTEGER NOT NULL, "
                    + ProductContract.ProductEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                    + ProductContract.ProductEntry.COLUMN_TAG   + " INTEGER NOT NULL DEFAULT 0)"
                    + ProductContract.ProductEntry.COLUMN_IMAGE + " BLOB NOT NULL;";

    // delete table statement
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ProductContract.ProductEntry.TABLE_NAME;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
