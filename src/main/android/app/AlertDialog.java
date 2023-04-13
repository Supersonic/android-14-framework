package android.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ResourceId;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.p008os.Bundle;
import android.p008os.Message;
import android.text.method.MovementMethod;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.android.internal.C4057R;
import com.android.internal.app.AlertController;
/* loaded from: classes.dex */
public class AlertDialog extends Dialog implements DialogInterface {
    public static final int LAYOUT_HINT_NONE = 0;
    public static final int LAYOUT_HINT_SIDE = 1;
    @Deprecated
    public static final int THEME_DEVICE_DEFAULT_DARK = 4;
    @Deprecated
    public static final int THEME_DEVICE_DEFAULT_LIGHT = 5;
    @Deprecated
    public static final int THEME_HOLO_DARK = 2;
    @Deprecated
    public static final int THEME_HOLO_LIGHT = 3;
    @Deprecated
    public static final int THEME_TRADITIONAL = 1;
    private AlertController mAlert;

    /* JADX INFO: Access modifiers changed from: protected */
    public AlertDialog(Context context) {
        this(context, 0);
    }

    protected AlertDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        this(context, 0);
        setCancelable(cancelable);
        setOnCancelListener(cancelListener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AlertDialog(Context context, int themeResId) {
        this(context, themeResId, true);
    }

    AlertDialog(Context context, int themeResId, boolean createContextThemeWrapper) {
        super(context, createContextThemeWrapper ? resolveDialogTheme(context, themeResId) : 0, createContextThemeWrapper);
        this.mWindow.alwaysReadCloseOnTouchAttr();
        this.mAlert = AlertController.create(getContext(), this, getWindow());
    }

    static int resolveDialogTheme(Context context, int themeResId) {
        if (themeResId == 1) {
            return C4057R.C4062style.Theme_Dialog_Alert;
        }
        if (themeResId == 2) {
            return C4057R.C4062style.Theme_Holo_Dialog_Alert;
        }
        if (themeResId == 3) {
            return C4057R.C4062style.Theme_Holo_Light_Dialog_Alert;
        }
        if (themeResId == 4) {
            return 16974545;
        }
        if (themeResId == 5) {
            return 16974546;
        }
        if (ResourceId.isValid(themeResId)) {
            return themeResId;
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(16843529, outValue, true);
        return outValue.resourceId;
    }

    public Button getButton(int whichButton) {
        return this.mAlert.getButton(whichButton);
    }

    public ListView getListView() {
        return this.mAlert.getListView();
    }

    @Override // android.app.Dialog
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        this.mAlert.setTitle(title);
    }

    public void setCustomTitle(View customTitleView) {
        this.mAlert.setCustomTitle(customTitleView);
    }

    public void setMessage(CharSequence message) {
        this.mAlert.setMessage(message);
    }

    public void setMessageMovementMethod(MovementMethod movementMethod) {
        this.mAlert.setMessageMovementMethod(movementMethod);
    }

    public void setMessageHyphenationFrequency(int hyphenationFrequency) {
        this.mAlert.setMessageHyphenationFrequency(hyphenationFrequency);
    }

    public void setView(View view) {
        this.mAlert.setView(view);
    }

    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
        this.mAlert.setView(view, viewSpacingLeft, viewSpacingTop, viewSpacingRight, viewSpacingBottom);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setButtonPanelLayoutHint(int layoutHint) {
        this.mAlert.setButtonPanelLayoutHint(layoutHint);
    }

    public void setButton(int whichButton, CharSequence text, Message msg) {
        this.mAlert.setButton(whichButton, text, null, msg);
    }

    public void setButton(int whichButton, CharSequence text, DialogInterface.OnClickListener listener) {
        this.mAlert.setButton(whichButton, text, listener, null);
    }

    @Deprecated
    public void setButton(CharSequence text, Message msg) {
        setButton(-1, text, msg);
    }

    @Deprecated
    public void setButton2(CharSequence text, Message msg) {
        setButton(-2, text, msg);
    }

    @Deprecated
    public void setButton3(CharSequence text, Message msg) {
        setButton(-3, text, msg);
    }

    @Deprecated
    public void setButton(CharSequence text, DialogInterface.OnClickListener listener) {
        setButton(-1, text, listener);
    }

    @Deprecated
    public void setButton2(CharSequence text, DialogInterface.OnClickListener listener) {
        setButton(-2, text, listener);
    }

    @Deprecated
    public void setButton3(CharSequence text, DialogInterface.OnClickListener listener) {
        setButton(-3, text, listener);
    }

    public void setIcon(int resId) {
        this.mAlert.setIcon(resId);
    }

    public void setIcon(Drawable icon) {
        this.mAlert.setIcon(icon);
    }

    public void setIconAttribute(int attrId) {
        TypedValue out = new TypedValue();
        this.mContext.getTheme().resolveAttribute(attrId, out, true);
        this.mAlert.setIcon(out.resourceId);
    }

    public void setInverseBackgroundForced(boolean forceInverseBackground) {
        this.mAlert.setInverseBackgroundForced(forceInverseBackground);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Dialog
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAlert.installContent();
    }

    @Override // android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.mAlert.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (this.mAlert.onKeyUp(keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /* loaded from: classes.dex */
    public static class Builder {

        /* renamed from: P */
        private final AlertController.AlertParams f16P;

        public Builder(Context context) {
            this(context, AlertDialog.resolveDialogTheme(context, 0));
        }

        public Builder(Context context, int themeResId) {
            this.f16P = new AlertController.AlertParams(new ContextThemeWrapper(context, AlertDialog.resolveDialogTheme(context, themeResId)));
        }

        public Context getContext() {
            return this.f16P.mContext;
        }

        public Builder setTitle(int titleId) {
            AlertController.AlertParams alertParams = this.f16P;
            alertParams.mTitle = alertParams.mContext.getText(titleId);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.f16P.mTitle = title;
            return this;
        }

        public Builder setCustomTitle(View customTitleView) {
            this.f16P.mCustomTitleView = customTitleView;
            return this;
        }

        public Builder setMessage(int messageId) {
            AlertController.AlertParams alertParams = this.f16P;
            alertParams.mMessage = alertParams.mContext.getText(messageId);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            this.f16P.mMessage = message;
            return this;
        }

        public Builder setIcon(int iconId) {
            this.f16P.mIconId = iconId;
            return this;
        }

        public Builder setIcon(Drawable icon) {
            this.f16P.mIcon = icon;
            return this;
        }

        public Builder setIconAttribute(int attrId) {
            TypedValue out = new TypedValue();
            this.f16P.mContext.getTheme().resolveAttribute(attrId, out, true);
            this.f16P.mIconId = out.resourceId;
            return this;
        }

        public Builder setPositiveButton(int textId, DialogInterface.OnClickListener listener) {
            AlertController.AlertParams alertParams = this.f16P;
            alertParams.mPositiveButtonText = alertParams.mContext.getText(textId);
            this.f16P.mPositiveButtonListener = listener;
            return this;
        }

        public Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
            this.f16P.mPositiveButtonText = text;
            this.f16P.mPositiveButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(int textId, DialogInterface.OnClickListener listener) {
            AlertController.AlertParams alertParams = this.f16P;
            alertParams.mNegativeButtonText = alertParams.mContext.getText(textId);
            this.f16P.mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
            this.f16P.mNegativeButtonText = text;
            this.f16P.mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(int textId, DialogInterface.OnClickListener listener) {
            AlertController.AlertParams alertParams = this.f16P;
            alertParams.mNeutralButtonText = alertParams.mContext.getText(textId);
            this.f16P.mNeutralButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
            this.f16P.mNeutralButtonText = text;
            this.f16P.mNeutralButtonListener = listener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.f16P.mCancelable = cancelable;
            return this;
        }

        public Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
            this.f16P.mOnCancelListener = onCancelListener;
            return this;
        }

        public Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            this.f16P.mOnDismissListener = onDismissListener;
            return this;
        }

        public Builder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
            this.f16P.mOnKeyListener = onKeyListener;
            return this;
        }

