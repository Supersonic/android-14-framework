package com.android.internal.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.p008os.Handler;
import android.p008os.Message;
import android.text.TextUtils;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.android.internal.C4057R;
import java.lang.ref.WeakReference;
/* loaded from: classes4.dex */
public class AlertController {
    public static final int MICRO = 1;
    private ListAdapter mAdapter;
    private int mAlertDialogLayout;
    private Button mButtonNegative;
    private Message mButtonNegativeMessage;
    private CharSequence mButtonNegativeText;
    private Button mButtonNeutral;
    private Message mButtonNeutralMessage;
    private CharSequence mButtonNeutralText;
    private int mButtonPanelSideLayout;
    private Button mButtonPositive;
    private Message mButtonPositiveMessage;
    private CharSequence mButtonPositiveText;
    private final Context mContext;
    private View mCustomTitleView;
    private final DialogInterface mDialogInterface;
    private boolean mForceInverseBackground;
    private Handler mHandler;
    private Drawable mIcon;
    private ImageView mIconView;
    private int mListItemLayout;
    private int mListLayout;
    protected ListView mListView;
    protected CharSequence mMessage;
    private Integer mMessageHyphenationFrequency;
    private MovementMethod mMessageMovementMethod;
    protected TextView mMessageView;
    private int mMultiChoiceItemLayout;
    protected ScrollView mScrollView;
    private boolean mShowTitle;
    private int mSingleChoiceItemLayout;
    private CharSequence mTitle;
    private TextView mTitleView;
    private View mView;
    private int mViewLayoutResId;
    private int mViewSpacingBottom;
    private int mViewSpacingLeft;
    private int mViewSpacingRight;
    private int mViewSpacingTop;
    protected final Window mWindow;
    private boolean mViewSpacingSpecified = false;
    private int mIconId = 0;
    private int mCheckedItem = -1;
    private int mButtonPanelLayoutHint = 0;
    private final View.OnClickListener mButtonHandler = new View.OnClickListener() { // from class: com.android.internal.app.AlertController.1
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            Message m;
            if (v == AlertController.this.mButtonPositive && AlertController.this.mButtonPositiveMessage != null) {
                m = Message.obtain(AlertController.this.mButtonPositiveMessage);
            } else if (v == AlertController.this.mButtonNegative && AlertController.this.mButtonNegativeMessage != null) {
                m = Message.obtain(AlertController.this.mButtonNegativeMessage);
            } else if (v == AlertController.this.mButtonNeutral && AlertController.this.mButtonNeutralMessage != null) {
                m = Message.obtain(AlertController.this.mButtonNeutralMessage);
            } else {
                m = null;
            }
            if (m != null) {
                m.sendToTarget();
            }
            AlertController.this.mHandler.obtainMessage(1, AlertController.this.mDialogInterface).sendToTarget();
        }
    };

    /* loaded from: classes4.dex */
    private static final class ButtonHandler extends Handler {
        private static final int MSG_DISMISS_DIALOG = 1;
        private WeakReference<DialogInterface> mDialog;

        public ButtonHandler(DialogInterface dialog) {
            this.mDialog = new WeakReference<>(dialog);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -3:
                case -2:
                case -1:
                    ((DialogInterface.OnClickListener) msg.obj).onClick(this.mDialog.get(), msg.what);
                    return;
                case 0:
                default:
                    return;
                case 1:
                    ((DialogInterface) msg.obj).dismiss();
                    return;
            }
        }
    }

    private static boolean shouldCenterSingleButton(Context context) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(C4057R.attr.alertDialogCenterButtons, outValue, true);
        return outValue.data != 0;
    }

    public static final AlertController create(Context context, DialogInterface di, Window window) {
        TypedArray a = context.obtainStyledAttributes(null, C4057R.styleable.AlertDialog, 16842845, 16974371);
        int controllerType = a.getInt(12, 0);
        a.recycle();
        switch (controllerType) {
            case 1:
                return new MicroAlertController(context, di, window);
            default:
                return new AlertController(context, di, window);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AlertController(Context context, DialogInterface di, Window window) {
        this.mContext = context;
        this.mDialogInterface = di;
        this.mWindow = window;
        this.mHandler = new ButtonHandler(di);
        TypedArray a = context.obtainStyledAttributes(null, C4057R.styleable.AlertDialog, 16842845, 0);
        this.mAlertDialogLayout = a.getResourceId(10, C4057R.layout.alert_dialog);
        this.mButtonPanelSideLayout = a.getResourceId(11, 0);
        this.mListLayout = a.getResourceId(15, C4057R.layout.select_dialog);
        this.mMultiChoiceItemLayout = a.getResourceId(16, 17367059);
        this.mSingleChoiceItemLayout = a.getResourceId(21, 17367058);
        this.mListItemLayout = a.getResourceId(14, 17367057);
        this.mShowTitle = a.getBoolean(20, true);
        a.recycle();
        window.requestFeature(1);
    }

    static boolean canTextInput(View v) {
        if (v.onCheckIsTextEditor()) {
            return true;
        }
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            int i = vg.getChildCount();
            while (i > 0) {
                i--;
                if (canTextInput(vg.getChildAt(i))) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public void installContent(AlertParams params) {
        params.apply(this);
        installContent();
    }

    public void installContent() {
        int contentView = selectContentView();
        this.mWindow.setContentView(contentView);
        setupView();
    }

    private int selectContentView() {
        int i = this.mButtonPanelSideLayout;
        if (i == 0) {
            return this.mAlertDialogLayout;
        }
        if (this.mButtonPanelLayoutHint == 1) {
            return i;
        }
        return this.mAlertDialogLayout;
    }

    public void setTitle(CharSequence title) {
        this.mTitle = title;
        TextView textView = this.mTitleView;
        if (textView != null) {
            textView.setText(title);
        }
        this.mWindow.setTitle(title);
    }

    public void setCustomTitle(View customTitleView) {
        this.mCustomTitleView = customTitleView;
    }

    public void setMessage(CharSequence message) {
        this.mMessage = message;
        TextView textView = this.mMessageView;
        if (textView != null) {
            textView.setText(message);
        }
    }

    public void setMessageMovementMethod(MovementMethod movementMethod) {
        this.mMessageMovementMethod = movementMethod;
        TextView textView = this.mMessageView;
        if (textView != null) {
            textView.setMovementMethod(movementMethod);
        }
    }

    public void setMessageHyphenationFrequency(int hyphenationFrequency) {
        this.mMessageHyphenationFrequency = Integer.valueOf(hyphenationFrequency);
        TextView textView = this.mMessageView;
        if (textView != null) {
            textView.setHyphenationFrequency(hyphenationFrequency);
        }
    }

    public void setView(int layoutResId) {
        this.mView = null;
        this.mViewLayoutResId = layoutResId;
        this.mViewSpacingSpecified = false;
    }

    public void setView(View view) {
        this.mView = view;
        this.mViewLayoutResId = 0;
        this.mViewSpacingSpecified = false;
    }

    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
        this.mView = view;
        this.mViewLayoutResId = 0;
        this.mViewSpacingSpecified = true;
        this.mViewSpacingLeft = viewSpacingLeft;
        this.mViewSpacingTop = viewSpacingTop;
        this.mViewSpacingRight = viewSpacingRight;
        this.mViewSpacingBottom = viewSpacingBottom;
    }

    public void setButtonPanelLayoutHint(int layoutHint) {
        this.mButtonPanelLayoutHint = layoutHint;
    }

    public void setButton(int whichButton, CharSequence text, DialogInterface.OnClickListener listener, Message msg) {
        if (msg == null && listener != null) {
            msg = this.mHandler.obtainMessage(whichButton, listener);
        }
        switch (whichButton) {
            case -3:
                this.mButtonNeutralText = text;
                this.mButtonNeutralMessage = msg;
                return;
            case -2:
                this.mButtonNegativeText = text;
                this.mButtonNegativeMessage = msg;
                return;
            case -1:
                this.mButtonPositiveText = text;
                this.mButtonPositiveMessage = msg;
                return;
            default:
                throw new IllegalArgumentException("Button does not exist");
        }
    }

    public void setIcon(int resId) {
        this.mIcon = null;
        this.mIconId = resId;
        ImageView imageView = this.mIconView;
        if (imageView != null) {
            if (resId != 0) {
                imageView.setVisibility(0);
                this.mIconView.setImageResource(this.mIconId);
                return;
            }
            imageView.setVisibility(8);
        }
    }

    public void setIcon(Drawable icon) {
        this.mIcon = icon;
        this.mIconId = 0;
        ImageView imageView = this.mIconView;
        if (imageView != null) {
            if (icon != null) {
                imageView.setVisibility(0);
                this.mIconView.setImageDrawable(icon);
                return;
            }
            imageView.setVisibility(8);
        }
    }

    public int getIconAttributeResId(int attrId) {
        TypedValue out = new TypedValue();
        this.mContext.getTheme().resolveAttribute(attrId, out, true);
        return out.resourceId;
    }

    public void setInverseBackgroundForced(boolean forceInverseBackground) {
        this.mForceInverseBackground = forceInverseBackground;
    }

    public ListView getListView() {
        return this.mListView;
    }

    public Button getButton(int whichButton) {
        switch (whichButton) {
            case -3:
                return this.mButtonNeutral;
            case -2:
                return this.mButtonNegative;
            case -1:
                return this.mButtonPositive;
            default:
                return null;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        ScrollView scrollView = this.mScrollView;
        return scrollView != null && scrollView.executeKeyEvent(event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        ScrollView scrollView = this.mScrollView;
        return scrollView != null && scrollView.executeKeyEvent(event);
    }

    private ViewGroup resolvePanel(View customPanel, View defaultPanel) {
        if (customPanel == null) {
            if (defaultPanel instanceof ViewStub) {
                defaultPanel = ((ViewStub) defaultPanel).inflate();
            }
            return (ViewGroup) defaultPanel;
        }
        if (defaultPanel != null) {
            ViewParent parent = defaultPanel.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(defaultPanel);
            }
        }
        if (customPanel instanceof ViewStub) {
            customPanel = ((ViewStub) customPanel).inflate();
        }
        return (ViewGroup) customPanel;
    }

    private void setupView() {
        boolean z;
        View spacer;
        boolean hasButtonPanel;
        View spacer2;
        View parentPanel = this.mWindow.findViewById(C4057R.C4059id.parentPanel);
        View defaultTopPanel = parentPanel.findViewById(C4057R.C4059id.topPanel);
        View defaultContentPanel = parentPanel.findViewById(C4057R.C4059id.contentPanel);
        View defaultButtonPanel = parentPanel.findViewById(C4057R.C4059id.buttonPanel);
        ViewGroup customPanel = (ViewGroup) parentPanel.findViewById(C4057R.C4059id.customPanel);
        setupCustomContent(customPanel);
        View customTopPanel = customPanel.findViewById(C4057R.C4059id.topPanel);
        View customContentPanel = customPanel.findViewById(C4057R.C4059id.contentPanel);
        View customButtonPanel = customPanel.findViewById(C4057R.C4059id.buttonPanel);
        ViewGroup topPanel = resolvePanel(customTopPanel, defaultTopPanel);
        ViewGroup contentPanel = resolvePanel(customContentPanel, defaultContentPanel);
        ViewGroup buttonPanel = resolvePanel(customButtonPanel, defaultButtonPanel);
        setupContent(contentPanel);
        setupButtons(buttonPanel);
        setupTitle(topPanel);
        boolean hasCustomPanel = (customPanel == null || customPanel.getVisibility() == 8) ? false : true;
        boolean hasTopPanel = (topPanel == null || topPanel.getVisibility() == 8) ? false : true;
        boolean hasButtonPanel2 = (buttonPanel == null || buttonPanel.getVisibility() == 8) ? false : true;
        if (!parentPanel.isInTouchMode()) {
            if (!requestFocusForContent(hasCustomPanel ? customPanel : contentPanel)) {
                requestFocusForDefaultButton();
            }
        }
        if (hasButtonPanel2) {
            z = true;
        } else {
            if (contentPanel != null && (spacer2 = contentPanel.findViewById(C4057R.C4059id.textSpacerNoButtons)) != null) {
                spacer2.setVisibility(0);
            }
            z = true;
            this.mWindow.setCloseOnTouchOutsideIfNotSet(true);
        }
        if (hasTopPanel) {
            ScrollView scrollView = this.mScrollView;
            if (scrollView != null) {
                scrollView.setClipToPadding(z);
            }
            View divider = null;
            if (this.mMessage != null || this.mListView != null || hasCustomPanel) {
                if (!hasCustomPanel) {
                    divider = topPanel.findViewById(C4057R.C4059id.titleDividerNoCustom);
                }
                if (divider == null) {
                    divider = topPanel.findViewById(C4057R.C4059id.titleDivider);
                }
            } else {
                divider = topPanel.findViewById(C4057R.C4059id.titleDividerTop);
            }
            if (divider != null) {
                divider.setVisibility(0);
            }
        } else if (contentPanel != null && (spacer = contentPanel.findViewById(C4057R.C4059id.textSpacerNoTitle)) != null) {
            spacer.setVisibility(0);
        }
        View spacer3 = this.mListView;
        if (spacer3 instanceof RecycleListView) {
            ((RecycleListView) spacer3).setHasDecor(hasTopPanel, hasButtonPanel2);
        }
        if (hasCustomPanel) {
            hasButtonPanel = hasButtonPanel2;
        } else {
            View content = this.mListView;
            if (content == null) {
                content = this.mScrollView;
            }
            if (content == null) {
                hasButtonPanel = hasButtonPanel2;
            } else {
                int indicators = (hasTopPanel ? 1 : 0) | (hasButtonPanel2 ? 2 : 0);
                hasButtonPanel = hasButtonPanel2;
                content.setScrollIndicators(indicators, 3);
            }
        }
        TypedArray a = this.mContext.obtainStyledAttributes(null, C4057R.styleable.AlertDialog, 16842845, 0);
        setBackground(a, topPanel, contentPanel, customPanel, buttonPanel, hasTopPanel, hasCustomPanel, hasButtonPanel);
        a.recycle();
    }

    private boolean requestFocusForContent(View content) {
        if (content != null && content.requestFocus()) {
            return true;
        }
        ListView listView = this.mListView;
        if (listView != null) {
            listView.setSelection(0);
            return true;
        }
        return false;
    }

    private void requestFocusForDefaultButton() {
        if (this.mButtonPositive.getVisibility() == 0) {
            this.mButtonPositive.requestFocus();
        } else if (this.mButtonNegative.getVisibility() == 0) {
            this.mButtonNegative.requestFocus();
        } else if (this.mButtonNeutral.getVisibility() == 0) {
            this.mButtonNeutral.requestFocus();
        }
    }

    private void setupCustomContent(ViewGroup customPanel) {
        View customView;
        if (this.mView != null) {
            customView = this.mView;
        } else if (this.mViewLayoutResId != 0) {
            LayoutInflater inflater = LayoutInflater.from(this.mContext);
            customView = inflater.inflate(this.mViewLayoutResId, customPanel, false);
        } else {
            customView = null;
        }
        boolean hasCustomView = customView != null;
        if (!hasCustomView || !canTextInput(customView)) {
            this.mWindow.setFlags(131072, 131072);
        }
        if (hasCustomView) {
            FrameLayout custom = (FrameLayout) this.mWindow.findViewById(16908331);
            custom.addView(customView, new ViewGroup.LayoutParams(-1, -1));
            if (this.mViewSpacingSpecified) {
                custom.setPadding(this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom);
            }
            if (this.mListView != null) {
                ((LinearLayout.LayoutParams) customPanel.getLayoutParams()).weight = 0.0f;
                return;
            }
            return;
        }
        customPanel.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setupTitle(ViewGroup topPanel) {
        if (this.mCustomTitleView != null && this.mShowTitle) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(-1, -2);
            topPanel.addView(this.mCustomTitleView, 0, lp);
            View titleTemplate = this.mWindow.findViewById(C4057R.C4059id.title_template);
            titleTemplate.setVisibility(8);
            return;
        }
        this.mIconView = (ImageView) this.mWindow.findViewById(16908294);
        boolean hasTextTitle = !TextUtils.isEmpty(this.mTitle);
        if (hasTextTitle && this.mShowTitle) {
            TextView textView = (TextView) this.mWindow.findViewById(C4057R.C4059id.alertTitle);
            this.mTitleView = textView;
            textView.setText(this.mTitle);
            int i = this.mIconId;
            if (i != 0) {
                this.mIconView.setImageResource(i);
                return;
            }
            Drawable drawable = this.mIcon;
            if (drawable != null) {
                this.mIconView.setImageDrawable(drawable);
                return;
            }
            this.mTitleView.setPadding(this.mIconView.getPaddingLeft(), this.mIconView.getPaddingTop(), this.mIconView.getPaddingRight(), this.mIconView.getPaddingBottom());
            this.mIconView.setVisibility(8);
            return;
        }
        View titleTemplate2 = this.mWindow.findViewById(C4057R.C4059id.title_template);
        titleTemplate2.setVisibility(8);
        this.mIconView.setVisibility(8);
        topPanel.setVisibility(8);
    }

    protected void setupContent(ViewGroup contentPanel) {
        ScrollView scrollView = (ScrollView) contentPanel.findViewById(C4057R.C4059id.scrollView);
        this.mScrollView = scrollView;
        scrollView.setFocusable(false);
        TextView textView = (TextView) contentPanel.findViewById(16908299);
        this.mMessageView = textView;
        if (textView == null) {
            return;
        }
        CharSequence charSequence = this.mMessage;
        if (charSequence != null) {
            textView.setText(charSequence);
            MovementMethod movementMethod = this.mMessageMovementMethod;
            if (movementMethod != null) {
                this.mMessageView.setMovementMethod(movementMethod);
            }
            Integer num = this.mMessageHyphenationFrequency;
            if (num != null) {
                this.mMessageView.setHyphenationFrequency(num.intValue());
                return;
            }
            return;
        }
        textView.setVisibility(8);
        this.mScrollView.removeView(this.mMessageView);
        if (this.mListView != null) {
            ViewGroup scrollParent = (ViewGroup) this.mScrollView.getParent();
            int childIndex = scrollParent.indexOfChild(this.mScrollView);
            scrollParent.removeViewAt(childIndex);
            scrollParent.addView(this.mListView, childIndex, new ViewGroup.LayoutParams(-1, -1));
            return;
        }
        contentPanel.setVisibility(8);
    }

    private static void manageScrollIndicators(View v, View upIndicator, View downIndicator) {
        if (upIndicator != null) {
            upIndicator.setVisibility(v.canScrollVertically(-1) ? 0 : 4);
        }
        if (downIndicator != null) {
            downIndicator.setVisibility(v.canScrollVertically(1) ? 0 : 4);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setupButtons(ViewGroup buttonPanel) {
        int whichButtons = 0;
        Button button = (Button) buttonPanel.findViewById(16908313);
        this.mButtonPositive = button;
        button.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonPositiveText)) {
            this.mButtonPositive.setVisibility(8);
        } else {
            this.mButtonPositive.setText(this.mButtonPositiveText);
            this.mButtonPositive.setVisibility(0);
            whichButtons = 0 | 1;
        }
        Button button2 = (Button) buttonPanel.findViewById(16908314);
        this.mButtonNegative = button2;
        button2.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonNegativeText)) {
            this.mButtonNegative.setVisibility(8);
        } else {
            this.mButtonNegative.setText(this.mButtonNegativeText);
            this.mButtonNegative.setVisibility(0);
            whichButtons |= 2;
        }
        Button button3 = (Button) buttonPanel.findViewById(16908315);
        this.mButtonNeutral = button3;
        button3.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonNeutralText)) {
            this.mButtonNeutral.setVisibility(8);
        } else {
            this.mButtonNeutral.setText(this.mButtonNeutralText);
            this.mButtonNeutral.setVisibility(0);
            whichButtons |= 4;
        }
        if (shouldCenterSingleButton(this.mContext)) {
            if (whichButtons == 1) {
                centerButton(this.mButtonPositive);
            } else if (whichButtons == 2) {
                centerButton(this.mButtonNegative);
            } else if (whichButtons == 4) {
                centerButton(this.mButtonNeutral);
            }
        }
        boolean hasButtons = whichButtons != 0;
        if (!hasButtons) {
            buttonPanel.setVisibility(8);
        }
    }

    private void centerButton(Button button) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button.getLayoutParams();
        params.gravity = 1;
        params.weight = 0.5f;
        button.setLayoutParams(params);
        View leftSpacer = this.mWindow.findViewById(C4057R.C4059id.leftSpacer);
        if (leftSpacer != null) {
            leftSpacer.setVisibility(0);
        }
        View rightSpacer = this.mWindow.findViewById(C4057R.C4059id.rightSpacer);
        if (rightSpacer != null) {
            rightSpacer.setVisibility(0);
        }
    }

    private void setBackground(TypedArray a, View topPanel, View contentPanel, View customPanel, View buttonPanel, boolean hasTitle, boolean hasCustomView, boolean hasButtons) {
        ListAdapter listAdapter;
        int topBright;
        int fullDark = 0;
        int topDark = 0;
        int centerDark = 0;
        int bottomDark = 0;
        int fullBright = 0;
        int topBright2 = 0;
        int centerBright = 0;
        int bottomBright = 0;
        int bottomMedium = 0;
        boolean needsDefaultBackgrounds = a.getBoolean(17, true);
        if (needsDefaultBackgrounds) {
            fullDark = C4057R.C4058drawable.popup_full_dark;
            topDark = C4057R.C4058drawable.popup_top_dark;
            centerDark = C4057R.C4058drawable.popup_center_dark;
            bottomDark = C4057R.C4058drawable.popup_bottom_dark;
            fullBright = C4057R.C4058drawable.popup_full_bright;
            topBright2 = C4057R.C4058drawable.popup_top_bright;
            centerBright = C4057R.C4058drawable.popup_center_bright;
            bottomBright = C4057R.C4058drawable.popup_bottom_bright;
            bottomMedium = C4057R.C4058drawable.popup_bottom_medium;
        }
        int topBright3 = a.getResourceId(5, topBright2);
        int topDark2 = a.getResourceId(1, topDark);
        int centerBright2 = a.getResourceId(6, centerBright);
        int centerDark2 = a.getResourceId(2, centerDark);
        View[] views = new View[4];
        boolean[] light = new boolean[4];
        boolean lastLight = false;
        int pos = 0;
        if (hasTitle) {
            views[0] = topPanel;
            light[0] = false;
            pos = 0 + 1;
        }
        views[pos] = contentPanel.getVisibility() == 8 ? null : contentPanel;
        light[pos] = this.mListView != null;
        int pos2 = pos + 1;
        if (hasCustomView) {
            views[pos2] = customPanel;
            light[pos2] = this.mForceInverseBackground;
            pos2++;
        }
        if (hasButtons) {
            views[pos2] = buttonPanel;
            light[pos2] = true;
        }
        boolean setView = false;
        View lastView = null;
        int topDark3 = 0;
        while (true) {
            int centerDark3 = centerDark2;
            int centerDark4 = views.length;
            if (topDark3 >= centerDark4) {
                break;
            }
            View v = views[topDark3];
            if (v == null) {
                topBright = topBright3;
            } else {
                if (lastView == null) {
                    topBright = topBright3;
                } else {
                    if (!setView) {
                        topBright = topBright3;
                        if (!lastLight) {
                            topBright3 = topDark2;
                        }
                        lastView.setBackgroundResource(topBright3);
                    } else {
                        topBright = topBright3;
                        lastView.setBackgroundResource(lastLight ? centerBright2 : centerDark3);
                    }
                    setView = true;
                }
                lastLight = light[topDark3];
                lastView = v;
            }
            topDark3++;
            centerDark2 = centerDark3;
            topBright3 = topBright;
        }
        if (lastView != null) {
            if (setView) {
                lastView.setBackgroundResource(lastLight ? hasButtons ? a.getResourceId(8, bottomMedium) : a.getResourceId(7, bottomBright) : a.getResourceId(3, bottomDark));
            } else {
                int fullBright2 = a.getResourceId(4, fullBright);
                fullDark = a.getResourceId(0, fullDark);
                lastView.setBackgroundResource(lastLight ? fullBright2 : fullDark);
            }
        }
        ListView listView = this.mListView;
        if (listView != null && (listAdapter = this.mAdapter) != null) {
            listView.setAdapter(listAdapter);
            int checkedItem = this.mCheckedItem;
            if (checkedItem > -1) {
                listView.setItemChecked(checkedItem, true);
                listView.setSelectionFromTop(checkedItem, a.getDimensionPixelSize(19, 0));
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class RecycleListView extends ListView {
        private final int mPaddingBottomNoButtons;
        private final int mPaddingTopNoTitle;
        boolean mRecycleOnMeasure;

        public RecycleListView(Context context) {
            this(context, null);
        }

        public RecycleListView(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.mRecycleOnMeasure = true;
            TypedArray ta = context.obtainStyledAttributes(attrs, C4057R.styleable.RecycleListView);
            this.mPaddingBottomNoButtons = ta.getDimensionPixelOffset(0, -1);
            this.mPaddingTopNoTitle = ta.getDimensionPixelOffset(1, -1);
        }

        public void setHasDecor(boolean hasTitle, boolean hasButtons) {
            if (!hasButtons || !hasTitle) {
                int paddingLeft = getPaddingLeft();
                int paddingTop = hasTitle ? getPaddingTop() : this.mPaddingTopNoTitle;
                int paddingRight = getPaddingRight();
                int paddingBottom = hasButtons ? getPaddingBottom() : this.mPaddingBottomNoButtons;
                setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            }
        }

        @Override // android.widget.ListView
        protected boolean recycleOnMeasure() {
            return this.mRecycleOnMeasure;
        }
    }

    /* loaded from: classes4.dex */
    public static class AlertParams {
        public ListAdapter mAdapter;
        public boolean[] mCheckedItems;
        public final Context mContext;
        public Cursor mCursor;
        public View mCustomTitleView;
        public boolean mForceInverseBackground;
        public Drawable mIcon;
        public final LayoutInflater mInflater;
        public String mIsCheckedColumn;
        public boolean mIsMultiChoice;
        public boolean mIsSingleChoice;
        public CharSequence[] mItems;
        public String mLabelColumn;
        public CharSequence mMessage;
        public DialogInterface.OnClickListener mNegativeButtonListener;
        public CharSequence mNegativeButtonText;
        public DialogInterface.OnClickListener mNeutralButtonListener;
        public CharSequence mNeutralButtonText;
        public DialogInterface.OnCancelListener mOnCancelListener;
        public DialogInterface.OnMultiChoiceClickListener mOnCheckboxClickListener;
        public DialogInterface.OnClickListener mOnClickListener;
        public DialogInterface.OnDismissListener mOnDismissListener;
        public AdapterView.OnItemSelectedListener mOnItemSelectedListener;
        public DialogInterface.OnKeyListener mOnKeyListener;
        public OnPrepareListViewListener mOnPrepareListViewListener;
        public DialogInterface.OnClickListener mPositiveButtonListener;
        public CharSequence mPositiveButtonText;
        public CharSequence mTitle;
        public View mView;
        public int mViewLayoutResId;
        public int mViewSpacingBottom;
        public int mViewSpacingLeft;
        public int mViewSpacingRight;
        public int mViewSpacingTop;
        public int mIconId = 0;
        public int mIconAttrId = 0;
        public boolean mViewSpacingSpecified = false;
        public int mCheckedItem = -1;
        public boolean mRecycleOnMeasure = true;
        public boolean mCancelable = true;

        /* loaded from: classes4.dex */
        public interface OnPrepareListViewListener {
            void onPrepareListView(ListView listView);
        }

        public AlertParams(Context context) {
            this.mContext = context;
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void apply(AlertController dialog) {
            View view = this.mCustomTitleView;
            if (view != null) {
                dialog.setCustomTitle(view);
            } else {
                CharSequence charSequence = this.mTitle;
                if (charSequence != null) {
                    dialog.setTitle(charSequence);
                }
                Drawable drawable = this.mIcon;
                if (drawable != null) {
                    dialog.setIcon(drawable);
                }
                int i = this.mIconId;
                if (i != 0) {
                    dialog.setIcon(i);
                }
                int i2 = this.mIconAttrId;
                if (i2 != 0) {
                    dialog.setIcon(dialog.getIconAttributeResId(i2));
                }
            }
            CharSequence charSequence2 = this.mMessage;
            if (charSequence2 != null) {
                dialog.setMessage(charSequence2);
            }
            CharSequence charSequence3 = this.mPositiveButtonText;
            if (charSequence3 != null) {
                dialog.setButton(-1, charSequence3, this.mPositiveButtonListener, null);
            }
            CharSequence charSequence4 = this.mNegativeButtonText;
            if (charSequence4 != null) {
                dialog.setButton(-2, charSequence4, this.mNegativeButtonListener, null);
            }
            CharSequence charSequence5 = this.mNeutralButtonText;
            if (charSequence5 != null) {
                dialog.setButton(-3, charSequence5, this.mNeutralButtonListener, null);
            }
            if (this.mForceInverseBackground) {
                dialog.setInverseBackgroundForced(true);
            }
            if (this.mItems != null || this.mCursor != null || this.mAdapter != null) {
                createListView(dialog);
            }
            View view2 = this.mView;
            if (view2 != null) {
                if (this.mViewSpacingSpecified) {
                    dialog.setView(view2, this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom);
                    return;
                } else {
                    dialog.setView(view2);
                    return;
                }
            }
            int i3 = this.mViewLayoutResId;
            if (i3 != 0) {
                dialog.setView(i3);
            }
        }

        private void createListView(final AlertController dialog) {
            int layout;
            ListAdapter adapter;
            final RecycleListView listView = (RecycleListView) this.mInflater.inflate(dialog.mListLayout, (ViewGroup) null);
            if (this.mIsMultiChoice) {
                Cursor cursor = this.mCursor;
                if (cursor == null) {
                    adapter = new ArrayAdapter<CharSequence>(this.mContext, dialog.mMultiChoiceItemLayout, 16908308, this.mItems) { // from class: com.android.internal.app.AlertController.AlertParams.1
                        @Override // android.widget.ArrayAdapter, android.widget.Adapter
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            if (AlertParams.this.mCheckedItems != null) {
                                boolean isItemChecked = AlertParams.this.mCheckedItems[position];
                                if (isItemChecked) {
                                    listView.setItemChecked(position, true);
                                }
                            }
                            return view;
                        }
                    };
                } else {
                    adapter = new CursorAdapter(this.mContext, cursor, false) { // from class: com.android.internal.app.AlertController.AlertParams.2
                        private final int mIsCheckedIndex;
                        private final int mLabelIndex;

                        {
                            Cursor cursor2 = getCursor();
                            this.mLabelIndex = cursor2.getColumnIndexOrThrow(AlertParams.this.mLabelColumn);
                            this.mIsCheckedIndex = cursor2.getColumnIndexOrThrow(AlertParams.this.mIsCheckedColumn);
                        }

                        @Override // android.widget.CursorAdapter
                        public void bindView(View view, Context context, Cursor cursor2) {
                            CheckedTextView text = (CheckedTextView) view.findViewById(16908308);
                            text.setText(cursor2.getString(this.mLabelIndex));
                            listView.setItemChecked(cursor2.getPosition(), cursor2.getInt(this.mIsCheckedIndex) == 1);
                        }

                        @Override // android.widget.CursorAdapter
                        public View newView(Context context, Cursor cursor2, ViewGroup parent) {
                            return AlertParams.this.mInflater.inflate(dialog.mMultiChoiceItemLayout, parent, false);
                        }
                    };
                }
            } else {
                if (this.mIsSingleChoice) {
                    layout = dialog.mSingleChoiceItemLayout;
                } else {
                    layout = dialog.mListItemLayout;
                }
                Cursor cursor2 = this.mCursor;
                if (cursor2 != null) {
                    adapter = new SimpleCursorAdapter(this.mContext, layout, cursor2, new String[]{this.mLabelColumn}, new int[]{16908308});
                } else if (this.mAdapter != null) {
                    adapter = this.mAdapter;
                } else {
                    adapter = new CheckedItemAdapter(this.mContext, layout, 16908308, this.mItems);
                }
            }
            OnPrepareListViewListener onPrepareListViewListener = this.mOnPrepareListViewListener;
            if (onPrepareListViewListener != null) {
                onPrepareListViewListener.onPrepareListView(listView);
            }
            dialog.mAdapter = adapter;
            dialog.mCheckedItem = this.mCheckedItem;
            if (this.mOnClickListener != null) {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.internal.app.AlertController.AlertParams.3
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        AlertParams.this.mOnClickListener.onClick(dialog.mDialogInterface, position);
                        if (!AlertParams.this.mIsSingleChoice) {
                            dialog.mDialogInterface.dismiss();
                        }
                    }
                });
            } else if (this.mOnCheckboxClickListener != null) {
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.internal.app.AlertController.AlertParams.4
                    @Override // android.widget.AdapterView.OnItemClickListener
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        if (AlertParams.this.mCheckedItems != null) {
                            AlertParams.this.mCheckedItems[position] = listView.isItemChecked(position);
                        }
                        AlertParams.this.mOnCheckboxClickListener.onClick(dialog.mDialogInterface, position, listView.isItemChecked(position));
                    }
                });
            }
            AdapterView.OnItemSelectedListener onItemSelectedListener = this.mOnItemSelectedListener;
            if (onItemSelectedListener != null) {
                listView.setOnItemSelectedListener(onItemSelectedListener);
            }
            if (this.mIsSingleChoice) {
                listView.setChoiceMode(1);
            } else if (this.mIsMultiChoice) {
                listView.setChoiceMode(2);
            }
            listView.mRecycleOnMeasure = this.mRecycleOnMeasure;
            dialog.mListView = listView;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class CheckedItemAdapter extends ArrayAdapter<CharSequence> {
        public CheckedItemAdapter(Context context, int resource, int textViewResourceId, CharSequence[] objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean hasStableIds() {
            return true;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public long getItemId(int position) {
            return position;
        }
    }
}
