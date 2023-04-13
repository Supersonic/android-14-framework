package android.test;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.content.res.Resources;
import android.database.DatabaseUtils;
import android.test.mock.MockContentProvider;
import android.test.mock.MockContentResolver;
import android.test.mock.MockContext;
import java.io.File;
/* loaded from: classes.dex */
public abstract class ProviderTestCase2<T extends ContentProvider> extends AndroidTestCase {
    private T mProvider;
    String mProviderAuthority;
    Class<T> mProviderClass;
    private IsolatedContext mProviderContext;
    private MockContentResolver mResolver;

    /* loaded from: classes.dex */
    private class MockContext2 extends MockContext {
        private MockContext2() {
        }

        public Resources getResources() {
            return ProviderTestCase2.this.getContext().getResources();
        }

        public File getDir(String name, int mode) {
            return ProviderTestCase2.this.getContext().getDir("mockcontext2_" + name, mode);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public Context getApplicationContext() {
            return this;
        }
    }

    public ProviderTestCase2(Class<T> providerClass, String providerAuthority) {
        this.mProviderClass = providerClass;
        this.mProviderAuthority = providerAuthority;
    }

    public T getProvider() {
        return this.mProvider;
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.mResolver = new MockContentResolver();
        RenamingDelegatingContext targetContextWrapper = new RenamingDelegatingContext(new MockContext2(), getContext(), "test.");
        IsolatedContext isolatedContext = new IsolatedContext(this.mResolver, targetContextWrapper);
        this.mProviderContext = isolatedContext;
        this.mProvider = (T) createProviderForTest(isolatedContext, this.mProviderClass, this.mProviderAuthority);
        this.mResolver.addProvider(this.mProviderAuthority, getProvider());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T extends ContentProvider> T createProviderForTest(Context context, Class<T> providerClass, String authority) throws IllegalAccessException, InstantiationException {
        T instance = providerClass.newInstance();
        ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.authority = authority;
        MockContentProvider.attachInfoForTesting(instance, context, providerInfo);
        return instance;
    }

    protected void tearDown() throws Exception {
        this.mProvider.shutdown();
        super.tearDown();
    }

    public MockContentResolver getMockContentResolver() {
        return this.mResolver;
    }

    public IsolatedContext getMockContext() {
        return this.mProviderContext;
    }

    public static <T extends ContentProvider> ContentResolver newResolverWithContentProviderFromSql(Context targetContext, String filenamePrefix, Class<T> providerClass, String authority, String databaseName, int databaseVersion, String sql) throws IllegalAccessException, InstantiationException {
        MockContentResolver resolver = new MockContentResolver();
        RenamingDelegatingContext targetContextWrapper = new RenamingDelegatingContext(new MockContext(), targetContext, filenamePrefix);
        Context context = new IsolatedContext(resolver, targetContextWrapper);
        DatabaseUtils.createDbFromSqlStatements(context, databaseName, databaseVersion, sql);
        resolver.addProvider(authority, createProviderForTest(context, providerClass, authority));
        return resolver;
    }
}
