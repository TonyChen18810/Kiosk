package com.dbc.kiosk.Helpers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.dbc.kiosk.R;
import java.util.ArrayList;
import java.util.List;

public class EmailSuggestionAdapter extends ArrayAdapter<String> {

    private Context context;
    private int resourceId;
    private List<String> items, tempItems, suggestions;
    private AutoCompleteTextView emailAddressBox;

    public EmailSuggestionAdapter(@NonNull Context context, int resource, List<String> items, AutoCompleteTextView emailAddressBox) {
        super(context, resource, items);
        this.context = context;
        this.resourceId = resource;
        this.items = items;
        tempItems = new ArrayList<>(items);
        suggestions = new ArrayList<>();
        this.emailAddressBox = emailAddressBox;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        try {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                view = inflater.inflate(resourceId, parent, false);
            }
            TextView email = view.findViewById(R.id.textView);
            email.setText(getItem(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getSuggestionsCount() {
        return suggestions.size();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return emailFilter;
    }
    private Filter emailFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return (String) resultValue;
        }
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence != null) {
                suggestions.clear();
                for (String email: tempItems) {
                    if (email.toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                        suggestions.add(email);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            ArrayList<String> tempValues = (ArrayList<String>) filterResults.values;
            if (filterResults != null && filterResults.count > 0) {
                clear();
                for (String email : tempValues) {
                    add(email);
                }
                notifyDataSetChanged();
                if (filterResults.count == 1) {
                    emailAddressBox.showDropDown();
                } else {
                    emailAddressBox.dismissDropDown();
                }
            } else {
                clear();
                notifyDataSetChanged();
                if (filterResults.count == 1) {
                    emailAddressBox.showDropDown();
                } else {
                    emailAddressBox.dismissDropDown();
                }
            }
        }
    };
}
