package red.jinge.inventory;

import android.app.AlertDialog;
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

public class InsertActivity extends AppCompatActivity {
    private final static String LOG_TAG = InsertActivity.class.getName();

    private final static int PICK_IMAGE_TAKE_PHOTO = 0;
    private final static int PICK_IMAGE_FROM_GALLERY = 1;
    private final static int PICK_IMAGE_CANCEL = 2;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_FROM_GALLERY = 2;
    private int mPickImageSelected;
    private Uri mSelectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
