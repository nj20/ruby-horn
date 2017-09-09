package none.rubyhorn.views;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;

import none.rubyhorn.R;
import none.rubyhorn.models.Order;
import none.rubyhorn.models.RestaurantMenu;

public class WaitView
{
    private AppCompatActivity context;
    private String tableNumber;
    private Order order;
    private RestaurantMenu menu;
    Response.Listener onAdd;

    public WaitView(AppCompatActivity context, String tableNumber, Order order, RestaurantMenu menu, Response.Listener onAdd)
    {
        this.context = context;
        this.tableNumber = tableNumber;
        this.order = order;
        this.menu = menu;
        this.onAdd = onAdd;
        setItemList();
        setAddItemsButton();
    }

    private void setItemList()
    {
        LinearLayout layout = (LinearLayout)context.findViewById(R.id.layout);
        layout.removeAllViews();
        View sectionHeaderView = View.inflate(context, R.layout.confirmation_table_number, null);
        TextView sectinoHeaderText = sectionHeaderView.findViewById(R.id.tableNumberHeader);
        sectinoHeaderText.setText("Table Number: " + tableNumber);
        layout.addView(sectionHeaderView);
        Log.d("save", order.toString());
        for (String itemId : order.items.keySet())
        {
            MenuItemView menuItemView = null;
            menuItemView = new MenuItemView(context, menu.getItem(itemId), null, null, false);
            menuItemView.setQuantity(order.items.get(itemId), false);
            layout.addView(menuItemView.getView());
        }
    }

    private void setAddItemsButton()
    {
        Button addItems = (Button)context.findViewById(R.id.addButton);
        addItems.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onAdd.onResponse(null);
            }
        });
    }
}
