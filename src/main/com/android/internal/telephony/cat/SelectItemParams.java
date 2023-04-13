package com.android.internal.telephony.cat;

import android.compat.annotation.UnsupportedAppUsage;
import android.graphics.Bitmap;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: CommandParams.java */
/* loaded from: classes.dex */
public class SelectItemParams extends CommandParams {
    boolean mLoadTitleIcon;
    Menu mMenu;

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public SelectItemParams(CommandDetails commandDetails, Menu menu, boolean z) {
        super(commandDetails);
        this.mMenu = menu;
        this.mLoadTitleIcon = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.telephony.cat.CommandParams
    public boolean setIcon(Bitmap bitmap) {
        Menu menu;
        if (bitmap == null || (menu = this.mMenu) == null) {
            return false;
        }
        if (this.mLoadTitleIcon && menu.titleIcon == null) {
            menu.titleIcon = bitmap;
            return true;
        }
        for (Item item : menu.items) {
            if (item.icon == null) {
                item.icon = bitmap;
                return true;
            }
        }
        return true;
    }
}
