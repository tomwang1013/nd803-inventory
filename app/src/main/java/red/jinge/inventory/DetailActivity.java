package red.jinge.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import red.jinge.inventory.data.ProductContract;

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private Uri mProductUri;

    private TextView mTextViewName;
    private TextView mTextViewCount;
    private TextView mTextViewPrice;
    private ImageView mImageViewImage;

    private int mCurrentCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        Intent intent = getIntent();
        mProductUri = intent.getData();

        mTextViewName = findViewById(R.id.text_view_name);
        mTextViewCount = findViewById(R.id.text_view_count);
        mTextViewPrice = findViewById(R.id.text_view_price);
        mImageViewImage = findViewById(R.id.image_view_image);

        if (mProductUri != null) {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // T增加库存
            case R.id.action_increase_count:
                modifyCount(true);
                return true;

            // 减少库存
            case R.id.action_decrement_count:
                modifyCount(false);
                return true;

            // 订购更多
            case R.id.action_order:
                orderProduct();
                return true;

            // 删除产品
            case R.id.action_delete_product:
                deleteProduct();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 修改库存
     * @param bAdd 是否是增加
     */
    private void modifyCount(final boolean bAdd) {
        final EditText input = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(input);

        if (bAdd) {
            input.setHint(R.string.increase_count_hint);
            builder.setTitle(R.string.increase_count_title);
        } else {
            input.setHint(R.string.decrement_count_hint);
            builder.setTitle(R.string.decrement_count_title);
        }


        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int modifiedCount = Integer.parseInt(input.getText().toString());
                mCurrentCount = bAdd ? mCurrentCount + modifiedCount : mCurrentCount - modifiedCount;

                if (mCurrentCount < 0) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(DetailActivity.this, R.string.modified_count_failed,
                            Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_COUNT, mCurrentCount);
                    getContentResolver().update(mProductUri, values, null, null);
                    dialog.dismiss();
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * 发订单
     */
    private void orderProduct() {
        String orderMessage = getString(R.string.product_name) + ": " + mTextViewName.getText()
                    + "\n" + getString(R.string.product_count) + ": " + mTextViewCount.getText()
                    + "\n" + getString(R.string.product_price) + ": " + mTextViewPrice.getText();

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_mail_title));
        intent.putExtra(Intent.EXTRA_TEXT, orderMessage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * 弹出确认对话框：用户确认后再删除
     */
    private void deleteProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.action_delete_product, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int deletedCount = getContentResolver().delete(mProductUri, null, null);

                if (deletedCount > 0) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(DetailActivity.this, R.string.delete_product_successful,
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(DetailActivity.this, R.string.delete_product_failed,
                            Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] columns = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_NAME,
                ProductContract.ProductEntry.COLUMN_COUNT,
                ProductContract.ProductEntry.COLUMN_PRICE,
                ProductContract.ProductEntry.COLUMN_IMAGE
        };

        return new CursorLoader(
                this,
                mProductUri,
                columns,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            int nameIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME);
            int countIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_COUNT);
            int priceIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE);
            int imageIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_IMAGE);

            mCurrentCount = data.getInt(countIndex);

            mTextViewName.setText(data.getString(nameIndex));
            mTextViewCount.setText(String.valueOf(data.getInt(countIndex)));
            mTextViewPrice.setText(ProductCursorAdapter.priceFormat(data.getInt(priceIndex)));

            byte[] blob = data.getBlob(imageIndex);
            mImageViewImage.setImageBitmap(BitmapFactory.decodeByteArray(blob, 0, blob.length));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTextViewName.setText(null);
        mTextViewCount.setText(null);
        mTextViewPrice.setText(null);
        mImageViewImage.setImageBitmap(null);
    }
}
