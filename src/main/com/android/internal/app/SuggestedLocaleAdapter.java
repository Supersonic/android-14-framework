package com.android.internal.app;

import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.C4057R;
import com.android.internal.app.LocaleHelper;
import com.android.internal.app.LocaleStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
/* loaded from: classes4.dex */
public class SuggestedLocaleAdapter extends BaseAdapter implements Filterable {
    protected static final int APP_LANGUAGE_PICKER_TYPE_COUNT = 5;
    protected static final int MIN_REGIONS_FOR_SUGGESTIONS = 6;
    protected static final int SYSTEM_LANGUAGE_TYPE_COUNT = 3;
    protected static final int SYSTEM_LANGUAGE_WITHOUT_HEADER_TYPE_COUNT = 1;
    protected static final int TYPE_CURRENT_LOCALE = 4;
    protected static final int TYPE_HEADER_ALL_OTHERS = 1;
    protected static final int TYPE_HEADER_SUGGESTED = 0;
    protected static final int TYPE_LOCALE = 2;
    protected static final int TYPE_SYSTEM_LANGUAGE_FOR_APP_LANGUAGE_PICKER = 3;
    protected Context mContextOverride;
    protected final boolean mCountryMode;
    protected Locale mDisplayLocale;
    private boolean mHasSpecificAppPackageName;
    protected LayoutInflater mInflater;
    protected ArrayList<LocaleStore.LocaleInfo> mLocaleOptions;
    protected ArrayList<LocaleStore.LocaleInfo> mOriginalLocaleOptions;
    protected int mSuggestionCount;

    public SuggestedLocaleAdapter(Set<LocaleStore.LocaleInfo> localeOptions, boolean countryMode) {
        this(localeOptions, countryMode, false);
    }

