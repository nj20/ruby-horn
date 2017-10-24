package none.rubyhorn.views;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import none.rubyhorn.models.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private String tableNumber;
    private Response.Listener<MenuItem> onAddItem;
    private Response.Listener<MenuItem> onDeleteItem;
    private Response.Listener onConfirm;
    private String message = "";

    public ConfirmationView(AppCompatActivity context, RestaurantMenu menu, Order order, String tableNumber, Response.Listener<MenuItem> onAddItem, Response.Listener<MenuItem> onDeleteItem, Response.Listener onConfirm)
    {
        this.context = context;
        this.menu = menu;
        this.order = order;
        this.onAddItem = onAddItem;
        this.onDeleteItem = onDeleteItem;
        this.onConfirm = onConfirm;
        this.tableNumber = tableNumber;

        setBackButton();
        setConfirmationList();
        setEditButton();
        updateOrderTotal();
        setPayButton();
        setAddExtraInstructionsButton();
    }

    private void setConfirmationList()
    {
        LinearLayout layout = (LinearLayout)context.findViewById(R.id.layout);
        layout.removeAllViews();
        View sectionHeaderView = View.inflate(context, R.layout.confirmation_table_number, null);
        TextView sectinoHeaderText = sectionHeaderView.findViewById(R.id.itemName);
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
                //TextView textView = (TextView)context.findViewById(R.id.chefNotes);
                //String chefNotes = textView.getText().toString();
                onConfirm.onResponse(message);
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

    public void setAddExtraInstructionsButton()
    {
        Button addExtraInstructionsButton = (Button)context.findViewById(R.id.addMessageButton);
        addExtraInstructionsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showAddMessageDialog();
            }
        });
    }

    private void showAddMessageDialog()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        final View view = context.getLayoutInflater().inflate(R.layout.add_instruction_dialog, null);
        final EditText instructions = view.findViewById(R.id.chefNotes);
        instructions.setText(message);
        dialogBuilder.setView(view);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                message = instructions.getText().toString();
            }
        });
        dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                message = instructions.getText().toString();
            }
        });
        dialogBuilder.show();
    }
}
