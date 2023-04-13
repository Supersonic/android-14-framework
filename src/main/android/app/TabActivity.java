package android.app;

import android.p008os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import com.android.internal.C4057R;
@Deprecated
/* loaded from: classes.dex */
public class TabActivity extends ActivityGroup {
    private String mDefaultTab = null;
    private int mDefaultTabIndex = -1;
    private TabHost mTabHost;

    public void setDefaultTab(String tag) {
        this.mDefaultTab = tag;
        this.mDefaultTabIndex = -1;
    }

    public void setDefaultTab(int index) {
        this.mDefaultTab = null;
        this.mDefaultTabIndex = index;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        ensureTabHost();
        String cur = state.getString("currentTab");
        if (cur != null) {
            this.mTabHost.setCurrentTabByTag(cur);
        }
        if (this.mTabHost.getCurrentTab() < 0) {
            String str = this.mDefaultTab;
            if (str != null) {
                this.mTabHost.setCurrentTabByTag(str);
                return;
            }
            int i = this.mDefaultTabIndex;
            if (i >= 0) {
                this.mTabHost.setCurrentTab(i);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onPostCreate(Bundle icicle) {
        super.onPostCreate(icicle);
        ensureTabHost();
        if (this.mTabHost.getCurrentTab() == -1) {
            this.mTabHost.setCurrentTab(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.ActivityGroup, android.app.Activity
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String currentTabTag = this.mTabHost.getCurrentTabTag();
        if (currentTabTag != null) {
            outState.putString("currentTab", currentTabTag);
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onContentChanged() {
        super.onContentChanged();
        TabHost tabHost = (TabHost) findViewById(16908306);
        this.mTabHost = tabHost;
        if (tabHost == null) {
            throw new RuntimeException("Your content must have a TabHost whose id attribute is 'android.R.id.tabhost'");
        }
        tabHost.setup(getLocalActivityManager());
    }

    private void ensureTabHost() {
        if (this.mTabHost == null) {
            setContentView(C4057R.layout.tab_content);
        }
    }

    @Override // android.app.Activity
    protected void onChildTitleChanged(Activity childActivity, CharSequence title) {
        View tabView;
        if (getLocalActivityManager().getCurrentActivity() == childActivity && (tabView = this.mTabHost.getCurrentTabView()) != null && (tabView instanceof TextView)) {
            ((TextView) tabView).setText(title);
        }
    }

    public TabHost getTabHost() {
        ensureTabHost();
        return this.mTabHost;
    }

    public TabWidget getTabWidget() {
        return this.mTabHost.getTabWidget();
    }
}
