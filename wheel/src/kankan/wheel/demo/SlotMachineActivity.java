package kankan.wheel.demo;

import kankan.wheel.R;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.WheelViewAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SlotMachineActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.slot_machine_layout);
        initWheel(R.id.slot_1);
        initWheel(R.id.slot_2);
        initWheel(R.id.slot_3);
        
        Button mix = (Button)findViewById(R.id.btn_mix);
        mix.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mixWheel(R.id.slot_1);
                mixWheel(R.id.slot_2);
                mixWheel(R.id.slot_3);
            }
        });
        
        updateStatus();
    }
    
    // Wheel scrolled flag
    private boolean wheelScrolled = false;
    
    // Wheel scrolled listener
    OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
        public void onScrollingStarted(WheelView wheel) {
            wheelScrolled = true;
        }
        public void onScrollingFinished(WheelView wheel) {
            wheelScrolled = false;
            updateStatus();
        }
    };
    
    // Wheel changed listener
    private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            if (!wheelScrolled) {
                updateStatus();
            }
        }
    };
    
    /**
     * Updates status
     */
    private void updateStatus() {
        TextView text = (TextView) findViewById(R.id.pwd_status);
        if (test()) {
            text.setText("Congratulation!");
        } else {
            text.setText("");
        }
    }

    /**
     * Initializes wheel
     * @param id the wheel widget Id
     */
    private void initWheel(int id) {
        WheelView wheel = getWheel(id);
        wheel.setViewAdapter(new SlotMachineAdapter(this));
        wheel.setCurrentItem((int)(Math.random() * 10));
        
        wheel.addChangingListener(changedListener);
        wheel.addScrollingListener(scrolledListener);
        wheel.setCyclic(true);
        wheel.setInterpolator(new AnticipateOvershootInterpolator());
    }
    
    /**
     * Returns wheel by Id
     * @param id the wheel Id
     * @return the wheel with passed Id
     */
    private WheelView getWheel(int id) {
        return (WheelView) findViewById(id);
    }
    
    /**
     * Tests wheels
     * @return true 
     */
    private boolean test() {
        int value = getWheel(R.id.slot_1).getCurrentItem();
        return testWheelValue(R.id.slot_2, value) && testWheelValue(R.id.slot_3, value);
    }
    
    /**
     * Tests wheel value
     * @param id the wheel Id
     * @param value the value to test
     * @return true if wheel value is equal to passed value
     */
    private boolean testWheelValue(int id, int value) {
        return getWheel(id).getCurrentItem() == value;
    }
    
    /**
     * Mixes wheel
     * @param id the wheel id
     */
    private void mixWheel(int id) {
        WheelView wheel = getWheel(id);
        wheel.scroll(-25 + (int)(Math.random() * 50), 2000);
    }
    
    /**
     * Slot machine adapter
     */
    private class SlotMachineAdapter implements WheelViewAdapter {
        // Slot machine symbols
        private final int items[] = new int[] {
                android.R.drawable.star_big_on,
                android.R.drawable.stat_sys_warning,
                android.R.drawable.radiobutton_on_background,
                android.R.drawable.ic_delete
        };
        
        // Layout inflater
        private LayoutInflater inflater;
        
        /**
         * Constructor
         */
        public SlotMachineAdapter(Context context) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getItemsCount() {
            return items.length;
        }

        @Override
        public View getItem(int index, View cachedView) {
            View layout;
            if (cachedView != null) {
                layout = cachedView;
            } else {
                layout = inflater.inflate(R.layout.slot_item, null, false);
            }
            
            ImageView view = (ImageView) layout.findViewById(R.id.item);
            view.setImageResource(items[index]);

            return layout;
        }

        @Override
        public View getEmptyItem(View cachedView) {
            return null;
        }

        @Override
        public View getEmptyExtendedItem(View cachedView) {
            return null;
        }

        @Override
        public View getLabelItem(String label, View cachedView) {
            return null;
        }
    }
}
