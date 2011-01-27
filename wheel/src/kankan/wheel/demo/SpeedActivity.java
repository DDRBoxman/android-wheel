package kankan.wheel.demo;

import kankan.wheel.R;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SpeedActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.speed1_layout);
        final WheelView speed = (WheelView) findViewById(R.id.speed);
        final SpeedAdapter speedAdapter = new SpeedAdapter(this, 245, 5);
        speed.setViewAdapter(speedAdapter);
        speed.setLabel("           "); // for units
        
        final String unitsValues[] = new String[] {
                "km/h",
                "m/h",
                "m/s",
        };
        
        final WheelView units = (WheelView) findViewById(R.id.units);
        ArrayWheelAdapter<String> unitsAdapter =
            new ArrayWheelAdapter<String>(this, unitsValues);
        unitsAdapter.setItemResource(R.layout.units_item);
        unitsAdapter.setItemTextResource(R.id.text);
        unitsAdapter.setEmptyItemResource(R.layout.units_item);
        unitsAdapter.setExtendedItemResource(R.layout.units_item);
        unitsAdapter.setLabelResource(R.layout.wheel_text_item);
        unitsAdapter.setLabelTextResource(R.id.text);
        units.setViewAdapter(unitsAdapter);
        units.setLabel("units");
        //units.setVisibleItems(3);
        
        units.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String value = unitsValues[units.getCurrentItem()];
                speedAdapter.setUnits(" " + value);
                speed.invalidateWheel(false);
            }
        });
        
        units.setCurrentItem(1);
    }
    
    /**
     * Speed adapter
     */
    private class SpeedAdapter extends NumericWheelAdapter {
        // Speed units
        private String units;
        // Items step value
        private int step;

        /**
         * Constructor
         */
        public SpeedAdapter(Context context, int maxValue, int step) {
            super(context, 0, maxValue / step);
            this.step = step;
            
            setItemResource(R.layout.wheel_text_item);
            setItemTextResource(R.id.text);
            setExtendedItemResource(R.layout.wheel_text_item);
            setLabelResource(R.layout.wheel_text_item);
            setLabelTextResource(R.id.text);
        }
        
        /**
         * Sets units
         */
        public void setUnits(String units) {
            this.units = units;
        }
        
        @Override
        public CharSequence getItemText(int index) {
            if (index >= 0 && index < getItemsCount()) {
                int value = index * step;
                return Integer.toString(value);
            }
            return null;
        }
        
        @Override
        public View getEmptyExtendedItem(View cachedView) {
            View view = super.getEmptyExtendedItem(cachedView);
            TextView text = (TextView) view.findViewById(R.id.text);
            text.setText(units);
            text.setTextColor(0xFF444444);
            return view;
        }
    }
}
