package kankan.wheel.demo;

import kankan.wheel.R;
import kankan.wheel.widget.ArrayWheelAdapter;
import kankan.wheel.widget.WheelView;

import android.app.Activity;
import android.os.Bundle;

public class CitiesActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cities_layout);
        
        WheelView city = (WheelView) findViewById(R.id.city);
        String cities[] = new String[] {"New York", "Washington", "Chicago",
        		"Los Angeles", "Atlanta", "Boston", "Miami", "Orlando"};
        city.setAdapter(new ArrayWheelAdapter<String>(cities));
        city.setVisibleItems(7);
        city.setCurrentItem(5);
    }
}