        public Builder setItems(int itemsId, DialogInterface.OnClickListener listener) {
            AlertController.AlertParams alertParams = this.f16P;
            alertParams.mItems = alertParams.mContext.getResources().getTextArray(itemsId);
            this.f16P.mOnClickListener = listener;
            return this;
        }

        public Builder setItems(CharSequence[] items, DialogInterface.OnClickListener listener) {
            this.f16P.mItems = items;
            this.f16P.mOnClickListener = listener;
            return this;
        }

        public Builder setAdapter(ListAdapter adapter, DialogInterface.OnClickListener listener) {
            this.f16P.mAdapter = adapter;
            this.f16P.mOnClickListener = listener;
            return this;
        }

        public Builder setCursor(Cursor cursor, DialogInterface.OnClickListener listener, String labelColumn) {
            this.f16P.mCursor = cursor;
            this.f16P.mLabelColumn = labelColumn;
            this.f16P.mOnClickListener = listener;
            return this;
        }

        public Builder setMultiChoiceItems(int itemsId, boolean[] checkedItems, DialogInterface.OnMultiChoiceClickListener listener) {
            AlertController.AlertParams alertParams = this.f16P;
            alertParams.mItems = alertParams.mContext.getResources().getTextArray(itemsId);
            this.f16P.mOnCheckboxClickListener = listener;
            this.f16P.mCheckedItems = checkedItems;
            this.f16P.mIsMultiChoice = true;
            return this;
        }

