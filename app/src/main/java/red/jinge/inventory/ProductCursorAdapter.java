package red.jinge.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import red.jinge.inventory.data.ProductContract;

/**
 * Created by Seeyon on 2017-12-9.
 */

public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView textViewName = view.findViewById(R.id.li_text_view_name);
        final TextView textViewCount = view.findViewById(R.id.li_text_view_count);
        TextView textViewPrice = view.findViewById(R.id.li_text_view_price);

        final int idColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME);
        int countColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_COUNT);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE);

        final long id = cursor.getLong(idColumnIndex);
        final int count = cursor.getInt(countColumnIndex);
        textViewName.setText(cursor.getString(nameColumnIndex));
        textViewCount.setText(String.valueOf(count));
        textViewPrice.setText(priceFormat(cursor.getInt(priceColumnIndex)));

        Button saleButton = view.findViewById(R.id.li_button_sale);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 库存减1
                if (count > 0) {
                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_COUNT, count - 1);

                    int updatedCount = context.getContentResolver().update(
                            ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, id),
                            values,
                            null,
                            null);

                    if (updatedCount > 0) {
                        Toast.makeText(context, R.string.sale_successful, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, R.string.sale_failed, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, R.string.no_count_hint, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    static public String priceFormat(int priceFen) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(priceFen / 100.0) + " 元";
    }
}
