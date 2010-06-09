/*
 *  Copyright (C) 2010  Yuri Kanivets
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package kankan.wheel;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;

public class TimeActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time_layout);
        
        NumericWheel hours = (NumericWheel) findViewById(R.id.hour);
        hours.setMaxValue(23);
        hours.setLabel("hour");
        
        NumericWheel mins = (NumericWheel) findViewById(R.id.mins);
        mins.setMaxValue(59);
        mins.setLabel("mins");
        
        Calendar c = Calendar.getInstance();
        mins.setValue(c.get(Calendar.MINUTE));
        hours.setValue(c.get(Calendar.HOUR_OF_DAY));
    }
}
