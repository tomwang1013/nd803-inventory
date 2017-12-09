package red.jinge.inventory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
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
            // TODO 增加库存
            case R.id.action_increase_count:
                return true;

            // TODO 减少库存
            case R.id.action_decrement_count:
                return true;

            // TODO 发订单
            case R.id.action_order:
                return true;

            // TODO 删除产品
            case R.id.action_delete_product:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
