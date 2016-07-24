package com.inc.playground.playgroundApp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inc.playground.playgroundApp.utils.UserImageEntry;


public class InviteArrayAdapter extends ArrayAdapter<UserImageEntry>
{
    private static final String tag = "SearchItemArrayAdapter";
    private UserImageEntry userImageEntry;
    private TextView autoItem;
    private ImageView categoryIcon;
    private int journalEntryTypeId;
    private LinearLayout.LayoutParams mImageViewLayoutParams;
    private final Drawable mDefaultContactPic;
    private List<UserImageEntry> arrayList = new ArrayList<UserImageEntry>();
    private List<UserImageEntry> mOriginalValues = new ArrayList<UserImageEntry>();

    /**
     *
     * @param context
     * @param textViewResourceId
     * @param objects
     */
    public InviteArrayAdapter(Context context, int textViewResourceId, List<UserImageEntry> objects)
    {
        super(context, textViewResourceId, objects);
        arrayList = objects;
        Log.d(tag, "Search List -> journalEntryList := " + arrayList.toString());
        mDefaultContactPic = context.getResources().getDrawable(R.drawable.india);
        mImageViewLayoutParams = new LinearLayout.LayoutParams(mDefaultContactPic.getIntrinsicWidth(), mDefaultContactPic.getIntrinsicHeight());
    }

    @Override
    public int getCount()
    {
        Log.d("getCountInviteArray",String.valueOf(arrayList.size()));
        return arrayList.size();
    }

    @Override
    public UserImageEntry getItem(int position)
    {
        try{
            UserImageEntry userEntry = arrayList.get(position);
            return userEntry;
        }
        catch (Exception e){

        }
        return null;
//        Log.d(tag, "*-> Retrieving JournalEntry @ position: " + String.valueOf(position) + " : " + userEntry.toString());

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Log.d("testinvteadapter",String.valueOf(position));
        View row = convertView;
        LayoutInflater inflater = LayoutInflater.from(getContext());

        if (row == null)
        {
            row = inflater.inflate(R.layout.autocomplete_layout, parent, false);
        }

        userImageEntry = this.arrayList.get(position);
        String searchItem = userImageEntry.fullname;
        autoItem = (TextView) row.findViewById(R.id.usernametext);
        autoItem.setText(searchItem);

        // Get a reference to ImageView holder
        categoryIcon = (ImageView) row.findViewById(R.id.userimage);
//        categoryIcon.setMaxHeight();
//        categoryIcon.setMaxWidth();
        categoryIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        categoryIcon.setLayoutParams(mImageViewLayoutParams);
        categoryIcon.setImageBitmap(userImageEntry.image);

        return row;
    }
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                return ((UserImageEntry)resultValue).fullname;
            }
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                arrayList = (List<UserImageEntry>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                List<UserImageEntry> FilteredArrList = new ArrayList<UserImageEntry>();

                if (mOriginalValues.size()==0) {
                    mOriginalValues = new ArrayList<UserImageEntry>(arrayList); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = mOriginalValues.get(i).fullname;
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(mOriginalValues.get(i));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }
}

