package none.rubyhorn.views;

import android.graphics.ColorFilter;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;

import none.rubyhorn.R;
import none.rubyhorn.models.MenuItem;
import none.rubyhorn.models.MenuSection;
import none.rubyhorn.models.RestaurantMenu;

public class MenuSliderView
{

    private AppCompatActivity context;
    private RestaurantMenu menu;
    private Response.Listener<MenuSection> onAddSectionToFilter;
    private Response.Listener<MenuSection> onRemoveSectionToFilter;

    public MenuSliderView(AppCompatActivity context, RestaurantMenu menu, Response.Listener<MenuSection> onAddSectionToFilter, Response.Listener<MenuSection> onRemoveSectionToFilter)
    {
        this.context = context;
        this.menu = menu;
        this.onAddSectionToFilter = onAddSectionToFilter;
        this.onRemoveSectionToFilter = onRemoveSectionToFilter;

        setSlider();
    }

    private void setSlider()
    {
        LinearLayout sliderLayout = (LinearLayout)context.findViewById(R.id.sliderLayout);
        sliderLayout.removeAllViews();

        for(int count = 0; count < menu.sections.length; count++)
        {
            sliderLayout.addView(getSliderItem(menu.sections[count], onAddSectionToFilter, onRemoveSectionToFilter));
        }
    }

    private View getSliderItem(final MenuSection section, final Response.Listener<MenuSection> onAddToFilter, final Response.Listener<MenuSection> onRemoveFromFilter)
    {
        final LinearLayout slider = (LinearLayout)context.findViewById(R.id.sliderLayout);

        View sectionButton = View.inflate(context, R.layout.menu_slider_item, null);
        final Button button = (Button)sectionButton.findViewById(R.id.button);
        button.setTag(R.string.selected, false);
        button.setText(section.name);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                boolean isSelected = (boolean)button.getTag(R.string.selected);

                for(int count = 0; count < slider.getChildCount(); count++)
                {
                    Button b = slider.getChildAt(count).findViewById(R.id.button);
                    b.setTag(R.string.selected, false);
                    b.setTextColor(context.getResources().getColor(R.color.darker_gray));
                }

                button.setTag(R.string.selected, !isSelected);

                if((boolean)button.getTag(R.string.selected))
                {
                    onAddToFilter.onResponse(section);
                    button.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                }
                else
                {
                    onRemoveFromFilter.onResponse(section);
                }
            }
        });
        return sectionButton;
    }
}
