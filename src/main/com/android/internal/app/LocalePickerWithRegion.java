package com.android.internal.app;

import android.app.ListFragment;
import android.content.Context;
import android.p008os.Bundle;
import android.p008os.LocaleList;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import com.android.internal.C4057R;
import com.android.internal.app.LocaleHelper;
import com.android.internal.app.LocaleStore;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
/* loaded from: classes4.dex */
public class LocalePickerWithRegion extends ListFragment implements SearchView.OnQueryTextListener {
    private static final String PARENT_FRAGMENT_NAME = "localeListEditor";
    private static final String TAG = LocalePickerWithRegion.class.getSimpleName();
    private SuggestedLocaleAdapter mAdapter;
    private LocaleSelectedListener mListener;
    private Set<LocaleStore.LocaleInfo> mLocaleList;
    private LocaleCollectorBase mLocalePickerCollector;
    private MenuItem.OnActionExpandListener mOnActionExpandListener;
    private LocaleStore.LocaleInfo mParentLocale;
    private boolean mTranslatedOnly = false;
    private SearchView mSearchView = null;
    private CharSequence mPreviousSearch = null;
    private boolean mPreviousSearchHadFocus = false;
    private int mFirstVisiblePosition = 0;
    private int mTopDistance = 0;
    private CharSequence mTitle = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public interface LocaleCollectorBase {
        HashSet<String> getIgnoredLocaleList(boolean z);

        Set<LocaleStore.LocaleInfo> getSupportedLocaleList(LocaleStore.LocaleInfo localeInfo, boolean z, boolean z2);

        boolean hasSpecificPackageName();
    }

    /* loaded from: classes4.dex */
    public interface LocaleSelectedListener {
        void onLocaleSelected(LocaleStore.LocaleInfo localeInfo);
    }

    private static LocalePickerWithRegion createCountryPicker(LocaleSelectedListener listener, LocaleStore.LocaleInfo parent, boolean translatedOnly, MenuItem.OnActionExpandListener onActionExpandListener, LocaleCollectorBase localePickerCollector) {
        LocalePickerWithRegion localePicker = new LocalePickerWithRegion();
        localePicker.setOnActionExpandListener(onActionExpandListener);
        boolean shouldShowTheList = localePicker.setListener(listener, parent, translatedOnly, localePickerCollector);
        if (shouldShowTheList) {
            return localePicker;
        }
        return null;
    }

    public static LocalePickerWithRegion createLanguagePicker(Context context, LocaleSelectedListener listener, boolean translatedOnly) {
        return createLanguagePicker(context, listener, translatedOnly, null, null, null);
    }

    public static LocalePickerWithRegion createLanguagePicker(Context context, LocaleSelectedListener listener, boolean translatedOnly, LocaleList explicitLocales) {
        return createLanguagePicker(context, listener, translatedOnly, explicitLocales, null, null);
    }

    public static LocalePickerWithRegion createLanguagePicker(Context context, LocaleSelectedListener listener, boolean translatedOnly, LocaleList explicitLocales, String appPackageName, MenuItem.OnActionExpandListener onActionExpandListener) {
        LocaleCollectorBase localePickerController;
        if (TextUtils.isEmpty(appPackageName)) {
            localePickerController = new SystemLocaleCollector(context, explicitLocales);
        } else {
            localePickerController = new AppLocaleCollector(context, appPackageName);
        }
        LocalePickerWithRegion localePicker = new LocalePickerWithRegion();
        localePicker.setOnActionExpandListener(onActionExpandListener);
        localePicker.setListener(listener, null, translatedOnly, localePickerController);
        return localePicker;
    }

    private boolean setListener(LocaleSelectedListener listener, LocaleStore.LocaleInfo parent, boolean translatedOnly, LocaleCollectorBase localePickerController) {
        boolean z;
        this.mParentLocale = parent;
        this.mListener = listener;
        this.mTranslatedOnly = translatedOnly;
        this.mLocalePickerCollector = localePickerController;
        setRetainInstance(true);
        if (parent != null) {
            z = true;
        } else {
            z = false;
        }
        Set<LocaleStore.LocaleInfo> supportedLocaleList = localePickerController.getSupportedLocaleList(parent, translatedOnly, z);
        this.mLocaleList = supportedLocaleList;
        if (parent == null || listener == null || supportedLocaleList.size() != 1) {
            return true;
        }
        listener.onLocaleSelected(this.mLocaleList.iterator().next());
        return false;
    }

    private void returnToParentFrame() {
        getFragmentManager().popBackStack(PARENT_FRAGMENT_NAME, 1);
    }

