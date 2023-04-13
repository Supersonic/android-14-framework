package com.android.internal.app;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.app.IActivityManager;
import android.app.ListFragment;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.p008os.Bundle;
import android.p008os.LocaleList;
import android.p008os.RemoteException;
import android.provider.Settings;
import android.sysprop.LocalizationProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.C4057R;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
/* loaded from: classes4.dex */
public class LocalePicker extends ListFragment {
    private static final boolean DEBUG = false;
    private static final String TAG = "LocalePicker";
    private static final String[] pseudoLocales = {"en-XA", "ar-XB"};
    LocaleSelectionListener mListener;

    /* loaded from: classes4.dex */
    public interface LocaleSelectionListener {
        void onLocaleSelected(Locale locale);
    }

    /* loaded from: classes4.dex */
    public static class LocaleInfo implements Comparable<LocaleInfo> {
        static final Collator sCollator = Collator.getInstance();
        String label;
        final Locale locale;

        public LocaleInfo(String label, Locale locale) {
            this.label = label;
            this.locale = locale;
        }

        public String getLabel() {
            return this.label;
        }

        public Locale getLocale() {
            return this.locale;
        }

        public String toString() {
            return this.label;
        }

        @Override // java.lang.Comparable
        public int compareTo(LocaleInfo another) {
            return sCollator.compare(this.label, another.label);
        }
    }

    public static String[] getSystemAssetLocales() {
        return Resources.getSystem().getAssets().getLocales();
    }

    public static String[] getSupportedLocales(Context context) {
        if (context == null) {
            return new String[0];
        }
        String[] allLocales = context.getResources().getStringArray(C4057R.array.supported_locales);
        Predicate<String> localeFilter = getLocaleFilter();
        if (localeFilter == null) {
            return allLocales;
        }
        List<String> result = new ArrayList<>(allLocales.length);
        for (String locale : allLocales) {
            if (localeFilter.test(locale)) {
                result.add(locale);
            }
        }
        int localeCount = result.size();
        return localeCount == allLocales.length ? allLocales : (String[]) result.toArray(new String[localeCount]);
    }

