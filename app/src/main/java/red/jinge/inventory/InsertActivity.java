package red.jinge.inventory;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import red.jinge.inventory.data.ProductContract;

public class InsertActivity extends AppCompatActivity {
    private final static String LOG_TAG = InsertActivity.class.getName();

    private final static int PICK_IMAGE_TAKE_PHOTO = 0;
    private final static int PICK_IMAGE_FROM_GALLERY = 1;
    private final static int PICK_IMAGE_CANCEL = 2;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_FROM_GALLERY = 2;
    private int mPickImageSelected;

    private EditText mEditTextName;
    private EditText mEditTextCount;
    private EditText mEditTextPrice;
    private Uri mSelectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_activity);

        mEditTextName = findViewById(R.id.edit_view_name);
        mEditTextCount = findViewById(R.id.edit_view_count);
        mEditTextPrice = findViewById(R.id.edit_view_price);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_insert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 保存产品
     */
    private void saveProduct() {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ProductContract.ProductEntry.COLUMN_NAME,
                    mEditTextName.getText().toString());
            contentValues.put(ProductContract.ProductEntry.COLUMN_COUNT,
                    mEditTextCount.getText().toString());
            contentValues.put(ProductContract.ProductEntry.COLUMN_PRICE,
                    mEditTextPrice.getText().toString());
            contentValues.put(ProductContract.ProductEntry.COLUMN_IMAGE,
                    getBytes(getContentResolver().openInputStream(mSelectedImageUri)));

            Uri newRowUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI,
                    contentValues);

            // Show a toast message depending on whether or not the insertion was successful
            if (newRowUri != null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.insert_product_successful,
                        Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.insert_product_failed,
                        Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, R.string.insert_product_failed,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

    /**
     * 点击"选择图片", 弹出对话框让用户选择
     * @param view button
     */
    public void pickImage(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pick_image);
        builder.setItems(R.array.array_pick_image_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                mPickImageSelected = item;

                if (item == PICK_IMAGE_TAKE_PHOTO) {
                    cameraIntent();
                } else if (item == PICK_IMAGE_FROM_GALLERY) {
                    galleryIntent();
                } else if (item == PICK_IMAGE_CANCEL) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * 照相选择图片
     */
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        }
    }

    /**
     * 从本地相册选择图片
     */
    private void galleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_FROM_GALLERY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mSelectedImageUri = data.getData();
        } else {
            Log.e(LOG_TAG, "onActivityResult error: " + String.valueOf(resultCode));
        }
    }
}