    @Override // android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean hasSpecificPackageName = true;
        setHasOptionsMenu(true);
        if (this.mLocaleList == null) {
            returnToParentFrame();
            return;
        }
        this.mTitle = getActivity().getTitle();
        LocaleStore.LocaleInfo localeInfo = this.mParentLocale;
        boolean countryMode = localeInfo != null;
        Locale sortingLocale = countryMode ? localeInfo.getLocale() : Locale.getDefault();
        LocaleCollectorBase localeCollectorBase = this.mLocalePickerCollector;
        if (localeCollectorBase == null || !localeCollectorBase.hasSpecificPackageName()) {
            hasSpecificPackageName = false;
        }
        this.mAdapter = new SuggestedLocaleAdapter(this.mLocaleList, countryMode, hasSpecificPackageName);
        LocaleHelper.LocaleInfoComparator comp = new LocaleHelper.LocaleInfoComparator(sortingLocale, countryMode);
        this.mAdapter.sort(comp);
        setListAdapter(this.mAdapter);
    }

    @Override // android.app.ListFragment, android.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setNestedScrollingEnabled(true);
        getListView().setDivider(null);
    }

    @Override // android.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case 16908332:
                getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mParentLocale != null) {
            getActivity().setTitle(this.mParentLocale.getFullNameNative());
        } else {
            getActivity().setTitle(this.mTitle);
        }
        getListView().requestFocus();
    }

    @Override // android.app.Fragment
    public void onPause() {
        super.onPause();
        SearchView searchView = this.mSearchView;
        if (searchView != null) {
            this.mPreviousSearchHadFocus = searchView.hasFocus();
            this.mPreviousSearch = this.mSearchView.getQuery();
        } else {
            this.mPreviousSearchHadFocus = false;
            this.mPreviousSearch = null;
        }
        ListView list = getListView();
        View firstChild = list.getChildAt(0);
        this.mFirstVisiblePosition = list.getFirstVisiblePosition();
        this.mTopDistance = firstChild != null ? firstChild.getTop() - list.getPaddingTop() : 0;
    }

    @Override // android.app.ListFragment
    public void onListItemClick(ListView parent, View v, int position, long id) {
        LocaleStore.LocaleInfo locale = (LocaleStore.LocaleInfo) parent.getAdapter().getItem(position);
        boolean isSystemLocale = locale.isSystemLocale();
        boolean isRegionLocale = locale.getParent() != null;
        if (isSystemLocale || isRegionLocale) {
            LocaleSelectedListener localeSelectedListener = this.mListener;
            if (localeSelectedListener != null) {
                localeSelectedListener.onLocaleSelected(locale);
            }
            returnToParentFrame();
            return;
        }
        LocalePickerWithRegion selector = createCountryPicker(this.mListener, locale, this.mTranslatedOnly, this.mOnActionExpandListener, this.mLocalePickerCollector);
        if (selector != null) {
            getFragmentManager().beginTransaction().setTransition(4097).replace(getId(), selector).addToBackStack(null).commit();
        } else {
            returnToParentFrame();
        }
    }

    @Override // android.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (this.mParentLocale == null) {
            inflater.inflate(C4057R.C4060menu.language_selection_list, menu);
            MenuItem searchMenuItem = menu.findItem(C4057R.C4059id.locale_search_menu);
            MenuItem.OnActionExpandListener onActionExpandListener = this.mOnActionExpandListener;
            if (onActionExpandListener != null) {
                searchMenuItem.setOnActionExpandListener(onActionExpandListener);
            }
            SearchView searchView = (SearchView) searchMenuItem.getActionView();
            this.mSearchView = searchView;
            searchView.setQueryHint(getText(C4057R.string.search_language_hint));
            this.mSearchView.setOnQueryTextListener(this);
            if (!TextUtils.isEmpty(this.mPreviousSearch)) {
                searchMenuItem.expandActionView();
                this.mSearchView.setIconified(false);
                this.mSearchView.setActivated(true);
                if (this.mPreviousSearchHadFocus) {
                    this.mSearchView.requestFocus();
                }
                this.mSearchView.setQuery(this.mPreviousSearch, true);
            } else {
                this.mSearchView.setQuery(null, false);
            }
            getListView().setSelectionFromTop(this.mFirstVisiblePosition, this.mTopDistance);
        }
    }

    @Override // android.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override // android.widget.SearchView.OnQueryTextListener
    public boolean onQueryTextChange(String newText) {
        SuggestedLocaleAdapter suggestedLocaleAdapter = this.mAdapter;
        if (suggestedLocaleAdapter != null) {
            suggestedLocaleAdapter.getFilter().filter(newText);
            return false;
        }
        return false;
    }

    public void setOnActionExpandListener(MenuItem.OnActionExpandListener onActionExpandListener) {
        this.mOnActionExpandListener = onActionExpandListener;
    }
}
