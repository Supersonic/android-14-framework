package com.android.internal.accessibility.dialog;

import android.app.Activity;
import android.content.ComponentName;
import android.p008os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.android.internal.C4057R;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.internal.accessibility.util.AccessibilityStatsLogUtils;
import com.android.internal.widget.ResolverDrawerLayout;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes4.dex */
public class AccessibilityButtonChooserActivity extends Activity {
    private final List<AccessibilityTarget> mTargets = new ArrayList();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        int i;
        int i2;
        super.onCreate(savedInstanceState);
        setContentView(C4057R.layout.accessibility_button_chooser);
        ResolverDrawerLayout rdl = (ResolverDrawerLayout) findViewById(C4057R.C4059id.contentPanel);
        if (rdl != null) {
            rdl.setOnDismissedListener(new ResolverDrawerLayout.OnDismissedListener() { // from class: com.android.internal.accessibility.dialog.AccessibilityButtonChooserActivity$$ExternalSyntheticLambda0
                @Override // com.android.internal.widget.ResolverDrawerLayout.OnDismissedListener
                public final void onDismissed() {
                    AccessibilityButtonChooserActivity.this.finish();
                }
            });
        }
        String component = Settings.Secure.getString(getContentResolver(), Settings.Secure.ACCESSIBILITY_BUTTON_TARGET_COMPONENT);
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(AccessibilityManager.class);
        boolean isTouchExploreOn = accessibilityManager.isTouchExplorationEnabled();
        boolean isGestureNavigateEnabled = 2 == getResources().getInteger(C4057R.integer.config_navBarInteractionMode);
        if (isGestureNavigateEnabled) {
            TextView promptPrologue = (TextView) findViewById(C4057R.C4059id.accessibility_button_prompt_prologue);
            if (isTouchExploreOn) {
                i2 = C4057R.string.accessibility_gesture_3finger_prompt_text;
            } else {
                i2 = C4057R.string.accessibility_gesture_prompt_text;
            }
            promptPrologue.setText(i2);
        }
        if (TextUtils.isEmpty(component)) {
            TextView prompt = (TextView) findViewById(C4057R.C4059id.accessibility_button_prompt);
            if (isGestureNavigateEnabled) {
                if (isTouchExploreOn) {
                    i = C4057R.string.accessibility_gesture_3finger_instructional_text;
                } else {
                    i = C4057R.string.accessibility_gesture_instructional_text;
                }
                prompt.setText(i);
            }
            prompt.setVisibility(0);
        }
        this.mTargets.addAll(AccessibilityTargetHelper.getTargets(this, 0));
        GridView gridview = (GridView) findViewById(C4057R.C4059id.accessibility_button_chooser_grid);
        gridview.setAdapter((ListAdapter) new ButtonTargetAdapter(this.mTargets));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.internal.accessibility.dialog.AccessibilityButtonChooserActivity$$ExternalSyntheticLambda1
            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i3, long j) {
                AccessibilityButtonChooserActivity.this.lambda$onCreate$0(adapterView, view, i3, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(AdapterView parent, View view, int position, long id) {
        String name = this.mTargets.get(position).getId();
        if (name.equals("com.android.server.accessibility.MagnificationController")) {
            name = AccessibilityShortcutController.MAGNIFICATION_COMPONENT_NAME.flattenToString();
        }
        ComponentName componentName = ComponentName.unflattenFromString(name);
        AccessibilityStatsLogUtils.logAccessibilityButtonLongPressStatus(componentName);
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ACCESSIBILITY_BUTTON_TARGET_COMPONENT, this.mTargets.get(position).getId());
        finish();
    }
}
