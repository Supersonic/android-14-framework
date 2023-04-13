package android.database.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Build;
import android.p008os.CancellationSignal;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import libcore.util.EmptyArray;
/* loaded from: classes.dex */
public class SQLiteQueryBuilder {
    private static final int STRICT_COLUMNS = 2;
    private static final int STRICT_GRAMMAR = 4;
    private static final int STRICT_PARENTHESES = 1;
    private static final String TAG = "SQLiteQueryBuilder";
    private static final Pattern sAggregationPattern = Pattern.compile("(?i)(AVG|COUNT|MAX|MIN|SUM|TOTAL|GROUP_CONCAT)\\((.+)\\)");
    private int mStrictFlags;
    private Map<String, String> mProjectionMap = null;
    private Collection<Pattern> mProjectionGreylist = null;
    private String mTables = "";
    private StringBuilder mWhereClause = null;
    private boolean mDistinct = false;
    private SQLiteDatabase.CursorFactory mFactory = null;

    public void setDistinct(boolean distinct) {
        this.mDistinct = distinct;
    }

    public boolean isDistinct() {
        return this.mDistinct;
    }

    public String getTables() {
        return this.mTables;
    }

    public void setTables(String inTables) {
        this.mTables = inTables;
    }

    public void appendWhere(CharSequence inWhere) {
        if (this.mWhereClause == null) {
            this.mWhereClause = new StringBuilder(inWhere.length() + 16);
        }
        this.mWhereClause.append(inWhere);
    }

    public void appendWhereEscapeString(String inWhere) {
        if (this.mWhereClause == null) {
            this.mWhereClause = new StringBuilder(inWhere.length() + 16);
        }
        DatabaseUtils.appendEscapedSQLString(this.mWhereClause, inWhere);
    }

    public void appendWhereStandalone(CharSequence inWhere) {
        if (this.mWhereClause == null) {
            this.mWhereClause = new StringBuilder(inWhere.length() + 16);
        }
        if (this.mWhereClause.length() > 0) {
            this.mWhereClause.append(" AND ");
        }
        this.mWhereClause.append('(').append(inWhere).append(')');
    }

    public void setProjectionMap(Map<String, String> columnMap) {
        this.mProjectionMap = columnMap;
    }

    public Map<String, String> getProjectionMap() {
        return this.mProjectionMap;
    }

    public void setProjectionGreylist(Collection<Pattern> projectionGreylist) {
        this.mProjectionGreylist = projectionGreylist;
    }

    public Collection<Pattern> getProjectionGreylist() {
        return this.mProjectionGreylist;
    }

    @Deprecated
    public void setProjectionAggregationAllowed(boolean projectionAggregationAllowed) {
    }

    @Deprecated
    public boolean isProjectionAggregationAllowed() {
        return true;
    }

    public void setCursorFactory(SQLiteDatabase.CursorFactory factory) {
        this.mFactory = factory;
    }

    public SQLiteDatabase.CursorFactory getCursorFactory() {
        return this.mFactory;
    }

    public void setStrict(boolean strict) {
        if (strict) {
            this.mStrictFlags |= 1;
        } else {
            this.mStrictFlags &= -2;
        }
    }

    public boolean isStrict() {
        return (this.mStrictFlags & 1) != 0;
    }

    public void setStrictColumns(boolean strictColumns) {
        if (strictColumns) {
            this.mStrictFlags |= 2;
        } else {
            this.mStrictFlags &= -3;
        }
    }

    public boolean isStrictColumns() {
        return (this.mStrictFlags & 2) != 0;
    }

    public void setStrictGrammar(boolean strictGrammar) {
        if (strictGrammar) {
            this.mStrictFlags |= 4;
        } else {
            this.mStrictFlags &= -5;
        }
    }

    public boolean isStrictGrammar() {
        return (this.mStrictFlags & 4) != 0;
    }

