/*
 *  Android Wheel Control.
 *  http://android-devblog.blogspot.com/2010/05/wheel-ui-contol.html
 *  
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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

/**
 * Numeric wheel view.
 * 
 * @author Yuri Kanivets
 */
public class NumericWheel extends View {
	/** Current value & label text color */
	private static final int VALUE_TEXT_COLOR = 0xE0000000;

	/** Items text color */
	private static final int ITEMS_TEXT_COLOR = 0xFF000000;

	/** Top and bottom shadows colors */
	private static final int[] SHADOWS_COLORS = new int[] { 0xFF111111,
			0x00AAAAAA, 0x00AAAAAA };

	/** Additional items height (is added to standard text item height) */
	private static final int ADDITIONAL_ITEM_HEIGHT = 15;

	/** Text size */
	private static final int TEXT_SIZE = 24;

	/** Top and bottom items offset (to hide that) */
	private static final int ITEM_OFFSET = TEXT_SIZE / 5;

	/** Additional width for items layout */
	private static final int ADDITIONAL_ITEMS_SPACE = 10;

	/** Label offset */
	private static final int LABEL_OFFSET = 8;

	/** Left and right padding value */
	private static final int PADDING = 10;

	/** Default count of visible items */
	private static final int DEF_VISIBLE_ITEMS = 5;

	// Wheel Values
	private int minValue = 0;
	private int maxValue = 9;
	private int currentValue = 3;

	// Count of visible items
	private int visibleItems = DEF_VISIBLE_ITEMS;

	// Text paints
	private TextPaint itemsPaint;
	private TextPaint valuePaint;

	// Layouts
	private StaticLayout itemsLayout;
	private StaticLayout labelLayout;
	private StaticLayout valueLayout;

	// Label & background
	private String label;
	private Drawable centerDrawable;

	// Shadows drawables
	private GradientDrawable topShadow;
	private GradientDrawable bottomShadow;

	// Last touch Y position
	private float lastYTouch;

	/**
	 * Constructor
	 */
	public NumericWheel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Constructor
	 */
	public NumericWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Constructor
	 */
	public NumericWheel(Context context) {
		super(context);
	}

	/**
	 * Gets min value
	 * 
	 * @return the min value
	 */
	public int getMinValue() {
		return minValue;
	}

	/**
	 * Sets min value
	 * 
	 * @param value
	 *            the min value to set
	 */
	public void setMinValue(int value) {
		minValue = value;
		invalidate();
	}

	/**
	 * Gets max value
	 * 
	 * @return the max value
	 */
	public int getMaxValue() {
		return maxValue;
	}

