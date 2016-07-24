package com.inc.playground.playgroundApp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import com.inc.playground.playgroundApp.utils.UserImageEntry;

/** Customizing AutoCompleteTextView to return Country Name
 *  corresponding to the selected item
 */
public class CustomAutoCompleteTextView extends AutoCompleteTextView {

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** Returns the country name corresponding to the selected item */
    @Override
    protected CharSequence convertSelectionToString(Object selectedItem) {
        /** Each item in the autocompetetextview suggestion list is a hashmap object */

        if(selectedItem instanceof UserImageEntry) {
            UserImageEntry u = (UserImageEntry) selectedItem;
            return u.fullname;
        }
        return null;
    }
}