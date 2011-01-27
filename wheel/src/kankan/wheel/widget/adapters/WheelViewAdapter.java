/*
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package kankan.wheel.widget.adapters;

import android.view.View;

/**
 * Wheel items adapter interface
 */
public interface WheelViewAdapter {
	/**
	 * Gets items count
	 * @return the count of wheel items
	 */
	public int getItemsCount();
	
	/**
	 * Get a View that displays the data at the specified position in the data set
	 * 
	 * @param index the item index
	 * @param cachedView the old view to reuse if possible
	 * @return the wheel item View
	 */
	public View getItem(int index, View cachedView);

	/**
	 * Get a View that displays an empty part of wheel
	 * 
	 * @param cachedView the old view to reuse if possible
	 * @return the empty wheel item View
	 */
	public View getEmptyItem(View cachedView);

	/**
	 * Get a View that displays an empty part of wheel
	 * 
	 * @param cachedView the old view to reuse if possible
	 * @return the wheel item View
	 */
	public View getEmptyExtendedItem(View cachedView);

	/**
	 * Get a View that displays the wheel label
	 * 
	 * @param label the wheel label
	 * @param cachedView the old view to reuse if possible
	 * @return the wheel item View
	 */
	public View getLabelItem(String label, View cachedView);
}