    public static String buildQueryString(boolean distinct, String tables, String[] columns, String where, String groupBy, String having, String orderBy, String limit) {
        if (TextUtils.isEmpty(groupBy) && !TextUtils.isEmpty(having)) {
            throw new IllegalArgumentException("HAVING clauses are only permitted when using a groupBy clause");
        }
        StringBuilder query = new StringBuilder(120);
        query.append("SELECT ");
        if (distinct) {
            query.append("DISTINCT ");
        }
        if (columns != null && columns.length != 0) {
            appendColumns(query, columns);
        } else {
            query.append("* ");
        }
        query.append("FROM ");
        query.append(tables);
        appendClause(query, " WHERE ", where);
        appendClause(query, " GROUP BY ", groupBy);
        appendClause(query, " HAVING ", having);
        appendClause(query, " ORDER BY ", orderBy);
        appendClause(query, " LIMIT ", limit);
        return query.toString();
    }

    private static void appendClause(StringBuilder s, String name, String clause) {
        if (!TextUtils.isEmpty(clause)) {
            s.append(name);
            s.append(clause);
        }
    }

    public static void appendColumns(StringBuilder s, String[] columns) {
        int n = columns.length;
        for (int i = 0; i < n; i++) {
            String column = columns[i];
            if (column != null) {
                if (i > 0) {
                    s.append(", ");
                }
                s.append(column);
            }
        }
        s.append(' ');
    }

    public Cursor query(SQLiteDatabase db, String[] projectionIn, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder) {
        return query(db, projectionIn, selection, selectionArgs, groupBy, having, sortOrder, null, null);
    }

    public Cursor query(SQLiteDatabase db, String[] projectionIn, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limit) {
        return query(db, projectionIn, selection, selectionArgs, groupBy, having, sortOrder, limit, null);
    }

    public Cursor query(SQLiteDatabase db, String[] projectionIn, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limit, CancellationSignal cancellationSignal) {
        String sql;
        if (this.mTables == null) {
            return null;
        }
        String unwrappedSql = buildQuery(projectionIn, selection, groupBy, having, sortOrder, limit);
        if (isStrictColumns()) {
            enforceStrictColumns(projectionIn);
        }
        if (isStrictGrammar()) {
            enforceStrictGrammar(selection, groupBy, having, sortOrder, limit);
        }
        if (isStrict()) {
            db.validateSql(unwrappedSql, cancellationSignal);
            sql = buildQuery(projectionIn, wrap(selection), groupBy, wrap(having), sortOrder, limit);
        } else {
            sql = unwrappedSql;
        }
        if (Log.isLoggable(TAG, 3)) {
            if (Build.IS_DEBUGGABLE) {
                Log.m112d(TAG, sql + " with args " + Arrays.toString(selectionArgs));
            } else {
                Log.m112d(TAG, sql);
            }
        }
        return db.rawQueryWithFactory(this.mFactory, sql, selectionArgs, SQLiteDatabase.findEditTable(this.mTables), cancellationSignal);
    }

    public long insert(SQLiteDatabase db, ContentValues values) {
        Objects.requireNonNull(this.mTables, "No tables defined");
        Objects.requireNonNull(db, "No database defined");
        Objects.requireNonNull(values, "No values defined");
        if (isStrictColumns()) {
            enforceStrictColumns(values);
        }
        String sql = buildInsert(values);
        ArrayMap<String, Object> rawValues = values.getValues();
        int valuesLength = rawValues.size();
        Object[] sqlArgs = new Object[valuesLength];
        for (int i = 0; i < sqlArgs.length; i++) {
            sqlArgs[i] = rawValues.valueAt(i);
        }
        if (Log.isLoggable(TAG, 3)) {
            if (Build.IS_DEBUGGABLE) {
                Log.m112d(TAG, sql + " with args " + Arrays.toString(sqlArgs));
            } else {
                Log.m112d(TAG, sql);
            }
        }
        return DatabaseUtils.executeInsert(db, sql, sqlArgs);
    }

