package android.security.identity;

import android.content.Context;
import android.content.p001pm.FeatureInfo;
import android.content.p001pm.PackageManager;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.ServiceSpecificException;
import android.security.GenerateRkpKey;
import android.security.identity.ICredentialStoreFactory;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public class CredstoreIdentityCredentialStore extends IdentityCredentialStore {
    private static final String TAG = "CredstoreIdentityCredentialStore";
    private static CredstoreIdentityCredentialStore sInstanceDefault = null;
    private static CredstoreIdentityCredentialStore sInstanceDirectAccess = null;
    private Context mContext;
    private int mFeatureVersion;
    private ICredentialStore mStore;

    static int getFeatureVersion(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_IDENTITY_CREDENTIAL_HARDWARE)) {
            FeatureInfo[] infos = pm.getSystemAvailableFeatures();
            for (FeatureInfo info : infos) {
                if (info.name.equals(PackageManager.FEATURE_IDENTITY_CREDENTIAL_HARDWARE)) {
                    return info.version;
                }
            }
            return 202009;
        }
        return 202009;
    }

    private CredstoreIdentityCredentialStore(Context context, ICredentialStore store) {
        this.mContext = null;
        this.mStore = null;
        this.mContext = context;
        this.mStore = store;
        this.mFeatureVersion = getFeatureVersion(context);
    }

    static CredstoreIdentityCredentialStore getInstanceForType(Context context, int credentialStoreType) {
        ICredentialStoreFactory storeFactory = ICredentialStoreFactory.Stub.asInterface(ServiceManager.getService("android.security.identity"));
        if (storeFactory == null) {
            return null;
        }
        try {
            ICredentialStore credStore = storeFactory.getCredentialStore(credentialStoreType);
            if (credStore == null) {
                return null;
            }
            return new CredstoreIdentityCredentialStore(context, credStore);
        } catch (RemoteException e) {
            throw new RuntimeException("Unexpected RemoteException ", e);
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 1) {
                return null;
            }
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        }
    }

    public static IdentityCredentialStore getInstance(Context context) {
        if (sInstanceDefault == null) {
            sInstanceDefault = getInstanceForType(context, 0);
        }
        return sInstanceDefault;
    }

    public static IdentityCredentialStore getDirectAccessInstance(Context context) {
        if (sInstanceDirectAccess == null) {
            sInstanceDirectAccess = getInstanceForType(context, 1);
        }
        return sInstanceDirectAccess;
    }

    @Override // android.security.identity.IdentityCredentialStore
    public String[] getSupportedDocTypes() {
        try {
            SecurityHardwareInfoParcel info = this.mStore.getSecurityHardwareInfo();
            return info.supportedDocTypes;
        } catch (RemoteException e) {
            throw new RuntimeException("Unexpected RemoteException ", e);
        } catch (ServiceSpecificException e2) {
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        }
    }

    @Override // android.security.identity.IdentityCredentialStore
    public WritableIdentityCredential createCredential(String credentialName, String docType) throws AlreadyPersonalizedException, DocTypeNotSupportedException {
        try {
            try {
                IWritableCredential wc = this.mStore.createCredential(credentialName, docType);
                try {
                    GenerateRkpKey keyGen = new GenerateRkpKey(this.mContext);
                    keyGen.notifyKeyGenerated(1);
                    keyGen.notifyKeyGenerated(2);
                } catch (RemoteException e) {
                }
                return new CredstoreWritableIdentityCredential(this.mContext, credentialName, docType, wc);
            } catch (RemoteException e2) {
                throw new RuntimeException("Unexpected RemoteException ", e2);
            }
        } catch (ServiceSpecificException e3) {
            if (e3.errorCode == 2) {
                throw new AlreadyPersonalizedException(e3.getMessage(), e3);
            }
            if (e3.errorCode == 8) {
                throw new DocTypeNotSupportedException(e3.getMessage(), e3);
            }
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e3.errorCode, e3);
        }
    }

    @Override // android.security.identity.IdentityCredentialStore
    public IdentityCredential getCredentialByName(String credentialName, int cipherSuite) throws CipherSuiteNotSupportedException {
        try {
            ICredential credstoreCredential = this.mStore.getCredentialByName(credentialName, cipherSuite);
            return new CredstoreIdentityCredential(this.mContext, credentialName, cipherSuite, credstoreCredential, null, this.mFeatureVersion);
        } catch (RemoteException e) {
            throw new RuntimeException("Unexpected RemoteException ", e);
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 3) {
                return null;
            }
            if (e2.errorCode == 4) {
                throw new CipherSuiteNotSupportedException(e2.getMessage(), e2);
            }
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        }
    }

    @Override // android.security.identity.IdentityCredentialStore
    public byte[] deleteCredentialByName(String credentialName) {
        ICredential credstoreCredential = null;
        try {
            try {
                credstoreCredential = this.mStore.getCredentialByName(credentialName, 1);
            } catch (RemoteException e) {
                throw new RuntimeException("Unexpected RemoteException ", e);
            }
        } catch (ServiceSpecificException e2) {
            try {
                if (e2.errorCode == 3) {
                    return null;
                }
            } catch (ServiceSpecificException e3) {
                throw new RuntimeException("Unexpected ServiceSpecificException with code " + e3.errorCode, e3);
            }
        }
        byte[] proofOfDeletion = credstoreCredential.deleteCredential();
        return proofOfDeletion;
    }

    @Override // android.security.identity.IdentityCredentialStore
    public PresentationSession createPresentationSession(int cipherSuite) throws CipherSuiteNotSupportedException {
        try {
            ISession credstoreSession = this.mStore.createPresentationSession(cipherSuite);
            return new CredstorePresentationSession(this.mContext, cipherSuite, this, credstoreSession, this.mFeatureVersion);
        } catch (RemoteException e) {
            throw new RuntimeException("Unexpected RemoteException ", e);
        } catch (ServiceSpecificException e2) {
            if (e2.errorCode == 4) {
                throw new CipherSuiteNotSupportedException(e2.getMessage(), e2);
            }
            throw new RuntimeException("Unexpected ServiceSpecificException with code " + e2.errorCode, e2);
        }
    }
}
