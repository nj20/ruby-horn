package none.rubyhorn.views;

import android.support.v7.app.AppCompatActivity;
import none.rubyhorn.models.MenuItem;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import none.rubyhorn.R;
import none.rubyhorn.models.Order;
import none.rubyhorn.models.RestaurantMenu;

public class ConfirmationView
{
    private boolean editing = false;
    private AppCompatActivity context;
    private RestaurantMenu menu;
    private Order order;
    private Response.Listener<MenuItem> onAddItem;
    private Response.Listener<MenuItem> onDeleteItem;
    private Response.Listener onConfirm;

    public ConfirmationView(AppCompatActivity context, RestaurantMenu menu, Order order, Response.Listener<MenuItem> onAddItem, Response.Listener<MenuItem> onDeleteItem, Response.Listener onConfirm)
    {
        this.context = context;
        this.menu = menu;
        this.order = order;
        this.onAddItem = onAddItem;
        this.onDeleteItem = onDeleteItem;
        this.onConfirm = onConfirm;

        setBackButton();
        setConfirmationList();
        setEditButton();
        updateOrderTotal();
    }

    private void setConfirmationList()
    {
        LinearLayout layout = (LinearLayout)context.findViewById(R.id.layout);
        layout.removeAllViews();
        View sectionHeaderView = View.inflate(context, R.layout.menu_section_header, null);
        TextView sectinoHeaderText = sectionHeaderView.findViewById(R.id.tableNumberHeader);
        sectinoHeaderText.setText("Table Number: 5");
        layout.addView(sectionHeaderView);

        for (String itemId : order.items.keySet())
        {
            MenuItemView menuItemView = null;
            if(editing)
            {
                menuItemView = new MenuItemView(context, menu.getItem(itemId), onAddItem, onDeleteItem, false);
                menuItemView.setQuantity(order.items.get(itemId), true);
            }
            else
            {
                menuItemView = new MenuItemView(context, menu.getItem(itemId), null, null, false);
                menuItemView.setQuantity(order.items.get(itemId), false);
            }
            layout.addView(menuItemView.getView());
        }

    }

    private void setBackButton()
    {
        View backButton = context.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                context.finish();
            }
        });
    }

    private void setEditButton()
    {
        View editButton = context.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                editing = !editing;
                setConfirmationList();
            }
        });
    }

    public void updateOrderTotal()
    {
        TextView orderTotal = (TextView)context.findViewById(R.id.orderTotal);
        orderTotal.setText("£" + order.totalPrice);
    }
}