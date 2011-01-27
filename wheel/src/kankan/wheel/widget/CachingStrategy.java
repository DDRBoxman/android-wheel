/*
 *  Android Wheel Control.
 *  https://code.google.com/p/android-wheel/
 *  
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

package kankan.wheel.widget;

import java.util.LinkedList;
import java.util.List;

import android.view.View;
import android.widget.LinearLayout;

/**
 * Caching strategy stores wheel items to reuse. 
 */
public class CachingStrategy {
	
	// Cached center view
	private View labelView;
	
	// Cached items
	private List<View> cachedItems;
	
	// Cached empty items
	private List<View> cachedEmptyItems;
	
	// Wheel view
	private WheelView wheel;
	
	/**
	 * Constructor
	 * @param wheel the wheel view
	 */
	public CachingStrategy(WheelView wheel) {
		this.wheel = wheel;
	}

	/**
	 * Caches items from specified layout.
	 * There are saved only items not included to specified range.
	 * All the cached items are removed from original layout.
	 * 
	 * @param layout the layout containing items to be cached
	 * @param firstItem the number of first item in layout
	 * @param range the range of current wheel items 
	 * @return the new value of first item number
	 */
	public int cacheItems(LinearLayout layout, int firstItem, ItemsRange range) {
		int index = firstItem;
		for (int i = 0; i < layout.getChildCount();) {
			if (!range.contains(index)) {
				addViewToCache(layout.getChildAt(i), index);
				layout.removeViewAt(i);
				if (i == 0) { // first item
					firstItem++;
				}
			} else {
				i++; // go to next item
			}
			index++;
		}
		return firstItem;
	}
	
	/**
	 * Caches label view
	 * @param view the view to be cached
	 */
	public void cacheLabelView(View view) {
		labelView = view;
	}
	
	/**
	 * Gets cached label view
	 * @return the cached view
	 */
	public View getCachedLabelView() {
		return labelView;
	}
	
	/**
	 * Gets item view
	 * @return the cached view
	 */
	public View getCachedItem() {
		return getCachedView(cachedItems);
	}

	/**
	 * Gets empty item view
	 * @return the cached empty view
	 */
	public View getCachedEmptyItem() {
		return getCachedView(cachedEmptyItems);
	}
	
	/**
	 * Clears all cached views 
	 */
	public void clearCache() {
		if (cachedItems != null) {
			cachedItems.clear();
		}
		if (cachedEmptyItems != null) {
			cachedEmptyItems.clear();
		}
		
		labelView = null;
	}

	/**
	 * Adds view to specified cache. Creates a cache list if it is null.
	 * @param view the view to be cached
	 * @param cache the cache list
	 * @return the cache list
	 */
	private List<View> addViewToCache(View view, List<View> cache) {
		if (cache == null) {
			cache = new LinkedList<View>();
		}
		
		cache.add(view);
		return cache;
	}

	/**
	 * Adds view to cache. Determines view type (item view or empty one) by index.
	 * @param view the view to be cached
	 * @param index the index of view
	 */
	private void addViewToCache(View view, int index) {
		int count = wheel.getViewAdapter().getItemsCount();

		if ((index < 0 || index >= count) && !wheel.isCyclic()) {
			// empty view
			cachedEmptyItems = addViewToCache(view, cachedEmptyItems);
		} else {
			while (index < 0) {
				index = count + index;
			}
			index %= count;
			cachedItems = addViewToCache(view, cachedItems);
		}
	}
	
	/**
	 * Gets view from specified cache.
	 * @param cache the cache
	 * @return the first view from cache.
	 */
	private View getCachedView(List<View> cache) {
		if (cache != null && cache.size() > 0) {
			View view = cache.get(0);
			cache.remove(0);
			return view;
		}
		return null;
	}

}
