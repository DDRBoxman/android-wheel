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

import kankan.wheel.R;
import kankan.wheel.widget.adapters.AdapterWheel;
import kankan.wheel.widget.adapters.WheelViewAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Numeric wheel view.
 * 
 * @author Yuri Kanivets
 */
public class WheelView extends View {
	/** Scrolling duration */
	private static final int SCROLLING_DURATION = 400;

	/** Minimum delta for scrolling */
	private static final int MIN_DELTA_FOR_SCROLLING = 1;

	/** Top and bottom shadows colors */
	private static final int[] SHADOWS_COLORS = new int[] { 0xFF111111,
			0x00AAAAAA, 0x00AAAAAA };

	/** Top and bottom items offset (to hide that) */
	private static final int ITEM_OFFSET_PERCENT = 10;

	/** Label offset */
	private static final int LABEL_OFFSET = 8;

	/** Left and right padding value */
	private static final int PADDING = 10;

	/** Default count of visible items */
	private static final int DEF_VISIBLE_ITEMS = 5;

	// Wheel Values
	private int currentItem = 0;
	
	// Widths
	private int labelWidth = 0;

	// Count of visible items
	private int visibleItems = DEF_VISIBLE_ITEMS;
	
	// Item height
	private int itemHeight = 0;

	// Label & background
	private String label;
	private Drawable centerDrawable;

	// Shadows drawables
	private GradientDrawable topShadow;
	private GradientDrawable bottomShadow;

	// Scrolling
	private boolean isScrollingPerformed; 
	private int scrollingOffset;

	// Scrolling animation
	private GestureDetector gestureDetector;
	private Scroller scroller;
	private int lastScrollY;

	// Cyclic
	boolean isCyclic = false;
	
	// Items layout
	private LinearLayout itemsLayout;
	
	// The number of first item in layout
	private int firstItem;

	// Label view
	private View labelView;
	
	// Layout for extended views
	private LinearLayout extendedLayout;
	
	// Label rebuild flag
	boolean rebuildLabel;
	
	// View adapter
	private WheelViewAdapter viewAdapter;
	
	// Caching
	CachingStrategy cache = new CachingStrategy(this);

	// Listeners
	private List<OnWheelChangedListener> changingListeners = new LinkedList<OnWheelChangedListener>();
	private List<OnWheelScrollListener> scrollingListeners = new LinkedList<OnWheelScrollListener>();

