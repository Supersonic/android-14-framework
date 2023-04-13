package com.android.internal.accessibility.dialog;

import android.C0001R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.p008os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.android.internal.C4057R;
import com.android.internal.accessibility.util.AccessibilityUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes4.dex */
public class AccessibilityShortcutChooserActivity extends Activity {
    private static final String KEY_ACCESSIBILITY_SHORTCUT_MENU_MODE = "accessibility_shortcut_menu_mode";
    private AlertDialog mMenuDialog;
    private AlertDialog mPermissionDialog;
    private ShortcutTargetAdapter mTargetAdapter;
    private final int mShortcutType = 1;
    private final List<AccessibilityTarget> mTargets = new ArrayList();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypedArray theme = getTheme().obtainStyledAttributes(C0001R.styleable.Theme);
        if (!theme.getBoolean(38, false)) {
            requestWindowFeature(1);
        }
        this.mTargets.addAll(AccessibilityTargetHelper.getTargets(this, 1));
        this.mTargetAdapter = new ShortcutTargetAdapter(this.mTargets);
        AlertDialog createMenuDialog = createMenuDialog();
        this.mMenuDialog = createMenuDialog;
        createMenuDialog.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.android.internal.accessibility.dialog.AccessibilityShortcutChooserActivity$$ExternalSyntheticLambda3
            @Override // android.content.DialogInterface.OnShowListener
            public final void onShow(DialogInterface dialogInterface) {
                AccessibilityShortcutChooserActivity.this.lambda$onCreate$0(dialogInterface);
            }
        });
        this.mMenuDialog.show();
        if (savedInstanceState != null) {
            int restoreShortcutMenuMode = savedInstanceState.getInt(KEY_ACCESSIBILITY_SHORTCUT_MENU_MODE, 0);
            if (restoreShortcutMenuMode == 1) {
                onEditButtonClicked();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(DialogInterface dialog) {
        updateDialogListeners();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onDestroy() {
        this.mMenuDialog.setOnDismissListener(null);
        this.mMenuDialog.dismiss();
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_ACCESSIBILITY_SHORTCUT_MENU_MODE, this.mTargetAdapter.getShortcutMenuMode());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTargetSelected(AdapterView<?> parent, View view, int position, long id) {
        AccessibilityTarget target = this.mTargets.get(position);
        target.onSelected();
        this.mMenuDialog.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTargetChecked(AdapterView<?> parent, View view, int position, long id) {
        AccessibilityTarget target = this.mTargets.get(position);
        if ((target instanceof AccessibilityServiceTarget) && !target.isShortcutEnabled()) {
            showPermissionDialogIfNeeded(this, (AccessibilityServiceTarget) target, this.mTargetAdapter);
            return;
        }
        target.onCheckedChanged(!target.isShortcutEnabled());
        this.mTargetAdapter.notifyDataSetChanged();
    }

    private void showPermissionDialogIfNeeded(Context context, AccessibilityServiceTarget serviceTarget, final ShortcutTargetAdapter targetAdapter) {
        if (this.mPermissionDialog != null) {
            return;
        }
        AlertDialog create = new AlertDialog.Builder(context).setView(AccessibilityTargetHelper.createEnableDialogContentView(context, serviceTarget, new View.OnClickListener() { // from class: com.android.internal.accessibility.dialog.AccessibilityShortcutChooserActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AccessibilityShortcutChooserActivity.this.lambda$showPermissionDialogIfNeeded$1(targetAdapter, view);
            }
        }, new View.OnClickListener() { // from class: com.android.internal.accessibility.dialog.AccessibilityShortcutChooserActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AccessibilityShortcutChooserActivity.this.lambda$showPermissionDialogIfNeeded$2(view);
            }
        })).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.internal.accessibility.dialog.AccessibilityShortcutChooserActivity$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                AccessibilityShortcutChooserActivity.this.lambda$showPermissionDialogIfNeeded$3(dialogInterface);
            }
        }).create();
        this.mPermissionDialog = create;
        create.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showPermissionDialogIfNeeded$1(ShortcutTargetAdapter targetAdapter, View v) {
        this.mPermissionDialog.dismiss();
        targetAdapter.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showPermissionDialogIfNeeded$2(View v) {
        this.mPermissionDialog.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showPermissionDialogIfNeeded$3(DialogInterface dialog) {
        this.mPermissionDialog = null;
    }

    private void onDoneButtonClicked() {
        this.mTargets.clear();
        this.mTargets.addAll(AccessibilityTargetHelper.getTargets(this, 1));
        if (this.mTargets.isEmpty()) {
            this.mMenuDialog.dismiss();
            return;
        }
        this.mTargetAdapter.setShortcutMenuMode(0);
        this.mTargetAdapter.notifyDataSetChanged();
        this.mMenuDialog.getButton(-1).setText(getString(C4057R.string.edit_accessibility_shortcut_menu_button));
        updateDialogListeners();
    }

    private void onEditButtonClicked() {
        this.mTargets.clear();
        this.mTargets.addAll(AccessibilityTargetHelper.getInstalledTargets(this, 1));
        this.mTargetAdapter.setShortcutMenuMode(1);
        this.mTargetAdapter.notifyDataSetChanged();
        this.mMenuDialog.getButton(-1).setText(getString(C4057R.string.done_accessibility_shortcut_menu_button));
        updateDialogListeners();
    }

    private void updateDialogListeners() {
        boolean isEditMenuMode = this.mTargetAdapter.getShortcutMenuMode() == 1;
        this.mMenuDialog.setTitle(getString(isEditMenuMode ? C4057R.string.accessibility_edit_shortcut_menu_volume_title : C4057R.string.accessibility_select_shortcut_menu_title));
        this.mMenuDialog.getButton(-1).setOnClickListener(isEditMenuMode ? new View.OnClickListener() { // from class: com.android.internal.accessibility.dialog.AccessibilityShortcutChooserActivity$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AccessibilityShortcutChooserActivity.this.lambda$updateDialogListeners$4(view);
            }
        } : new View.OnClickListener() { // from class: com.android.internal.accessibility.dialog.AccessibilityShortcutChooserActivity$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AccessibilityShortcutChooserActivity.this.lambda$updateDialogListeners$5(view);
            }
        });
        this.mMenuDialog.getListView().setOnItemClickListener(isEditMenuMode ? new AdapterView.OnItemClickListener() { // from class: com.android.internal.accessibility.dialog.AccessibilityShortcutChooserActivity$$ExternalSyntheticLambda7
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                AccessibilityShortcutChooserActivity.this.onTargetChecked(adapterView, view, i, j);
            }
        } : new AdapterView.OnItemClickListener() { // from class: com.android.internal.accessibility.dialog.AccessibilityShortcutChooserActivity$$ExternalSyntheticLambda8
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                AccessibilityShortcutChooserActivity.this.onTargetSelected(adapterView, view, i, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateDialogListeners$4(View view) {
        onDoneButtonClicked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateDialogListeners$5(View view) {
        onEditButtonClicked();
    }

    private AlertDialog createMenuDialog() {
        String dialogTitle = getString(C4057R.string.accessibility_select_shortcut_menu_title);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(dialogTitle).setAdapter(this.mTargetAdapter, null).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.internal.accessibility.dialog.AccessibilityShortcutChooserActivity$$ExternalSyntheticLambda4
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                AccessibilityShortcutChooserActivity.this.lambda$createMenuDialog$6(dialogInterface);
            }
        });
        if (AccessibilityUtils.isUserSetupCompleted(this)) {
            String positiveButtonText = getString(C4057R.string.edit_accessibility_shortcut_menu_button);
            builder.setPositiveButton(positiveButtonText, (DialogInterface.OnClickListener) null);
        }
        return builder.create();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createMenuDialog$6(DialogInterface dialog) {
        finish();
    }
}
