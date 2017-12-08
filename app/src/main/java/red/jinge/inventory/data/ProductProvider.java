package red.jinge.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Seeyon on 2017-12-8.
 */

public class ProductProvider extends ContentProvider {
    /** URI matcher code for the content URI for the pets table */
    private static final int PRODUCTS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int PRODUCT_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    /** Tag for the log messages */
    private static final String LOG_TAG = ProductProvider.class.getSimpleName();

    private ProductDbHelper mProductDbHelper;

    @Override
    public boolean onCreate() {
        mProductDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        SQLiteDatabase db = mProductDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case PRODUCTS:
                cursor = db.query(
                        ProductContract.ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PRODUCT_ID:
                cursor = db.query(
                        ProductContract.ProductEntry.TABLE_NAME,
                        projection,
                        ProductContract.ProductEntry._ID + "=?",
                        new String[] { String.valueOf(ContentUris.parseId(uri)) },
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("can not query unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match");
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (sUriMatcher.match(uri) != PRODUCTS) {
            throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

        String name = values.getAsString(ProductContract.ProductEntry.COLUMN_NAME);
        if (TextUtils.isEmpty(name)) {
            return null;
        }

        Integer count = values.getAsInteger(ProductContract.ProductEntry.COLUMN_COUNT);
        if (count == null || count < 0) {
            return null;
        }

        Integer price = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRICE);
        if (price == null || price < 0) {
            return null;
        }

        byte[] image = values.getAsByteArray(ProductContract.ProductEntry.COLUMN_IMAGE);
        if (image == null) {
            return null;
        }

        SQLiteDatabase db = mProductDbHelper.getWritableDatabase();
        long newRowId = db.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Log.e(LOG_TAG, "failed to insert row for: " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, newRowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mProductDbHelper.getWritableDatabase();
        int deletedCount;

        switch (sUriMatcher.match(uri)) {
            case PRODUCTS:
                deletedCount = db.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                deletedCount = db.delete(
                        ProductContract.ProductEntry.TABLE_NAME,
                        ProductContract.ProductEntry._ID + "=?",
                        new String[] { String.valueOf(ContentUris.parseId(uri)) });
                break;
            default:
                throw new IllegalStateException("Delete is not supported for: " + uri);
        }

        if (deletedCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedCount;
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues values,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_NAME)) {
            String name = values.getAsString(ProductContract.ProductEntry.COLUMN_NAME);
            if (TextUtils.isEmpty(name)) {
                return 0;
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_COUNT)) {
            Integer count = values.getAsInteger(ProductContract.ProductEntry.COLUMN_COUNT);
            if (count != null || count < 0) {
                return 0;
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRICE);
            if (price == null || price < 0) {
                return 0;
            }
        }

        if (values.containsKey(ProductContract.ProductEntry.COLUMN_IMAGE)) {
            byte[] image = values.getAsByteArray(ProductContract.ProductEntry.COLUMN_IMAGE);
            if (image == null) {
                return 0;
            }
        }

        SQLiteDatabase db = mProductDbHelper.getWritableDatabase();
        int updatedCount;

        switch (sUriMatcher.match(uri)) {
            case PRODUCTS:
                updatedCount = db.update(ProductContract.ProductEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                updatedCount = db.update(ProductContract.ProductEntry.TABLE_NAME,
                        values,
                        ProductContract.ProductEntry._ID + "=?",
                        new String[] { String.valueOf(ContentUris.parseId(uri)) });
                break;
            default:
                throw new IllegalStateException("Update is not supported for: " + uri);
        }

        if (updatedCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updatedCount;
    }
}
