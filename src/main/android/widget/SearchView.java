package android.widget;

import android.app.PendingIntent;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.CollapsibleActionView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.TextView;
import com.android.internal.C4057R;
import java.util.WeakHashMap;
/* loaded from: classes4.dex */
public class SearchView extends LinearLayout implements CollapsibleActionView {
    private static final boolean DBG = false;
    private static final String IME_OPTION_NO_MICROPHONE = "nm";
    private static final String LOG_TAG = "SearchView";
    private Bundle mAppSearchData;
    private boolean mClearingFocus;
    private final ImageView mCloseButton;
    private final ImageView mCollapsedIcon;
    private int mCollapsedImeOptions;
    private final CharSequence mDefaultQueryHint;
    private final View mDropDownAnchor;
    private boolean mExpandedInActionView;
    private final ImageView mGoButton;
    private boolean mIconified;
    private boolean mIconifiedByDefault;
    private int mMaxWidth;
    private CharSequence mOldQueryText;
    private final View.OnClickListener mOnClickListener;
    private OnCloseListener mOnCloseListener;
    private final TextView.OnEditorActionListener mOnEditorActionListener;
    private final AdapterView.OnItemClickListener mOnItemClickListener;
    private final AdapterView.OnItemSelectedListener mOnItemSelectedListener;
    private OnQueryTextListener mOnQueryChangeListener;
    private View.OnFocusChangeListener mOnQueryTextFocusChangeListener;
    private View.OnClickListener mOnSearchClickListener;
    private OnSuggestionListener mOnSuggestionListener;
    private final WeakHashMap<String, Drawable.ConstantState> mOutsideDrawablesCache;
    private CharSequence mQueryHint;
    private boolean mQueryRefinement;
    private Runnable mReleaseCursorRunnable;
    private final ImageView mSearchButton;
    private final View mSearchEditFrame;
    private final Drawable mSearchHintIcon;
    private final View mSearchPlate;
    private final SearchAutoComplete mSearchSrcTextView;
    private Rect mSearchSrcTextViewBounds;
    private Rect mSearchSrtTextViewBoundsExpanded;
    private SearchableInfo mSearchable;
    private final View mSubmitArea;
    private boolean mSubmitButtonEnabled;
    private final int mSuggestionCommitIconResId;
    private final int mSuggestionRowLayout;
    private CursorAdapter mSuggestionsAdapter;
    private int[] mTemp;
    private int[] mTemp2;
    View.OnKeyListener mTextKeyListener;
    private TextWatcher mTextWatcher;
    private UpdatableTouchDelegate mTouchDelegate;
    private Runnable mUpdateDrawableStateRunnable;
    private CharSequence mUserQuery;
    private final Intent mVoiceAppSearchIntent;
    private final ImageView mVoiceButton;
    private boolean mVoiceButtonEnabled;
    private final Intent mVoiceWebSearchIntent;

    /* loaded from: classes4.dex */
    public interface OnCloseListener {
        boolean onClose();
    }

    /* loaded from: classes4.dex */
    public interface OnQueryTextListener {
        boolean onQueryTextChange(String str);

        boolean onQueryTextSubmit(String str);
    }

    /* loaded from: classes4.dex */
    public interface OnSuggestionListener {
        boolean onSuggestionClick(int i);

        boolean onSuggestionSelect(int i);
    }

    /* loaded from: classes4.dex */
    public final class InspectionCompanion implements android.view.inspector.InspectionCompanion<SearchView> {
        private int mIconifiedByDefaultId;
        private int mIconifiedId;
        private int mMaxWidthId;
        private boolean mPropertiesMapped = false;
        private int mQueryHintId;
        private int mQueryId;

        @Override // android.view.inspector.InspectionCompanion
        public void mapProperties(PropertyMapper propertyMapper) {
            this.mIconifiedId = propertyMapper.mapBoolean("iconified", 0);
            this.mIconifiedByDefaultId = propertyMapper.mapBoolean("iconifiedByDefault", 16843514);
            this.mMaxWidthId = propertyMapper.mapInt("maxWidth", 16843039);
            this.mQueryId = propertyMapper.mapObject("query", 0);
            this.mQueryHintId = propertyMapper.mapObject("queryHint", 16843608);
            this.mPropertiesMapped = true;
        }

