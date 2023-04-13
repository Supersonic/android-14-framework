package android.provider;

import android.util.Base64;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.util.Preconditions;
import java.util.Collections;
import java.util.List;
@Deprecated
/* loaded from: classes3.dex */
public final class FontRequest {
    private final List<List<byte[]>> mCertificates;
    private final String mIdentifier;
    private final String mProviderAuthority;
    private final String mProviderPackage;
    private final String mQuery;

    public FontRequest(String providerAuthority, String providerPackage, String query) {
        String str = (String) Preconditions.checkNotNull(providerAuthority);
        this.mProviderAuthority = str;
        String str2 = (String) Preconditions.checkNotNull(query);
        this.mQuery = str2;
        String str3 = (String) Preconditions.checkNotNull(providerPackage);
        this.mProviderPackage = str3;
        this.mCertificates = Collections.emptyList();
        this.mIdentifier = str + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + str3 + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + str2;
    }

    public FontRequest(String providerAuthority, String providerPackage, String query, List<List<byte[]>> certificates) {
        String str = (String) Preconditions.checkNotNull(providerAuthority);
        this.mProviderAuthority = str;
        String str2 = (String) Preconditions.checkNotNull(providerPackage);
        this.mProviderPackage = str2;
        String str3 = (String) Preconditions.checkNotNull(query);
        this.mQuery = str3;
        this.mCertificates = (List) Preconditions.checkNotNull(certificates);
        this.mIdentifier = str + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + str2 + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + str3;
    }

    public String getProviderAuthority() {
        return this.mProviderAuthority;
    }

    public String getProviderPackage() {
        return this.mProviderPackage;
    }

    public String getQuery() {
        return this.mQuery;
    }

    public List<List<byte[]>> getCertificates() {
        return this.mCertificates;
    }

    public String getIdentifier() {
        return this.mIdentifier;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FontRequest {mProviderAuthority: " + this.mProviderAuthority + ", mProviderPackage: " + this.mProviderPackage + ", mQuery: " + this.mQuery + ", mCertificates:");
        for (int i = 0; i < this.mCertificates.size(); i++) {
            builder.append(" [");
            List<byte[]> set = this.mCertificates.get(i);
            for (int j = 0; j < set.size(); j++) {
                builder.append(" \"");
                byte[] array = set.get(j);
                builder.append(Base64.encodeToString(array, 0));
                builder.append("\"");
            }
            builder.append(" ]");
        }
        builder.append("}");
        return builder.toString();
    }
}
