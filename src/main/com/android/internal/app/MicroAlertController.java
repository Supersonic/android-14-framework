package com.android.internal.app;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.internal.C4057R;
/* loaded from: classes4.dex */
public class MicroAlertController extends AlertController {
    public MicroAlertController(Context context, DialogInterface di, Window window) {
        super(context, di, window);
    }

    @Override // com.android.internal.app.AlertController
    protected void setupContent(ViewGroup contentPanel) {
        this.mScrollView = (ScrollView) this.mWindow.findViewById(C4057R.C4059id.scrollView);
        this.mMessageView = (TextView) contentPanel.findViewById(16908299);
        if (this.mMessageView == null) {
            return;
        }
        if (this.mMessage != null) {
            this.mMessageView.setText(this.mMessage);
            return;
        }
        this.mMessageView.setVisibility(8);
        contentPanel.removeView(this.mMessageView);
        if (this.mListView != null) {
            View topPanel = this.mScrollView.findViewById(C4057R.C4059id.topPanel);
            ((ViewGroup) topPanel.getParent()).removeView(topPanel);
            FrameLayout.LayoutParams topParams = new FrameLayout.LayoutParams(topPanel.getLayoutParams());
            topParams.gravity = 48;
            topPanel.setLayoutParams(topParams);
            View buttonPanel = this.mScrollView.findViewById(C4057R.C4059id.buttonPanel);
            ((ViewGroup) buttonPanel.getParent()).removeView(buttonPanel);
            FrameLayout.LayoutParams buttonParams = new FrameLayout.LayoutParams(buttonPanel.getLayoutParams());
            buttonParams.gravity = 80;
            buttonPanel.setLayoutParams(buttonParams);
            ViewGroup scrollParent = (ViewGroup) this.mScrollView.getParent();
            int childIndex = scrollParent.indexOfChild(this.mScrollView);
            scrollParent.removeViewAt(childIndex);
            scrollParent.addView(this.mListView, new ViewGroup.LayoutParams(-1, -1));
            scrollParent.addView(topPanel);
            scrollParent.addView(buttonPanel);
            return;
        }
        contentPanel.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.app.AlertController
    public void setupTitle(ViewGroup topPanel) {
        super.setupTitle(topPanel);
        if (topPanel.getVisibility() == 8) {
            topPanel.setVisibility(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.app.AlertController
    public void setupButtons(ViewGroup buttonPanel) {
        super.setupButtons(buttonPanel);
        if (buttonPanel.getVisibility() == 8) {
            buttonPanel.setVisibility(4);
        }
    }
}
