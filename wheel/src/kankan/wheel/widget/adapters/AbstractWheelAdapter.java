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

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Abstract wheel adapter provides common functionality for adapters.
 */
public abstract class AbstractWheelAdapter implements WheelViewAdapter {
    
	/** Text view resource. Used as a default view for adapter. */
	public static final int TEXT_VIEW_ITEM_RESOURCE = -1;
	
	/** No resource constant. */
	protected static final int NO_RESOURCE = 0;
	
    /** Default text color */
    public static final int DEFAULT_TEXT_COLOR = 0xFF101010;
    
    /** Default text color */
    public static final int LABEL_COLOR = 0xFF700070;

    /** Default text size */
    public static final int DEFAULT_TEXT_SIZE = 24;
    
    // Text settings
    private int textColor = DEFAULT_TEXT_COLOR;
    private int textSize = DEFAULT_TEXT_SIZE;
	
	// Current context
	protected Context context;
	// Layout inflater
	protected LayoutInflater inflater;
	
	// Items resources
	protected int itemResourceId;
	protected int itemTextResourceId;

	// Empty items resources
	protected int emptyItemResourceId;
	
	// Extended items resources
	protected int extendedItemResourceId;

	// Label resources
	protected int labelResourceId;
	protected int labelTextResourceId;
	
	/**
	 * Constructor
	 * @param context the current context
	 */
	protected AbstractWheelAdapter(Context context) {
		this(context, TEXT_VIEW_ITEM_RESOURCE, TEXT_VIEW_ITEM_RESOURCE);
	}

	/**
	 * Constructor
     * @param context the current context
	 * @param itemResource the resource ID for a layout file containing a TextView to use when instantiating items views
	 * @param labelResource the resource ID for a layout file containing a TextView to use when instantiating label view
	 */
	protected AbstractWheelAdapter(Context context, int itemResource, int labelResource) {
		this(context, itemResource, labelResource, NO_RESOURCE, NO_RESOURCE);
	}
	
