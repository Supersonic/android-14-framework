package android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.PopupWindow;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;
import android.window.WindowOnBackInvokedDispatcher;
import com.android.internal.C4057R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
/* loaded from: classes4.dex */
public class AutoCompleteTextView extends EditText implements Filter.FilterListener {
    static final boolean DEBUG = false;
    static final int EXPAND_MAX = 3;
    static final String TAG = "AutoCompleteTextView";
    private ListAdapter mAdapter;
    private MyWatcher mAutoCompleteTextWatcher;
    private final OnBackInvokedCallback mBackCallback;
    private boolean mBackCallbackRegistered;
    private boolean mBlockCompletion;
    private int mDropDownAnchorId;
    private boolean mDropDownDismissedOnCompletion;
    private Filter mFilter;
    private int mHintResource;
    private CharSequence mHintText;
    private TextView mHintView;
    private AdapterView.OnItemClickListener mItemClickListener;
    private AdapterView.OnItemSelectedListener mItemSelectedListener;
    private int mLastKeyCode;
    private PopupDataSetObserver mObserver;
    private final PassThroughClickListener mPassThroughClickListener;
    private final ListPopupWindow mPopup;
    private boolean mPopupCanBeUpdated;
    private final Context mPopupContext;
    private int mThreshold;
    private Validator mValidator;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface InputMethodMode {
    }

    /* loaded from: classes4.dex */
    public interface OnDismissListener {
        void onDismiss();
    }

    /* loaded from: classes4.dex */
    public interface Validator {
        CharSequence fixText(CharSequence charSequence);

        boolean isValid(CharSequence charSequence);
    }

    /* loaded from: classes4.dex */
    public final class InspectionCompanion implements android.view.inspector.InspectionCompanion<AutoCompleteTextView> {
        private int mCompletionHintId;
        private int mCompletionThresholdId;
        private int mDropDownHeightId;
        private int mDropDownHorizontalOffsetId;
        private int mDropDownVerticalOffsetId;
        private int mDropDownWidthId;
        private int mPopupBackgroundId;
        private boolean mPropertiesMapped = false;

        @Override // android.view.inspector.InspectionCompanion
        public void mapProperties(PropertyMapper propertyMapper) {
            this.mCompletionHintId = propertyMapper.mapObject("completionHint", 16843122);
            this.mCompletionThresholdId = propertyMapper.mapInt("completionThreshold", 16843124);
            this.mDropDownHeightId = propertyMapper.mapInt("dropDownHeight", 16843395);
            this.mDropDownHorizontalOffsetId = propertyMapper.mapInt("dropDownHorizontalOffset", 16843436);
            this.mDropDownVerticalOffsetId = propertyMapper.mapInt("dropDownVerticalOffset", 16843437);
            this.mDropDownWidthId = propertyMapper.mapInt("dropDownWidth", 16843362);
            this.mPopupBackgroundId = propertyMapper.mapObject("popupBackground", 16843126);
            this.mPropertiesMapped = true;
        }

        @Override // android.view.inspector.InspectionCompanion
        public void readProperties(AutoCompleteTextView node, PropertyReader propertyReader) {
            if (!this.mPropertiesMapped) {
                throw new InspectionCompanion.UninitializedPropertyMapException();
            }
            propertyReader.readObject(this.mCompletionHintId, node.getCompletionHint());
            propertyReader.readInt(this.mCompletionThresholdId, node.getThreshold());
            propertyReader.readInt(this.mDropDownHeightId, node.getDropDownHeight());
            propertyReader.readInt(this.mDropDownHorizontalOffsetId, node.getDropDownHorizontalOffset());
            propertyReader.readInt(this.mDropDownVerticalOffsetId, node.getDropDownVerticalOffset());
            propertyReader.readInt(this.mDropDownWidthId, node.getDropDownWidth());
            propertyReader.readObject(this.mPopupBackgroundId, node.getDropDownBackground());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (isPopupShowing()) {
            dismissDropDown();
        }
    }

    public AutoCompleteTextView(Context context) {
        this(context, null);
    }

