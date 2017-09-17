package none.rubyhorn.views;

import android.support.v7.app.AppCompatActivity;
import none.rubyhorn.models.MenuItem;

import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Response;

import org.w3c.dom.Text;

import none.rubyhorn.R;
import none.rubyhorn.models.Order;
import none.rubyhorn.models.RestaurantMenu;

public class ConfirmationView
{
    private boolean editing = false;
    private AppCompatActivity context;
    private RestaurantMenu menu;
    private Order order;
    private String tableNumber;
    private Response.Listener<MenuItem> onAddItem;
    private Response.Listener<Integer> onTipChange;
    private Response.Listener<MenuItem> onDeleteItem;
    private Response.Listener onConfirm;

    public ConfirmationView(AppCompatActivity context, RestaurantMenu menu, Order order, String tableNumber, Response.Listener<MenuItem> onAddItem, Response.Listener<Integer> onTipchange, Response.Listener<MenuItem> onDeleteItem, Response.Listener onConfirm)
    {
        this.context = context;
        this.menu = menu;
        this.order = order;
        this.onAddItem = onAddItem;
        this.onDeleteItem = onDeleteItem;
        this.onConfirm = onConfirm;
        this.tableNumber = tableNumber;
        this.onTipChange = onTipchange;

        setBackButton();
        setConfirmationList();
        setEditButton();
        updateOrderTotal();
        setPayButton();
        setTipSlider();
    }

    private void setConfirmationList()
    {
        LinearLayout layout = (LinearLayout)context.findViewById(R.id.layout);
        layout.removeAllViews();
        View sectionHeaderView = View.inflate(context, R.layout.confirmation_table_number, null);
        TextView sectinoHeaderText = sectionHeaderView.findViewById(R.id.tableNumberHeader);
        sectinoHeaderText.setText("Table Number: " + tableNumber);
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

    private void setPayButton()
    {
        Button payButton = (Button)context.findViewById(R.id.payButton);
        payButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onConfirm.onResponse(null);
            }
        });
    }

    private void setTipSlider()
    {
        SeekBar slider = (SeekBar)context.findViewById(R.id.tipSlider);
        final TextView tipText = (TextView)context.findViewById(R.id.tipText);

        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                tipText.setText("£" + i);
                onTipChange.onResponse(i);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
    }

    public void updateOrderTotal()
    {
        TextView orderTotal = (TextView)context.findViewById(R.id.orderTotal);
        orderTotal.setText("£" + order.getOrderTotal(menu));
    }

    public void removeMenuItem(MenuItem item)
    {
        LinearLayout layout = (LinearLayout) context.findViewById(R.id.layout);
        layout.removeView(layout.findViewWithTag(item));
    }
}
