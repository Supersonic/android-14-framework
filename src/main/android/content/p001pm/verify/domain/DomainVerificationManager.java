package android.content.p001pm.verify.domain;

import android.annotation.SystemApi;
import android.content.Context;
import android.content.p001pm.PackageManager;
import android.p008os.RemoteException;
import android.p008os.ServiceSpecificException;
import com.android.internal.util.CollectionUtils;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.ToIntFunction;
/* renamed from: android.content.pm.verify.domain.DomainVerificationManager */
/* loaded from: classes.dex */
public final class DomainVerificationManager {
    @SystemApi
    public static final int ERROR_DOMAIN_SET_ID_INVALID = 1;
    @SystemApi
    public static final int ERROR_UNABLE_TO_APPROVE = 3;
    @SystemApi
    public static final int ERROR_UNKNOWN_DOMAIN = 2;
    @SystemApi
    public static final String EXTRA_VERIFICATION_REQUEST = "android.content.pm.verify.domain.extra.VERIFICATION_REQUEST";
    public static final int INTERNAL_ERROR_NAME_NOT_FOUND = 1;
    @SystemApi
    public static final int STATUS_OK = 0;
    private final Context mContext;
    private final IDomainVerificationManager mDomainVerificationManager;

    /* renamed from: android.content.pm.verify.domain.DomainVerificationManager$Error */
    /* loaded from: classes.dex */
    public @interface Error {
    }

    public DomainVerificationManager(Context context, IDomainVerificationManager domainVerificationManager) {
        this.mContext = context;
        this.mDomainVerificationManager = domainVerificationManager;
    }

    @SystemApi
    public List<String> queryValidVerificationPackageNames() {
        try {
            return this.mDomainVerificationManager.queryValidVerificationPackageNames();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public DomainVerificationInfo getDomainVerificationInfo(String packageName) throws PackageManager.NameNotFoundException {
        try {
            return this.mDomainVerificationManager.getDomainVerificationInfo(packageName);
        } catch (Exception e) {
            Exception converted = rethrow(e, packageName);
            if (converted instanceof PackageManager.NameNotFoundException) {
                throw ((PackageManager.NameNotFoundException) converted);
            }
            if (converted instanceof RuntimeException) {
                throw ((RuntimeException) converted);
            }
            throw new RuntimeException(converted);
        }
    }

    @SystemApi
    public int setDomainVerificationStatus(UUID domainSetId, Set<String> domains, int state) throws PackageManager.NameNotFoundException {
        validateInput(domainSetId, domains);
        try {
            return this.mDomainVerificationManager.setDomainVerificationStatus(domainSetId.toString(), new DomainSet(domains), state);
        } catch (Exception e) {
            Exception converted = rethrow(e, null);
            if (converted instanceof PackageManager.NameNotFoundException) {
                throw ((PackageManager.NameNotFoundException) converted);
            }
            if (converted instanceof RuntimeException) {
                throw ((RuntimeException) converted);
            }
            throw new RuntimeException(converted);
        }
    }

    @SystemApi
    public void setDomainVerificationLinkHandlingAllowed(String packageName, boolean allowed) throws PackageManager.NameNotFoundException {
        try {
            this.mDomainVerificationManager.setDomainVerificationLinkHandlingAllowed(packageName, allowed, this.mContext.getUserId());
        } catch (Exception e) {
            Exception converted = rethrow(e, null);
            if (converted instanceof PackageManager.NameNotFoundException) {
                throw ((PackageManager.NameNotFoundException) converted);
            }
            if (converted instanceof RuntimeException) {
                throw ((RuntimeException) converted);
            }
            throw new RuntimeException(converted);
        }
    }

    @SystemApi
    public int setDomainVerificationUserSelection(UUID domainSetId, Set<String> domains, boolean enabled) throws PackageManager.NameNotFoundException {
        validateInput(domainSetId, domains);
        try {
            return this.mDomainVerificationManager.setDomainVerificationUserSelection(domainSetId.toString(), new DomainSet(domains), enabled, this.mContext.getUserId());
        } catch (Exception e) {
            Exception converted = rethrow(e, null);
            if (converted instanceof PackageManager.NameNotFoundException) {
                throw ((PackageManager.NameNotFoundException) converted);
            }
            if (converted instanceof RuntimeException) {
                throw ((RuntimeException) converted);
            }
            throw new RuntimeException(converted);
        }
    }

    public DomainVerificationUserState getDomainVerificationUserState(String packageName) throws PackageManager.NameNotFoundException {
        try {
            return this.mDomainVerificationManager.getDomainVerificationUserState(packageName, this.mContext.getUserId());
        } catch (Exception e) {
            Exception converted = rethrow(e, packageName);
            if (converted instanceof PackageManager.NameNotFoundException) {
                throw ((PackageManager.NameNotFoundException) converted);
            }
            if (converted instanceof RuntimeException) {
                throw ((RuntimeException) converted);
            }
            throw new RuntimeException(converted);
        }
    }

    @SystemApi
    public SortedSet<DomainOwner> getOwnersForDomain(String domain) {
        try {
            Objects.requireNonNull(domain);
            final List<DomainOwner> orderedList = this.mDomainVerificationManager.getOwnersForDomain(domain, this.mContext.getUserId());
            Objects.requireNonNull(orderedList);
            SortedSet<DomainOwner> set = new TreeSet<>(Comparator.comparingInt(new ToIntFunction() { // from class: android.content.pm.verify.domain.DomainVerificationManager$$ExternalSyntheticLambda0
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    return orderedList.indexOf((DomainOwner) obj);
                }
            }));
            set.addAll(orderedList);
            return set;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private Exception rethrow(Exception exception, String packageName) {
        if (exception instanceof ServiceSpecificException) {
            int serviceSpecificErrorCode = ((ServiceSpecificException) exception).errorCode;
            if (packageName == null) {
                packageName = exception.getMessage();
            }
            if (serviceSpecificErrorCode == 1) {
                return new PackageManager.NameNotFoundException(packageName);
            }
            return exception;
        } else if (exception instanceof RemoteException) {
            return ((RemoteException) exception).rethrowFromSystemServer();
        } else {
            return exception;
        }
    }

    private void validateInput(UUID domainSetId, Set<String> domains) {
        if (domainSetId == null) {
            throw new IllegalArgumentException("domainSetId cannot be null");
        }
        if (CollectionUtils.isEmpty(domains)) {
            throw new IllegalArgumentException("Provided domain set cannot be empty");
        }
    }
}