    public AutoCompleteTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 16842859);
    }

    public AutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr, defStyleRes, null);
    }

    public AutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray pa;
        this.mDropDownDismissedOnCompletion = true;
        this.mLastKeyCode = 0;
        this.mValidator = null;
        this.mPopupCanBeUpdated = true;
        this.mBackCallback = new OnBackInvokedCallback() { // from class: android.widget.AutoCompleteTextView$$ExternalSyntheticLambda0
            @Override // android.window.OnBackInvokedCallback
            public final void onBackInvoked() {
                AutoCompleteTextView.this.lambda$new$0();
            }
        };
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.AutoCompleteTextView, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C4057R.styleable.AutoCompleteTextView, attrs, a, defStyleAttr, defStyleRes);
        if (popupTheme == null) {
            int popupThemeResId = a.getResourceId(8, 0);
            if (popupThemeResId != 0) {
                this.mPopupContext = new ContextThemeWrapper(context, popupThemeResId);
            } else {
                this.mPopupContext = context;
            }
        } else {
            this.mPopupContext = new ContextThemeWrapper(context, popupTheme);
        }
        Context context2 = this.mPopupContext;
        if (context2 != context) {
            TypedArray pa2 = context2.obtainStyledAttributes(attrs, C4057R.styleable.AutoCompleteTextView, defStyleAttr, defStyleRes);
            saveAttributeDataForStyleable(context, C4057R.styleable.AutoCompleteTextView, attrs, a, defStyleAttr, defStyleRes);
            pa = pa2;
        } else {
            pa = a;
        }
        Drawable popupListSelector = pa.getDrawable(3);
        int popupWidth = pa.getLayoutDimension(5, -2);
        int popupHeight = pa.getLayoutDimension(7, -2);
        int popupHintLayoutResId = pa.getResourceId(1, C4057R.layout.simple_dropdown_hint);
        CharSequence popupHintText = pa.getText(0);
        if (pa != a) {
            pa.recycle();
        }
        ListPopupWindow listPopupWindow = new ListPopupWindow(this.mPopupContext, attrs, defStyleAttr, defStyleRes);
        this.mPopup = listPopupWindow;
        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: android.widget.AutoCompleteTextView$$ExternalSyntheticLambda1
            @Override // android.widget.PopupWindow.OnDismissListener
            public final void onDismiss() {
                AutoCompleteTextView.this.lambda$new$1();
            }
        });
        listPopupWindow.setSoftInputMode(16);
        listPopupWindow.setPromptPosition(1);
        listPopupWindow.setListSelector(popupListSelector);
        listPopupWindow.setOnItemClickListener(new DropDownItemClickListener());
        listPopupWindow.setWidth(popupWidth);
        listPopupWindow.setHeight(popupHeight);
        this.mHintResource = popupHintLayoutResId;
        setCompletionHint(popupHintText);
        this.mDropDownAnchorId = a.getResourceId(6, -1);
        this.mThreshold = a.getInt(2, 2);
        a.recycle();
        int inputType = getInputType();
        if ((inputType & 15) == 1) {
            setRawInputType(inputType | 65536);
        }
        setFocusable(true);
        MyWatcher myWatcher = new MyWatcher();
        this.mAutoCompleteTextWatcher = myWatcher;
        addTextChangedListener(myWatcher);
        PassThroughClickListener passThroughClickListener = new PassThroughClickListener();
        this.mPassThroughClickListener = passThroughClickListener;
        super.setOnClickListener(passThroughClickListener);
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener listener) {
        this.mPassThroughClickListener.mWrapped = listener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClickImpl() {
        if (isPopupShowing()) {
            ensureImeVisible(true);
        }
    }

    public void setCompletionHint(CharSequence hint) {
        this.mHintText = hint;
        if (hint == null) {
            this.mPopup.setPromptView(null);
            this.mHintView = null;
            return;
        }
        TextView textView = this.mHintView;
        if (textView == null) {
            TextView hintView = (TextView) LayoutInflater.from(this.mPopupContext).inflate(this.mHintResource, (ViewGroup) null).findViewById(16908308);
            hintView.setText(this.mHintText);
            this.mHintView = hintView;
            this.mPopup.setPromptView(hintView);
            return;
        }
        textView.setText(hint);
    }

    public CharSequence getCompletionHint() {
        return this.mHintText;
    }

    public int getDropDownWidth() {
        return this.mPopup.getWidth();
    }

    public void setDropDownWidth(int width) {
        this.mPopup.setWidth(width);
    }

    public int getDropDownHeight() {
        return this.mPopup.getHeight();
    }

    public void setDropDownHeight(int height) {
        this.mPopup.setHeight(height);
    }

    public int getDropDownAnchor() {
        return this.mDropDownAnchorId;
    }

    public void setDropDownAnchor(int id) {
        this.mDropDownAnchorId = id;
        this.mPopup.setAnchorView(null);
    }

    public Drawable getDropDownBackground() {
        return this.mPopup.getBackground();
    }

    public void setDropDownBackgroundDrawable(Drawable d) {
        this.mPopup.setBackgroundDrawable(d);
    }

    public void setDropDownBackgroundResource(int id) {
        this.mPopup.setBackgroundDrawable(getContext().getDrawable(id));
    }

    public void setDropDownVerticalOffset(int offset) {
        this.mPopup.setVerticalOffset(offset);
    }

    public int getDropDownVerticalOffset() {
        return this.mPopup.getVerticalOffset();
    }

    public void setDropDownHorizontalOffset(int offset) {
        this.mPopup.setHorizontalOffset(offset);
    }

    public int getDropDownHorizontalOffset() {
        return this.mPopup.getHorizontalOffset();
    }

    public void setDropDownAnimationStyle(int animationStyle) {
        this.mPopup.setAnimationStyle(animationStyle);
    }

    public int getDropDownAnimationStyle() {
        return this.mPopup.getAnimationStyle();
    }

    public boolean isDropDownAlwaysVisible() {
        return this.mPopup.isDropDownAlwaysVisible();
    }

    public void setDropDownAlwaysVisible(boolean dropDownAlwaysVisible) {
        this.mPopup.setDropDownAlwaysVisible(dropDownAlwaysVisible);
    }

    public boolean isDropDownDismissedOnCompletion() {
        return this.mDropDownDismissedOnCompletion;
    }

    public void setDropDownDismissedOnCompletion(boolean dropDownDismissedOnCompletion) {
        this.mDropDownDismissedOnCompletion = dropDownDismissedOnCompletion;
    }

    public int getThreshold() {
        return this.mThreshold;
    }

    public void setThreshold(int threshold) {
        if (threshold <= 0) {
            threshold = 1;
        }
        this.mThreshold = threshold;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener l) {
        this.mItemClickListener = l;
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener l) {
        this.mItemSelectedListener = l;
    }

    @Deprecated
    public AdapterView.OnItemClickListener getItemClickListener() {
        return this.mItemClickListener;
    }

    @Deprecated
    public AdapterView.OnItemSelectedListener getItemSelectedListener() {
        return this.mItemSelectedListener;
    }

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return this.mItemClickListener;
    }

    public AdapterView.OnItemSelectedListener getOnItemSelectedListener() {
        return this.mItemSelectedListener;
    }

    public void setOnDismissListener(final OnDismissListener dismissListener) {
        PopupWindow.OnDismissListener wrappedListener = null;
        if (dismissListener != null) {
            wrappedListener = new PopupWindow.OnDismissListener() { // from class: android.widget.AutoCompleteTextView.1
                @Override // android.widget.PopupWindow.OnDismissListener
                public void onDismiss() {
                    dismissListener.onDismiss();
                    AutoCompleteTextView.this.lambda$new$1();
                }
            };
        }
        this.mPopup.setOnDismissListener(wrappedListener);
    }

    public ListAdapter getAdapter() {
        return this.mAdapter;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
        PopupDataSetObserver popupDataSetObserver = this.mObserver;
        if (popupDataSetObserver == null) {
            this.mObserver = new PopupDataSetObserver();
        } else {
            ListAdapter listAdapter = this.mAdapter;
            if (listAdapter != null) {
                listAdapter.unregisterDataSetObserver(popupDataSetObserver);
            }
        }
        this.mAdapter = adapter;
        if (adapter != 0) {
            this.mFilter = adapter.getFilter();
            adapter.registerDataSetObserver(this.mObserver);
        } else {
            this.mFilter = null;
        }
        this.mPopup.setAdapter(this.mAdapter);
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if ((keyCode == 4 || keyCode == 111) && isPopupShowing() && !this.mPopup.isDropDownAlwaysVisible()) {
            if (event.getAction() == 0 && event.getRepeatCount() == 0) {
                KeyEvent.DispatcherState state = getKeyDispatcherState();
                if (state != null) {
                    state.startTracking(event, this);
                }
                return true;
            } else if (event.getAction() == 1) {
                KeyEvent.DispatcherState state2 = getKeyDispatcherState();
                if (state2 != null) {
                    state2.handleUpEvent(event);
                }
                if (event.isTracking() && !event.isCanceled()) {
                    dismissDropDown();
                    return true;
                }
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }

    @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean consumed = this.mPopup.onKeyUp(keyCode, event);
        if (consumed) {
            switch (keyCode) {
                case 23:
                case 61:
                case 66:
                case 160:
                    if (event.hasNoModifiers()) {
                        performCompletion();
                    }
                    return true;
            }
        }
        if (isPopupShowing() && keyCode == 61 && event.hasNoModifiers()) {
            performCompletion();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.mPopup.onKeyDown(keyCode, event)) {
            return true;
        }
        if (!isPopupShowing()) {
            switch (keyCode) {
                case 20:
                    if (event.hasNoModifiers()) {
                        performValidation();
                        break;
                    }
                    break;
            }
        }
        if (isPopupShowing() && keyCode == 61 && event.hasNoModifiers()) {
            return true;
        }
        this.mLastKeyCode = keyCode;
        boolean handled = super.onKeyDown(keyCode, event);
        this.mLastKeyCode = 0;
        if (handled && isPopupShowing()) {
            clearListSelection();
        }
        return handled;
    }

    public boolean enoughToFilter() {
        return getText().length() >= this.mThreshold;
    }

    /* loaded from: classes4.dex */
    private class MyWatcher implements TextWatcher {
        private boolean mOpenBefore;

        private MyWatcher() {
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (AutoCompleteTextView.this.mBlockCompletion) {
                return;
            }
            this.mOpenBefore = AutoCompleteTextView.this.isPopupShowing();
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable s) {
            if (AutoCompleteTextView.this.mBlockCompletion) {
                return;
            }
            if (!this.mOpenBefore || AutoCompleteTextView.this.isPopupShowing()) {
                AutoCompleteTextView.this.refreshAutoCompleteResults();
            }
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doBeforeTextChanged() {
        this.mAutoCompleteTextWatcher.beforeTextChanged(null, 0, 0, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doAfterTextChanged() {
        this.mAutoCompleteTextWatcher.afterTextChanged(null);
    }

    public final void refreshAutoCompleteResults() {
        if (enoughToFilter()) {
            if (this.mFilter != null) {
                this.mPopupCanBeUpdated = true;
                performFiltering(getText(), this.mLastKeyCode);
                return;
            }
            return;
        }
        if (!this.mPopup.isDropDownAlwaysVisible()) {
            dismissDropDown();
        }
        Filter filter = this.mFilter;
        if (filter != null) {
            filter.filter(null);
        }
    }

    public boolean isPopupShowing() {
        return this.mPopup.isShowing();
    }

    protected CharSequence convertSelectionToString(Object selectedItem) {
        return this.mFilter.convertResultToString(selectedItem);
    }

    public void clearListSelection() {
        this.mPopup.clearListSelection();
    }

    public void setListSelection(int position) {
        this.mPopup.setSelection(position);
    }

    public int getListSelection() {
        return this.mPopup.getSelectedItemPosition();
    }

    protected void performFiltering(CharSequence text, int keyCode) {
        this.mFilter.filter(text, this);
    }

    public void performCompletion() {
        performCompletion(null, -1, -1L);
    }

    @Override // android.widget.TextView
    public void onCommitCompletion(CompletionInfo completion) {
        if (isPopupShowing()) {
            this.mPopup.performItemClick(completion.getPosition());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performCompletion(View selectedView, int position, long id) {
        Object selectedItem;
        if (isPopupShowing()) {
            if (position < 0) {
                selectedItem = this.mPopup.getSelectedItem();
            } else {
                selectedItem = this.mAdapter.getItem(position);
            }
            if (selectedItem == null) {
                Log.m104w(TAG, "performCompletion: no selected item");
                return;
            }
            this.mBlockCompletion = true;
            replaceText(convertSelectionToString(selectedItem));
            this.mBlockCompletion = false;
            if (this.mItemClickListener != null) {
                ListPopupWindow list = this.mPopup;
                if (selectedView == null || position < 0) {
                    selectedView = list.getSelectedView();
                    position = list.getSelectedItemPosition();
                    id = list.getSelectedItemId();
                }
                this.mItemClickListener.onItemClick(list.getListView(), selectedView, position, id);
            }
        }
        if (this.mDropDownDismissedOnCompletion && !this.mPopup.isDropDownAlwaysVisible()) {
            dismissDropDown();
        }
    }

    public boolean isPerformingCompletion() {
        return this.mBlockCompletion;
    }

    public void setText(CharSequence text, boolean filter) {
        if (filter) {
            setText(text);
            return;
        }
        this.mBlockCompletion = true;
        setText(text);
        this.mBlockCompletion = false;
    }

    protected void replaceText(CharSequence text) {
        clearComposingText();
        setText(text);
        Editable spannable = getText();
        Selection.setSelection(spannable, spannable.length());
    }

    @Override // android.widget.Filter.FilterListener
    public void onFilterComplete(int count) {
        updateDropDownForFilter(count);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDropDownForFilter(int count) {
        if (getWindowVisibility() == 8) {
            return;
        }
        boolean dropDownAlwaysVisible = this.mPopup.isDropDownAlwaysVisible();
        boolean enoughToFilter = enoughToFilter();
        if ((count > 0 || dropDownAlwaysVisible) && enoughToFilter) {
            if (hasFocus() && hasWindowFocus() && this.mPopupCanBeUpdated) {
                showDropDown();
            }
        } else if (!dropDownAlwaysVisible && isPopupShowing()) {
            dismissDropDown();
            this.mPopupCanBeUpdated = true;
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus && !this.mPopup.isDropDownAlwaysVisible()) {
            dismissDropDown();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDisplayHint(int hint) {
        super.onDisplayHint(hint);
        switch (hint) {
            case 4:
                if (!this.mPopup.isDropDownAlwaysVisible()) {
                    dismissDropDown();
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (isTemporarilyDetached()) {
            return;
        }
        if (!focused) {
            performValidation();
        }
        if (!focused && !this.mPopup.isDropDownAlwaysVisible()) {
            dismissDropDown();
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindow() {
        dismissDropDown();
        super.onDetachedFromWindow();
    }

    public void dismissDropDown() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(InputMethodManager.class);
        if (imm != null) {
            imm.displayCompletions(this, null);
        }
        this.mPopup.dismiss();
        this.mPopupCanBeUpdated = false;
    }

    @Override // android.widget.TextView, android.view.View
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean result = super.setFrame(l, t, r, b);
        if (isPopupShowing()) {
            showDropDown();
        }
        return result;
    }

    public void showDropDownAfterLayout() {
        this.mPopup.postShow();
    }

    public void ensureImeVisible(boolean visible) {
        this.mPopup.setInputMethodMode(visible ? 1 : 2);
        if (this.mPopup.isDropDownAlwaysVisible() || (this.mFilter != null && enoughToFilter())) {
            showDropDown();
        }
    }

    public boolean isInputMethodNotNeeded() {
        return this.mPopup.getInputMethodMode() == 2;
    }

    public int getInputMethodMode() {
        return this.mPopup.getInputMethodMode();
    }

    public void setInputMethodMode(int mode) {
        this.mPopup.setInputMethodMode(mode);
    }

    public void showDropDown() {
        buildImeCompletions();
        if (this.mPopup.getAnchorView() == null) {
            if (this.mDropDownAnchorId != -1) {
                this.mPopup.setAnchorView(getRootView().findViewById(this.mDropDownAnchorId));
            } else {
                this.mPopup.setAnchorView(this);
            }
        }
        if (!isPopupShowing()) {
            this.mPopup.setInputMethodMode(1);
            this.mPopup.setListItemExpandMax(3);
        }
        this.mPopup.show();
        if (!this.mPopup.isDropDownAlwaysVisible()) {
            registerOnBackInvokedCallback();
        }
        this.mPopup.getListView().setOverScrollMode(0);
    }

    public void setForceIgnoreOutsideTouch(boolean forceIgnoreOutsideTouch) {
        this.mPopup.setForceIgnoreOutsideTouch(forceIgnoreOutsideTouch);
    }

    private void buildImeCompletions() {
        InputMethodManager imm;
        ListAdapter adapter = this.mAdapter;
        if (adapter != null && (imm = (InputMethodManager) getContext().getSystemService(InputMethodManager.class)) != null) {
            int count = Math.min(adapter.getCount(), 20);
            CompletionInfo[] completions = new CompletionInfo[count];
            int realCount = 0;
            for (int i = 0; i < count; i++) {
                if (adapter.isEnabled(i)) {
                    Object item = adapter.getItem(i);
                    long id = adapter.getItemId(i);
                    completions[realCount] = new CompletionInfo(id, realCount, convertSelectionToString(item));
                    realCount++;
                }
            }
            if (realCount != count) {
                CompletionInfo[] tmp = new CompletionInfo[realCount];
                System.arraycopy(completions, 0, tmp, 0, realCount);
                completions = tmp;
            }
            imm.displayCompletions(this, completions);
        }
    }

    public void setValidator(Validator validator) {
        this.mValidator = validator;
    }

    public Validator getValidator() {
        return this.mValidator;
    }

    public void performValidation() {
        if (this.mValidator == null) {
            return;
        }
        CharSequence text = getText();
        if (!TextUtils.isEmpty(text) && !this.mValidator.isValid(text)) {
            setText(this.mValidator.fixText(text));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Filter getFilter() {
        return this.mFilter;
    }

    @Override // android.widget.EditText, android.widget.TextView, android.view.View
    public CharSequence getAccessibilityClassName() {
        return AutoCompleteTextView.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: unregisterOnBackInvokedCallback */
    public void lambda$new$1() {
        OnBackInvokedDispatcher dispatcher;
        if (!this.mBackCallbackRegistered || (dispatcher = findOnBackInvokedDispatcher()) == null) {
            return;
        }
        if (WindowOnBackInvokedDispatcher.isOnBackInvokedCallbackEnabled(this.mPopupContext)) {
            dispatcher.unregisterOnBackInvokedCallback(this.mBackCallback);
        }
        this.mBackCallbackRegistered = false;
    }

    private void registerOnBackInvokedCallback() {
        OnBackInvokedDispatcher dispatcher;
        if (this.mBackCallbackRegistered || (dispatcher = findOnBackInvokedDispatcher()) == null) {
            return;
        }
        if (WindowOnBackInvokedDispatcher.isOnBackInvokedCallbackEnabled(this.mPopupContext)) {
            dispatcher.registerOnBackInvokedCallback(1000000, this.mBackCallback);
        }
        this.mBackCallbackRegistered = true;
    }

    /* loaded from: classes4.dex */
    private class DropDownItemClickListener implements AdapterView.OnItemClickListener {
        private DropDownItemClickListener() {
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            AutoCompleteTextView.this.performCompletion(v, position, id);
        }
    }

    /* loaded from: classes4.dex */
    private class PassThroughClickListener implements View.OnClickListener {
        private View.OnClickListener mWrapped;

        private PassThroughClickListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            AutoCompleteTextView.this.onClickImpl();
            View.OnClickListener onClickListener = this.mWrapped;
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    /* loaded from: classes4.dex */
    private static class PopupDataSetObserver extends DataSetObserver {
        private final WeakReference<AutoCompleteTextView> mViewReference;
        private final Runnable updateRunnable;

        private PopupDataSetObserver(AutoCompleteTextView view) {
            this.updateRunnable = new Runnable() { // from class: android.widget.AutoCompleteTextView.PopupDataSetObserver.1
                @Override // java.lang.Runnable
                public void run() {
                    ListAdapter adapter;
                    AutoCompleteTextView textView = (AutoCompleteTextView) PopupDataSetObserver.this.mViewReference.get();
                    if (textView == null || (adapter = textView.mAdapter) == null) {
                        return;
                    }
                    textView.updateDropDownForFilter(adapter.getCount());
                }
            };
            this.mViewReference = new WeakReference<>(view);
        }

        @Override // android.database.DataSetObserver
        public void onChanged() {
            AutoCompleteTextView textView = this.mViewReference.get();
            if (textView != null && textView.mAdapter != null) {
                textView.post(this.updateRunnable);
            }
        }
    }
}
