package android.test;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.DatabaseUtils;
import android.test.mock.MockContentResolver;
import android.test.mock.MockContext;
@Deprecated
/* loaded from: classes.dex */
public abstract class ProviderTestCase<T extends ContentProvider> extends InstrumentationTestCase {
    private T mProvider;
    String mProviderAuthority;
    Class<T> mProviderClass;
    private IsolatedContext mProviderContext;
    private MockContentResolver mResolver;

    public ProviderTestCase(Class<T> providerClass, String providerAuthority) {
        this.mProviderClass = providerClass;
        this.mProviderAuthority = providerAuthority;
    }

    public T getProvider() {
        return this.mProvider;
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.mResolver = new MockContentResolver();
        RenamingDelegatingContext targetContextWrapper = new RenamingDelegatingContext(new MockContext(), getInstrumentation().getTargetContext(), "test.");
        IsolatedContext isolatedContext = new IsolatedContext(this.mResolver, targetContextWrapper);
        this.mProviderContext = isolatedContext;
        this.mProvider = (T) ProviderTestCase2.createProviderForTest(isolatedContext, this.mProviderClass, this.mProviderAuthority);
        this.mResolver.addProvider(this.mProviderAuthority, getProvider());
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

    public static <T extends ContentProvider> ContentResolver newResolverWithContentProviderFromSql(Context targetContext, Class<T> providerClass, String authority, String databaseName, int databaseVersion, String sql) throws IllegalAccessException, InstantiationException {
        MockContentResolver resolver = new MockContentResolver();
        RenamingDelegatingContext targetContextWrapper = new RenamingDelegatingContext(new MockContext(), targetContext, "test.");
        Context context = new IsolatedContext(resolver, targetContextWrapper);
        DatabaseUtils.createDbFromSqlStatements(context, databaseName, databaseVersion, sql);
        resolver.addProvider(authority, ProviderTestCase2.createProviderForTest(context, providerClass, authority));
        return resolver;
    }
}
