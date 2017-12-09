package red.jinge.inventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import red.jinge.inventory.data.ProductContract;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private ProductCursorAdapter mProductCursorAdapter;
    private ListView mProductListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View emptyView = findViewById(R.id.empty_view);

        mProductCursorAdapter = new ProductCursorAdapter(this, null);
        mProductListView = findViewById(R.id.list);
        mProductListView.setAdapter(mProductCursorAdapter);
        mProductListView.setEmptyView(emptyView);

        // 点击进入产品详情页
        mProductListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.setData(ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, id));
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(0, null, this);
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
            case R.id.action_insert:
                Intent intent = new Intent(this, InsertActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] columns = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_NAME,
                ProductContract.ProductEntry.COLUMN_COUNT,
                ProductContract.ProductEntry.COLUMN_PRICE
        };

        return new CursorLoader(this,
                ProductContract.ProductEntry.CONTENT_URI,
                columns,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mProductCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductCursorAdapter.swapCursor(null);
    }
}
