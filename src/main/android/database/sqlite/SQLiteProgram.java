package android.database.sqlite;

import android.database.DatabaseUtils;
import android.p008os.CancellationSignal;
import java.util.Arrays;
/* loaded from: classes.dex */
public abstract class SQLiteProgram extends SQLiteClosable {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private final Object[] mBindArgs;
    private final String[] mColumnNames;
    private final SQLiteDatabase mDatabase;
    private final int mNumParameters;
    private final boolean mReadOnly;
    private final String mSql;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SQLiteProgram(SQLiteDatabase db, String sql, Object[] bindArgs, CancellationSignal cancellationSignalForPrepare) {
        this.mDatabase = db;
        String trim = sql.trim();
        this.mSql = trim;
        int n = DatabaseUtils.getSqlStatementType(trim);
        switch (n) {
            case 4:
            case 5:
            case 6:
                this.mReadOnly = false;
                this.mColumnNames = EMPTY_STRING_ARRAY;
                this.mNumParameters = 0;
                break;
            default:
                boolean assumeReadOnly = n == 1;
                SQLiteStatementInfo info = new SQLiteStatementInfo();
                db.getThreadSession().prepare(trim, db.getThreadDefaultConnectionFlags(assumeReadOnly), cancellationSignalForPrepare, info);
                this.mReadOnly = info.readOnly;
                this.mColumnNames = info.columnNames;
                this.mNumParameters = info.numParameters;
                break;
        }
        if (bindArgs != null && bindArgs.length > this.mNumParameters) {
            throw new IllegalArgumentException("Too many bind arguments.  " + bindArgs.length + " arguments were provided but the statement needs " + this.mNumParameters + " arguments.");
        }
        int i = this.mNumParameters;
        if (i != 0) {
            Object[] objArr = new Object[i];
            this.mBindArgs = objArr;
            if (bindArgs != null) {
                System.arraycopy(bindArgs, 0, objArr, 0, bindArgs.length);
                return;
            }
            return;
        }
        this.mBindArgs = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final SQLiteDatabase getDatabase() {
        return this.mDatabase;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String getSql() {
        return this.mSql;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Object[] getBindArgs() {
        return this.mBindArgs;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String[] getColumnNames() {
        return this.mColumnNames;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final SQLiteSession getSession() {
        return this.mDatabase.getThreadSession();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int getConnectionFlags() {
        return this.mDatabase.getThreadDefaultConnectionFlags(this.mReadOnly);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void onCorruption() {
        this.mDatabase.onCorruption();
    }

    @Deprecated
    public final int getUniqueId() {
        return -1;
    }

    public void bindNull(int index) {
        bind(index, null);
    }

    public void bindLong(int index, long value) {
        bind(index, Long.valueOf(value));
    }

    public void bindDouble(int index, double value) {
        bind(index, Double.valueOf(value));
    }

    public void bindString(int index, String value) {
        if (value == null) {
            throw new IllegalArgumentException("the bind value at index " + index + " is null");
        }
        bind(index, value);
    }

    public void bindBlob(int index, byte[] value) {
        if (value == null) {
            throw new IllegalArgumentException("the bind value at index " + index + " is null");
        }
        bind(index, value);
    }

    public void clearBindings() {
        Object[] objArr = this.mBindArgs;
        if (objArr != null) {
            Arrays.fill(objArr, (Object) null);
        }
    }

    public void bindAllArgsAsStrings(String[] bindArgs) {
        if (bindArgs != null) {
            for (int i = bindArgs.length; i != 0; i--) {
                bindString(i, bindArgs[i - 1]);
            }
        }
    }

    @Override // android.database.sqlite.SQLiteClosable
    protected void onAllReferencesReleased() {
        clearBindings();
    }

    private void bind(int index, Object value) {
        if (index < 1 || index > this.mNumParameters) {
            throw new IllegalArgumentException("Cannot bind argument at index " + index + " because the index is out of range.  The statement has " + this.mNumParameters + " parameters.");
        }
        this.mBindArgs[index - 1] = value;
    }
}
