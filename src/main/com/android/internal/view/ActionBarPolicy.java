package com.android.internal.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import com.android.internal.C4057R;
/* loaded from: classes2.dex */
public class ActionBarPolicy {
    private Context mContext;

    public static ActionBarPolicy get(Context context) {
        return new ActionBarPolicy(context);
    }

    private ActionBarPolicy(Context context) {
        this.mContext = context;
    }

    public int getMaxActionButtons() {
        Configuration config = this.mContext.getResources().getConfiguration();
        int width = config.screenWidthDp;
        int height = config.screenHeightDp;
        int smallest = config.smallestScreenWidthDp;
        if (smallest <= 600) {
            if (width <= 960 || height <= 720) {
                if (width > 720 && height > 960) {
                    return 5;
                }
                if (width < 500) {
                    if (width <= 640 || height <= 480) {
                        if (width > 480 && height > 640) {
                            return 4;
                        }
                        if (width >= 360) {
                            return 3;
                        }
                        return 2;
                    }
                    return 4;
                }
                return 4;
            }
            return 5;
        }
        return 5;
    }

    public boolean showsOverflowMenuButton() {
        return true;
    }

    public int getEmbeddedMenuWidthLimit() {
        return this.mContext.getResources().getDisplayMetrics().widthPixels / 2;
    }

    public boolean hasEmbeddedTabs() {
        int targetSdk = this.mContext.getApplicationInfo().targetSdkVersion;
        if (targetSdk >= 16) {
            return this.mContext.getResources().getBoolean(C4057R.bool.action_bar_embed_tabs);
        }
        Configuration configuration = this.mContext.getResources().getConfiguration();
        int width = configuration.screenWidthDp;
        int height = configuration.screenHeightDp;
        return configuration.orientation == 2 || width >= 480 || (width >= 640 && height >= 480);
    }

    public int getTabContainerHeight() {
        TypedArray a = this.mContext.obtainStyledAttributes(null, C4057R.styleable.ActionBar, 16843470, 0);
        int height = a.getLayoutDimension(4, 0);
        Resources r = this.mContext.getResources();
        if (!hasEmbeddedTabs()) {
            height = Math.min(height, r.getDimensionPixelSize(C4057R.dimen.action_bar_stacked_max_height));
        }
        a.recycle();
        return height;
    }

    public boolean enableHomeButtonByDefault() {
        return this.mContext.getApplicationInfo().targetSdkVersion < 14;
    }

    public int getStackedTabMaxWidth() {
        return this.mContext.getResources().getDimensionPixelSize(C4057R.dimen.action_bar_stacked_tab_max_width);
    }
}
