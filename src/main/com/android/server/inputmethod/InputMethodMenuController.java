package com.android.server.inputmethod;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import com.android.internal.R;
import com.android.internal.annotations.GuardedBy;
import com.android.server.LocalServices;
import com.android.server.inputmethod.InputMethodSubtypeSwitchingController;
import com.android.server.inputmethod.InputMethodUtils;
import com.android.server.p014wm.WindowManagerInternal;
import java.util.List;
/* loaded from: classes.dex */
public final class InputMethodMenuController {
    public AlertDialog.Builder mDialogBuilder;
    @GuardedBy({"ImfLock.class"})
    public InputMethodDialogWindowContext mDialogWindowContext;
    public InputMethodInfo[] mIms;
    public final ArrayMap<String, InputMethodInfo> mMethodMap;
    public final InputMethodManagerService mService;
    public final InputMethodUtils.InputMethodSettings mSettings;
    public boolean mShowImeWithHardKeyboard;
    public int[] mSubtypeIds;
    public final InputMethodSubtypeSwitchingController mSwitchingController;
    public AlertDialog mSwitchingDialog;
    public View mSwitchingDialogTitleView;
    public final WindowManagerInternal mWindowManagerInternal = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);

    public InputMethodMenuController(InputMethodManagerService inputMethodManagerService) {
        this.mService = inputMethodManagerService;
        this.mSettings = inputMethodManagerService.mSettings;
        this.mSwitchingController = inputMethodManagerService.mSwitchingController;
        this.mMethodMap = inputMethodManagerService.mMethodMap;
    }

    public void showInputMethodMenu(boolean z, int i) {
        int i2;
        InputMethodSubtype currentInputMethodSubtypeLocked;
        boolean isScreenLocked = isScreenLocked();
        String selectedInputMethod = this.mSettings.getSelectedInputMethod();
        int selectedInputMethodSubtypeId = this.mSettings.getSelectedInputMethodSubtypeId(selectedInputMethod);
        synchronized (ImfLock.class) {
            List<InputMethodSubtypeSwitchingController.ImeSubtypeListItem> sortedInputMethodAndSubtypeListForImeMenuLocked = this.mSwitchingController.getSortedInputMethodAndSubtypeListForImeMenuLocked(z, isScreenLocked);
            if (sortedInputMethodAndSubtypeListForImeMenuLocked.isEmpty()) {
                return;
            }
            hideInputMethodMenuLocked();
            if (selectedInputMethodSubtypeId == -1 && (currentInputMethodSubtypeLocked = this.mService.getCurrentInputMethodSubtypeLocked()) != null) {
                selectedInputMethodSubtypeId = SubtypeUtils.getSubtypeIdFromHashCode(this.mMethodMap.get(this.mService.getSelectedMethodIdLocked()), currentInputMethodSubtypeLocked.hashCode());
            }
            int size = sortedInputMethodAndSubtypeListForImeMenuLocked.size();
            this.mIms = new InputMethodInfo[size];
            this.mSubtypeIds = new int[size];
            int i3 = 0;
            int i4 = 0;
            for (int i5 = 0; i5 < size; i5++) {
                InputMethodSubtypeSwitchingController.ImeSubtypeListItem imeSubtypeListItem = sortedInputMethodAndSubtypeListForImeMenuLocked.get(i5);
                InputMethodInfo[] inputMethodInfoArr = this.mIms;
                InputMethodInfo inputMethodInfo = imeSubtypeListItem.mImi;
                inputMethodInfoArr[i5] = inputMethodInfo;
                this.mSubtypeIds[i5] = imeSubtypeListItem.mSubtypeId;
                if (inputMethodInfo.getId().equals(selectedInputMethod) && ((i2 = this.mSubtypeIds[i5]) == -1 || ((selectedInputMethodSubtypeId == -1 && i2 == 0) || i2 == selectedInputMethodSubtypeId))) {
                    i4 = i5;
                }
            }
            if (this.mDialogWindowContext == null) {
                this.mDialogWindowContext = new InputMethodDialogWindowContext();
            }
            Context context = this.mDialogWindowContext.get(i);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            this.mDialogBuilder = builder;
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.android.server.inputmethod.InputMethodMenuController$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnCancelListener
                public final void onCancel(DialogInterface dialogInterface) {
                    InputMethodMenuController.this.lambda$showInputMethodMenu$0(dialogInterface);
                }
            });
            Context context2 = this.mDialogBuilder.getContext();
            TypedArray obtainStyledAttributes = context2.obtainStyledAttributes(null, R.styleable.DialogPreference, 16842845, 0);
            Drawable drawable = obtainStyledAttributes.getDrawable(2);
            obtainStyledAttributes.recycle();
            this.mDialogBuilder.setIcon(drawable);
            View inflate = ((LayoutInflater) context2.getSystemService(LayoutInflater.class)).inflate(17367187, (ViewGroup) null);
            this.mDialogBuilder.setCustomTitle(inflate);
            this.mSwitchingDialogTitleView = inflate;
            View findViewById = inflate.findViewById(16909077);
            if (!this.mWindowManagerInternal.isHardKeyboardAvailable()) {
                i3 = 8;
            }
            findViewById.setVisibility(i3);
            Switch r14 = (Switch) this.mSwitchingDialogTitleView.findViewById(16909078);
            r14.setChecked(this.mShowImeWithHardKeyboard);
            r14.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.server.inputmethod.InputMethodMenuController$$ExternalSyntheticLambda1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public final void onCheckedChanged(CompoundButton compoundButton, boolean z2) {
                    InputMethodMenuController.this.lambda$showInputMethodMenu$1(compoundButton, z2);
                }
            });
            final ImeSubtypeListAdapter imeSubtypeListAdapter = new ImeSubtypeListAdapter(context2, 17367188, sortedInputMethodAndSubtypeListForImeMenuLocked, i4);
            this.mDialogBuilder.setSingleChoiceItems(imeSubtypeListAdapter, i4, new DialogInterface.OnClickListener() { // from class: com.android.server.inputmethod.InputMethodMenuController$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i6) {
                    InputMethodMenuController.this.lambda$showInputMethodMenu$2(imeSubtypeListAdapter, dialogInterface, i6);
                }
            });
            AlertDialog create = this.mDialogBuilder.create();
            this.mSwitchingDialog = create;
            create.setCanceledOnTouchOutside(true);
            Window window = this.mSwitchingDialog.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            window.setType(2012);
            window.setHideOverlayWindows(true);
            attributes.token = context.getWindowContextToken();
            attributes.privateFlags |= 16;
            attributes.setTitle("Select input method");
            window.setAttributes(attributes);
            this.mService.updateSystemUiLocked();
            this.mService.sendOnNavButtonFlagsChangedLocked();
            this.mSwitchingDialog.show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showInputMethodMenu$0(DialogInterface dialogInterface) {
        hideInputMethodMenu();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showInputMethodMenu$1(CompoundButton compoundButton, boolean z) {
        this.mSettings.setShowImeWithHardKeyboard(z);
        hideInputMethodMenu();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showInputMethodMenu$2(ImeSubtypeListAdapter imeSubtypeListAdapter, DialogInterface dialogInterface, int i) {
        int[] iArr;
        synchronized (ImfLock.class) {
            InputMethodInfo[] inputMethodInfoArr = this.mIms;
            if (inputMethodInfoArr != null && inputMethodInfoArr.length > i && (iArr = this.mSubtypeIds) != null && iArr.length > i) {
                InputMethodInfo inputMethodInfo = inputMethodInfoArr[i];
                int i2 = iArr[i];
                imeSubtypeListAdapter.mCheckedItem = i;
                imeSubtypeListAdapter.notifyDataSetChanged();
                hideInputMethodMenu();
                if (inputMethodInfo != null) {
                    this.mService.setInputMethodLocked(inputMethodInfo.getId(), (i2 < 0 || i2 >= inputMethodInfo.getSubtypeCount()) ? -1 : -1);
                }
            }
        }
    }

    public final boolean isScreenLocked() {
        return this.mWindowManagerInternal.isKeyguardLocked() && this.mWindowManagerInternal.isKeyguardSecure(this.mSettings.getCurrentUserId());
    }

    public void updateKeyboardFromSettingsLocked() {
        this.mShowImeWithHardKeyboard = this.mSettings.isShowImeWithHardKeyboardEnabled();
        AlertDialog alertDialog = this.mSwitchingDialog;
        if (alertDialog == null || this.mSwitchingDialogTitleView == null || !alertDialog.isShowing()) {
            return;
        }
        ((Switch) this.mSwitchingDialogTitleView.findViewById(16909078)).setChecked(this.mShowImeWithHardKeyboard);
    }

    public void hideInputMethodMenu() {
        synchronized (ImfLock.class) {
            hideInputMethodMenuLocked();
        }
    }

    @GuardedBy({"ImfLock.class"})
    public void hideInputMethodMenuLocked() {
        AlertDialog alertDialog = this.mSwitchingDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mSwitchingDialog = null;
            this.mSwitchingDialogTitleView = null;
            this.mService.updateSystemUiLocked();
            this.mService.sendOnNavButtonFlagsChangedLocked();
            this.mDialogBuilder = null;
            this.mIms = null;
        }
    }

    public AlertDialog getSwitchingDialogLocked() {
        return this.mSwitchingDialog;
    }

    public boolean getShowImeWithHardKeyboard() {
        return this.mShowImeWithHardKeyboard;
    }

    public boolean isisInputMethodPickerShownForTestLocked() {
        AlertDialog alertDialog = this.mSwitchingDialog;
        if (alertDialog == null) {
            return false;
        }
        return alertDialog.isShowing();
    }

    public void handleHardKeyboardStatusChange(boolean z) {
        synchronized (ImfLock.class) {
            AlertDialog alertDialog = this.mSwitchingDialog;
            if (alertDialog != null && this.mSwitchingDialogTitleView != null && alertDialog.isShowing()) {
                this.mSwitchingDialogTitleView.findViewById(16909077).setVisibility(z ? 0 : 8);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class ImeSubtypeListAdapter extends ArrayAdapter<InputMethodSubtypeSwitchingController.ImeSubtypeListItem> {
        public int mCheckedItem;
        public final LayoutInflater mInflater;
        public final List<InputMethodSubtypeSwitchingController.ImeSubtypeListItem> mItemsList;
        public final int mTextViewResourceId;

        public ImeSubtypeListAdapter(Context context, int i, List<InputMethodSubtypeSwitchingController.ImeSubtypeListItem> list, int i2) {
            super(context, i, list);
            this.mTextViewResourceId = i;
            this.mItemsList = list;
            this.mCheckedItem = i2;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = this.mInflater.inflate(this.mTextViewResourceId, (ViewGroup) null);
            }
            if (i >= 0 && i < this.mItemsList.size()) {
                InputMethodSubtypeSwitchingController.ImeSubtypeListItem imeSubtypeListItem = this.mItemsList.get(i);
                CharSequence charSequence = imeSubtypeListItem.mImeName;
                CharSequence charSequence2 = imeSubtypeListItem.mSubtypeName;
                TextView textView = (TextView) view.findViewById(16908308);
                TextView textView2 = (TextView) view.findViewById(16908309);
                if (TextUtils.isEmpty(charSequence2)) {
                    textView.setText(charSequence);
                    textView2.setVisibility(8);
                } else {
                    textView.setText(charSequence2);
                    textView2.setText(charSequence);
                    textView2.setVisibility(0);
                }
                ((RadioButton) view.findViewById(16909402)).setChecked(i == this.mCheckedItem);
            }
            return view;
        }
    }
}