    public int update(SQLiteDatabase db, ContentValues values, String selection, String[] selectionArgs) {
        String sql;
        Objects.requireNonNull(this.mTables, "No tables defined");
        Objects.requireNonNull(db, "No database defined");
        Objects.requireNonNull(values, "No values defined");
        String unwrappedSql = buildUpdate(values, selection);
        if (isStrictColumns()) {
            enforceStrictColumns(values);
        }
        if (isStrictGrammar()) {
            enforceStrictGrammar(selection, null, null, null, null);
        }
        if (isStrict()) {
            db.validateSql(unwrappedSql, null);
            sql = buildUpdate(values, wrap(selection));
        } else {
            sql = unwrappedSql;
        }
        if (selectionArgs == null) {
            selectionArgs = EmptyArray.STRING;
        }
        ArrayMap<String, Object> rawValues = values.getValues();
        int valuesLength = rawValues.size();
        Object[] sqlArgs = new Object[selectionArgs.length + valuesLength];
        for (int i = 0; i < sqlArgs.length; i++) {
            if (i < valuesLength) {
                sqlArgs[i] = rawValues.valueAt(i);
            } else {
                sqlArgs[i] = selectionArgs[i - valuesLength];
            }
        }
        if (Log.isLoggable(TAG, 3)) {
            if (Build.IS_DEBUGGABLE) {
                Log.m112d(TAG, sql + " with args " + Arrays.toString(sqlArgs));
            } else {
                Log.m112d(TAG, sql);
            }
        }
        return DatabaseUtils.executeUpdateDelete(db, sql, sqlArgs);
    }

    public int delete(SQLiteDatabase db, String selection, String[] selectionArgs) {
        String sql;
        Objects.requireNonNull(this.mTables, "No tables defined");
        Objects.requireNonNull(db, "No database defined");
        String unwrappedSql = buildDelete(selection);
        if (isStrictGrammar()) {
            enforceStrictGrammar(selection, null, null, null, null);
        }
        if (isStrict()) {
            db.validateSql(unwrappedSql, null);
            sql = buildDelete(wrap(selection));
        } else {
            sql = unwrappedSql;
        }
        if (Log.isLoggable(TAG, 3)) {
            if (Build.IS_DEBUGGABLE) {
                Log.m112d(TAG, sql + " with args " + Arrays.toString(selectionArgs));
            } else {
                Log.m112d(TAG, sql);
            }
        }
        return DatabaseUtils.executeUpdateDelete(db, sql, selectionArgs);
    }

    private void enforceStrictColumns(String[] projection) {
        Objects.requireNonNull(this.mProjectionMap, "No projection map defined");
        computeProjection(projection);
    }

    private void enforceStrictColumns(ContentValues values) {
        Objects.requireNonNull(this.mProjectionMap, "No projection map defined");
        ArrayMap<String, Object> rawValues = values.getValues();
        for (int i = 0; i < rawValues.size(); i++) {
            String column = rawValues.keyAt(i);
            if (!this.mProjectionMap.containsKey(column)) {
                throw new IllegalArgumentException("Invalid column " + column);
            }
        }
    }

