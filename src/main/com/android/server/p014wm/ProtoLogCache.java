package com.android.server.p014wm;

import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
/* renamed from: com.android.server.wm.ProtoLogCache */
/* loaded from: classes2.dex */
public class ProtoLogCache {
    public static boolean TEST_GROUP_enabled = false;
    public static boolean WM_DEBUG_ADD_REMOVE_enabled = false;
    public static boolean WM_DEBUG_ANIM_enabled = false;
    public static boolean WM_DEBUG_APP_TRANSITIONS_ANIM_enabled = false;
    public static boolean WM_DEBUG_APP_TRANSITIONS_enabled = false;
    public static boolean WM_DEBUG_BACK_PREVIEW_enabled = false;
    public static boolean WM_DEBUG_BOOT_enabled = false;
    public static boolean WM_DEBUG_CONFIGURATION_enabled = false;
    public static boolean WM_DEBUG_CONTAINERS_enabled = false;
    public static boolean WM_DEBUG_CONTENT_RECORDING_enabled = false;
    public static boolean WM_DEBUG_DRAW_enabled = false;
    public static boolean WM_DEBUG_DREAM_enabled = false;
    public static boolean WM_DEBUG_FOCUS_LIGHT_enabled = false;
    public static boolean WM_DEBUG_FOCUS_enabled = false;
    public static boolean WM_DEBUG_IME_enabled = false;
    public static boolean WM_DEBUG_IMMERSIVE_enabled = false;
    public static boolean WM_DEBUG_KEEP_SCREEN_ON_enabled = false;
    public static boolean WM_DEBUG_LOCKTASK_enabled = false;
    public static boolean WM_DEBUG_ORIENTATION_enabled = false;
    public static boolean WM_DEBUG_RECENTS_ANIMATIONS_enabled = false;
    public static boolean WM_DEBUG_REMOTE_ANIMATIONS_enabled = false;
    public static boolean WM_DEBUG_RESIZE_enabled = false;
    public static boolean WM_DEBUG_SCREEN_ON_enabled = false;
    public static boolean WM_DEBUG_STARTING_WINDOW_enabled = false;
    public static boolean WM_DEBUG_STATES_enabled = false;
    public static boolean WM_DEBUG_SWITCH_enabled = false;
    public static boolean WM_DEBUG_SYNC_ENGINE_enabled = false;
    public static boolean WM_DEBUG_TASKS_enabled = false;
    public static boolean WM_DEBUG_WALLPAPER_enabled = false;
    public static boolean WM_DEBUG_WINDOW_INSETS_enabled = false;
    public static boolean WM_DEBUG_WINDOW_MOVEMENT_enabled = false;
    public static boolean WM_DEBUG_WINDOW_ORGANIZER_enabled = false;
    public static boolean WM_DEBUG_WINDOW_TRANSITIONS_MIN_enabled = false;
    public static boolean WM_DEBUG_WINDOW_TRANSITIONS_enabled = false;
    public static boolean WM_ERROR_enabled = false;
    public static boolean WM_SHOW_SURFACE_ALLOC_enabled = false;
    public static boolean WM_SHOW_TRANSACTIONS_enabled = false;

    static {
        ProtoLogImpl.sCacheUpdater = new Runnable() { // from class: com.android.server.wm.ProtoLogCache$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ProtoLogCache.update();
            }
        };
        update();
    }

    public static void update() {
        WM_ERROR_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_ERROR);
        WM_DEBUG_ORIENTATION_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_ORIENTATION);
        WM_DEBUG_FOCUS_LIGHT_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_FOCUS_LIGHT);
        WM_DEBUG_BOOT_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_BOOT);
        WM_DEBUG_RESIZE_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_RESIZE);
        WM_DEBUG_ADD_REMOVE_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_ADD_REMOVE);
        WM_DEBUG_CONFIGURATION_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_CONFIGURATION);
        WM_DEBUG_SWITCH_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_SWITCH);
        WM_DEBUG_CONTAINERS_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_CONTAINERS);
        WM_DEBUG_FOCUS_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_FOCUS);
        WM_DEBUG_IMMERSIVE_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_IMMERSIVE);
        WM_DEBUG_LOCKTASK_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_LOCKTASK);
        WM_DEBUG_STATES_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_STATES);
        WM_DEBUG_TASKS_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_TASKS);
        WM_DEBUG_STARTING_WINDOW_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_STARTING_WINDOW);
        WM_SHOW_TRANSACTIONS_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_SHOW_TRANSACTIONS);
        WM_SHOW_SURFACE_ALLOC_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_SHOW_SURFACE_ALLOC);
        WM_DEBUG_APP_TRANSITIONS_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS);
        WM_DEBUG_ANIM_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_ANIM);
        WM_DEBUG_APP_TRANSITIONS_ANIM_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_APP_TRANSITIONS_ANIM);
        WM_DEBUG_RECENTS_ANIMATIONS_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_RECENTS_ANIMATIONS);
        WM_DEBUG_DRAW_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_DRAW);
        WM_DEBUG_REMOTE_ANIMATIONS_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_REMOTE_ANIMATIONS);
        WM_DEBUG_SCREEN_ON_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_SCREEN_ON);
        WM_DEBUG_KEEP_SCREEN_ON_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_KEEP_SCREEN_ON);
        WM_DEBUG_WINDOW_MOVEMENT_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_WINDOW_MOVEMENT);
        WM_DEBUG_IME_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_IME);
        WM_DEBUG_WINDOW_ORGANIZER_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_WINDOW_ORGANIZER);
        WM_DEBUG_SYNC_ENGINE_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_SYNC_ENGINE);
        WM_DEBUG_WINDOW_TRANSITIONS_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS);
        WM_DEBUG_WINDOW_TRANSITIONS_MIN_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_WINDOW_TRANSITIONS_MIN);
        WM_DEBUG_WINDOW_INSETS_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_WINDOW_INSETS);
        WM_DEBUG_CONTENT_RECORDING_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_CONTENT_RECORDING);
        WM_DEBUG_WALLPAPER_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_WALLPAPER);
        WM_DEBUG_BACK_PREVIEW_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_BACK_PREVIEW);
        WM_DEBUG_DREAM_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.WM_DEBUG_DREAM);
        TEST_GROUP_enabled = ProtoLogImpl.isEnabled(ProtoLogGroup.TEST_GROUP);
    }
}