        public Builder setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems, DialogInterface.OnMultiChoiceClickListener listener) {
            this.f16P.mItems = items;
            this.f16P.mOnCheckboxClickListener = listener;
            this.f16P.mCheckedItems = checkedItems;
            this.f16P.mIsMultiChoice = true;
            return this;
        }

        public Builder setMultiChoiceItems(Cursor cursor, String isCheckedColumn, String labelColumn, DialogInterface.OnMultiChoiceClickListener listener) {
            this.f16P.mCursor = cursor;
            this.f16P.mOnCheckboxClickListener = listener;
            this.f16P.mIsCheckedColumn = isCheckedColumn;
            this.f16P.mLabelColumn = labelColumn;
            this.f16P.mIsMultiChoice = true;
            return this;
        }

        public Builder setSingleChoiceItems(int itemsId, int checkedItem, DialogInterface.OnClickListener listener) {
            AlertController.AlertParams alertParams = this.f16P;
            alertParams.mItems = alertParams.mContext.getResources().getTextArray(itemsId);
            this.f16P.mOnClickListener = listener;
            this.f16P.mCheckedItem = checkedItem;
            this.f16P.mIsSingleChoice = true;
            return this;
        }

        public Builder setSingleChoiceItems(Cursor cursor, int checkedItem, String labelColumn, DialogInterface.OnClickListener listener) {
            this.f16P.mCursor = cursor;
            this.f16P.mOnClickListener = listener;
            this.f16P.mCheckedItem = checkedItem;
            this.f16P.mLabelColumn = labelColumn;
            this.f16P.mIsSingleChoice = true;
            return this;
        }

        public Builder setSingleChoiceItems(CharSequence[] items, int checkedItem, DialogInterface.OnClickListener listener) {
            this.f16P.mItems = items;
            this.f16P.mOnClickListener = listener;
            this.f16P.mCheckedItem = checkedItem;
            this.f16P.mIsSingleChoice = true;
            return this;
        }

        public Builder setSingleChoiceItems(ListAdapter adapter, int checkedItem, DialogInterface.OnClickListener listener) {
            this.f16P.mAdapter = adapter;
            this.f16P.mOnClickListener = listener;
            this.f16P.mCheckedItem = checkedItem;
            this.f16P.mIsSingleChoice = true;
            return this;
        }

        public Builder setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
            this.f16P.mOnItemSelectedListener = listener;
            return this;
        }

        public Builder setView(int layoutResId) {
            this.f16P.mView = null;
            this.f16P.mViewLayoutResId = layoutResId;
            this.f16P.mViewSpacingSpecified = false;
            return this;
        }

        public Builder setView(View view) {
            this.f16P.mView = view;
            this.f16P.mViewLayoutResId = 0;
            this.f16P.mViewSpacingSpecified = false;
            return this;
        }

        @Deprecated
        public Builder setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
            this.f16P.mView = view;
            this.f16P.mViewLayoutResId = 0;
            this.f16P.mViewSpacingSpecified = true;
            this.f16P.mViewSpacingLeft = viewSpacingLeft;
            this.f16P.mViewSpacingTop = viewSpacingTop;
            this.f16P.mViewSpacingRight = viewSpacingRight;
            this.f16P.mViewSpacingBottom = viewSpacingBottom;
            return this;
        }

        @Deprecated
        public Builder setInverseBackgroundForced(boolean useInverseBackground) {
            this.f16P.mForceInverseBackground = useInverseBackground;
            return this;
        }

        public Builder setRecycleOnMeasureEnabled(boolean enabled) {
            this.f16P.mRecycleOnMeasure = enabled;
            return this;
        }

        public AlertDialog create() {
            AlertDialog dialog = new AlertDialog(this.f16P.mContext, 0, false);
            this.f16P.apply(dialog.mAlert);
            dialog.setCancelable(this.f16P.mCancelable);
            if (this.f16P.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(this.f16P.mOnCancelListener);
            dialog.setOnDismissListener(this.f16P.mOnDismissListener);
            if (this.f16P.mOnKeyListener != null) {
                dialog.setOnKeyListener(this.f16P.mOnKeyListener);
            }
            return dialog;
        }

        public AlertDialog show() {
            AlertDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }
}