    private void enforceStrictGrammar(String selection, String groupBy, String having, String sortOrder, String limit) {
        SQLiteTokenizer.tokenize(selection, 0, new Consumer() { // from class: android.database.sqlite.SQLiteQueryBuilder$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SQLiteQueryBuilder.this.enforceStrictToken((String) obj);
            }
        });
        SQLiteTokenizer.tokenize(groupBy, 0, new Consumer() { // from class: android.database.sqlite.SQLiteQueryBuilder$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SQLiteQueryBuilder.this.enforceStrictToken((String) obj);
            }
        });
        SQLiteTokenizer.tokenize(having, 0, new Consumer() { // from class: android.database.sqlite.SQLiteQueryBuilder$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SQLiteQueryBuilder.this.enforceStrictToken((String) obj);
            }
        });
        SQLiteTokenizer.tokenize(sortOrder, 0, new Consumer() { // from class: android.database.sqlite.SQLiteQueryBuilder$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SQLiteQueryBuilder.this.enforceStrictToken((String) obj);
            }
        });
        SQLiteTokenizer.tokenize(limit, 0, new Consumer() { // from class: android.database.sqlite.SQLiteQueryBuilder$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                SQLiteQueryBuilder.this.enforceStrictToken((String) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public void enforceStrictToken(String token) {
        char c;
        if (TextUtils.isEmpty(token) || isTableOrColumn(token) || SQLiteTokenizer.isFunction(token) || SQLiteTokenizer.isType(token)) {
            return;
        }
        boolean isAllowedKeyword = SQLiteTokenizer.isKeyword(token);
        String upperCase = token.toUpperCase(Locale.US);
        switch (upperCase.hashCode()) {
            case -1852692228:
                if (upperCase.equals("SELECT")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1770483422:
                if (upperCase.equals("VALUES")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -1734422544:
                if (upperCase.equals("WINDOW")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 2166698:
                if (upperCase.equals("FROM")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 68091487:
                if (upperCase.equals("GROUP")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 72438683:
                if (upperCase.equals("LIMIT")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 75468590:
                if (upperCase.equals("ORDER")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 82560199:
                if (upperCase.equals("WHERE")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 2123962405:
                if (upperCase.equals("HAVING")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case '\b':
                isAllowedKeyword = false;
                break;
        }
        if (!isAllowedKeyword) {
            throw new IllegalArgumentException("Invalid token " + token);
        }
    }

    public String buildQuery(String[] projectionIn, String selection, String groupBy, String having, String sortOrder, String limit) {
        String[] projection = computeProjection(projectionIn);
        String where = computeWhere(selection);
        return buildQueryString(this.mDistinct, this.mTables, projection, where, groupBy, having, sortOrder, limit);
    }

    @Deprecated
    public String buildQuery(String[] projectionIn, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limit) {
        return buildQuery(projectionIn, selection, groupBy, having, sortOrder, limit);
    }

    public String buildInsert(ContentValues values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Empty values");
        }
        StringBuilder sql = new StringBuilder(120);
        sql.append("INSERT INTO ");
        sql.append(SQLiteDatabase.findEditTable(this.mTables));
        sql.append(" (");
        ArrayMap<String, Object> rawValues = values.getValues();
        for (int i = 0; i < rawValues.size(); i++) {
            if (i > 0) {
                sql.append(',');
            }
            sql.append(rawValues.keyAt(i));
        }
        sql.append(") VALUES (");
        for (int i2 = 0; i2 < rawValues.size(); i2++) {
            if (i2 > 0) {
                sql.append(',');
            }
            sql.append('?');
        }
        sql.append(NavigationBarInflaterView.KEY_CODE_END);
        return sql.toString();
    }

    public String buildUpdate(ContentValues values, String selection) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Empty values");
        }
        StringBuilder sql = new StringBuilder(120);
        sql.append("UPDATE ");
        sql.append(SQLiteDatabase.findEditTable(this.mTables));
        sql.append(" SET ");
        ArrayMap<String, Object> rawValues = values.getValues();
        for (int i = 0; i < rawValues.size(); i++) {
            if (i > 0) {
                sql.append(',');
            }
            sql.append(rawValues.keyAt(i));
            sql.append("=?");
        }
        String where = computeWhere(selection);
        appendClause(sql, " WHERE ", where);
        return sql.toString();
    }

    public String buildDelete(String selection) {
        StringBuilder sql = new StringBuilder(120);
        sql.append("DELETE FROM ");
        sql.append(SQLiteDatabase.findEditTable(this.mTables));
        String where = computeWhere(selection);
        appendClause(sql, " WHERE ", where);
        return sql.toString();
    }

    public String buildUnionSubQuery(String typeDiscriminatorColumn, String[] unionColumns, Set<String> columnsPresentInTable, int computedColumnsOffset, String typeDiscriminatorValue, String selection, String groupBy, String having) {
        int unionColumnsCount = unionColumns.length;
        String[] projectionIn = new String[unionColumnsCount];
        for (int i = 0; i < unionColumnsCount; i++) {
            String unionColumn = unionColumns[i];
            if (unionColumn.equals(typeDiscriminatorColumn)) {
                projectionIn[i] = "'" + typeDiscriminatorValue + "' AS " + typeDiscriminatorColumn;
            } else {
                if (i > computedColumnsOffset && !columnsPresentInTable.contains(unionColumn)) {
                    projectionIn[i] = "NULL AS " + unionColumn;
                }
                projectionIn[i] = unionColumn;
            }
        }
        return buildQuery(projectionIn, selection, groupBy, having, null, null);
    }

    @Deprecated
    public String buildUnionSubQuery(String typeDiscriminatorColumn, String[] unionColumns, Set<String> columnsPresentInTable, int computedColumnsOffset, String typeDiscriminatorValue, String selection, String[] selectionArgs, String groupBy, String having) {
        return buildUnionSubQuery(typeDiscriminatorColumn, unionColumns, columnsPresentInTable, computedColumnsOffset, typeDiscriminatorValue, selection, groupBy, having);
    }

    public String buildUnionQuery(String[] subQueries, String sortOrder, String limit) {
        StringBuilder query = new StringBuilder(128);
        int subQueryCount = subQueries.length;
        String unionOperator = this.mDistinct ? " UNION " : " UNION ALL ";
        for (int i = 0; i < subQueryCount; i++) {
            if (i > 0) {
                query.append(unionOperator);
            }
            query.append(subQueries[i]);
        }
        appendClause(query, " ORDER BY ", sortOrder);
        appendClause(query, " LIMIT ", limit);
        return query.toString();
    }

    private static String maybeWithOperator(String operator, String column) {
        if (operator != null) {
            return operator + NavigationBarInflaterView.KEY_CODE_START + column + NavigationBarInflaterView.KEY_CODE_END;
        }
        return column;
    }

    public String[] computeProjection(String[] projectionIn) {
        if (!ArrayUtils.isEmpty(projectionIn)) {
            String[] projectionOut = new String[projectionIn.length];
            for (int i = 0; i < projectionIn.length; i++) {
                projectionOut[i] = computeSingleProjectionOrThrow(projectionIn[i]);
            }
            return projectionOut;
        }
        Map<String, String> map = this.mProjectionMap;
        if (map != null) {
            Set<Map.Entry<String, String>> entrySet = map.entrySet();
            String[] projection = new String[entrySet.size()];
            int i2 = 0;
            for (Map.Entry<String, String> entry : entrySet) {
                if (!entry.getKey().equals(BaseColumns._COUNT)) {
                    projection[i2] = entry.getValue();
                    i2++;
                }
            }
            return projection;
        }
        return null;
    }

    private String computeSingleProjectionOrThrow(String userColumn) {
        String column = computeSingleProjection(userColumn);
        if (column != null) {
            return column;
        }
        throw new IllegalArgumentException("Invalid column " + userColumn);
    }

    private String computeSingleProjection(String userColumn) {
        Map<String, String> map = this.mProjectionMap;
        if (map == null) {
            return userColumn;
        }
        String operator = null;
        String column = map.get(userColumn);
        if (column == null) {
            Matcher matcher = sAggregationPattern.matcher(userColumn);
            if (matcher.matches()) {
                operator = matcher.group(1);
                userColumn = matcher.group(2);
                column = this.mProjectionMap.get(userColumn);
            }
        }
        if (column != null) {
            return maybeWithOperator(operator, column);
        }
        if (this.mStrictFlags == 0 && (userColumn.contains(" AS ") || userColumn.contains(" as "))) {
            return maybeWithOperator(operator, userColumn);
        }
        Collection<Pattern> collection = this.mProjectionGreylist;
        if (collection != null) {
            boolean match = false;
            Iterator<Pattern> it = collection.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Pattern p = it.next();
                if (p.matcher(userColumn).matches()) {
                    match = true;
                    break;
                }
            }
            if (match) {
                Log.m104w(TAG, "Allowing abusive custom column: " + userColumn);
                return maybeWithOperator(operator, userColumn);
            }
            return null;
        }
        return null;
    }

    private boolean isTableOrColumn(String token) {
        return this.mTables.equals(token) || computeSingleProjection(token) != null;
    }

    public String computeWhere(String selection) {
        boolean hasInternal = !TextUtils.isEmpty(this.mWhereClause);
        boolean hasExternal = !TextUtils.isEmpty(selection);
        if (hasInternal || hasExternal) {
            StringBuilder where = new StringBuilder();
            if (hasInternal) {
                where.append('(').append((CharSequence) this.mWhereClause).append(')');
            }
            if (hasInternal && hasExternal) {
                where.append(" AND ");
            }
            if (hasExternal) {
                where.append('(').append(selection).append(')');
            }
            return where.toString();
        }
        return null;
    }

    private String wrap(String arg) {
        if (TextUtils.isEmpty(arg)) {
            return arg;
        }
        return NavigationBarInflaterView.KEY_CODE_START + arg + NavigationBarInflaterView.KEY_CODE_END;
    }
}