	/**
	 * Constructor
	 */
	public WheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData(context);
	}

	/**
	 * Constructor
	 */
	public WheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context);
	}

	/**
	 * Constructor
	 */
	public WheelView(Context context) {
		super(context);
		initData(context);
	}
	
	/**
	 * Initializes class data
	 * @param context the context
	 */
	private void initData(Context context) {
		gestureDetector = new GestureDetector(context, gestureListener);
		gestureDetector.setIsLongpressEnabled(false);
		
		scroller = new Scroller(context);
	}
	
    /**
     * Gets wheel adapter
     * @deprecated Use {@link getViewAdapter} instead
     * @return the adapter
     */
    public WheelAdapter getAdapter() {
        if (viewAdapter instanceof AdapterWheel) {
            AdapterWheel adapter = (AdapterWheel) viewAdapter;
            return adapter.getAdapter();
        }
        return null;
    }
    
	/**
     * Sets wheel adapter
     * @deprecated Use {@link setViewAdapter} instead
     * @param adapter the new wheel adapter
     */
    public void setAdapter(WheelAdapter adapter) {
        setViewAdapter(new AdapterWheel(getContext(), adapter));
    }
	
	/**
	 * Set the the specified scrolling interpolator
	 * @param interpolator the interpolator
	 */
	public void setInterpolator(Interpolator interpolator) {
		scroller.forceFinished(true);
		scroller = new Scroller(getContext(), interpolator);
	}
	
	/**
	 * Gets count of visible items
	 * 
	 * @return the count of visible items
	 */
	public int getVisibleItems() {
		return visibleItems;
	}

	/**
	 * Sets the desired count of visible items.
	 * Actual amount of visible items depends on wheel layout parameters.
	 * To apply changes and rebuild view call measure(). 
	 * 
	 * @param count the desired count for visible items
	 */
	public void setVisibleItems(int count) {
		visibleItems = count;
	}

	/**
	 * Gets label
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets label. The wheel will be rebuilt, but it will have the same width as before.
	 * Call measure() to totally rebuild the view. 
	 * 
	 * @param newLabel the label to set
	 */
	public void setLabel(String newLabel) {
		if (label == null || !label.equals(newLabel)) {
			label = newLabel;
			
			rebuildLabel = true;
			scrollingOffset = 0;
			invalidate();
		}
	}
	
	/**
	 * Gets label width
	 * @return the label width or 0 if it is not set
	 */
	public int getLabelWidth() {
	    return labelWidth;
	}
	
	/**
	 * Sets label width. Does not take effect if label is not set.
	 * The items area width is the read control width.
	 * @param width the new width
	 */
	public void setLabelWidth(int width) {
	    if (labelWidth != width) {
	        labelWidth = width;
	        
	        if (labelView != null) {
	            invalidateWheel(false);
	        }
	    }
	}
	
	/**
	 * Gets view adapter
	 * @return the view adapter
	 */
	public WheelViewAdapter getViewAdapter() {
		return viewAdapter;
	}

	/**
	 * Sets view adapter. Usually new adapters contain different views, so
	 * it needs to rebuild view by calling measure().
	 *  
	 * @param viewAdapter the view adapter
	 */
	public void setViewAdapter(WheelViewAdapter viewAdapter) {
        this.viewAdapter = viewAdapter;
        
        invalidateWheel(true);
	}
	
	/**
	 * Adds wheel changing listener
	 * @param listener the listener 
	 */
	public void addChangingListener(OnWheelChangedListener listener) {
		changingListeners.add(listener);
	}

	/**
	 * Removes wheel changing listener
	 * @param listener the listener
	 */
	public void removeChangingListener(OnWheelChangedListener listener) {
		changingListeners.remove(listener);
	}
	
	/**
	 * Notifies changing listeners
	 * @param oldValue the old wheel value
	 * @param newValue the new wheel value
	 */
	protected void notifyChangingListeners(int oldValue, int newValue) {
		for (OnWheelChangedListener listener : changingListeners) {
			listener.onChanged(this, oldValue, newValue);
		}
	}

	/**
	 * Adds wheel scrolling listener
	 * @param listener the listener 
	 */
	public void addScrollingListener(OnWheelScrollListener listener) {
		scrollingListeners.add(listener);
	}

	/**
	 * Removes wheel scrolling listener
	 * @param listener the listener
	 */
	public void removeScrollingListener(OnWheelScrollListener listener) {
		scrollingListeners.remove(listener);
	}
	
	/**
	 * Notifies listeners about starting scrolling
	 */
	protected void notifyScrollingListenersAboutStart() {
		for (OnWheelScrollListener listener : scrollingListeners) {
			listener.onScrollingStarted(this);
		}
	}

	/**
	 * Notifies listeners about ending scrolling
	 */
	protected void notifyScrollingListenersAboutEnd() {
		for (OnWheelScrollListener listener : scrollingListeners) {
			listener.onScrollingFinished(this);
		}
	}

	/**
	 * Gets current value
	 * 
	 * @return the current value
	 */
	public int getCurrentItem() {
		return currentItem;
	}

	/**
	 * Sets the current item. Does nothing when index is wrong.
	 * 
	 * @param index the item index
	 * @param animated the animation flag
	 */
	public void setCurrentItem(int index, boolean animated) {
		if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
			return; // throw?
		}
		if (index < 0 || index >= viewAdapter.getItemsCount()) {
			if (isCyclic) {
				while (index < 0) {
					index += viewAdapter.getItemsCount();
				}
				index %= viewAdapter.getItemsCount();
			} else{
				return; // throw?
			}
		}
		if (index != currentItem) {
			if (animated) {
				scroll(index - currentItem, SCROLLING_DURATION);
			} else {
				scrollingOffset = 0;
			
				int old = currentItem;
				currentItem = index;
			
				notifyChangingListeners(old, currentItem);
			
				invalidate();
			}
		}
	}

	/**
	 * Sets the current item w/o animation. Does nothing when index is wrong.
	 * 
	 * @param index the item index
	 */
	public void setCurrentItem(int index) {
		setCurrentItem(index, false);
	}	
	
	/**
	 * Tests if wheel is cyclic. That means before the 1st item there is shown the last one
	 * @return true if wheel is cyclic
	 */
	public boolean isCyclic() {
		return isCyclic;
	}

	/**
	 * Set wheel cyclic flag
	 * @param isCyclic the flag to set
	 */
	public void setCyclic(boolean isCyclic) {
		this.isCyclic = isCyclic;
		invalidateWheel(false);
	}
	
	/**
	 * Invalidates wheel
	 * @param clearCaches if true then cached views will be clear
	 */
    public void invalidateWheel(boolean clearCaches) {
        if (clearCaches) {
            cache.clearCache();
            if (itemsLayout != null) {
                itemsLayout.removeAllViews();
            }
        } else if (itemsLayout != null) {
            // cache all items
	        cache.cacheItems(itemsLayout, firstItem, new ItemsRange(0, 0));         
        }
        
        // extended items are not cached now, so clear it
        if (extendedLayout != null) {
            extendedLayout.removeAllViews();
        }
        
        rebuildLabel = true;
        scrollingOffset = 0;
        invalidate();
	}

	/**
	 * Initializes resources
	 */
	private void initResourcesIfNecessary() {
		if (centerDrawable == null) {
			centerDrawable = getContext().getResources().getDrawable(R.drawable.wheel_val);
		}

		if (topShadow == null) {
			topShadow = new GradientDrawable(Orientation.TOP_BOTTOM, SHADOWS_COLORS);
		}

		if (bottomShadow == null) {
			bottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP, SHADOWS_COLORS);
		}

		setBackgroundResource(R.drawable.wheel_bg);
	}

	/**
	 * Calculates desired height for layout
	 * 
	 * @param layout
	 *            the source layout
	 * @return the desired layout height
	 */
	private int getDesiredHeight(LinearLayout layout) {
		if (layout != null && layout.getChildAt(0) != null) {
			itemHeight = layout.getChildAt(0).getMeasuredHeight();
		}

		int desired = itemHeight * visibleItems - itemHeight * ITEM_OFFSET_PERCENT / 50;

		return Math.max(desired, getSuggestedMinimumHeight());
	}

	/**
	 * Returns height of wheel item
	 * @return the item height
	 */
	private int getItemHeight() {
		if (itemHeight != 0) {
			return itemHeight;
		}
		
		if (itemsLayout != null && itemsLayout.getChildAt(0) != null) {
			itemHeight = itemsLayout.getChildAt(0).getHeight();
			return itemHeight;
		}
		
		return getHeight() / visibleItems;
	}

	/**
	 * Calculates control width and creates text layouts
	 * @param widthSize the input layout width
	 * @param mode the layout mode
	 * @return the calculated control width
	 */
	private int calculateLayoutWidth(int widthSize, int mode) {
		initResourcesIfNecessary();

		int width = widthSize;

		itemsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    itemsLayout.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.UNSPECIFIED), 
	                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		int itemsWidth = itemsLayout.getMeasuredWidth();

		int newLabelWidth = 0;
		if (labelView != null) {
            labelView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            
		    if (labelWidth != 0) {
		        newLabelWidth = labelWidth;
                labelView.measure(MeasureSpec.makeMeasureSpec(labelWidth, MeasureSpec.EXACTLY), 
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		    } else {
	            labelView.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.UNSPECIFIED), 
	                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	            newLabelWidth = labelView.getMeasuredWidth();
		    }
		}

		boolean recalculate = false;
		if (mode == MeasureSpec.EXACTLY) {
			width = widthSize;
			recalculate = true;
		} else {
			width = itemsWidth + newLabelWidth + 2 * PADDING;
			if (newLabelWidth > 0) {
				width += LABEL_OFFSET;
			}

			// Check against our minimum width
			width = Math.max(width, getSuggestedMinimumWidth());

			if (mode == MeasureSpec.AT_MOST && widthSize < width) {
				width = widthSize;
				recalculate = true;
			}
		}
		
		if (recalculate) {
		    int pureWidth = width - LABEL_OFFSET - 2 * PADDING;
		    if (newLabelWidth > 0) {
	            itemsWidth = Math.max(pureWidth - newLabelWidth, 0);
	            newLabelWidth = Math.min(pureWidth, newLabelWidth);
	        } else {
	            itemsWidth = pureWidth + LABEL_OFFSET; // no label
	        }
		}

		measureLayouts(itemsWidth, newLabelWidth);

		return width;
	}
	
	void measureLayouts(int itemsWidth, int labelWidth) {
		itemsLayout.measure(MeasureSpec.makeMeasureSpec(itemsWidth, MeasureSpec.EXACTLY), 
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

		if (labelView != null) {
			labelView.measure(MeasureSpec.makeMeasureSpec(labelWidth, MeasureSpec.EXACTLY), 
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			
			extendedLayout.measure(MeasureSpec.makeMeasureSpec(labelWidth + LABEL_OFFSET, MeasureSpec.EXACTLY), 
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		}
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		buildViewForMeasuring();
		
		int width = calculateLayoutWidth(widthSize, widthMode);

		int height;
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = getDesiredHeight(itemsLayout);

			if (heightMode == MeasureSpec.AT_MOST) {
				height = Math.min(height, heightSize);
			}
		}

		setMeasuredDimension(width, height);
	}
	
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	layout(r - l, b - t);
    }

    /**
     * Sets layouts width and height
     * @param width the layout width
     * @param height the layout height
     */
    private void layout(int width, int height) {
		int pureWidth = width - LABEL_OFFSET - 2 * PADDING;
		
		int itemsWidth = 0;
		int newLabelWidth = 0;
		if (labelView != null) {
		    newLabelWidth = labelView.getMeasuredWidth();
		}

		if (newLabelWidth > 0) {
		    itemsWidth = Math.max(pureWidth - newLabelWidth, 0);
		    newLabelWidth = Math.min(pureWidth, newLabelWidth);
		} else {
			itemsWidth = pureWidth + LABEL_OFFSET; // no label
		}
		
		itemsLayout.layout(0, 0, itemsWidth, height);

		if (labelView != null) {
			labelView.layout(0, 0, newLabelWidth, Math.min(labelView.getMeasuredHeight(), height));
			
			extendedLayout.layout(0, 0, newLabelWidth + LABEL_OFFSET, height);
		}		
    }

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
		    return;
		}
		
		updateView();

		drawItems(canvas);
		drawLabel(canvas);
		drawCenterRect(canvas);
        drawShadows(canvas);
	}

	/**
	 * Draws shadows on top and bottom of control
	 * @param canvas the canvas for drawing
	 */
	private void drawShadows(Canvas canvas) {
		int height = (int)(1.5 * getItemHeight());
		topShadow.setBounds(0, 0, getWidth(), height);
		topShadow.draw(canvas);

		bottomShadow.setBounds(0, getHeight() - height, getWidth(), getHeight());
		bottomShadow.draw(canvas);
	}

	/**
	 * Draws label layout
	 * @param canvas the canvas for drawing
	 */
	private void drawLabel(Canvas canvas) {
		if (labelView != null) {
	        int top = (getHeight() - getItemHeight())/ 2;
	        
			canvas.save();
			canvas.translate(itemsLayout.getWidth() + LABEL_OFFSET + PADDING, top);
			labelView.draw(canvas);
			canvas.restore();
		}
	}

	/**
	 * Draws items
	 * @param canvas the canvas for drawing
	 */
	private void drawItems(Canvas canvas) {
		canvas.save();
		
		int top = (currentItem - firstItem) * getItemHeight() + (getItemHeight() - getHeight()) / 2;
		canvas.translate(PADDING, - top + scrollingOffset);
		
		// draw items
		itemsLayout.draw(canvas);
		
		// draw empty space
		if (labelView != null) {
			canvas.translate(itemsLayout.getWidth(), 0);
			extendedLayout.draw(canvas);
		}
		
		canvas.restore();
	}

	/**
	 * Draws rect for current value
	 * @param canvas the canvas for drawing
	 */
	private void drawCenterRect(Canvas canvas) {
		int center = getHeight() / 2;
		int offset = (int) (getItemHeight() / 2 * 1.2);
		centerDrawable.setBounds(0, center - offset, getWidth(), center + offset);
		centerDrawable.draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (getViewAdapter() == null) {
			return true;
		}
		
		switch (event.getAction()) {
		    case MotionEvent.ACTION_MOVE:
		        if (getParent() != null) {
		            getParent().requestDisallowInterceptTouchEvent(true);
		        }
		        break;
		        
		    case MotionEvent.ACTION_UP:
		        if (!isScrollingPerformed) {
		            // scroll to this position
		            int distance = (int) event.getY() - getHeight() / 2;
		            if (distance > 0) {
		                distance += getItemHeight() / 2;
		            } else {
                        distance -= getItemHeight() / 2;
		            }
		            int items = distance / getItemHeight();
		            if (items != 0 && isValidItemIndex(currentItem + items)) {
                        scroll(items, SCROLLING_DURATION);
		            }
		        }
		        break;
		}
		
		if (!gestureDetector.onTouchEvent(event) && event.getAction() == MotionEvent.ACTION_UP) {
			justify();
		}

		return true;
	}
	
	/**
	 * Scrolls the wheel
	 * @param delta the scrolling value
	 */
	private void doScroll(int delta) {
		scrollingOffset += delta;
		
		int count = scrollingOffset / getItemHeight();
		int pos = currentItem - count;
		int itemCount = viewAdapter.getItemsCount();
		if (isCyclic && itemCount > 0) {
			// fix position by rotating
			while (pos < 0) {
				pos += itemCount;
			}
			pos %= itemCount;
		} else if (isScrollingPerformed) {
			// 
			if (pos < 0) {
				count = currentItem;
				pos = 0;
			} else if (pos >= itemCount) {
				count = currentItem - itemCount + 1;
				pos = itemCount - 1;
			}
		} else {
			// fix position
			pos = Math.max(pos, 0);
			pos = Math.min(pos, itemCount - 1);
		}
		
		int offset = scrollingOffset;
		if (pos != currentItem) {
			setCurrentItem(pos, false);
		} else {
			invalidate();
		}
		
		// update offset
		scrollingOffset = offset - count * getItemHeight();
		if (scrollingOffset > getHeight()) {
			scrollingOffset = scrollingOffset % getHeight() + getHeight();
		}
	}
	
	// gesture listener
	private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
		public boolean onDown(MotionEvent e) {
			if (isScrollingPerformed) {
				scroller.forceFinished(true);
				clearMessages();
				return true;
			}
			return false;
		}
		
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			startScrolling();
			doScroll((int)-distanceY);
			return true;
		}
		
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			lastScrollY = currentItem * getItemHeight() + scrollingOffset;
			int maxY = isCyclic ? 0x7FFFFFFF : viewAdapter.getItemsCount() * getItemHeight();
			int minY = isCyclic ? -maxY : 0;
			scroller.fling(0, lastScrollY, 0, (int) -velocityY / 2, 0, 0, minY, maxY);
			setNextMessage(MESSAGE_SCROLL);
			return true;
		}
	};

	// Messages
	private final int MESSAGE_SCROLL = 0;
	private final int MESSAGE_JUSTIFY = 1;
	
	/**
	 * Set next message to queue. Clears queue before.
	 * 
	 * @param message the message to set
	 */
	private void setNextMessage(int message) {
		clearMessages();
		animationHandler.sendEmptyMessage(message);
	}

	/**
	 * Clears messages from queue
	 */
	private void clearMessages() {
		animationHandler.removeMessages(MESSAGE_SCROLL);
		animationHandler.removeMessages(MESSAGE_JUSTIFY);
	}
	
	// animation handler
	private Handler animationHandler = new Handler() {
		public void handleMessage(Message msg) {
			scroller.computeScrollOffset();
			int currY = scroller.getCurrY();
			int delta = lastScrollY - currY;
			lastScrollY = currY;
			if (delta != 0) {
				doScroll(delta);
			}
			
			// scrolling is not finished when it comes to final Y
			// so, finish it manually 
			if (Math.abs(currY - scroller.getFinalY()) < MIN_DELTA_FOR_SCROLLING) {
				currY = scroller.getFinalY();
				scroller.forceFinished(true);
			}
			if (!scroller.isFinished()) {
				animationHandler.sendEmptyMessage(msg.what);
			} else if (msg.what == MESSAGE_SCROLL) {
				justify();
			} else {
				finishScrolling();
			}
		}
	};
	
	/**
	 * Justifies wheel
	 */
	private void justify() {
		if (viewAdapter == null) {
			return;
		}
		
		lastScrollY = 0;
		int offset = scrollingOffset;
		int itemHeight = getItemHeight();
		boolean needToIncrease = offset > 0 ? currentItem < viewAdapter.getItemsCount() : currentItem > 0; 
		if ((isCyclic || needToIncrease) && Math.abs((float) offset) > (float) itemHeight / 2) {
			if (offset < 0)
				offset += itemHeight + MIN_DELTA_FOR_SCROLLING;
			else
				offset -= itemHeight + MIN_DELTA_FOR_SCROLLING;
		}
		if (Math.abs(offset) > MIN_DELTA_FOR_SCROLLING) {
			scroller.startScroll(0, 0, 0, offset, SCROLLING_DURATION);
			setNextMessage(MESSAGE_JUSTIFY);
		} else {
			finishScrolling();
		}
	}
	
	/**
	 * Starts scrolling
	 */
	private void startScrolling() {
		if (!isScrollingPerformed) {
			isScrollingPerformed = true;
			notifyScrollingListenersAboutStart();
		}
	}

	/**
	 * Finishes scrolling
	 */
	void finishScrolling() {
		if (isScrollingPerformed) {
			notifyScrollingListenersAboutEnd();
			isScrollingPerformed = false;
		}
		
		scrollingOffset = 0;
		invalidate();
	}
	
	
	/**
	 * Scroll the wheel
	 * @param itemsToSkip items to scroll
	 * @param time scrolling duration
	 */
	public void scroll(int itemsToScroll, int time) {
		scroller.forceFinished(true);

		lastScrollY = scrollingOffset;
		int offset = itemsToScroll * getItemHeight();
		
		scroller.startScroll(0, lastScrollY, 0, offset - lastScrollY, time);
		setNextMessage(MESSAGE_SCROLL);
		
		startScrolling();
	}
	
	/**
	 * Calculates range for wheel items
	 * @return the items range
	 */
	private ItemsRange getItemsRange() {
        if (getItemHeight() == 0) {
            return null;
        }
        
		int first = currentItem;
		int count = 1;
		
		while (count * getItemHeight() < getHeight()) {
			first--;
			count += 2; // top + bottom items
		}
		
		if (scrollingOffset != 0) {
			if (scrollingOffset > 0) {
				first--;
			}
			count++;
			first -= scrollingOffset / getItemHeight();			
		}
		return new ItemsRange(first, count);
	}
	
	/**
	 * Rebuilds wheel items if necessary. Caches all unused items.
	 * 
	 * @return true if items are rebuilt
	 */
	private boolean rebuildItems() {
		boolean updated = false;
		ItemsRange range = getItemsRange();
		if (itemsLayout != null) {
			int first = cache.cacheItems(itemsLayout, firstItem, range);
			updated = firstItem != first;
			firstItem = first;
		} else {
			createItemsLayout();
			updated = true;
		}
		
		if (!updated) {
			updated = firstItem != range.getFirst() || itemsLayout.getChildCount() != range.getCount();
		}
		
		if (firstItem > range.getFirst() && firstItem <= range.getLast()) {
			for (int i = firstItem - 1; i >= range.getFirst(); i--) {
				if (!addViewItem(i, true)) {
				    break;
				}
				firstItem = i;
			}			
		} else {
		    firstItem = range.getFirst();
		}
		
		int first = firstItem;
		for (int i = itemsLayout.getChildCount(); i < range.getCount(); i++) {
			if (!addViewItem(firstItem + i, false) && itemsLayout.getChildCount() == 0) {
			    first++;
			}
		}
		firstItem = first;
		
		return updated;
	}
	
	/**
	 * Updates view. Rebuilds items and label if necessary, recalculate items sizes.
	 */
	private void updateView() {
		boolean updated = rebuildItems();
		
		if (rebuildLabel) {
			rebuildLabel();
		} else if (labelView != null) {
		    createExtendedLayout(itemsLayout.getChildCount());
		}
		
		if (updated || rebuildLabel) {
			calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
			layout(getWidth(), getHeight());
			
            rebuildLabel = false;
		}
	}

	/**
	 * Creates item layouts if necessary
	 */
	private void createItemsLayout() {
		if (itemsLayout == null) {
			itemsLayout = new LinearLayout(getContext());
			itemsLayout.setOrientation(LinearLayout.VERTICAL);
		}
	}

	/**
	 * Creates layout for extended items
	 * @param size the extended items count
	 */
	private void createExtendedLayout(int size) {
		if (extendedLayout == null) {
			extendedLayout = new LinearLayout(getContext());
			extendedLayout.setOrientation(LinearLayout.VERTICAL);
		}
		
		if (extendedLayout.getChildCount() > size) {
			extendedLayout.removeViews(size, extendedLayout.getChildCount() - size);
		} else {
			for (int i = extendedLayout.getChildCount(); i < size; i++) {
				View emptyView = viewAdapter.getEmptyExtendedItem(null);
				if (emptyView != null) {
					extendedLayout.addView(emptyView);
				}
			}
		}
	}

	/**
	 * Rebuilds label view
	 */
	private void rebuildLabel() {
		if (label != null && label.length() > 0 && viewAdapter != null) {
			labelView = viewAdapter.getLabelItem(label, cache.getCachedLabelView());
			cache.cacheLabelView(labelView);
			
			int size = itemsLayout != null ? itemsLayout.getChildCount() : 0;
			createExtendedLayout(size);
		} else {
			labelView = null;
			if (extendedLayout != null) {
				extendedLayout.removeAllViews();				
			}
		}
	}
	
	/**
	 * Builds view for measuring
	 */
	private void buildViewForMeasuring() {
		// clear all items
		if (itemsLayout != null) {
			cache.cacheItems(itemsLayout, firstItem, new ItemsRange(0, 0));			
		} else {
			createItemsLayout();
		}
		
		// add views
		int addItems = visibleItems / 2;
		for (int i = currentItem + addItems; i >= currentItem - addItems; i--) {
			if (addViewItem(i, true)) {
			    firstItem = i;
			}
		}
		
		rebuildLabel();
	}

	/**
	 * Adds view for item to items layout
	 * @param index the item index
	 * @param first the flag indicates if view should be first
	 * @return true if corresponding item exists and is added
	 */
	private boolean addViewItem(int index, boolean first) {
		View view = getItemView(index);
		if (view != null) {
			if (first) {
				itemsLayout.addView(view, 0);
			} else {
				itemsLayout.addView(view);
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks whether intem index is valid
	 * @param index the item index
	 * @return true if item index is not out of bounds or the wheel is cyclic
	 */
	private boolean isValidItemIndex(int index) {
	    return viewAdapter != null && viewAdapter.getItemsCount() > 0 &&
	        (isCyclic || index >= 0 && index < viewAdapter.getItemsCount());
	}
	
	/**
	 * Returns view for specified item
	 * @param index the item index
	 * @return item view or empty view if index is out of bounds
	 */
	private View getItemView(int index) {
		if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
			return null;
		}
		int count = viewAdapter.getItemsCount();
		if (!isValidItemIndex(index)) {
			return viewAdapter.getEmptyItem(cache.getCachedEmptyItem());
		} else {
			while (index < 0) {
				index = count + index;
			}
		}
		
		index %= count;
		return viewAdapter.getItem(index, cache.getCachedItem());
	}
}