	/**
	 * Constructor
     * @param context the current context
	 * @param itemRes the resource ID for a layout file containing a TextView to use when instantiating items views
	 * @param labelRes the resource ID for a layout file containing a TextView to use when instantiating label view
	 * @param emptyRes the resource ID for a layout file for empty items views
	 * @param extRes the resource ID for a layout file for extended items views
	 */
	protected AbstractWheelAdapter(Context context, int itemRes, int labelRes, int emptyRes, int extRes) {
        itemResourceId = itemRes;
        labelResourceId = labelRes;
        emptyItemResourceId = emptyRes;
        extendedItemResourceId = extRes;
        
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
    /**
     * Gets text color
     * @return the text color
     */
    public int getTextColor() {
        return textColor;
    }

    /**
     * Sets text color
     * @param textColor the text color to set
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    /**
     * Gets text size
     * @return the text size
     */
    public int getTextSize() {
        return textSize;
    }

    /**
     * Sets text size
     * @param textSize the text size to set
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    /**
     * Gets resource Id for items views
     * @return the item resource Id
     */
    public int getItemResource() {
        return itemResourceId;
    }

    /**
     * Sets resource Id for items views
     * @param itemResourceId the resource Id to set
     */
    public void setItemResource(int itemResourceId) {
        this.itemResourceId = itemResourceId;
    }

    /**
     * Gets resource Id for text view in item layout 
     * @return the item text resource Id
     */
    public int getItemTextResource() {
        return itemTextResourceId;
    }

    /**
     * Sets resource Id for text view in item layout 
     * @param itemTextResourceId the item text resource Id to set
     */
    public void setItemTextResource(int itemTextResourceId) {
        this.itemTextResourceId = itemTextResourceId;
    }

    /**
     * Gets resource Id for empty items views
     * @return the empty item resource Id
     */
    public int getEmptyItemResource() {
        return emptyItemResourceId;
    }

    /**
     * Sets resource Id for empty items views
     * @param emptyItemResourceId the empty item resource Id to set
     */
    public void setEmptyItemResource(int emptyItemResourceId) {
        this.emptyItemResourceId = emptyItemResourceId;
    }

    /**
     * Gets resource Id for extended items views
     * @return the extended item resource Id
     */
    public int getExtendedItemResource() {
        return extendedItemResourceId;
    }

    /**
     * Sets resource Id for extended items views
     * @param extendedItemResourceId the extended item resource Id to set
     */
    public void setExtendedItemResource(int extendedItemResourceId) {
        this.extendedItemResourceId = extendedItemResourceId;
    }

    /**
     * Gets resource Id for label view
     * @return the label resource Id
     */
    public int getLabelResource() {
        return labelResourceId;
    }

    /**
     * Sets resource Id for label view
     * @param labelResourceId the label resource Id to set
     */
    public void setLabelResource(int labelResourceId) {
        this.labelResourceId = labelResourceId;
    }

    /**
     * Gets resource Id for text view in label layout
     * @return the label text resource Id
     */
    public int getLabelTextResource() {
        return labelTextResourceId;
    }

    /**
     * Sets resource Id for text view in label layout
     * @param labelTextResourceId the the label text resource Id to set
     */
    public void setLabelTextResource(int labelTextResourceId) {
        this.labelTextResourceId = labelTextResourceId;
    }

    /**
	 * Returns text for specified item
	 * @param index the item index
	 * @return the text of specified items
	 */
	protected abstract CharSequence getItemText(int index);

	@Override
	public View getItem(int index, View cachedView) {
	    if (index >= 0 && index < getItemsCount()) {
	        return loadTextView(getItemText(index), cachedView, itemResourceId, itemTextResourceId);
	    }
		return null;
	}

	@Override
	public View getEmptyItem(View cachedView) {
		return getEmptyView(cachedView, emptyItemResourceId);
	}

	@Override
	public View getEmptyExtendedItem(View cachedView) {
	    return getEmptyView(cachedView, extendedItemResourceId);
	}

	@Override
	public View getLabelItem(String label, View cachedView) {
	    if (label != null && label.length() > 0) {
	        View view = loadTextView(label, cachedView, labelResourceId, labelTextResourceId);
	        if (labelResourceId == TEXT_VIEW_ITEM_RESOURCE && view instanceof TextView) {
	            // set color
	            TextView text = (TextView)view;
	            text.setTextColor(LABEL_COLOR);
	        }
	        return view;
	    }
		return null;
	}

	/**
	 * Gets view for empty or extended item. Configures it in case it is a TEXT_VIEW_ITEM_RESOURCE
	 * @param cachedView the cached view
	 * @param recource the view resource Id
	 * @return the view
	 */
	private View getEmptyView(View cachedView, int recource) {
	    View view = cachedView != null ? cachedView : getView(recource);

	    if (recource == TEXT_VIEW_ITEM_RESOURCE && view instanceof TextView) {
	        configureTextView((TextView)view);
	    }
	        
	    return view;
	}
	
	/**
	 * Configures text view. Is called for the TEXT_VIEW_ITEM_RESOURCE views.
	 * @param view the text view to be configured
	 */
	protected void configureTextView(TextView view) {
        view.setTextColor(textColor);
        view.setGravity(Gravity.CENTER);
        view.setTextSize(textSize);
        view.setLines(1);
        view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
	}
	
	/**
	 * Loads text view and set text to it
	 * @param text the text for view
	 * @param cachedView the cached view
	 * @param resource the view resource Id 
	 * @param textResource the text resource Id in layout view
	 * @return the loaded view
	 */
	private View loadTextView(CharSequence text, View cachedView, int resource, int textResource) {
	    View view = cachedView != null ? cachedView : getView(resource);
	    if (view != null) {
	        TextView textView = getTextView(view, textResource);
	        if (textView != null) {
                if (text != null) {
                    textView.setText(text);
                }

	            if (resource == TEXT_VIEW_ITEM_RESOURCE) {
	                configureTextView(textView);
	            }
	        }
	    }
	    return view;
	}
	
	/**
	 * Loads a text view from view
	 * @param view the text view or layout containing it
	 * @param textResource the text resource Id in layout
	 * @return the loaded text view
	 */
	private TextView getTextView(View view, int textResource) {
		TextView text = null;
		try {
            if (textResource == NO_RESOURCE && view instanceof TextView) {
                text = (TextView) view;
            } else if (textResource != NO_RESOURCE) {
                text = (TextView) view.findViewById(textResource);
            }
        } catch (ClassCastException e) {
            Log.e("AbstractWheelAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "AbstractWheelAdapter requires the resource ID to be a TextView", e);
        }
        
        return text;
	}
	
	/**
	 * Loads view from resources
	 * @param resource the resource Id
	 * @return the loaded view or null if resource is not set
	 */
	private View getView(int resource) {
	    switch (resource) {
	    case NO_RESOURCE:
	        return null;
	    case TEXT_VIEW_ITEM_RESOURCE:
	        return new TextView(context);
	    default:
	        return inflater.inflate(resource, null, false);    
	    }
	}
}