    public SuggestedLocaleAdapter(Set<LocaleStore.LocaleInfo> localeOptions, boolean countryMode, boolean hasSpecificAppPackageName) {
        this.mDisplayLocale = null;
        this.mContextOverride = null;
        this.mCountryMode = countryMode;
        this.mLocaleOptions = new ArrayList<>(localeOptions.size());
        this.mHasSpecificAppPackageName = hasSpecificAppPackageName;
        for (LocaleStore.LocaleInfo li : localeOptions) {
            if (li.isSuggested()) {
                this.mSuggestionCount++;
            }
            this.mLocaleOptions.add(li);
        }
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean isEnabled(int position) {
        return getItemViewType(position) == 2 || getItemViewType(position) == 3 || getItemViewType(position) == 4;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int position) {
        if (!showHeaders()) {
            LocaleStore.LocaleInfo item = (LocaleStore.LocaleInfo) getItem(position);
            if (item.isSystemLocale()) {
                return 3;
            }
            return item.isAppCurrentLocale() ? 4 : 2;
        } else if (position == 0) {
            return 0;
        } else {
            if (position == this.mSuggestionCount + 1) {
                return 1;
            }
            LocaleStore.LocaleInfo item2 = (LocaleStore.LocaleInfo) getItem(position);
            if (item2 == null) {
                throw new NullPointerException("Non header locale cannot be null");
            }
            if (item2.isSystemLocale()) {
                return 3;
            }
            return item2.isAppCurrentLocale() ? 4 : 2;
        }
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        if (this.mHasSpecificAppPackageName && showHeaders()) {
            return 5;
        }
        if (showHeaders()) {
            return 3;
        }
        return 1;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        if (showHeaders()) {
            return this.mLocaleOptions.size() + 2;
        }
        return this.mLocaleOptions.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        if (isHeaderPosition(position)) {
            return null;
        }
        int offset = 0;
        if (showHeaders()) {
            offset = position > this.mSuggestionCount ? -2 : -1;
        }
        return this.mLocaleOptions.get(position + offset);
    }

    private boolean isHeaderPosition(int position) {
        return showHeaders() && (position == 0 || position == this.mSuggestionCount + 1);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    public void setDisplayLocale(Context context, Locale locale) {
        if (locale == null) {
            this.mDisplayLocale = null;
            this.mContextOverride = null;
        } else if (!locale.equals(this.mDisplayLocale)) {
            this.mDisplayLocale = locale;
            Configuration configOverride = new Configuration();
            configOverride.setLocale(locale);
            this.mContextOverride = context.createConfigurationContext(configOverride);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setTextTo(TextView textView, int resId) {
        Context context = this.mContextOverride;
        if (context == null) {
            textView.setText(resId);
        } else {
            textView.setText(context.getText(resId));
        }
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView title;
        if (convertView == null && this.mInflater == null) {
            this.mInflater = LayoutInflater.from(parent.getContext());
        }
        int itemType = getItemViewType(position);
        View itemView = getNewViewIfNeeded(convertView, parent, itemType, position);
        switch (itemType) {
            case 0:
            case 1:
                TextView textView = (TextView) itemView;
                if (itemType == 0) {
                    if (this.mCountryMode) {
                        setTextTo(textView, C4057R.string.language_picker_regions_section_suggested);
                    } else {
                        setTextTo(textView, C4057R.string.language_picker_section_suggested);
                    }
                } else if (this.mCountryMode) {
                    setTextTo(textView, C4057R.string.region_picker_section_all);
                } else {
                    setTextTo(textView, C4057R.string.language_picker_section_all);
                }
                Locale locale = this.mDisplayLocale;
                if (locale == null) {
                    locale = Locale.getDefault();
                }
                textView.setTextLocale(locale);
                break;
            case 2:
            default:
                updateTextView(itemView, (TextView) itemView.findViewById(C4057R.C4059id.locale), position);
                break;
            case 3:
                LocaleStore.LocaleInfo info = (LocaleStore.LocaleInfo) getItem(position);
                if (info == null) {
                    throw new NullPointerException("Non header locale cannot be null.");
                }
                if (info.isAppCurrentLocale()) {
                    title = (TextView) itemView.findViewById(C4057R.C4059id.language_picker_item);
                } else {
                    title = (TextView) itemView.findViewById(C4057R.C4059id.locale);
                }
                title.setText(C4057R.string.system_locale_title);
                break;
            case 4:
                updateTextView(itemView, (TextView) itemView.findViewById(C4057R.C4059id.language_picker_item), position);
                break;
        }
        return itemView;
    }

    private View getNewViewIfNeeded(View convertView, ViewGroup parent, int itemType, int position) {
        boolean z = true;
        switch (itemType) {
            case 0:
            case 1:
                boolean shouldReuseView = convertView instanceof TextView;
                if (!shouldReuseView || convertView.findViewById(C4057R.C4059id.language_picker_header) == null) {
                    z = false;
                }
                boolean shouldReuseView2 = z;
                if (shouldReuseView2) {
                    return convertView;
                }
                View updatedView = this.mInflater.inflate(C4057R.layout.language_picker_section_header, parent, false);
                return updatedView;
            case 2:
            default:
                if (!(convertView instanceof TextView) || convertView.findViewById(C4057R.C4059id.locale) == null) {
                    z = false;
                }
                boolean shouldReuseView3 = z;
                if (shouldReuseView3) {
                    return convertView;
                }
                View updatedView2 = this.mInflater.inflate(C4057R.layout.language_picker_item, parent, false);
                return updatedView2;
            case 3:
                if (((LocaleStore.LocaleInfo) getItem(position)).isAppCurrentLocale()) {
                    if (!(convertView instanceof LinearLayout) || convertView.findViewById(C4057R.C4059id.language_picker_item) == null) {
                        z = false;
                    }
                    boolean shouldReuseView4 = z;
                    if (shouldReuseView4) {
                        return convertView;
                    }
                    View updatedView3 = this.mInflater.inflate(C4057R.layout.app_language_picker_current_locale_item, parent, false);
                    addStateDescriptionIntoCurrentLocaleItem(updatedView3);
                    return updatedView3;
                }
                if (!(convertView instanceof TextView) || convertView.findViewById(C4057R.C4059id.locale) == null) {
                    z = false;
                }
                boolean shouldReuseView5 = z;
                if (shouldReuseView5) {
                    return convertView;
                }
                View updatedView4 = this.mInflater.inflate(C4057R.layout.language_picker_item, parent, false);
                return updatedView4;
            case 4:
                if (!(convertView instanceof LinearLayout) || convertView.findViewById(C4057R.C4059id.language_picker_item) == null) {
                    z = false;
                }
                boolean shouldReuseView6 = z;
                if (shouldReuseView6) {
                    return convertView;
                }
                View updatedView5 = this.mInflater.inflate(C4057R.layout.app_language_picker_current_locale_item, parent, false);
                addStateDescriptionIntoCurrentLocaleItem(updatedView5);
                return updatedView5;
        }
    }

    protected boolean showHeaders() {
        int i;
        return ((this.mCountryMode && this.mLocaleOptions.size() < 6) || (i = this.mSuggestionCount) == 0 || i == this.mLocaleOptions.size()) ? false : true;
    }

    public void sort(LocaleHelper.LocaleInfoComparator comp) {
        Collections.sort(this.mLocaleOptions, comp);
    }

    /* loaded from: classes4.dex */
    class FilterByNativeAndUiNames extends Filter {
        FilterByNativeAndUiNames() {
        }

        @Override // android.widget.Filter
        protected Filter.FilterResults performFiltering(CharSequence prefix) {
            Filter.FilterResults results = new Filter.FilterResults();
            if (SuggestedLocaleAdapter.this.mOriginalLocaleOptions == null) {
                SuggestedLocaleAdapter.this.mOriginalLocaleOptions = new ArrayList<>(SuggestedLocaleAdapter.this.mLocaleOptions);
            }
            ArrayList<LocaleStore.LocaleInfo> values = new ArrayList<>(SuggestedLocaleAdapter.this.mOriginalLocaleOptions);
            if (prefix == null || prefix.length() == 0) {
                results.values = values;
                results.count = values.size();
            } else {
                Locale locale = Locale.getDefault();
                String prefixString = LocaleHelper.normalizeForSearch(prefix.toString(), locale);
                int count = values.size();
                ArrayList<LocaleStore.LocaleInfo> newValues = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    LocaleStore.LocaleInfo value = values.get(i);
                    String nameToCheck = LocaleHelper.normalizeForSearch(value.getFullNameInUiLanguage(), locale);
                    String nativeNameToCheck = LocaleHelper.normalizeForSearch(value.getFullNameNative(), locale);
                    if (wordMatches(nativeNameToCheck, prefixString) || wordMatches(nameToCheck, prefixString)) {
                        newValues.add(value);
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        boolean wordMatches(String valueText, String prefixString) {
            if (valueText.startsWith(prefixString)) {
                return true;
            }
            String[] words = valueText.split(" ");
            for (String word : words) {
                if (word.startsWith(prefixString)) {
                    return true;
                }
            }
            return false;
        }

        @Override // android.widget.Filter
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            SuggestedLocaleAdapter.this.mLocaleOptions = (ArrayList) results.values;
            SuggestedLocaleAdapter.this.mSuggestionCount = 0;
            Iterator<LocaleStore.LocaleInfo> it = SuggestedLocaleAdapter.this.mLocaleOptions.iterator();
            while (it.hasNext()) {
                LocaleStore.LocaleInfo li = it.next();
                if (li.isSuggested()) {
                    SuggestedLocaleAdapter.this.mSuggestionCount++;
                }
            }
            if (results.count > 0) {
                SuggestedLocaleAdapter.this.notifyDataSetChanged();
            } else {
                SuggestedLocaleAdapter.this.notifyDataSetInvalidated();
            }
        }
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        return new FilterByNativeAndUiNames();
    }

    private void updateTextView(View convertView, TextView text, int position) {
        int i;
        LocaleStore.LocaleInfo item = (LocaleStore.LocaleInfo) getItem(position);
        text.setText(item.getLabel(this.mCountryMode));
        text.setTextLocale(item.getLocale());
        text.setContentDescription(item.getContentDescription(this.mCountryMode));
        if (this.mCountryMode) {
            int layoutDir = TextUtils.getLayoutDirectionFromLocale(item.getParent());
            convertView.setLayoutDirection(layoutDir);
            if (layoutDir == 1) {
                i = 4;
            } else {
                i = 3;
            }
            text.setTextDirection(i);
        }
    }

    private void addStateDescriptionIntoCurrentLocaleItem(View root) {
        String description = root.getContext().getResources().getString(C4057R.string.checked);
        root.setStateDescription(description);
    }
}
