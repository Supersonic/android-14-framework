package com.android.server.textclassifier;

import android.net.Uri;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
@VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
/* loaded from: classes2.dex */
public final class IconsUriHelper {
    public static final Supplier<String> DEFAULT_ID_SUPPLIER = new Supplier() { // from class: com.android.server.textclassifier.IconsUriHelper$$ExternalSyntheticLambda0
        @Override // java.util.function.Supplier
        public final Object get() {
            String lambda$static$0;
            lambda$static$0 = IconsUriHelper.lambda$static$0();
            return lambda$static$0;
        }
    };
    public static final IconsUriHelper sSingleton = new IconsUriHelper(null);
    public final Supplier<String> mIdSupplier;
    @GuardedBy({"mPackageIds"})
    public final Map<String, String> mPackageIds;

    public static /* synthetic */ String lambda$static$0() {
        return UUID.randomUUID().toString();
    }

    public IconsUriHelper(Supplier<String> supplier) {
        ArrayMap arrayMap = new ArrayMap();
        this.mPackageIds = arrayMap;
        this.mIdSupplier = supplier == null ? DEFAULT_ID_SUPPLIER : supplier;
        arrayMap.put(PackageManagerShellCommandDataLoader.PACKAGE, PackageManagerShellCommandDataLoader.PACKAGE);
    }

    public static IconsUriHelper getInstance() {
        return sSingleton;
    }

    public Uri getContentUri(String str, int i) {
        Uri build;
        Objects.requireNonNull(str);
        synchronized (this.mPackageIds) {
            if (!this.mPackageIds.containsKey(str)) {
                this.mPackageIds.put(str, this.mIdSupplier.get());
            }
            build = new Uri.Builder().scheme("content").authority("com.android.textclassifier.icons").path(this.mPackageIds.get(str)).appendPath(Integer.toString(i)).build();
        }
        return build;
    }

    public ResourceInfo getResourceInfo(Uri uri) {
        if ("content".equals(uri.getScheme()) && "com.android.textclassifier.icons".equals(uri.getAuthority())) {
            List<String> pathSegments = uri.getPathSegments();
            try {
            } catch (Exception e) {
                Log.v("IconsUriHelper", "Could not get resource info. Reason: " + e.getMessage());
            }
            synchronized (this.mPackageIds) {
                String str = pathSegments.get(0);
                int parseInt = Integer.parseInt(pathSegments.get(1));
                for (String str2 : this.mPackageIds.keySet()) {
                    if (str.equals(this.mPackageIds.get(str2))) {
                        return new ResourceInfo(str2, parseInt);
                    }
                }
                return null;
            }
        }
        return null;
    }

    /* loaded from: classes2.dex */
    public static final class ResourceInfo {

        /* renamed from: id */
        public final int f1155id;
        public final String packageName;

        public ResourceInfo(String str, int i) {
            Objects.requireNonNull(str);
            this.packageName = str;
            this.f1155id = i;
        }
    }
}
