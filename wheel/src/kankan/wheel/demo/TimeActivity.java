package kankan.wheel.demo;

import java.util.Calendar;

import kankan.wheel.R;
import kankan.wheel.widget.NumericWheelAdapter;
import kankan.wheel.widget.WheelView;

import android.app.Activity;
import android.os.Bundle;

public class TimeActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time_layout);
        
        WheelView hours = (WheelView) findViewById(R.id.hour);
        hours.setAdapter(new NumericWheelAdapter(0, 23));
        hours.setLabel("hour");
        
        WheelView mins = (WheelView) findViewById(R.id.mins);
        mins.setAdapter(new NumericWheelAdapter(0, 59));
        mins.setLabel("mins");
        
        Calendar c = Calendar.getInstance();
        mins.setCurrentItem(c.get(Calendar.MINUTE));
        hours.setCurrentItem(c.get(Calendar.HOUR_OF_DAY));
    }
}
