package com.android.server.appwidget;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.os.Build;
import android.text.TextUtils;
import android.util.Slog;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.util.Objects;
/* loaded from: classes.dex */
public class AppWidgetXmlUtil {
    public static void writeAppWidgetProviderInfoLocked(TypedXmlSerializer typedXmlSerializer, AppWidgetProviderInfo appWidgetProviderInfo) throws IOException {
        Objects.requireNonNull(typedXmlSerializer);
        Objects.requireNonNull(appWidgetProviderInfo);
        typedXmlSerializer.attributeInt((String) null, "min_width", appWidgetProviderInfo.minWidth);
        typedXmlSerializer.attributeInt((String) null, "min_height", appWidgetProviderInfo.minHeight);
        typedXmlSerializer.attributeInt((String) null, "min_resize_width", appWidgetProviderInfo.minResizeWidth);
        typedXmlSerializer.attributeInt((String) null, "min_resize_height", appWidgetProviderInfo.minResizeHeight);
        typedXmlSerializer.attributeInt((String) null, "max_resize_width", appWidgetProviderInfo.maxResizeWidth);
        typedXmlSerializer.attributeInt((String) null, "max_resize_height", appWidgetProviderInfo.maxResizeHeight);
        typedXmlSerializer.attributeInt((String) null, "target_cell_width", appWidgetProviderInfo.targetCellWidth);
        typedXmlSerializer.attributeInt((String) null, "target_cell_height", appWidgetProviderInfo.targetCellHeight);
        typedXmlSerializer.attributeInt((String) null, "update_period_millis", appWidgetProviderInfo.updatePeriodMillis);
        typedXmlSerializer.attributeInt((String) null, "initial_layout", appWidgetProviderInfo.initialLayout);
        typedXmlSerializer.attributeInt((String) null, "initial_keyguard_layout", appWidgetProviderInfo.initialKeyguardLayout);
        ComponentName componentName = appWidgetProviderInfo.configure;
        if (componentName != null) {
            typedXmlSerializer.attribute((String) null, "configure", componentName.flattenToShortString());
        }
        String str = appWidgetProviderInfo.label;
        if (str != null) {
            typedXmlSerializer.attribute((String) null, "label", str);
        } else {
            Slog.e("AppWidgetXmlUtil", "Label is empty in " + appWidgetProviderInfo.provider);
        }
        typedXmlSerializer.attributeInt((String) null, "icon", appWidgetProviderInfo.icon);
        typedXmlSerializer.attributeInt((String) null, "preview_image", appWidgetProviderInfo.previewImage);
        typedXmlSerializer.attributeInt((String) null, "preview_layout", appWidgetProviderInfo.previewLayout);
        typedXmlSerializer.attributeInt((String) null, "auto_advance_view_id", appWidgetProviderInfo.autoAdvanceViewId);
        typedXmlSerializer.attributeInt((String) null, "resize_mode", appWidgetProviderInfo.resizeMode);
        typedXmlSerializer.attributeInt((String) null, "widget_category", appWidgetProviderInfo.widgetCategory);
        typedXmlSerializer.attributeInt((String) null, "widget_features", appWidgetProviderInfo.widgetFeatures);
        typedXmlSerializer.attributeInt((String) null, "description_res", appWidgetProviderInfo.descriptionRes);
        typedXmlSerializer.attributeBoolean((String) null, "provider_inheritance", appWidgetProviderInfo.isExtendedFromAppWidgetProvider);
        typedXmlSerializer.attribute((String) null, "os_fingerprint", Build.FINGERPRINT);
    }

    public static AppWidgetProviderInfo readAppWidgetProviderInfoLocked(TypedXmlPullParser typedXmlPullParser) {
        Objects.requireNonNull(typedXmlPullParser);
        if (Build.FINGERPRINT.equals(typedXmlPullParser.getAttributeValue((String) null, "os_fingerprint"))) {
            AppWidgetProviderInfo appWidgetProviderInfo = new AppWidgetProviderInfo();
            appWidgetProviderInfo.minWidth = typedXmlPullParser.getAttributeInt((String) null, "min_width", 0);
            appWidgetProviderInfo.minHeight = typedXmlPullParser.getAttributeInt((String) null, "min_height", 0);
            appWidgetProviderInfo.minResizeWidth = typedXmlPullParser.getAttributeInt((String) null, "min_resize_width", 0);
            appWidgetProviderInfo.minResizeHeight = typedXmlPullParser.getAttributeInt((String) null, "min_resize_height", 0);
            appWidgetProviderInfo.maxResizeWidth = typedXmlPullParser.getAttributeInt((String) null, "max_resize_width", 0);
            appWidgetProviderInfo.maxResizeHeight = typedXmlPullParser.getAttributeInt((String) null, "max_resize_height", 0);
            appWidgetProviderInfo.targetCellWidth = typedXmlPullParser.getAttributeInt((String) null, "target_cell_width", 0);
            appWidgetProviderInfo.targetCellHeight = typedXmlPullParser.getAttributeInt((String) null, "target_cell_height", 0);
            appWidgetProviderInfo.updatePeriodMillis = typedXmlPullParser.getAttributeInt((String) null, "update_period_millis", 0);
            appWidgetProviderInfo.initialLayout = typedXmlPullParser.getAttributeInt((String) null, "initial_layout", 0);
            appWidgetProviderInfo.initialKeyguardLayout = typedXmlPullParser.getAttributeInt((String) null, "initial_keyguard_layout", 0);
            String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "configure");
            if (!TextUtils.isEmpty(attributeValue)) {
                appWidgetProviderInfo.configure = ComponentName.unflattenFromString(attributeValue);
            }
            appWidgetProviderInfo.label = typedXmlPullParser.getAttributeValue((String) null, "label");
            appWidgetProviderInfo.icon = typedXmlPullParser.getAttributeInt((String) null, "icon", 0);
            appWidgetProviderInfo.previewImage = typedXmlPullParser.getAttributeInt((String) null, "preview_image", 0);
            appWidgetProviderInfo.previewLayout = typedXmlPullParser.getAttributeInt((String) null, "preview_layout", 0);
            appWidgetProviderInfo.autoAdvanceViewId = typedXmlPullParser.getAttributeInt((String) null, "auto_advance_view_id", 0);
            appWidgetProviderInfo.resizeMode = typedXmlPullParser.getAttributeInt((String) null, "resize_mode", 0);
            appWidgetProviderInfo.widgetCategory = typedXmlPullParser.getAttributeInt((String) null, "widget_category", 0);
            appWidgetProviderInfo.widgetFeatures = typedXmlPullParser.getAttributeInt((String) null, "widget_features", 0);
            appWidgetProviderInfo.descriptionRes = typedXmlPullParser.getAttributeInt((String) null, "description_res", 0);
            appWidgetProviderInfo.isExtendedFromAppWidgetProvider = typedXmlPullParser.getAttributeBoolean((String) null, "provider_inheritance", false);
            return appWidgetProviderInfo;
        }
        return null;
    }
}
