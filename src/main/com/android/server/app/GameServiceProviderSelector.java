package com.android.server.app;

import com.android.server.SystemService;
/* loaded from: classes.dex */
public interface GameServiceProviderSelector {
    GameServiceConfiguration get(SystemService.TargetUser targetUser, String str);
}
