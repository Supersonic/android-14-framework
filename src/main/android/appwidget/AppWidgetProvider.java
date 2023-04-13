package android.appwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.p008os.Bundle;
/* loaded from: classes.dex */
public class AppWidgetProvider extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Bundle extras;
        int[] appWidgetIds;
        String action = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_ENABLE_AND_UPDATE.equals(action)) {
            onReceive(context, new Intent(intent).setAction(AppWidgetManager.ACTION_APPWIDGET_ENABLED));
            onReceive(context, new Intent(intent).setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE));
        } else if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            Bundle extras2 = intent.getExtras();
            if (extras2 != null && (appWidgetIds = extras2.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)) != null && appWidgetIds.length > 0) {
                onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
            }
        } else if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
            Bundle extras3 = intent.getExtras();
            if (extras3 != null && extras3.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                int appWidgetId = extras3.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
                onDeleted(context, new int[]{appWidgetId});
            }
        } else if (AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED.equals(action)) {
            Bundle extras4 = intent.getExtras();
            if (extras4 != null && extras4.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID) && extras4.containsKey(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS)) {
                int appWidgetId2 = extras4.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
                Bundle widgetExtras = extras4.getBundle(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS);
                onAppWidgetOptionsChanged(context, AppWidgetManager.getInstance(context), appWidgetId2, widgetExtras);
            }
        } else if (AppWidgetManager.ACTION_APPWIDGET_ENABLED.equals(action)) {
            onEnabled(context);
        } else if (AppWidgetManager.ACTION_APPWIDGET_DISABLED.equals(action)) {
            onDisabled(context);
        } else if (AppWidgetManager.ACTION_APPWIDGET_RESTORED.equals(action) && (extras = intent.getExtras()) != null) {
            int[] oldIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_OLD_IDS);
            int[] newIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            if (oldIds != null && oldIds.length > 0) {
                onRestored(context, oldIds, newIds);
                onUpdate(context, AppWidgetManager.getInstance(context), newIds);
            }
        }
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    }

    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
    }

    public void onDeleted(Context context, int[] appWidgetIds) {
    }

    public void onEnabled(Context context) {
    }

    public void onDisabled(Context context) {
    }

    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
    }
}