        @Override // android.view.inspector.InspectionCompanion
        public void readProperties(SearchView node, PropertyReader propertyReader) {
            if (!this.mPropertiesMapped) {
                throw new InspectionCompanion.UninitializedPropertyMapException();
            }
            propertyReader.readBoolean(this.mIconifiedId, node.isIconified());
            propertyReader.readBoolean(this.mIconifiedByDefaultId, node.isIconifiedByDefault());
            propertyReader.readInt(this.mMaxWidthId, node.getMaxWidth());
            propertyReader.readObject(this.mQueryId, node.getQuery());
            propertyReader.readObject(this.mQueryHintId, node.getQueryHint());
        }
    }

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 16843904);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mSearchSrcTextViewBounds = new Rect();
        this.mSearchSrtTextViewBoundsExpanded = new Rect();
        this.mTemp = new int[2];
        this.mTemp2 = new int[2];
        this.mUpdateDrawableStateRunnable = new Runnable() { // from class: android.widget.SearchView.1
            @Override // java.lang.Runnable
            public void run() {
                SearchView.this.updateFocusedState();
            }
        };
        this.mReleaseCursorRunnable = new Runnable() { // from class: android.widget.SearchView.2
            @Override // java.lang.Runnable
            public void run() {
                if (SearchView.this.mSuggestionsAdapter != null && (SearchView.this.mSuggestionsAdapter instanceof SuggestionsAdapter)) {
                    SearchView.this.mSuggestionsAdapter.changeCursor(null);
                }
            }
        };
        this.mOutsideDrawablesCache = new WeakHashMap<>();
        View.OnClickListener onClickListener = new View.OnClickListener() { // from class: android.widget.SearchView.5
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (v == SearchView.this.mSearchButton) {
                    SearchView.this.onSearchClicked();
                } else if (v == SearchView.this.mCloseButton) {
                    SearchView.this.onCloseClicked();
                } else if (v == SearchView.this.mGoButton) {
                    SearchView.this.onSubmitQuery();
                } else if (v == SearchView.this.mVoiceButton) {
                    SearchView.this.onVoiceClicked();
                } else if (v == SearchView.this.mSearchSrcTextView) {
                    SearchView.this.forceSuggestionQuery();
                }
            }
        };
        this.mOnClickListener = onClickListener;
        this.mTextKeyListener = new View.OnKeyListener() { // from class: android.widget.SearchView.6
            @Override // android.view.View.OnKeyListener
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                SearchableInfo.ActionKeyInfo actionKey;
                if (SearchView.this.mSearchable == null) {
                    return false;
                }
                if (SearchView.this.mSearchSrcTextView.isPopupShowing() && SearchView.this.mSearchSrcTextView.getListSelection() != -1) {
                    return SearchView.this.onSuggestionsKey(v, keyCode, event);
                }
                if (!SearchView.this.mSearchSrcTextView.isEmpty() && event.hasNoModifiers()) {
                    if (event.getAction() == 1 && (keyCode == 66 || keyCode == 160)) {
                        v.cancelLongPress();
                        SearchView searchView = SearchView.this;
                        searchView.launchQuerySearch(0, null, searchView.mSearchSrcTextView.getText().toString());
                        return true;
                    } else if (event.getAction() == 0 && (actionKey = SearchView.this.mSearchable.findActionKey(keyCode)) != null && actionKey.getQueryActionMsg() != null) {
                        SearchView.this.launchQuerySearch(keyCode, actionKey.getQueryActionMsg(), SearchView.this.mSearchSrcTextView.getText().toString());
                        return true;
                    }
                }
                return false;
            }
        };
        TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() { // from class: android.widget.SearchView.7
            @Override // android.widget.TextView.OnEditorActionListener
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                SearchView.this.onSubmitQuery();
                return true;
            }
        };
        this.mOnEditorActionListener = onEditorActionListener;
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() { // from class: android.widget.SearchView.8
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchView.this.onItemClicked(position, 0, null);
            }
        };
        this.mOnItemClickListener = onItemClickListener;
        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() { // from class: android.widget.SearchView.9
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SearchView.this.onItemSelected(position);
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        this.mOnItemSelectedListener = onItemSelectedListener;
        this.mTextWatcher = new TextWatcher() { // from class: android.widget.SearchView.10
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int before, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int after) {
                SearchView.this.onTextChanged(s);
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
            }
        };
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.SearchView, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C4057R.styleable.SearchView, attrs, a, defStyleAttr, defStyleRes);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int layoutResId = a.getResourceId(0, C4057R.layout.search_view);
        inflater.inflate(layoutResId, (ViewGroup) this, true);
        SearchAutoComplete searchAutoComplete = (SearchAutoComplete) findViewById(C4057R.C4059id.search_src_text);
        this.mSearchSrcTextView = searchAutoComplete;
        searchAutoComplete.setSearchView(this);
        this.mSearchEditFrame = findViewById(C4057R.C4059id.search_edit_frame);
        View findViewById = findViewById(C4057R.C4059id.search_plate);
        this.mSearchPlate = findViewById;
        View findViewById2 = findViewById(C4057R.C4059id.submit_area);
        this.mSubmitArea = findViewById2;
        ImageView imageView = (ImageView) findViewById(C4057R.C4059id.search_button);
        this.mSearchButton = imageView;
        ImageView imageView2 = (ImageView) findViewById(C4057R.C4059id.search_go_btn);
        this.mGoButton = imageView2;
        ImageView imageView3 = (ImageView) findViewById(C4057R.C4059id.search_close_btn);
        this.mCloseButton = imageView3;
        ImageView imageView4 = (ImageView) findViewById(C4057R.C4059id.search_voice_btn);
        this.mVoiceButton = imageView4;
        ImageView imageView5 = (ImageView) findViewById(C4057R.C4059id.search_mag_icon);
        this.mCollapsedIcon = imageView5;
        findViewById.setBackground(a.getDrawable(12));
        findViewById2.setBackground(a.getDrawable(13));
        imageView.setImageDrawable(a.getDrawable(8));
        imageView2.setImageDrawable(a.getDrawable(7));
        imageView3.setImageDrawable(a.getDrawable(6));
        imageView4.setImageDrawable(a.getDrawable(9));
        imageView5.setImageDrawable(a.getDrawable(8));
        if (a.hasValueOrEmpty(14)) {
            this.mSearchHintIcon = a.getDrawable(14);
        } else {
            this.mSearchHintIcon = a.getDrawable(8);
        }
        this.mSuggestionRowLayout = a.getResourceId(11, C4057R.layout.search_dropdown_item_icons_2line);
        this.mSuggestionCommitIconResId = a.getResourceId(10, 0);
        imageView.setOnClickListener(onClickListener);
        imageView3.setOnClickListener(onClickListener);
        imageView2.setOnClickListener(onClickListener);
        imageView4.setOnClickListener(onClickListener);
        searchAutoComplete.setOnClickListener(onClickListener);
        searchAutoComplete.addTextChangedListener(this.mTextWatcher);
        searchAutoComplete.setOnEditorActionListener(onEditorActionListener);
        searchAutoComplete.setOnItemClickListener(onItemClickListener);
        searchAutoComplete.setOnItemSelectedListener(onItemSelectedListener);
        searchAutoComplete.setOnKeyListener(this.mTextKeyListener);
        searchAutoComplete.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: android.widget.SearchView.3
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View v, boolean hasFocus) {
                if (SearchView.this.mOnQueryTextFocusChangeListener != null) {
                    SearchView.this.mOnQueryTextFocusChangeListener.onFocusChange(SearchView.this, hasFocus);
                }
            }
        });
        setIconifiedByDefault(a.getBoolean(4, true));
        int maxWidth = a.getDimensionPixelSize(1, -1);
        if (maxWidth != -1) {
            setMaxWidth(maxWidth);
        }
        this.mDefaultQueryHint = a.getText(15);
        this.mQueryHint = a.getText(5);
        int imeOptions = a.getInt(3, -1);
        if (imeOptions != -1) {
            setImeOptions(imeOptions);
        }
        int inputType = a.getInt(2, -1);
        if (inputType != -1) {
            setInputType(inputType);
        }
        if (getFocusable() == 16) {
            setFocusable(1);
        }
        a.recycle();
        Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
        this.mVoiceWebSearchIntent = intent;
        intent.addFlags(268435456);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        Intent intent2 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        this.mVoiceAppSearchIntent = intent2;
        intent2.addFlags(268435456);
        View findViewById3 = findViewById(searchAutoComplete.getDropDownAnchor());
        this.mDropDownAnchor = findViewById3;
        if (findViewById3 != null) {
            findViewById3.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: android.widget.SearchView.4
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    SearchView.this.adjustDropDownSizeAndPosition();
                }
            });
        }
        updateViewsVisibility(this.mIconifiedByDefault);
        updateQueryHint();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSuggestionRowLayout() {
        return this.mSuggestionRowLayout;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSuggestionCommitIconResId() {
        return this.mSuggestionCommitIconResId;
    }

    public void setSearchableInfo(SearchableInfo searchable) {
        this.mSearchable = searchable;
        if (searchable != null) {
            updateSearchAutoComplete();
            updateQueryHint();
        }
        boolean hasVoiceSearch = hasVoiceSearch();
        this.mVoiceButtonEnabled = hasVoiceSearch;
        if (hasVoiceSearch) {
            this.mSearchSrcTextView.setPrivateImeOptions(IME_OPTION_NO_MICROPHONE);
        }
        updateViewsVisibility(isIconified());
    }

    public void setAppSearchData(Bundle appSearchData) {
        this.mAppSearchData = appSearchData;
    }

    public void setImeOptions(int imeOptions) {
        this.mSearchSrcTextView.setImeOptions(imeOptions);
    }

    public int getImeOptions() {
        return this.mSearchSrcTextView.getImeOptions();
    }

    public void setInputType(int inputType) {
        this.mSearchSrcTextView.setInputType(inputType);
    }

    public int getInputType() {
        return this.mSearchSrcTextView.getInputType();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        if (!this.mClearingFocus && isFocusable()) {
            if (!isIconified()) {
                if (direction == 1) {
                    View found = focusSearch(1);
                    if (found != null) {
                        return found.requestFocus(1, previouslyFocusedRect);
                    }
                    return false;
                }
                boolean result = this.mSearchSrcTextView.requestFocus(direction, previouslyFocusedRect);
                if (result) {
                    updateViewsVisibility(false);
                }
                return result;
            }
            return super.requestFocus(direction, previouslyFocusedRect);
        }
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void clearFocus() {
        this.mClearingFocus = true;
        super.clearFocus();
        this.mSearchSrcTextView.clearFocus();
        this.mSearchSrcTextView.setImeVisibility(false);
        this.mClearingFocus = false;
    }

    public void setOnQueryTextListener(OnQueryTextListener listener) {
        this.mOnQueryChangeListener = listener;
    }

    public void setOnCloseListener(OnCloseListener listener) {
        this.mOnCloseListener = listener;
    }

    public void setOnQueryTextFocusChangeListener(View.OnFocusChangeListener listener) {
        this.mOnQueryTextFocusChangeListener = listener;
    }

    public void setOnSuggestionListener(OnSuggestionListener listener) {
        this.mOnSuggestionListener = listener;
    }

    public void setOnSearchClickListener(View.OnClickListener listener) {
        this.mOnSearchClickListener = listener;
    }

    public CharSequence getQuery() {
        return this.mSearchSrcTextView.getText();
    }

    public void setQuery(CharSequence query, boolean submit) {
        this.mSearchSrcTextView.setText(query);
        if (query != null) {
            SearchAutoComplete searchAutoComplete = this.mSearchSrcTextView;
            searchAutoComplete.setSelection(searchAutoComplete.length());
            this.mUserQuery = query;
        }
        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery();
        }
    }

    public void setQueryHint(CharSequence hint) {
        this.mQueryHint = hint;
        updateQueryHint();
    }

    public CharSequence getQueryHint() {
        if (this.mQueryHint != null) {
            CharSequence hint = this.mQueryHint;
            return hint;
        }
        SearchableInfo searchableInfo = this.mSearchable;
        if (searchableInfo != null && searchableInfo.getHintId() != 0) {
            CharSequence hint2 = getContext().getText(this.mSearchable.getHintId());
            return hint2;
        }
        CharSequence hint3 = this.mDefaultQueryHint;
        return hint3;
    }

    public void setIconifiedByDefault(boolean iconified) {
        if (this.mIconifiedByDefault == iconified) {
            return;
        }
        this.mIconifiedByDefault = iconified;
        updateViewsVisibility(iconified);
        updateQueryHint();
    }

    @Deprecated
    public boolean isIconfiedByDefault() {
        return this.mIconifiedByDefault;
    }

    public boolean isIconifiedByDefault() {
        return this.mIconifiedByDefault;
    }

    public void setIconified(boolean iconify) {
        if (iconify) {
            onCloseClicked();
        } else {
            onSearchClicked();
        }
    }

    public boolean isIconified() {
        return this.mIconified;
    }

    public void setSubmitButtonEnabled(boolean enabled) {
        this.mSubmitButtonEnabled = enabled;
        updateViewsVisibility(isIconified());
    }

    public boolean isSubmitButtonEnabled() {
        return this.mSubmitButtonEnabled;
    }

    public void setQueryRefinementEnabled(boolean enable) {
        this.mQueryRefinement = enable;
        CursorAdapter cursorAdapter = this.mSuggestionsAdapter;
        if (cursorAdapter instanceof SuggestionsAdapter) {
            ((SuggestionsAdapter) cursorAdapter).setQueryRefinement(enable ? 2 : 1);
        }
    }

    public boolean isQueryRefinementEnabled() {
        return this.mQueryRefinement;
    }

    public void setSuggestionsAdapter(CursorAdapter adapter) {
        this.mSuggestionsAdapter = adapter;
        this.mSearchSrcTextView.setAdapter(adapter);
    }

    public CursorAdapter getSuggestionsAdapter() {
        return this.mSuggestionsAdapter;
    }

    public void setMaxWidth(int maxpixels) {
        this.mMaxWidth = maxpixels;
        requestLayout();
    }

    public int getMaxWidth() {
        return this.mMaxWidth;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isIconified()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        switch (widthMode) {
            case Integer.MIN_VALUE:
                int i = this.mMaxWidth;
                if (i > 0) {
                    width = Math.min(i, width);
                    break;
                } else {
                    width = Math.min(getPreferredWidth(), width);
                    break;
                }
            case 0:
                int i2 = this.mMaxWidth;
                if (i2 <= 0) {
                    i2 = getPreferredWidth();
                }
                width = i2;
                break;
            case 1073741824:
                int i3 = this.mMaxWidth;
                if (i3 > 0) {
                    width = Math.min(i3, width);
                    break;
                }
                break;
        }
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        switch (heightMode) {
            case Integer.MIN_VALUE:
                height = Math.min(getPreferredHeight(), height);
                break;
            case 0:
                height = getPreferredHeight();
                break;
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            getChildBoundsWithinSearchView(this.mSearchSrcTextView, this.mSearchSrcTextViewBounds);
            this.mSearchSrtTextViewBoundsExpanded.set(this.mSearchSrcTextViewBounds.left, 0, this.mSearchSrcTextViewBounds.right, bottom - top);
            UpdatableTouchDelegate updatableTouchDelegate = this.mTouchDelegate;
            if (updatableTouchDelegate == null) {
                UpdatableTouchDelegate updatableTouchDelegate2 = new UpdatableTouchDelegate(this.mSearchSrtTextViewBoundsExpanded, this.mSearchSrcTextViewBounds, this.mSearchSrcTextView);
                this.mTouchDelegate = updatableTouchDelegate2;
                setTouchDelegate(updatableTouchDelegate2);
                return;
            }
            updatableTouchDelegate.setBounds(this.mSearchSrtTextViewBoundsExpanded, this.mSearchSrcTextViewBounds);
        }
    }

    private void getChildBoundsWithinSearchView(View view, Rect rect) {
        view.getLocationInWindow(this.mTemp);
        getLocationInWindow(this.mTemp2);
        int[] iArr = this.mTemp;
        int i = iArr[1];
        int[] iArr2 = this.mTemp2;
        int top = i - iArr2[1];
        int left = iArr[0] - iArr2[0];
        rect.set(left, top, view.getWidth() + left, view.getHeight() + top);
    }

    private int getPreferredWidth() {
        return getContext().getResources().getDimensionPixelSize(C4057R.dimen.search_view_preferred_width);
    }

    private int getPreferredHeight() {
        return getContext().getResources().getDimensionPixelSize(C4057R.dimen.search_view_preferred_height);
    }

    private void updateViewsVisibility(boolean collapsed) {
        int iconVisibility;
        this.mIconified = collapsed;
        int visCollapsed = collapsed ? 0 : 8;
        boolean hasText = !TextUtils.isEmpty(this.mSearchSrcTextView.getText());
        this.mSearchButton.setVisibility(visCollapsed);
        updateSubmitButton(hasText);
        this.mSearchEditFrame.setVisibility(collapsed ? 8 : 0);
        if (this.mCollapsedIcon.getDrawable() == null || this.mIconifiedByDefault) {
            iconVisibility = 8;
        } else {
            iconVisibility = 0;
        }
        this.mCollapsedIcon.setVisibility(iconVisibility);
        updateCloseButton();
        updateVoiceButton(hasText ? false : true);
        updateSubmitArea();
    }

    private boolean hasVoiceSearch() {
        SearchableInfo searchableInfo = this.mSearchable;
        if (searchableInfo != null && searchableInfo.getVoiceSearchEnabled()) {
            Intent testIntent = null;
            if (this.mSearchable.getVoiceSearchLaunchWebSearch()) {
                testIntent = this.mVoiceWebSearchIntent;
            } else if (this.mSearchable.getVoiceSearchLaunchRecognizer()) {
                testIntent = this.mVoiceAppSearchIntent;
            }
            if (testIntent != null) {
                ResolveInfo ri = getContext().getPackageManager().resolveActivity(testIntent, 65536);
                return ri != null;
            }
        }
        return false;
    }

    private boolean isSubmitAreaEnabled() {
        return (this.mSubmitButtonEnabled || this.mVoiceButtonEnabled) && !isIconified();
    }

    private void updateSubmitButton(boolean hasText) {
        int visibility = 8;
        if (this.mSubmitButtonEnabled && isSubmitAreaEnabled() && hasFocus() && (hasText || !this.mVoiceButtonEnabled)) {
            visibility = 0;
        }
        this.mGoButton.setVisibility(visibility);
    }

    private void updateSubmitArea() {
        int visibility = 8;
        if (isSubmitAreaEnabled() && (this.mGoButton.getVisibility() == 0 || this.mVoiceButton.getVisibility() == 0)) {
            visibility = 0;
        }
        this.mSubmitArea.setVisibility(visibility);
    }

    private void updateCloseButton() {
        boolean showClose = true;
        boolean hasText = !TextUtils.isEmpty(this.mSearchSrcTextView.getText());
        if (!hasText && (!this.mIconifiedByDefault || this.mExpandedInActionView)) {
            showClose = false;
        }
        this.mCloseButton.setVisibility(showClose ? 0 : 8);
        Drawable closeButtonImg = this.mCloseButton.getDrawable();
        if (closeButtonImg != null) {
            closeButtonImg.setState(hasText ? ENABLED_STATE_SET : EMPTY_STATE_SET);
        }
    }

    private void postUpdateFocusedState() {
        post(this.mUpdateDrawableStateRunnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFocusedState() {
        boolean focused = this.mSearchSrcTextView.hasFocus();
        int[] stateSet = focused ? FOCUSED_STATE_SET : EMPTY_STATE_SET;
        Drawable searchPlateBg = this.mSearchPlate.getBackground();
        if (searchPlateBg != null) {
            searchPlateBg.setState(stateSet);
        }
        Drawable submitAreaBg = this.mSubmitArea.getBackground();
        if (submitAreaBg != null) {
            submitAreaBg.setState(stateSet);
        }
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        removeCallbacks(this.mUpdateDrawableStateRunnable);
        post(this.mReleaseCursorRunnable);
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onQueryRefine(CharSequence queryText) {
        setQuery(queryText);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        SearchableInfo searchableInfo = this.mSearchable;
        if (searchableInfo == null) {
            return false;
        }
        SearchableInfo.ActionKeyInfo actionKey = searchableInfo.findActionKey(keyCode);
        if (actionKey != null && actionKey.getQueryActionMsg() != null) {
            launchQuerySearch(keyCode, actionKey.getQueryActionMsg(), this.mSearchSrcTextView.getText().toString());
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onSuggestionsKey(View v, int keyCode, KeyEvent event) {
        SearchableInfo.ActionKeyInfo actionKey;
        int position;
        String actionMsg;
        if (this.mSearchable != null && this.mSuggestionsAdapter != null && event.getAction() == 0 && event.hasNoModifiers()) {
            if (keyCode == 66 || keyCode == 84 || keyCode == 61) {
                return onItemClicked(this.mSearchSrcTextView.getListSelection(), 0, null);
            }
            if (keyCode == 21 || keyCode == 22) {
                int selPoint = keyCode == 21 ? 0 : this.mSearchSrcTextView.length();
                this.mSearchSrcTextView.setSelection(selPoint);
                this.mSearchSrcTextView.setListSelection(0);
                this.mSearchSrcTextView.clearListSelection();
                this.mSearchSrcTextView.ensureImeVisible(true);
                return true;
            } else if ((keyCode != 19 || this.mSearchSrcTextView.getListSelection() != 0) && (actionKey = this.mSearchable.findActionKey(keyCode)) != null && ((actionKey.getSuggestActionMsg() != null || actionKey.getSuggestActionMsgColumn() != null) && (position = this.mSearchSrcTextView.getListSelection()) != -1)) {
                Cursor c = this.mSuggestionsAdapter.getCursor();
                if (c.moveToPosition(position) && (actionMsg = getActionKeyMessage(c, actionKey)) != null && actionMsg.length() > 0) {
                    return onItemClicked(position, keyCode, actionMsg);
                }
            }
        }
        return false;
    }

    private static String getActionKeyMessage(Cursor c, SearchableInfo.ActionKeyInfo actionKey) {
        String result = null;
        String column = actionKey.getSuggestActionMsgColumn();
        if (column != null) {
            result = SuggestionsAdapter.getColumnString(c, column);
        }
        if (result == null) {
            String result2 = actionKey.getSuggestActionMsg();
            return result2;
        }
        return result;
    }

    private CharSequence getDecoratedHint(CharSequence hintText) {
        if (!this.mIconifiedByDefault || this.mSearchHintIcon == null) {
            return hintText;
        }
        int textSize = (int) (this.mSearchSrcTextView.getTextSize() * 1.25d);
        this.mSearchHintIcon.setBounds(0, 0, textSize, textSize);
        SpannableStringBuilder ssb = new SpannableStringBuilder("   ");
        ssb.setSpan(new ImageSpan(this.mSearchHintIcon), 1, 2, 33);
        ssb.append(hintText);
        return ssb;
    }

    private void updateQueryHint() {
        CharSequence hint = getQueryHint();
        this.mSearchSrcTextView.setHint(getDecoratedHint(hint == null ? "" : hint));
    }

    private void updateSearchAutoComplete() {
        this.mSearchSrcTextView.setDropDownAnimationStyle(0);
        this.mSearchSrcTextView.setThreshold(this.mSearchable.getSuggestThreshold());
        this.mSearchSrcTextView.setImeOptions(this.mSearchable.getImeOptions());
        int inputType = this.mSearchable.getInputType();
        if ((inputType & 15) == 1) {
            inputType &= -65537;
            if (this.mSearchable.getSuggestAuthority() != null) {
                inputType = inputType | 65536 | 524288;
            }
        }
        this.mSearchSrcTextView.setInputType(inputType);
        CursorAdapter cursorAdapter = this.mSuggestionsAdapter;
        if (cursorAdapter != null) {
            cursorAdapter.changeCursor(null);
        }
        if (this.mSearchable.getSuggestAuthority() != null) {
            SuggestionsAdapter suggestionsAdapter = new SuggestionsAdapter(getContext(), this, this.mSearchable, this.mOutsideDrawablesCache);
            this.mSuggestionsAdapter = suggestionsAdapter;
            this.mSearchSrcTextView.setAdapter(suggestionsAdapter);
            ((SuggestionsAdapter) this.mSuggestionsAdapter).setQueryRefinement(this.mQueryRefinement ? 2 : 1);
        }
    }

    private void updateVoiceButton(boolean empty) {
        int visibility = 8;
        if (this.mVoiceButtonEnabled && !isIconified() && empty) {
            visibility = 0;
            this.mGoButton.setVisibility(8);
        }
        this.mVoiceButton.setVisibility(visibility);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTextChanged(CharSequence newText) {
        CharSequence text = this.mSearchSrcTextView.getText();
        this.mUserQuery = text;
        boolean hasText = !TextUtils.isEmpty(text);
        updateSubmitButton(hasText);
        updateVoiceButton(hasText ? false : true);
        updateCloseButton();
        updateSubmitArea();
        if (this.mOnQueryChangeListener != null && !TextUtils.equals(newText, this.mOldQueryText)) {
            this.mOnQueryChangeListener.onQueryTextChange(newText.toString());
        }
        this.mOldQueryText = newText.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSubmitQuery() {
        CharSequence query = this.mSearchSrcTextView.getText();
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            OnQueryTextListener onQueryTextListener = this.mOnQueryChangeListener;
            if (onQueryTextListener == null || !onQueryTextListener.onQueryTextSubmit(query.toString())) {
                if (this.mSearchable != null) {
                    launchQuerySearch(0, null, query.toString());
                }
                this.mSearchSrcTextView.setImeVisibility(false);
                dismissSuggestions();
            }
        }
    }

    private void dismissSuggestions() {
        this.mSearchSrcTextView.dismissDropDown();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCloseClicked() {
        CharSequence text = this.mSearchSrcTextView.getText();
        if (TextUtils.isEmpty(text)) {
            if (this.mIconifiedByDefault) {
                OnCloseListener onCloseListener = this.mOnCloseListener;
                if (onCloseListener == null || !onCloseListener.onClose()) {
                    clearFocus();
                    updateViewsVisibility(true);
                    return;
                }
                return;
            }
            return;
        }
        this.mSearchSrcTextView.setText("");
        this.mSearchSrcTextView.requestFocus();
        this.mSearchSrcTextView.setImeVisibility(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSearchClicked() {
        updateViewsVisibility(false);
        this.mSearchSrcTextView.requestFocus();
        this.mSearchSrcTextView.setImeVisibility(true);
        View.OnClickListener onClickListener = this.mOnSearchClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onVoiceClicked() {
        if (this.mSearchable == null) {
            return;
        }
        SearchableInfo searchable = this.mSearchable;
        try {
            if (searchable.getVoiceSearchLaunchWebSearch()) {
                Intent webSearchIntent = createVoiceWebSearchIntent(this.mVoiceWebSearchIntent, searchable);
                getContext().startActivity(webSearchIntent);
            } else if (searchable.getVoiceSearchLaunchRecognizer()) {
                Intent appSearchIntent = createVoiceAppSearchIntent(this.mVoiceAppSearchIntent, searchable);
                getContext().startActivity(appSearchIntent);
            }
        } catch (ActivityNotFoundException e) {
            Log.m104w(LOG_TAG, "Could not find voice search activity");
        }
    }

    void onTextFocusChanged() {
        updateViewsVisibility(isIconified());
        postUpdateFocusedState();
        if (this.mSearchSrcTextView.hasFocus()) {
            forceSuggestionQuery();
        }
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        postUpdateFocusedState();
    }

    @Override // android.view.CollapsibleActionView
    public void onActionViewCollapsed() {
        setQuery("", false);
        clearFocus();
        updateViewsVisibility(true);
        this.mSearchSrcTextView.setImeOptions(this.mCollapsedImeOptions);
        this.mExpandedInActionView = false;
    }

    @Override // android.view.CollapsibleActionView
    public void onActionViewExpanded() {
        if (this.mExpandedInActionView) {
            return;
        }
        this.mExpandedInActionView = true;
        int imeOptions = this.mSearchSrcTextView.getImeOptions();
        this.mCollapsedImeOptions = imeOptions;
        this.mSearchSrcTextView.setImeOptions(imeOptions | 33554432);
        this.mSearchSrcTextView.setText("");
        setIconified(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.widget.SearchView.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        boolean isIconified;

        SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            this.isIconified = ((Boolean) source.readValue(null)).booleanValue();
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeValue(Boolean.valueOf(this.isIconified));
        }

        public String toString() {
            return "SearchView.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " isIconified=" + this.isIconified + "}";
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.isIconified = isIconified();
        return ss;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        updateViewsVisibility(ss.isIconified);
        requestLayout();
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return SearchView.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void adjustDropDownSizeAndPosition() {
        int iconOffset;
        int offset;
        if (this.mDropDownAnchor.getWidth() > 1) {
            Resources res = getContext().getResources();
            int anchorPadding = this.mSearchPlate.getPaddingLeft();
            Rect dropDownPadding = new Rect();
            boolean isLayoutRtl = isLayoutRtl();
            if (this.mIconifiedByDefault) {
                iconOffset = res.getDimensionPixelSize(C4057R.dimen.dropdownitem_icon_width) + res.getDimensionPixelSize(C4057R.dimen.dropdownitem_text_padding_left);
            } else {
                iconOffset = 0;
            }
            this.mSearchSrcTextView.getDropDownBackground().getPadding(dropDownPadding);
            if (isLayoutRtl) {
                offset = -dropDownPadding.left;
            } else {
                int offset2 = dropDownPadding.left;
                offset = anchorPadding - (offset2 + iconOffset);
            }
            this.mSearchSrcTextView.setDropDownHorizontalOffset(offset);
            int width = (((this.mDropDownAnchor.getWidth() + dropDownPadding.left) + dropDownPadding.right) + iconOffset) - anchorPadding;
            this.mSearchSrcTextView.setDropDownWidth(width);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onItemClicked(int position, int actionKey, String actionMsg) {
        OnSuggestionListener onSuggestionListener = this.mOnSuggestionListener;
        if (onSuggestionListener == null || !onSuggestionListener.onSuggestionClick(position)) {
            launchSuggestion(position, 0, null);
            this.mSearchSrcTextView.setImeVisibility(false);
            dismissSuggestions();
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onItemSelected(int position) {
        OnSuggestionListener onSuggestionListener = this.mOnSuggestionListener;
        if (onSuggestionListener == null || !onSuggestionListener.onSuggestionSelect(position)) {
            rewriteQueryFromSuggestion(position);
            return true;
        }
        return false;
    }

    private void rewriteQueryFromSuggestion(int position) {
        CharSequence oldQuery = this.mSearchSrcTextView.getText();
        Cursor c = this.mSuggestionsAdapter.getCursor();
        if (c == null) {
            return;
        }
        if (c.moveToPosition(position)) {
            CharSequence newQuery = this.mSuggestionsAdapter.convertToString(c);
            if (newQuery != null) {
                setQuery(newQuery);
                return;
            } else {
                setQuery(oldQuery);
                return;
            }
        }
        setQuery(oldQuery);
    }

    private boolean launchSuggestion(int position, int actionKey, String actionMsg) {
        Cursor c = this.mSuggestionsAdapter.getCursor();
        if (c != null && c.moveToPosition(position)) {
            Intent intent = createIntentFromSuggestion(c, actionKey, actionMsg);
            launchIntent(intent);
            return true;
        }
        return false;
    }

    private void launchIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        try {
            getContext().startActivity(intent);
        } catch (RuntimeException ex) {
            Log.m109e(LOG_TAG, "Failed launch activity: " + intent, ex);
        }
    }

    private void setQuery(CharSequence query) {
        this.mSearchSrcTextView.setText(query, true);
        this.mSearchSrcTextView.setSelection(TextUtils.isEmpty(query) ? 0 : query.length());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchQuerySearch(int actionKey, String actionMsg, String query) {
        Intent intent = createIntent(Intent.ACTION_SEARCH, null, null, query, actionKey, actionMsg);
        getContext().startActivity(intent);
    }

    private Intent createIntent(String action, Uri data, String extraData, String query, int actionKey, String actionMsg) {
        Intent intent = new Intent(action);
        intent.addFlags(268435456);
        if (data != null) {
            intent.setData(data);
        }
        intent.putExtra(SearchManager.USER_QUERY, this.mUserQuery);
        if (query != null) {
            intent.putExtra("query", query);
        }
        if (extraData != null) {
            intent.putExtra(SearchManager.EXTRA_DATA_KEY, extraData);
        }
        Bundle bundle = this.mAppSearchData;
        if (bundle != null) {
            intent.putExtra(SearchManager.APP_DATA, bundle);
        }
        if (actionKey != 0) {
            intent.putExtra(SearchManager.ACTION_KEY, actionKey);
            intent.putExtra(SearchManager.ACTION_MSG, actionMsg);
        }
        intent.setComponent(this.mSearchable.getSearchActivity());
        return intent;
    }

    private Intent createVoiceWebSearchIntent(Intent baseIntent, SearchableInfo searchable) {
        Intent voiceIntent = new Intent(baseIntent);
        ComponentName searchActivity = searchable.getSearchActivity();
        voiceIntent.putExtra("calling_package", searchActivity == null ? null : searchActivity.flattenToShortString());
        return voiceIntent;
    }

    private Intent createVoiceAppSearchIntent(Intent baseIntent, SearchableInfo searchable) {
        ComponentName searchActivity = searchable.getSearchActivity();
        Intent queryIntent = new Intent(Intent.ACTION_SEARCH);
        queryIntent.setComponent(searchActivity);
        PendingIntent pending = PendingIntent.getActivity(getContext(), 0, queryIntent, 1107296256);
        Bundle queryExtras = new Bundle();
        Bundle bundle = this.mAppSearchData;
        if (bundle != null) {
            queryExtras.putParcelable(SearchManager.APP_DATA, bundle);
        }
        Intent voiceIntent = new Intent(baseIntent);
        String languageModel = RecognizerIntent.LANGUAGE_MODEL_FREE_FORM;
        String prompt = null;
        String language = null;
        int maxResults = 1;
        Resources resources = getResources();
        if (searchable.getVoiceLanguageModeId() != 0) {
            languageModel = resources.getString(searchable.getVoiceLanguageModeId());
        }
        if (searchable.getVoicePromptTextId() != 0) {
            prompt = resources.getString(searchable.getVoicePromptTextId());
        }
        if (searchable.getVoiceLanguageId() != 0) {
            language = resources.getString(searchable.getVoiceLanguageId());
        }
        if (searchable.getVoiceMaxResults() != 0) {
            maxResults = searchable.getVoiceMaxResults();
        }
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, languageModel);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, prompt);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxResults);
        voiceIntent.putExtra("calling_package", searchActivity == null ? null : searchActivity.flattenToShortString());
        voiceIntent.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT, pending);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT_BUNDLE, queryExtras);
        return voiceIntent;
    }

    private Intent createIntentFromSuggestion(Cursor c, int actionKey, String actionMsg) {
        int rowNum;
        String data;
        String id;
        try {
            String action = SuggestionsAdapter.getColumnString(c, SearchManager.SUGGEST_COLUMN_INTENT_ACTION);
            if (action == null) {
                action = this.mSearchable.getSuggestIntentAction();
            }
            if (action == null) {
                action = Intent.ACTION_SEARCH;
            }
            String data2 = SuggestionsAdapter.getColumnString(c, SearchManager.SUGGEST_COLUMN_INTENT_DATA);
            if (data2 == null) {
                data2 = this.mSearchable.getSuggestIntentData();
            }
            if (data2 != null && (id = SuggestionsAdapter.getColumnString(c, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID)) != null) {
                data = data2 + "/" + Uri.encode(id);
            } else {
                data = data2;
            }
            Uri dataUri = data == null ? null : Uri.parse(data);
            String query = SuggestionsAdapter.getColumnString(c, SearchManager.SUGGEST_COLUMN_QUERY);
            String extraData = SuggestionsAdapter.getColumnString(c, SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA);
            return createIntent(action, dataUri, extraData, query, actionKey, actionMsg);
        } catch (RuntimeException e) {
            try {
                rowNum = c.getPosition();
            } catch (RuntimeException e2) {
                rowNum = -1;
            }
            Log.m103w(LOG_TAG, "Search suggestions cursor at row " + rowNum + " returned exception.", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void forceSuggestionQuery() {
        this.mSearchSrcTextView.doBeforeTextChanged();
        this.mSearchSrcTextView.doAfterTextChanged();
    }

    static boolean isLandscapeMode(Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }

    /* loaded from: classes4.dex */
    private static class UpdatableTouchDelegate extends TouchDelegate {
        private final Rect mActualBounds;
        private boolean mDelegateTargeted;
        private final View mDelegateView;
        private final int mSlop;
        private final Rect mSlopBounds;
        private final Rect mTargetBounds;

        public UpdatableTouchDelegate(Rect targetBounds, Rect actualBounds, View delegateView) {
            super(targetBounds, delegateView);
            this.mSlop = ViewConfiguration.get(delegateView.getContext()).getScaledTouchSlop();
            this.mTargetBounds = new Rect();
            this.mSlopBounds = new Rect();
            this.mActualBounds = new Rect();
            setBounds(targetBounds, actualBounds);
            this.mDelegateView = delegateView;
        }

        public void setBounds(Rect desiredBounds, Rect actualBounds) {
            this.mTargetBounds.set(desiredBounds);
            this.mSlopBounds.set(desiredBounds);
            Rect rect = this.mSlopBounds;
            int i = this.mSlop;
            rect.inset(-i, -i);
            this.mActualBounds.set(actualBounds);
        }

        @Override // android.view.TouchDelegate
        public boolean onTouchEvent(MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            boolean sendToDelegate = false;
            boolean hit = true;
            switch (event.getAction()) {
                case 0:
                    if (this.mTargetBounds.contains(x, y)) {
                        this.mDelegateTargeted = true;
                        sendToDelegate = true;
                        break;
                    }
                    break;
                case 1:
                case 2:
                    sendToDelegate = this.mDelegateTargeted;
                    if (sendToDelegate && !this.mSlopBounds.contains(x, y)) {
                        hit = false;
                        break;
                    }
                    break;
                case 3:
                    sendToDelegate = this.mDelegateTargeted;
                    this.mDelegateTargeted = false;
                    break;
            }
            if (!sendToDelegate) {
                return false;
            }
            if (hit && !this.mActualBounds.contains(x, y)) {
                event.setLocation(this.mDelegateView.getWidth() / 2, this.mDelegateView.getHeight() / 2);
            } else {
                event.setLocation(x - this.mActualBounds.left, y - this.mActualBounds.top);
            }
            boolean handled = this.mDelegateView.dispatchTouchEvent(event);
            return handled;
        }
    }

    /* loaded from: classes4.dex */
    public static class SearchAutoComplete extends AutoCompleteTextView {
        private boolean mHasPendingShowSoftInputRequest;
        final Runnable mRunShowSoftInputIfNecessary;
        private SearchView mSearchView;
        private int mThreshold;

        public SearchAutoComplete(Context context) {
            super(context);
            this.mRunShowSoftInputIfNecessary = new Runnable() { // from class: android.widget.SearchView$SearchAutoComplete$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SearchView.SearchAutoComplete.this.lambda$new$0();
                }
            };
            this.mThreshold = getThreshold();
        }

        public SearchAutoComplete(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.mRunShowSoftInputIfNecessary = new Runnable() { // from class: android.widget.SearchView$SearchAutoComplete$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SearchView.SearchAutoComplete.this.lambda$new$0();
                }
            };
            this.mThreshold = getThreshold();
        }

        public SearchAutoComplete(Context context, AttributeSet attrs, int defStyleAttrs) {
            super(context, attrs, defStyleAttrs);
            this.mRunShowSoftInputIfNecessary = new Runnable() { // from class: android.widget.SearchView$SearchAutoComplete$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SearchView.SearchAutoComplete.this.lambda$new$0();
                }
            };
            this.mThreshold = getThreshold();
        }

        public SearchAutoComplete(Context context, AttributeSet attrs, int defStyleAttrs, int defStyleRes) {
            super(context, attrs, defStyleAttrs, defStyleRes);
            this.mRunShowSoftInputIfNecessary = new Runnable() { // from class: android.widget.SearchView$SearchAutoComplete$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SearchView.SearchAutoComplete.this.lambda$new$0();
                }
            };
            this.mThreshold = getThreshold();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.View
        public void onFinishInflate() {
            super.onFinishInflate();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            setMinWidth((int) TypedValue.applyDimension(1, getSearchViewTextMinWidthDp(), metrics));
        }

        void setSearchView(SearchView searchView) {
            this.mSearchView = searchView;
        }

        @Override // android.widget.AutoCompleteTextView
        public void setThreshold(int threshold) {
            super.setThreshold(threshold);
            this.mThreshold = threshold;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isEmpty() {
            return TextUtils.getTrimmedLength(getText()) == 0;
        }

        @Override // android.widget.AutoCompleteTextView
        protected void replaceText(CharSequence text) {
        }

        @Override // android.widget.AutoCompleteTextView
        public void performCompletion() {
        }

        @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
        public void onWindowFocusChanged(boolean hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);
            if (hasWindowFocus && this.mSearchView.hasFocus() && getVisibility() == 0) {
                this.mHasPendingShowSoftInputRequest = true;
                if (SearchView.isLandscapeMode(getContext())) {
                    ensureImeVisible(true);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
        public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
            this.mSearchView.onTextFocusChanged();
        }

        @Override // android.widget.AutoCompleteTextView
        public boolean enoughToFilter() {
            return this.mThreshold <= 0 || super.enoughToFilter();
        }

        @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
        public boolean onKeyPreIme(int keyCode, KeyEvent event) {
            boolean consume = super.onKeyPreIme(keyCode, event);
            if (consume && keyCode == 4 && event.getAction() == 1) {
                setImeVisibility(false);
            }
            return consume;
        }

        private int getSearchViewTextMinWidthDp() {
            Configuration configuration = getResources().getConfiguration();
            int width = configuration.screenWidthDp;
            int height = configuration.screenHeightDp;
            int orientation = configuration.orientation;
            if (width >= 960 && height >= 720 && orientation == 2) {
                return 256;
            }
            if (width < 600) {
                if (width >= 640 && height >= 480) {
                    return 192;
                }
                return 160;
            }
            return 192;
        }

        @Override // android.widget.TextView, android.view.View
        public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
            InputConnection ic = super.onCreateInputConnection(editorInfo);
            if (this.mHasPendingShowSoftInputRequest) {
                removeCallbacks(this.mRunShowSoftInputIfNecessary);
                post(this.mRunShowSoftInputIfNecessary);
            }
            return ic;
        }

        @Override // android.view.View
        public boolean checkInputConnectionProxy(View view) {
            return view == this.mSearchView;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: showSoftInputIfNecessary */
        public void lambda$new$0() {
            if (this.mHasPendingShowSoftInputRequest) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(InputMethodManager.class);
                imm.showSoftInput(this, 0);
                this.mHasPendingShowSoftInputRequest = false;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setImeVisibility(boolean visible) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(InputMethodManager.class);
            if (!visible) {
                this.mHasPendingShowSoftInputRequest = false;
                removeCallbacks(this.mRunShowSoftInputIfNecessary);
                imm.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (imm.isActive(this)) {
                this.mHasPendingShowSoftInputRequest = false;
                removeCallbacks(this.mRunShowSoftInputIfNecessary);
                imm.showSoftInput(this, 0);
            } else {
                this.mHasPendingShowSoftInputRequest = true;
            }
        }
    }
}
