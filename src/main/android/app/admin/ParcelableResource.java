package android.app.admin;

import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Slog;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.function.Supplier;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class ParcelableResource implements Parcelable {
    private static final String ATTR_PACKAGE_NAME = "package-name";
    private static final String ATTR_RESOURCE_ID = "resource-id";
    private static final String ATTR_RESOURCE_NAME = "resource-name";
    private static final String ATTR_RESOURCE_TYPE = "resource-type";
    public static final int RESOURCE_TYPE_DRAWABLE = 1;
    public static final int RESOURCE_TYPE_STRING = 2;
    private final String mPackageName;
    private final int mResourceId;
    private final String mResourceName;
    private final int mResourceType;
    private static String TAG = "DevicePolicyManager";
    public static final Parcelable.Creator<ParcelableResource> CREATOR = new Parcelable.Creator<ParcelableResource>() { // from class: android.app.admin.ParcelableResource.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ParcelableResource createFromParcel(Parcel in) {
            int resourceId = in.readInt();
            String packageName = in.readString();
            String resourceName = in.readString();
            int resourceType = in.readInt();
            return new ParcelableResource(resourceId, packageName, resourceName, resourceType);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ParcelableResource[] newArray(int size) {
            return new ParcelableResource[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ResourceType {
    }

    public ParcelableResource(Context context, int resourceId, int resourceType) throws IllegalStateException, IllegalArgumentException {
        Objects.requireNonNull(context, "context must be provided");
        verifyResourceExistsInCallingPackage(context, resourceId, resourceType);
        this.mResourceId = resourceId;
        this.mPackageName = context.getResources().getResourcePackageName(resourceId);
        this.mResourceName = context.getResources().getResourceName(resourceId);
        this.mResourceType = resourceType;
    }

    private ParcelableResource(int resourceId, String packageName, String resourceName, int resourceType) {
        this.mResourceId = resourceId;
        this.mPackageName = (String) Objects.requireNonNull(packageName);
        this.mResourceName = (String) Objects.requireNonNull(resourceName);
        this.mResourceType = resourceType;
    }

    private static void verifyResourceExistsInCallingPackage(Context context, int resourceId, int resourceType) throws IllegalStateException, IllegalArgumentException {
        switch (resourceType) {
            case 1:
                if (!hasDrawableInCallingPackage(context, resourceId)) {
                    throw new IllegalStateException(String.format("Drawable with id %d doesn't exist in the calling package %s", Integer.valueOf(resourceId), context.getPackageName()));
                }
                return;
            case 2:
                if (!hasStringInCallingPackage(context, resourceId)) {
                    throw new IllegalStateException(String.format("String with id %d doesn't exist in the calling package %s", Integer.valueOf(resourceId), context.getPackageName()));
                }
                return;
            default:
                throw new IllegalArgumentException("Unknown ResourceType: " + resourceType);
        }
    }

    private static boolean hasDrawableInCallingPackage(Context context, int resourceId) {
        try {
            return "drawable".equals(context.getResources().getResourceTypeName(resourceId));
        } catch (Resources.NotFoundException e) {
            return false;
        }
    }

    private static boolean hasStringInCallingPackage(Context context, int resourceId) {
        try {
            return "string".equals(context.getResources().getResourceTypeName(resourceId));
        } catch (Resources.NotFoundException e) {
            return false;
        }
    }

    public int getResourceId() {
        return this.mResourceId;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getResourceName() {
        return this.mResourceName;
    }

    public int getResourceType() {
        return this.mResourceType;
    }

    public Drawable getDrawable(Context context, int density, Supplier<Drawable> defaultDrawableLoader) {
        try {
            Resources resources = getAppResourcesWithCallersConfiguration(context);
            verifyResourceName(resources);
            return resources.getDrawableForDensity(this.mResourceId, density, context.getTheme());
        } catch (PackageManager.NameNotFoundException | RuntimeException e) {
            Slog.m95e(TAG, "Unable to load drawable resource " + this.mResourceName, e);
            return loadDefaultDrawable(defaultDrawableLoader);
        }
    }

    public String getString(Context context, Supplier<String> defaultStringLoader) {
        try {
            Resources resources = getAppResourcesWithCallersConfiguration(context);
            verifyResourceName(resources);
            return resources.getString(this.mResourceId);
        } catch (PackageManager.NameNotFoundException | RuntimeException e) {
            Slog.m95e(TAG, "Unable to load string resource " + this.mResourceName, e);
            return loadDefaultString(defaultStringLoader);
        }
    }

    public String getString(Context context, Supplier<String> defaultStringLoader, Object... formatArgs) {
        try {
            Resources resources = getAppResourcesWithCallersConfiguration(context);
            verifyResourceName(resources);
            String rawString = resources.getString(this.mResourceId);
            return String.format(context.getResources().getConfiguration().getLocales().get(0), rawString, formatArgs);
        } catch (PackageManager.NameNotFoundException | RuntimeException e) {
            Slog.m95e(TAG, "Unable to load string resource " + this.mResourceName, e);
            return loadDefaultString(defaultStringLoader);
        }
    }

    private Resources getAppResourcesWithCallersConfiguration(Context context) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo ai = pm.getApplicationInfo(this.mPackageName, 9216);
        return pm.getResourcesForApplication(ai, context.getResources().getConfiguration());
    }

    private void verifyResourceName(Resources resources) throws IllegalStateException {
        String name = resources.getResourceName(this.mResourceId);
        if (!this.mResourceName.equals(name)) {
            throw new IllegalStateException(String.format("Current resource name %s for resource id %d has changed from the previously stored resource name %s.", name, Integer.valueOf(this.mResourceId), this.mResourceName));
        }
    }

    public static Drawable loadDefaultDrawable(Supplier<Drawable> defaultDrawableLoader) {
        Objects.requireNonNull(defaultDrawableLoader, "defaultDrawableLoader can't be null");
        return defaultDrawableLoader.get();
    }

    public static String loadDefaultString(Supplier<String> defaultStringLoader) {
        Objects.requireNonNull(defaultStringLoader, "defaultStringLoader can't be null");
        return defaultStringLoader.get();
    }

    public void writeToXmlFile(TypedXmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.attributeInt(null, ATTR_RESOURCE_ID, this.mResourceId);
        xmlSerializer.attribute(null, ATTR_PACKAGE_NAME, this.mPackageName);
        xmlSerializer.attribute(null, ATTR_RESOURCE_NAME, this.mResourceName);
        xmlSerializer.attributeInt(null, ATTR_RESOURCE_TYPE, this.mResourceType);
    }

    public static ParcelableResource createFromXml(TypedXmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        int resourceId = xmlPullParser.getAttributeInt(null, ATTR_RESOURCE_ID);
        String packageName = xmlPullParser.getAttributeValue(null, ATTR_PACKAGE_NAME);
        String resourceName = xmlPullParser.getAttributeValue(null, ATTR_RESOURCE_NAME);
        int resourceType = xmlPullParser.getAttributeInt(null, ATTR_RESOURCE_TYPE);
        return new ParcelableResource(resourceId, packageName, resourceName, resourceType);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParcelableResource other = (ParcelableResource) o;
        if (this.mResourceId == other.mResourceId && this.mPackageName.equals(other.mPackageName) && this.mResourceName.equals(other.mResourceName) && this.mResourceType == other.mResourceType) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mResourceId), this.mPackageName, this.mResourceName, Integer.valueOf(this.mResourceType));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mResourceId);
        dest.writeString(this.mPackageName);
        dest.writeString(this.mResourceName);
        dest.writeInt(this.mResourceType);
    }
}