	/**
	 * Sets max value
	 * 
	 * @param value
	 *            the max value to set
	 */
	public void setMaxValue(int value) {
		maxValue = value;
		invalidate();
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
	 * Sets count of visible items
	 * 
	 * @param count
	 *            the new count
	 */
	public void setVisibleItems(int count) {
		visibleItems = count;
		invalidate();
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
	 * Sets label
	 * 
	 * @param newLabel
	 *            the label to set
	 */
	public void setLabel(String newLabel) {
		label = newLabel;
		labelLayout = null;
		invalidate();
	}

	/**
	 * Gets current value
	 * 
	 * @return the current value
	 */
	public int getValue() {
		return currentValue;
	}

	/**
	 * Sets current value
	 * 
	 * @param value the value to set
	 */
	public void setValue(int value) {
		if (value != currentValue && value >= minValue && value <= maxValue) {
			itemsLayout = null;
			valueLayout = null;
			currentValue = value;
			invalidate();
		}
	}

	/**
	 * Initializes resources
	 */
	private void initResourcesIfNecessary() {
		if (itemsPaint == null) {
			itemsPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
					| Paint.FAKE_BOLD_TEXT_FLAG);
			itemsPaint.density = getResources().getDisplayMetrics().density;
			itemsPaint.setTextSize(TEXT_SIZE);
		}

		if (valuePaint == null) {
			valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
					| Paint.FAKE_BOLD_TEXT_FLAG | Paint.DITHER_FLAG);
			valuePaint.density = getResources().getDisplayMetrics().density;
			valuePaint.setTextSize(TEXT_SIZE);
			valuePaint.setShadowLayer(0.5f, 0, 0.5f, 0xFFFFFFFF);
		}

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
	private int getDesiredHeight(Layout layout) {
		if (layout == null) {
			return 0;
		}

		int linecount = layout.getLineCount();
		int desired = layout.getLineTop(linecount) - ITEM_OFFSET * 2
				- ADDITIONAL_ITEM_HEIGHT;

		// Check against our minimum height
		desired = Math.max(desired, getSuggestedMinimumHeight());

		return desired;
	}

	/**
	 * Builds text depending on current value
	 * 
	 * @return the text
	 */
	private String buildText() {
		StringBuilder itemsText = new StringBuilder();
		int addItems = visibleItems / 2;
		for (int i = currentValue - addItems; i < currentValue; i++) {
			if (i >= minValue) {
				itemsText.append(Integer.toString(i));
			}
			itemsText.append("\n");
		}
		itemsText.append("\n"); // here will be current value
		for (int i = currentValue + 1; i <= currentValue + addItems; i++) {
			if (i <= maxValue) {
				itemsText.append(Integer.toString(i));
			}
			if (i < currentValue + addItems) {
				itemsText.append("\n");
			}
		}
		return itemsText.toString();
	}

	/**
	 * Returns the text with max length that can be present
	 * @return the text
	 */
	private String getMaxText() {
		StringBuilder builder = new StringBuilder();
		int count = Integer.toString(
				Math.max(Math.abs(minValue), Math.abs(maxValue))).length();
		for (int i = 0; i < count; i++) {
			builder.append("0");
		}
		if (minValue < 0) {
			builder.insert(0, "-");
		}

		return builder.toString();
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

		int widthItems = (int) FloatMath.ceil(Layout.getDesiredWidth(
				getMaxText(), itemsPaint));
		widthItems += ADDITIONAL_ITEMS_SPACE; // make it some more

		int widthLabel = 0;
		if (label != null && label.length() > 0) {
			widthLabel = (int) FloatMath.ceil(Layout.getDesiredWidth(
					label, valuePaint));
		}

		boolean recalculate = false;
		if (mode == MeasureSpec.EXACTLY) {
			width = widthSize;
			recalculate = true;
		} else {
			width = widthItems + widthLabel + 2 * PADDING;
			if (widthLabel > 0) {
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
			// recalculate width
			int pureWidth = width - LABEL_OFFSET - 2 * PADDING;
			if (widthLabel > 0) {
				double newWidthItems = (double) widthItems * pureWidth
						/ (widthItems + widthLabel);
				widthItems = (int) newWidthItems;
				widthLabel = pureWidth - widthItems;
			} else {
				widthItems = pureWidth + LABEL_OFFSET; // no label
			}
		}

		createLayouts(widthItems, widthLabel);

		return width;
	}

	/**
	 * Creates layouts
	 * @param widthItems width of items layout
	 * @param widthLabel width of label layout
	 */
	private void createLayouts(int widthItems, int widthLabel) {
		if (itemsLayout == null || itemsLayout.getWidth() > widthItems) {
			itemsLayout = new StaticLayout(buildText(), itemsPaint, widthItems,
					widthLabel > 0 ? Layout.Alignment.ALIGN_OPPOSITE : Layout.Alignment.ALIGN_CENTER,
					1, ADDITIONAL_ITEM_HEIGHT, false);
		} else {
			itemsLayout.increaseWidthTo(widthItems);
		}

		if (valueLayout == null || valueLayout.getWidth() > widthItems) {
			valueLayout = new StaticLayout(Integer.toString(currentValue),
					valuePaint, widthItems, widthLabel > 0 ?
							Layout.Alignment.ALIGN_OPPOSITE : Layout.Alignment.ALIGN_CENTER,
							1, ADDITIONAL_ITEM_HEIGHT, false);
		} else {
			valueLayout.increaseWidthTo(widthItems);
		}

		if (widthLabel > 0) {
			if (labelLayout == null || labelLayout.getWidth() > widthLabel) {
				labelLayout = new StaticLayout(label, valuePaint,
						widthLabel, Layout.Alignment.ALIGN_NORMAL, 1,
						ADDITIONAL_ITEM_HEIGHT, false);
			} else {
				labelLayout.increaseWidthTo(widthLabel);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

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
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (itemsLayout == null) {
			calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
		}

		drawCenterRect(canvas);

		canvas.save();
		// Skip padding space and hide a part of top and bottom items
		canvas.translate(PADDING, -ITEM_OFFSET);
		drawItems(canvas);
		drawValue(canvas);
		canvas.restore();

		drawShadows(canvas);
	}

	/**
	 * Draws shadows on top and bottom of control
	 * @param canvas the canvas for drawing
	 */
	private void drawShadows(Canvas canvas) {
		topShadow.setBounds(0, 0, getWidth(), getHeight() / visibleItems);
		topShadow.draw(canvas);

		bottomShadow.setBounds(0, getHeight() - getHeight() / visibleItems,
				getWidth(), getHeight());
		bottomShadow.draw(canvas);
	}

	/**
	 * Draws value and label layout
	 * @param canvas the canvas for drawing
	 */
	private void drawValue(Canvas canvas) {
		valuePaint.setColor(VALUE_TEXT_COLOR);
		valuePaint.drawableState = getDrawableState();

		Rect bounds = new Rect();
		itemsLayout.getLineBounds(visibleItems / 2, bounds);

		// draw label
		if (labelLayout != null) {
			canvas.save();
			canvas.translate(itemsLayout.getWidth() + LABEL_OFFSET, bounds.top);
			labelLayout.draw(canvas);
			canvas.restore();
		}

		// draw current value
		canvas.save();
		canvas.translate(0, bounds.top);
		valueLayout.draw(canvas);
		canvas.restore();
	}

	/**
	 * Draws items
	 * @param canvas the canvas for drawing
	 */
	private void drawItems(Canvas canvas) {
		itemsPaint.setColor(ITEMS_TEXT_COLOR);
		itemsPaint.drawableState = getDrawableState();
		itemsLayout.draw(canvas);
	}

	/**
	 * Draws rect for current value
	 * @param canvas the canvas for drawing
	 */
	private void drawCenterRect(Canvas canvas) {
		int center = getHeight() / 2;
		int offset = getHeight() / visibleItems / 2;
		centerDrawable.setBounds(0, center - offset, getWidth(), center + offset);
		centerDrawable.draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastYTouch = event.getY();
			break;

		case MotionEvent.ACTION_MOVE:
			float delta = event.getY() - lastYTouch;
			int count = (int) (visibleItems * delta / getHeight());
			int pos = currentValue - count;
			pos = Math.max(pos, minValue);
			pos = Math.min(pos, maxValue);
			if (pos != currentValue) {
				lastYTouch = event.getY();
				setValue(pos);
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return true;
	}
}
