package com.android.internal.app;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.media.MediaRouter;
import android.util.Log;
import android.view.View;
/* loaded from: classes4.dex */
public abstract class MediaRouteDialogPresenter {
    private static final String CHOOSER_FRAGMENT_TAG = "android.app.MediaRouteButton:MediaRouteChooserDialogFragment";
    private static final String CONTROLLER_FRAGMENT_TAG = "android.app.MediaRouteButton:MediaRouteControllerDialogFragment";
    private static final String TAG = "MediaRouter";

    public static DialogFragment showDialogFragment(Activity activity, int routeTypes, View.OnClickListener extendedSettingsClickListener) {
        MediaRouter router = (MediaRouter) activity.getSystemService(Context.MEDIA_ROUTER_SERVICE);
        FragmentManager fm = activity.getFragmentManager();
        MediaRouter.RouteInfo route = router.getSelectedRoute();
        if (route.isDefault() || !route.matchesTypes(routeTypes)) {
            if (fm.findFragmentByTag(CHOOSER_FRAGMENT_TAG) != null) {
                Log.m104w(TAG, "showDialog(): Route chooser dialog already showing!");
                return null;
            }
            MediaRouteChooserDialogFragment f = new MediaRouteChooserDialogFragment();
            f.setRouteTypes(routeTypes);
            f.setExtendedSettingsClickListener(extendedSettingsClickListener);
            f.show(fm, CHOOSER_FRAGMENT_TAG);
            return f;
        } else if (fm.findFragmentByTag(CONTROLLER_FRAGMENT_TAG) != null) {
            Log.m104w(TAG, "showDialog(): Route controller dialog already showing!");
            return null;
        } else {
            MediaRouteControllerDialogFragment f2 = new MediaRouteControllerDialogFragment();
            f2.show(fm, CONTROLLER_FRAGMENT_TAG);
            return f2;
        }
    }

    public static Dialog createDialog(Context context, int routeTypes, View.OnClickListener extendedSettingsClickListener) {
        int theme;
        if (MediaRouteChooserDialog.isLightTheme(context)) {
            theme = 16974130;
        } else {
            theme = 16974126;
        }
        return createDialog(context, routeTypes, extendedSettingsClickListener, theme);
    }

    public static Dialog createDialog(Context context, int routeTypes, View.OnClickListener extendedSettingsClickListener, int theme) {
        return createDialog(context, routeTypes, extendedSettingsClickListener, theme, true);
    }

    public static Dialog createDialog(Context context, int routeTypes, View.OnClickListener extendedSettingsClickListener, int theme, boolean showProgressBarWhenEmpty) {
        MediaRouter router = (MediaRouter) context.getSystemService(MediaRouter.class);
        MediaRouter.RouteInfo route = router.getSelectedRoute();
        if (route.isDefault() || !route.matchesTypes(routeTypes)) {
            MediaRouteChooserDialog d = new MediaRouteChooserDialog(context, theme, showProgressBarWhenEmpty);
            d.setRouteTypes(routeTypes);
            d.setExtendedSettingsClickListener(extendedSettingsClickListener);
            return d;
        }
        return new MediaRouteControllerDialog(context, theme);
    }
}
