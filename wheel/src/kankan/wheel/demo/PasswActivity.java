package kankan.wheel.demo;

import kankan.wheel.R;
import kankan.wheel.widget.NumericWheelAdapter;
import kankan.wheel.widget.WheelView;
import android.app.Activity;
import android.os.Bundle;

public class PasswActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.passw_layout);
        initWheel(R.id.passw_1);
        initWheel(R.id.passw_2);
        initWheel(R.id.passw_3);
        initWheel(R.id.passw_4);
    }
    
    /**
     * Initializes wheel
     * @param id the wheel widget id
     */
    private void initWheel(int id) {
        WheelView wheel = (WheelView) findViewById(id);
        wheel.setAdapter(new NumericWheelAdapter(0, 9));
        wheel.setCurrentItem((int)(Math.random() * 10));
    }
}
