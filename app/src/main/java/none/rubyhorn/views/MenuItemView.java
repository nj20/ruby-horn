package none.rubyhorn.views;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.android.volley.Response;
import none.rubyhorn.R;
import none.rubyhorn.models.MenuItem;

public class MenuItemView
{
    private View menuItemView;
    private int quantity;

    public MenuItemView(AppCompatActivity context, MenuItem item, final Response.Listener<MenuItem> onAdd, final Response.Listener<MenuItem> onDelete, boolean showDescription)
    {
        if(item.description.trim().equals("") || !showDescription)
        {
            menuItemView = View.inflate(context, R.layout.menu_item_no_description, null);
        }
        else
        {
            menuItemView = View.inflate(context, R.layout.menu_item, null);
        }
        menuItemView.setTag(item);

        TextView itemName = menuItemView.findViewById(R.id.name);
        itemName.setText(item.name);

        TextView description = menuItemView.findViewById(R.id.description);
        if(description != null)
            description.setText(item.description);

        TextView price = menuItemView.findViewById(R.id.price);
        price.setText("£" + item.price);

        final MenuItemView instance = this;
        menuItemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(onAdd != null)
                {
                    instance.incrementQuantity();
                    onAdd.onResponse((MenuItem)instance.getView().getTag());
                }
            }
        });

        View delete = menuItemView.findViewById(R.id.deleteButton);
        delete.setTag(item);
        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(onDelete != null)
                {
                    instance.resetQuantity();
                    onDelete.onResponse((MenuItem)instance.getView().getTag());
                }
            }
        });
    }

    public void incrementQuantity()
    {
        Context context = menuItemView.getContext().getApplicationContext();
        Animation appear = AnimationUtils.loadAnimation(context, R.anim.view_pop);
        View quantityView = menuItemView.findViewById(R.id.quantity);
        playAnimation(quantityView, appear);
        setQuantity(quantity + 1, true);
    }

    public void resetQuantity()
    {
        setQuantity(0, true);
    }


    public void setQuantity(int quantity, boolean showDeleteButton)
    {
        this.quantity = quantity;
        TextView counter = menuItemView.findViewById(R.id.quantity);
        View delete = menuItemView.findViewById(R.id.deleteButton);

        if(quantity > 0)
        {
            if(showDeleteButton)
                delete.setVisibility(View.VISIBLE);

            counter.setVisibility(View.VISIBLE);
            counter.setText("x" + quantity);
        }
        else
        {
            delete.setVisibility(View.INVISIBLE);
            counter.setVisibility(View.INVISIBLE);
        }
    }

    private void playAnimation(View view, Animation animation)
    {
        animation.reset();
        view.clearAnimation();
        view.startAnimation(animation);
    }

    public MenuItem getItem()
    {
        return (MenuItem)menuItemView.getTag();
    }

    public View getView()
    {
        return menuItemView;
    }
}
