package android.service.controls.templates;

import android.content.Context;
import android.p008os.Bundle;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public abstract class ControlTemplate {
    private static final String KEY_TEMPLATE_ID = "key_template_id";
    private static final String KEY_TEMPLATE_TYPE = "key_template_type";
    private static final String TAG = "ControlTemplate";
    public static final int TYPE_ERROR = -1;
    public static final int TYPE_NO_TEMPLATE = 0;
    public static final int TYPE_RANGE = 2;
    public static final int TYPE_STATELESS = 8;
    public static final int TYPE_TEMPERATURE = 7;
    public static final int TYPE_THUMBNAIL = 3;
    public static final int TYPE_TOGGLE = 1;
    public static final int TYPE_TOGGLE_RANGE = 6;
    private final String mTemplateId;
    public static final ControlTemplate NO_TEMPLATE = new ControlTemplate("") { // from class: android.service.controls.templates.ControlTemplate.1
        @Override // android.service.controls.templates.ControlTemplate
        public int getTemplateType() {
            return 0;
        }
    };
    private static final ControlTemplate ERROR_TEMPLATE = new ControlTemplate("") { // from class: android.service.controls.templates.ControlTemplate.2
        @Override // android.service.controls.templates.ControlTemplate
        public int getTemplateType() {
            return -1;
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface TemplateType {
    }

    public abstract int getTemplateType();

    public String getTemplateId() {
        return this.mTemplateId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Bundle getDataBundle() {
        Bundle b = new Bundle();
        b.putInt(KEY_TEMPLATE_TYPE, getTemplateType());
        b.putString(KEY_TEMPLATE_ID, this.mTemplateId);
        return b;
    }

    private ControlTemplate() {
        this.mTemplateId = "";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ControlTemplate(Bundle b) {
        this.mTemplateId = b.getString(KEY_TEMPLATE_ID);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ControlTemplate(String templateId) {
        Preconditions.checkNotNull(templateId);
        this.mTemplateId = templateId;
    }

    public void prepareTemplateForBinder(Context context) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ControlTemplate createTemplateFromBundle(Bundle bundle) {
        if (bundle == null) {
            Log.m110e(TAG, "Null bundle");
            return ERROR_TEMPLATE;
        }
        int type = bundle.getInt(KEY_TEMPLATE_TYPE, -1);
        try {
            switch (type) {
                case 0:
                    return NO_TEMPLATE;
                case 1:
                    return new ToggleTemplate(bundle);
                case 2:
                    return new RangeTemplate(bundle);
                case 3:
                    return new ThumbnailTemplate(bundle);
                case 4:
                case 5:
                default:
                    return ERROR_TEMPLATE;
                case 6:
                    return new ToggleRangeTemplate(bundle);
                case 7:
                    return new TemperatureControlTemplate(bundle);
                case 8:
                    return new StatelessTemplate(bundle);
            }
        } catch (Exception e) {
            Log.m109e(TAG, "Error creating template", e);
            return ERROR_TEMPLATE;
        }
    }

    public static ControlTemplate getErrorTemplate() {
        return ERROR_TEMPLATE;
    }

    public static ControlTemplate getNoTemplateObject() {
        return NO_TEMPLATE;
    }
}