    private static Predicate<String> getLocaleFilter() {
        try {
            return (Predicate) LocalizationProperties.locale_filter().map(new Function() { // from class: com.android.internal.app.LocalePicker$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Predicate asPredicate;
                    asPredicate = Pattern.compile((String) obj).asPredicate();
                    return asPredicate;
                }
            }).orElse(null);
        } catch (SecurityException e) {
            Log.m109e(TAG, "Failed to read locale filter.", e);
            return null;
        } catch (PatternSyntaxException e2) {
            Log.m110e(TAG, "Bad locale filter format (\"" + e2.getPattern() + "\"), skipping.");
            return null;
        }
    }

    public static List<LocaleInfo> getAllAssetLocales(Context context, boolean isInDeveloperMode) {
        Resources resources = context.getResources();
        String[] locales = getSystemAssetLocales();
        List<String> localeList = new ArrayList<>(locales.length);
        Collections.addAll(localeList, locales);
        Collections.sort(localeList);
        String[] specialLocaleCodes = resources.getStringArray(C4057R.array.special_locale_codes);
        String[] specialLocaleNames = resources.getStringArray(C4057R.array.special_locale_names);
        ArrayList<LocaleInfo> localeInfos = new ArrayList<>(localeList.size());
        for (String locale : localeList) {
            Locale l = Locale.forLanguageTag(locale.replace('_', '-'));
            if (l != null && !"und".equals(l.getLanguage()) && !l.getLanguage().isEmpty() && !l.getCountry().isEmpty() && (isInDeveloperMode || !LocaleList.isPseudoLocale(l))) {
                if (localeInfos.isEmpty()) {
                    localeInfos.add(new LocaleInfo(toTitleCase(l.getDisplayLanguage(l)), l));
                } else {
                    LocaleInfo previous = localeInfos.get(localeInfos.size() - 1);
                    if (previous.locale.getLanguage().equals(l.getLanguage()) && !previous.locale.getLanguage().equals("zz")) {
                        previous.label = toTitleCase(getDisplayName(previous.locale, specialLocaleCodes, specialLocaleNames));
                        localeInfos.add(new LocaleInfo(toTitleCase(getDisplayName(l, specialLocaleCodes, specialLocaleNames)), l));
                    } else {
                        String displayName = toTitleCase(l.getDisplayLanguage(l));
                        localeInfos.add(new LocaleInfo(displayName, l));
                    }
                }
            }
        }
        Collections.sort(localeInfos);
        return localeInfos;
    }

    public static ArrayAdapter<LocaleInfo> constructAdapter(Context context) {
        return constructAdapter(context, C4057R.layout.locale_picker_item, C4057R.C4059id.locale);
    }

    public static ArrayAdapter<LocaleInfo> constructAdapter(Context context, final int layoutId, final int fieldId) {
        boolean isInDeveloperMode = Settings.Global.getInt(context.getContentResolver(), "development_settings_enabled", 0) != 0;
        List<LocaleInfo> localeInfos = getAllAssetLocales(context, isInDeveloperMode);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new ArrayAdapter<LocaleInfo>(context, layoutId, fieldId, localeInfos) { // from class: com.android.internal.app.LocalePicker.1
            @Override // android.widget.ArrayAdapter, android.widget.Adapter
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;
                TextView text;
                if (convertView == null) {
                    view = inflater.inflate(layoutId, parent, false);
                    text = (TextView) view.findViewById(fieldId);
                    view.setTag(text);
                } else {
                    view = convertView;
                    text = (TextView) view.getTag();
                }
                LocaleInfo item = getItem(position);
                text.setText(item.toString());
                text.setTextLocale(item.getLocale());
                return view;
            }
        };
    }

    private static String toTitleCase(String s) {
        if (s.length() == 0) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String getDisplayName(Locale l, String[] specialLocaleCodes, String[] specialLocaleNames) {
        String code = l.toString();
        for (int i = 0; i < specialLocaleCodes.length; i++) {
            if (specialLocaleCodes[i].equals(code)) {
                return specialLocaleNames[i];
            }
        }
        return l.getDisplayName(l);
    }

    @Override // android.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<LocaleInfo> adapter = constructAdapter(getActivity());
        setListAdapter(adapter);
    }

    public void setLocaleSelectionListener(LocaleSelectionListener listener) {
        this.mListener = listener;
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        getListView().requestFocus();
    }

    @Override // android.app.ListFragment
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (this.mListener != null) {
            Locale locale = ((LocaleInfo) getListAdapter().getItem(position)).locale;
            this.mListener.onLocaleSelected(locale);
        }
    }

    public static void updateLocale(Locale locale) {
        updateLocales(new LocaleList(locale));
    }

    public static void updateLocales(LocaleList locales) {
        if (locales != null) {
            locales = removeExcludedLocales(locales);
        }
        try {
            IActivityManager am = ActivityManager.getService();
            Configuration config = new Configuration();
            config.setLocales(locales);
            config.userSetLocale = true;
            am.updatePersistentConfigurationWithAttribution(config, ActivityThread.currentOpPackageName(), null);
            BackupManager.dataChanged("com.android.providers.settings");
        } catch (RemoteException e) {
        }
    }

    private static LocaleList removeExcludedLocales(LocaleList locales) {
        Predicate<String> localeFilter = getLocaleFilter();
        if (localeFilter == null) {
            return locales;
        }
        int localeCount = locales.size();
        ArrayList<Locale> filteredLocales = new ArrayList<>(localeCount);
        for (int i = 0; i < localeCount; i++) {
            Locale locale = locales.get(i);
            if (localeFilter.test(locale.toString())) {
                filteredLocales.add(locale);
            }
        }
        int i2 = filteredLocales.size();
        return localeCount == i2 ? locales : new LocaleList((Locale[]) filteredLocales.toArray(new Locale[0]));
    }

    public static LocaleList getLocales() {
        try {
            return ActivityManager.getService().getConfiguration().getLocales();
        } catch (RemoteException e) {
            return LocaleList.getDefault();
        }
    }
}
