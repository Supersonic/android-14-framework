package com.android.commands.content;

import android.app.ActivityManager;
import android.app.ContentProviderHolder;
import android.app.IActivityManager;
import android.content.AttributionSource;
import android.content.ContentValues;
import android.content.IContentProvider;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.IBinder;
import android.os.ICancellationSignal;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.text.TextUtils;
import android.util.Pair;
import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class Content {
    private static final String USAGE = "usage: adb shell content [subcommand] [options]\n\nusage: adb shell content insert --uri <URI> [--user <USER_ID>] --bind <BINDING> [--bind <BINDING>...] [--extra <BINDING>...]\n  <URI> a content provider URI.\n  <BINDING> binds a typed value to a column and is formatted:\n  <COLUMN_NAME>:<TYPE>:<COLUMN_VALUE> where:\n  <TYPE> specifies data type such as:\n  b - boolean, s - string, i - integer, l - long, f - float, d - double, n - null\n  Note: Omit the value for passing an empty string, e.g column:s:\n  Example:\n  # Add \"new_setting\" secure setting with value \"new_value\".\n  adb shell content insert --uri content://settings/secure --bind name:s:new_setting --bind value:s:new_value\n\nusage: adb shell content update --uri <URI> [--user <USER_ID>] [--where <WHERE>] [--extra <BINDING>...]\n  <WHERE> is a SQL style where clause in quotes (You have to escape single quotes - see example below).\n  Example:\n  # Change \"new_setting\" secure setting to \"newer_value\".\n  adb shell content update --uri content://settings/secure --bind value:s:newer_value --where \"name='new_setting'\"\n\nusage: adb shell content delete --uri <URI> [--user <USER_ID>] --bind <BINDING> [--bind <BINDING>...] [--where <WHERE>] [--extra <BINDING>...]\n  Example:\n  # Remove \"new_setting\" secure setting.\n  adb shell content delete --uri content://settings/secure --where \"name='new_setting'\"\n\nusage: adb shell content query --uri <URI> [--user <USER_ID>] [--projection <PROJECTION>] [--where <WHERE>] [--sort <SORT_ORDER>] [--extra <BINDING>...]\n  <PROJECTION> is a list of colon separated column names and is formatted:\n  <COLUMN_NAME>[:<COLUMN_NAME>...]\n  <SORT_ORDER> is the order in which rows in the result should be sorted.\n  Example:\n  # Select \"name\" and \"value\" columns from secure settings where \"name\" is equal to \"new_setting\" and sort the result by name in ascending order.\n  adb shell content query --uri content://settings/secure --projection name:value --where \"name='new_setting'\" --sort \"name ASC\"\n\nusage: adb shell content call --uri <URI> --method <METHOD> [--arg <ARG>]\n       [--extra <BINDING> ...]\n  <METHOD> is the name of a provider-defined method\n  <ARG> is an optional string argument\n  <BINDING> is like --bind above, typed data of the form <KEY>:{b,s,i,l,f,d}:<VAL>\n\nusage: adb shell content read --uri <URI> [--user <USER_ID>]\n  Example:\n  adb shell 'content read --uri content://settings/system/ringtone_cache' > host.ogg\n\nusage: adb shell content write --uri <URI> [--user <USER_ID>]\n  Example:\n  adb shell 'content write --uri content://settings/system/ringtone_cache' < host.ogg\n\nusage: adb shell content gettype --uri <URI> [--user <USER_ID>]\n  Example:\n  adb shell content gettype --uri content://media/internal/audio/media/\n\n";

    /* loaded from: classes.dex */
    private static class Parser {
        private static final String ARGUMENT_ARG = "--arg";
        private static final String ARGUMENT_BIND = "--bind";
        private static final String ARGUMENT_CALL = "call";
        private static final String ARGUMENT_DELETE = "delete";
        private static final String ARGUMENT_EXTRA = "--extra";
        private static final String ARGUMENT_GET_TYPE = "gettype";
        private static final String ARGUMENT_INSERT = "insert";
        private static final String ARGUMENT_METHOD = "--method";
        private static final String ARGUMENT_PREFIX = "--";
        private static final String ARGUMENT_PROJECTION = "--projection";
        private static final String ARGUMENT_QUERY = "query";
        private static final String ARGUMENT_READ = "read";
        private static final String ARGUMENT_SORT = "--sort";
        private static final String ARGUMENT_UPDATE = "update";
        private static final String ARGUMENT_URI = "--uri";
        private static final String ARGUMENT_USER = "--user";
        private static final String ARGUMENT_WHERE = "--where";
        private static final String ARGUMENT_WRITE = "write";
        private static final String COLON = ":";
        private static final String TYPE_BOOLEAN = "b";
        private static final String TYPE_DOUBLE = "d";
        private static final String TYPE_FLOAT = "f";
        private static final String TYPE_INTEGER = "i";
        private static final String TYPE_LONG = "l";
        private static final String TYPE_NULL = "n";
        private static final String TYPE_STRING = "s";
        private final Tokenizer mTokenizer;

        public Parser(String[] args) {
            this.mTokenizer = new Tokenizer(args);
        }

        public Command parseCommand() {
            try {
                String operation = this.mTokenizer.nextArg();
                if (ARGUMENT_INSERT.equals(operation)) {
                    return parseInsertCommand();
                }
                if (ARGUMENT_DELETE.equals(operation)) {
                    return parseDeleteCommand();
                }
                if (ARGUMENT_UPDATE.equals(operation)) {
                    return parseUpdateCommand();
                }
                if (ARGUMENT_QUERY.equals(operation)) {
                    return parseQueryCommand();
                }
                if (ARGUMENT_CALL.equals(operation)) {
                    return parseCallCommand();
                }
                if (ARGUMENT_READ.equals(operation)) {
                    return parseReadCommand();
                }
                if (ARGUMENT_WRITE.equals(operation)) {
                    return parseWriteCommand();
                }
                if (ARGUMENT_GET_TYPE.equals(operation)) {
                    return parseGetTypeCommand();
                }
                throw new IllegalArgumentException("Unsupported operation: " + operation);
            } catch (IllegalArgumentException iae) {
                System.out.println(Content.USAGE);
                System.out.println("[ERROR] " + iae.getMessage());
                return null;
            }
        }

        private InsertCommand parseInsertCommand() {
            Uri uri = null;
            int userId = 0;
            ContentValues values = new ContentValues();
            Bundle extras = new Bundle();
            while (true) {
                String argument = this.mTokenizer.nextArg();
                if (argument != null) {
                    if (ARGUMENT_URI.equals(argument)) {
                        uri = Uri.parse(argumentValueRequired(argument));
                    } else if (ARGUMENT_USER.equals(argument)) {
                        userId = Integer.parseInt(argumentValueRequired(argument));
                    } else if (ARGUMENT_BIND.equals(argument)) {
                        parseBindValue(values);
                    } else if (ARGUMENT_EXTRA.equals(argument)) {
                        parseBindValue(extras);
                    } else {
                        throw new IllegalArgumentException("Unsupported argument: " + argument);
                    }
                } else if (uri == null) {
                    throw new IllegalArgumentException("Content provider URI not specified. Did you specify --uri argument?");
                } else {
                    if (values.size() == 0) {
                        throw new IllegalArgumentException("Bindings not specified. Did you specify --bind argument(s)?");
                    }
                    return new InsertCommand(uri, userId, values, extras);
                }
            }
        }

        private DeleteCommand parseDeleteCommand() {
            Uri uri = null;
            int userId = 0;
            Bundle extras = new Bundle();
            while (true) {
                String argument = this.mTokenizer.nextArg();
                if (argument != null) {
                    if (ARGUMENT_URI.equals(argument)) {
                        uri = Uri.parse(argumentValueRequired(argument));
                    } else if (ARGUMENT_USER.equals(argument)) {
                        userId = Integer.parseInt(argumentValueRequired(argument));
                    } else if (ARGUMENT_WHERE.equals(argument)) {
                        extras.putString("android:query-arg-sql-selection", argumentValueRequired(argument));
                    } else if (ARGUMENT_EXTRA.equals(argument)) {
                        parseBindValue(extras);
                    } else {
                        throw new IllegalArgumentException("Unsupported argument: " + argument);
                    }
                } else if (uri == null) {
                    throw new IllegalArgumentException("Content provider URI not specified. Did you specify --uri argument?");
                } else {
                    return new DeleteCommand(uri, userId, extras);
                }
            }
        }

        private UpdateCommand parseUpdateCommand() {
            Uri uri = null;
            int userId = 0;
            ContentValues values = new ContentValues();
            Bundle extras = new Bundle();
            while (true) {
                String argument = this.mTokenizer.nextArg();
                if (argument != null) {
                    if (ARGUMENT_URI.equals(argument)) {
                        uri = Uri.parse(argumentValueRequired(argument));
                    } else if (ARGUMENT_USER.equals(argument)) {
                        userId = Integer.parseInt(argumentValueRequired(argument));
                    } else if (ARGUMENT_WHERE.equals(argument)) {
                        extras.putString("android:query-arg-sql-selection", argumentValueRequired(argument));
                    } else if (ARGUMENT_BIND.equals(argument)) {
                        parseBindValue(values);
                    } else if (ARGUMENT_EXTRA.equals(argument)) {
                        parseBindValue(extras);
                    } else {
                        throw new IllegalArgumentException("Unsupported argument: " + argument);
                    }
                } else if (uri == null) {
                    throw new IllegalArgumentException("Content provider URI not specified. Did you specify --uri argument?");
                } else {
                    if (values.size() == 0) {
                        throw new IllegalArgumentException("Bindings not specified. Did you specify --bind argument(s)?");
                    }
                    return new UpdateCommand(uri, userId, values, extras);
                }
            }
        }

        public CallCommand parseCallCommand() {
            String method = null;
            int userId = 0;
            String arg = null;
            Uri uri = null;
            Bundle extras = new Bundle();
            while (true) {
                String argument = this.mTokenizer.nextArg();
                if (argument != null) {
                    if (ARGUMENT_URI.equals(argument)) {
                        uri = Uri.parse(argumentValueRequired(argument));
                    } else if (ARGUMENT_USER.equals(argument)) {
                        userId = Integer.parseInt(argumentValueRequired(argument));
                    } else if (ARGUMENT_METHOD.equals(argument)) {
                        method = argumentValueRequired(argument);
                    } else if (ARGUMENT_ARG.equals(argument)) {
                        arg = argumentValueRequired(argument);
                    } else if (ARGUMENT_EXTRA.equals(argument)) {
                        parseBindValue(extras);
                    } else {
                        throw new IllegalArgumentException("Unsupported argument: " + argument);
                    }
                } else if (uri == null) {
                    throw new IllegalArgumentException("Content provider URI not specified. Did you specify --uri argument?");
                } else {
                    if (method == null) {
                        throw new IllegalArgumentException("Content provider method not specified.");
                    }
                    return new CallCommand(uri, userId, method, arg, extras);
                }
            }
        }

        private GetTypeCommand parseGetTypeCommand() {
            Uri uri = null;
            int userId = 0;
            while (true) {
                String argument = this.mTokenizer.nextArg();
                if (argument != null) {
                    if (ARGUMENT_URI.equals(argument)) {
                        uri = Uri.parse(argumentValueRequired(argument));
                    } else if (ARGUMENT_USER.equals(argument)) {
                        userId = Integer.parseInt(argumentValueRequired(argument));
                    } else {
                        throw new IllegalArgumentException("Unsupported argument: " + argument);
                    }
                } else if (uri == null) {
                    throw new IllegalArgumentException("Content provider URI not specified. Did you specify --uri argument?");
                } else {
                    return new GetTypeCommand(uri, userId);
                }
            }
        }

        private ReadCommand parseReadCommand() {
            Uri uri = null;
            int userId = 0;
            while (true) {
                String argument = this.mTokenizer.nextArg();
                if (argument != null) {
                    if (ARGUMENT_URI.equals(argument)) {
                        uri = Uri.parse(argumentValueRequired(argument));
                    } else if (ARGUMENT_USER.equals(argument)) {
                        userId = Integer.parseInt(argumentValueRequired(argument));
                    } else {
                        throw new IllegalArgumentException("Unsupported argument: " + argument);
                    }
                } else if (uri == null) {
                    throw new IllegalArgumentException("Content provider URI not specified. Did you specify --uri argument?");
                } else {
                    return new ReadCommand(uri, userId);
                }
            }
        }

        private WriteCommand parseWriteCommand() {
            Uri uri = null;
            int userId = 0;
            while (true) {
                String argument = this.mTokenizer.nextArg();
                if (argument != null) {
                    if (ARGUMENT_URI.equals(argument)) {
                        uri = Uri.parse(argumentValueRequired(argument));
                    } else if (ARGUMENT_USER.equals(argument)) {
                        userId = Integer.parseInt(argumentValueRequired(argument));
                    } else {
                        throw new IllegalArgumentException("Unsupported argument: " + argument);
                    }
                } else if (uri == null) {
                    throw new IllegalArgumentException("Content provider URI not specified. Did you specify --uri argument?");
                } else {
                    return new WriteCommand(uri, userId);
                }
            }
        }

        public QueryCommand parseQueryCommand() {
            Uri uri = null;
            int userId = 0;
            String[] projection = null;
            Bundle extras = new Bundle();
            while (true) {
                String argument = this.mTokenizer.nextArg();
                if (argument != null) {
                    if (ARGUMENT_URI.equals(argument)) {
                        uri = Uri.parse(argumentValueRequired(argument));
                    } else if (ARGUMENT_USER.equals(argument)) {
                        userId = Integer.parseInt(argumentValueRequired(argument));
                    } else if (ARGUMENT_WHERE.equals(argument)) {
                        extras.putString("android:query-arg-sql-selection", argumentValueRequired(argument));
                    } else if (ARGUMENT_SORT.equals(argument)) {
                        extras.putString("android:query-arg-sql-sort-order", argumentValueRequired(argument));
                    } else if (ARGUMENT_PROJECTION.equals(argument)) {
                        projection = argumentValueRequired(argument).split("[\\s]*:[\\s]*");
                    } else if (ARGUMENT_EXTRA.equals(argument)) {
                        parseBindValue(extras);
                    } else {
                        throw new IllegalArgumentException("Unsupported argument: " + argument);
                    }
                } else if (uri == null) {
                    throw new IllegalArgumentException("Content provider URI not specified. Did you specify --uri argument?");
                } else {
                    return new QueryCommand(uri, userId, projection, extras);
                }
            }
        }

        private List<String> splitWithEscaping(String argument, char splitChar) {
            List<String> res = new ArrayList<>();
            StringBuilder cur = new StringBuilder();
            int i = 0;
            while (i < argument.length()) {
                char c = argument.charAt(i);
                if (c == '\\') {
                    i++;
                    if (i == argument.length()) {
                        throw new IllegalArgumentException("Invalid escaping");
                    }
                    cur.append(argument.charAt(i));
                } else if (c == splitChar) {
                    res.add(cur.toString());
                    cur.setLength(0);
                } else {
                    cur.append(c);
                }
                i++;
            }
            res.add(cur.toString());
            return res;
        }

        private Pair<String, Object> parseBindValue() {
            String argument = this.mTokenizer.nextArg();
            if (TextUtils.isEmpty(argument)) {
                throw new IllegalArgumentException("Binding not well formed: " + argument);
            }
            List<String> split = splitWithEscaping(argument, COLON.charAt(0));
            if (split.size() != 3) {
                throw new IllegalArgumentException("Binding not well formed: " + argument);
            }
            String column = split.get(0);
            String type = split.get(1);
            String value = split.get(2);
            if (TYPE_STRING.equals(type)) {
                return Pair.create(column, value);
            }
            if (TYPE_BOOLEAN.equalsIgnoreCase(type)) {
                return Pair.create(column, Boolean.valueOf(Boolean.parseBoolean(value)));
            }
            if (TYPE_INTEGER.equalsIgnoreCase(type)) {
                return Pair.create(column, Integer.valueOf(Integer.parseInt(value)));
            }
            if (TYPE_LONG.equalsIgnoreCase(type)) {
                return Pair.create(column, Long.valueOf(Long.parseLong(value)));
            }
            if (TYPE_FLOAT.equalsIgnoreCase(type)) {
                return Pair.create(column, Float.valueOf(Float.parseFloat(value)));
            }
            if (TYPE_DOUBLE.equalsIgnoreCase(type)) {
                return Pair.create(column, Double.valueOf(Double.parseDouble(value)));
            }
            if (TYPE_NULL.equalsIgnoreCase(type)) {
                return Pair.create(column, null);
            }
            throw new IllegalArgumentException("Unsupported type: " + type);
        }

        private void parseBindValue(ContentValues values) {
            Pair<String, Object> columnValue = parseBindValue();
            values.putObject((String) columnValue.first, columnValue.second);
        }

        private void parseBindValue(Bundle extras) {
            Pair<String, Object> columnValue = parseBindValue();
            extras.putObject((String) columnValue.first, columnValue.second);
        }

        private String argumentValueRequired(String argument) {
            String value = this.mTokenizer.nextArg();
            if (TextUtils.isEmpty(value) || value.startsWith(ARGUMENT_PREFIX)) {
                throw new IllegalArgumentException("No value for argument: " + argument);
            }
            return value;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Tokenizer {
        private final String[] mArgs;
        private int mNextArg;

        public Tokenizer(String[] args) {
            this.mArgs = args;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String nextArg() {
            int i = this.mNextArg;
            String[] strArr = this.mArgs;
            if (i < strArr.length) {
                this.mNextArg = i + 1;
                return strArr[i];
            }
            return null;
        }
    }

    /* loaded from: classes.dex */
    private static abstract class Command {
        final Uri mUri;
        final int mUserId;

        protected abstract void onExecute(IContentProvider iContentProvider) throws Exception;

        public Command(Uri uri, int userId) {
            this.mUri = uri;
            this.mUserId = userId;
        }

        public final void execute() {
            String providerName = this.mUri.getAuthority();
            try {
                IActivityManager activityManager = ActivityManager.getService();
                IBinder token = new Binder();
                ContentProviderHolder holder = activityManager.getContentProviderExternal(providerName, this.mUserId, token, "*cmd*");
                if (holder == null) {
                    throw new IllegalStateException("Could not find provider: " + providerName);
                }
                IContentProvider provider = holder.provider;
                onExecute(provider);
                if (provider != null) {
                    activityManager.removeContentProviderExternalAsUser(providerName, token, this.mUserId);
                }
            } catch (Exception e) {
                System.err.println("Error while accessing provider:" + providerName);
                e.printStackTrace();
            }
        }

        public static String resolveCallingPackage() {
            switch (Process.myUid()) {
                case 0:
                    return "root";
                case 2000:
                    return "com.android.shell";
                default:
                    return null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class InsertCommand extends Command {
        final ContentValues mContentValues;
        final Bundle mExtras;

        public InsertCommand(Uri uri, int userId, ContentValues contentValues, Bundle extras) {
            super(uri, userId);
            this.mContentValues = contentValues;
            this.mExtras = extras;
        }

        @Override // com.android.commands.content.Content.Command
        public void onExecute(IContentProvider provider) throws Exception {
            provider.insert(new AttributionSource(Binder.getCallingUid(), resolveCallingPackage(), null), this.mUri, this.mContentValues, this.mExtras);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class DeleteCommand extends Command {
        final Bundle mExtras;

        public DeleteCommand(Uri uri, int userId, Bundle extras) {
            super(uri, userId);
            this.mExtras = extras;
        }

        @Override // com.android.commands.content.Content.Command
        public void onExecute(IContentProvider provider) throws Exception {
            provider.delete(new AttributionSource(Binder.getCallingUid(), resolveCallingPackage(), null), this.mUri, this.mExtras);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CallCommand extends Command {
        final String mArg;
        final Bundle mExtras;
        final String mMethod;

        public CallCommand(Uri uri, int userId, String method, String arg, Bundle extras) {
            super(uri, userId);
            this.mMethod = method;
            this.mArg = arg;
            this.mExtras = extras;
        }

        @Override // com.android.commands.content.Content.Command
        public void onExecute(IContentProvider provider) throws Exception {
            Bundle result = provider.call(new AttributionSource(Binder.getCallingUid(), resolveCallingPackage(), null), this.mUri.getAuthority(), this.mMethod, this.mArg, this.mExtras);
            if (result != null) {
                result.size();
            }
            System.out.println("Result: " + result);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class GetTypeCommand extends Command {
        public GetTypeCommand(Uri uri, int userId) {
            super(uri, userId);
        }

        @Override // com.android.commands.content.Content.Command
        public void onExecute(IContentProvider provider) throws Exception {
            String type = provider.getType(this.mUri);
            System.out.println("Result: " + type);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ReadCommand extends Command {
        public ReadCommand(Uri uri, int userId) {
            super(uri, userId);
        }

        @Override // com.android.commands.content.Content.Command
        public void onExecute(IContentProvider provider) throws Exception {
            ParcelFileDescriptor fd = provider.openFile(new AttributionSource(Binder.getCallingUid(), resolveCallingPackage(), null), this.mUri, "r", (ICancellationSignal) null);
            try {
                FileUtils.copy(fd.getFileDescriptor(), FileDescriptor.out);
                if (fd != null) {
                    fd.close();
                }
            } catch (Throwable th) {
                if (fd != null) {
                    try {
                        fd.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class WriteCommand extends Command {
        public WriteCommand(Uri uri, int userId) {
            super(uri, userId);
        }

        @Override // com.android.commands.content.Content.Command
        public void onExecute(IContentProvider provider) throws Exception {
            ParcelFileDescriptor fd = provider.openFile(new AttributionSource(Binder.getCallingUid(), resolveCallingPackage(), null), this.mUri, "w", (ICancellationSignal) null);
            try {
                FileUtils.copy(FileDescriptor.in, fd.getFileDescriptor());
                if (fd != null) {
                    fd.close();
                }
            } catch (Throwable th) {
                if (fd != null) {
                    try {
                        fd.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class QueryCommand extends Command {
        final Bundle mExtras;
        final String[] mProjection;

        public QueryCommand(Uri uri, int userId, String[] projection, Bundle extras) {
            super(uri, userId);
            this.mProjection = projection;
            this.mExtras = extras;
        }

        @Override // com.android.commands.content.Content.Command
        public void onExecute(IContentProvider provider) throws Exception {
            Cursor cursor = provider.query(new AttributionSource(Binder.getCallingUid(), resolveCallingPackage(), null), this.mUri, this.mProjection, this.mExtras, (ICancellationSignal) null);
            if (cursor == null) {
                System.out.println("No result found.");
                return;
            }
            try {
                if (!cursor.moveToFirst()) {
                    System.out.println("No result found.");
                } else {
                    int rowIndex = 0;
                    StringBuilder builder = new StringBuilder();
                    do {
                        builder.setLength(0);
                        builder.append("Row: ").append(rowIndex).append(" ");
                        rowIndex++;
                        int columnCount = cursor.getColumnCount();
                        for (int i = 0; i < columnCount; i++) {
                            if (i > 0) {
                                builder.append(", ");
                            }
                            String columnName = cursor.getColumnName(i);
                            String columnValue = null;
                            int columnIndex = cursor.getColumnIndex(columnName);
                            int type = cursor.getType(columnIndex);
                            switch (type) {
                                case 0:
                                    columnValue = "NULL";
                                    break;
                                case 1:
                                    columnValue = String.valueOf(cursor.getLong(columnIndex));
                                    break;
                                case 2:
                                    columnValue = String.valueOf(cursor.getFloat(columnIndex));
                                    break;
                                case 3:
                                    columnValue = cursor.getString(columnIndex);
                                    break;
                                case 4:
                                    columnValue = "BLOB";
                                    break;
                            }
                            builder.append(columnName).append("=").append(columnValue);
                        }
                        System.out.println(builder);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class UpdateCommand extends Command {
        final Bundle mExtras;
        final ContentValues mValues;

        public UpdateCommand(Uri uri, int userId, ContentValues values, Bundle extras) {
            super(uri, userId);
            this.mValues = values;
            this.mExtras = extras;
        }

        @Override // com.android.commands.content.Content.Command
        public void onExecute(IContentProvider provider) throws Exception {
            provider.update(new AttributionSource(Binder.getCallingUid(), resolveCallingPackage(), null), this.mUri, this.mValues, this.mExtras);
        }
    }

    public static void main(String[] args) {
        Parser parser = new Parser(args);
        Command command = parser.parseCommand();
        if (command != null) {
            command.execute();
        }
    }
}
