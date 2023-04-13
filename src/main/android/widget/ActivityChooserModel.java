package android.widget;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.ResolveInfo;
import android.database.DataSetObservable;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.AsyncTask;
import android.p008os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import com.android.internal.content.PackageMonitor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes4.dex */
public class ActivityChooserModel extends DataSetObservable {
    private static final String ATTRIBUTE_ACTIVITY = "activity";
    private static final String ATTRIBUTE_TIME = "time";
    private static final String ATTRIBUTE_WEIGHT = "weight";
    private static final boolean DEBUG = false;
    private static final int DEFAULT_ACTIVITY_INFLATION = 5;
    private static final float DEFAULT_HISTORICAL_RECORD_WEIGHT = 1.0f;
    public static final String DEFAULT_HISTORY_FILE_NAME = "activity_choser_model_history.xml";
    public static final int DEFAULT_HISTORY_MAX_LENGTH = 50;
    private static final String HISTORY_FILE_EXTENSION = ".xml";
    private static final int INVALID_INDEX = -1;
    private static final String TAG_HISTORICAL_RECORD = "historical-record";
    private static final String TAG_HISTORICAL_RECORDS = "historical-records";
    private OnChooseActivityListener mActivityChoserModelPolicy;
    private ActivitySorter mActivitySorter;
    private boolean mCanReadHistoricalData;
    private final Context mContext;
    private boolean mHistoricalRecordsChanged;
    private final String mHistoryFileName;
    private int mHistoryMaxSize;
    private Intent mIntent;
    private final PackageMonitor mPackageMonitor;
    private boolean mReadShareHistoryCalled;
    private boolean mReloadActivities;
    private static final String LOG_TAG = ActivityChooserModel.class.getSimpleName();
    private static final Object sRegistryLock = new Object();
    private static final Map<String, ActivityChooserModel> sDataModelRegistry = new HashMap();
    private final Object mInstanceLock = new Object();
    private final List<ActivityResolveInfo> mActivities = new ArrayList();
    private final List<HistoricalRecord> mHistoricalRecords = new ArrayList();

    /* loaded from: classes4.dex */
    public interface ActivityChooserModelClient {
        void setActivityChooserModel(ActivityChooserModel activityChooserModel);
    }

    /* loaded from: classes4.dex */
    public interface ActivitySorter {
        void sort(Intent intent, List<ActivityResolveInfo> list, List<HistoricalRecord> list2);
    }

    /* loaded from: classes4.dex */
    public interface OnChooseActivityListener {
        boolean onChooseActivity(ActivityChooserModel activityChooserModel, Intent intent);
    }

    public static ActivityChooserModel get(Context context, String historyFileName) {
        ActivityChooserModel dataModel;
        synchronized (sRegistryLock) {
            Map<String, ActivityChooserModel> map = sDataModelRegistry;
            dataModel = map.get(historyFileName);
            if (dataModel == null) {
                dataModel = new ActivityChooserModel(context, historyFileName);
                map.put(historyFileName, dataModel);
            }
        }
        return dataModel;
    }

    private ActivityChooserModel(Context context, String historyFileName) {
        DataModelPackageMonitor dataModelPackageMonitor = new DataModelPackageMonitor();
        this.mPackageMonitor = dataModelPackageMonitor;
        this.mActivitySorter = new DefaultSorter();
        this.mHistoryMaxSize = 50;
        this.mCanReadHistoricalData = true;
        this.mReadShareHistoryCalled = false;
        this.mHistoricalRecordsChanged = true;
        this.mReloadActivities = false;
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        if (!TextUtils.isEmpty(historyFileName) && !historyFileName.endsWith(HISTORY_FILE_EXTENSION)) {
            this.mHistoryFileName = historyFileName + HISTORY_FILE_EXTENSION;
        } else {
            this.mHistoryFileName = historyFileName;
        }
        dataModelPackageMonitor.register(applicationContext, null, true);
    }

    public void setIntent(Intent intent) {
        synchronized (this.mInstanceLock) {
            if (this.mIntent == intent) {
                return;
            }
            this.mIntent = intent;
            this.mReloadActivities = true;
            ensureConsistentState();
        }
    }

    public Intent getIntent() {
        Intent intent;
        synchronized (this.mInstanceLock) {
            intent = this.mIntent;
        }
        return intent;
    }

    public int getActivityCount() {
        int size;
        synchronized (this.mInstanceLock) {
            ensureConsistentState();
            size = this.mActivities.size();
        }
        return size;
    }

    public ResolveInfo getActivity(int index) {
        ResolveInfo resolveInfo;
        synchronized (this.mInstanceLock) {
            ensureConsistentState();
            resolveInfo = this.mActivities.get(index).resolveInfo;
        }
        return resolveInfo;
    }

    public int getActivityIndex(ResolveInfo activity) {
        synchronized (this.mInstanceLock) {
            ensureConsistentState();
            List<ActivityResolveInfo> activities = this.mActivities;
            int activityCount = activities.size();
            for (int i = 0; i < activityCount; i++) {
                ActivityResolveInfo currentActivity = activities.get(i);
                if (currentActivity.resolveInfo == activity) {
                    return i;
                }
            }
            return -1;
        }
    }

    public Intent chooseActivity(int index) {
        synchronized (this.mInstanceLock) {
            if (this.mIntent == null) {
                return null;
            }
            ensureConsistentState();
            ActivityResolveInfo chosenActivity = this.mActivities.get(index);
            ComponentName chosenName = new ComponentName(chosenActivity.resolveInfo.activityInfo.packageName, chosenActivity.resolveInfo.activityInfo.name);
            Intent choiceIntent = new Intent(this.mIntent);
            choiceIntent.setComponent(chosenName);
            if (this.mActivityChoserModelPolicy != null) {
                Intent choiceIntentCopy = new Intent(choiceIntent);
                boolean handled = this.mActivityChoserModelPolicy.onChooseActivity(this, choiceIntentCopy);
                if (handled) {
                    return null;
                }
            }
            HistoricalRecord historicalRecord = new HistoricalRecord(chosenName, System.currentTimeMillis(), 1.0f);
            addHisoricalRecord(historicalRecord);
            return choiceIntent;
        }
    }

    public void setOnChooseActivityListener(OnChooseActivityListener listener) {
        synchronized (this.mInstanceLock) {
            this.mActivityChoserModelPolicy = listener;
        }
    }

    public ResolveInfo getDefaultActivity() {
        synchronized (this.mInstanceLock) {
            ensureConsistentState();
            if (!this.mActivities.isEmpty()) {
                return this.mActivities.get(0).resolveInfo;
            }
            return null;
        }
    }

    public void setDefaultActivity(int index) {
        float weight;
        synchronized (this.mInstanceLock) {
            ensureConsistentState();
            ActivityResolveInfo newDefaultActivity = this.mActivities.get(index);
            ActivityResolveInfo oldDefaultActivity = this.mActivities.get(0);
            if (oldDefaultActivity != null) {
                weight = (oldDefaultActivity.weight - newDefaultActivity.weight) + 5.0f;
            } else {
                weight = 1.0f;
            }
            ComponentName defaultName = new ComponentName(newDefaultActivity.resolveInfo.activityInfo.packageName, newDefaultActivity.resolveInfo.activityInfo.name);
            HistoricalRecord historicalRecord = new HistoricalRecord(defaultName, System.currentTimeMillis(), weight);
            addHisoricalRecord(historicalRecord);
        }
    }

    private void persistHistoricalDataIfNeeded() {
        if (!this.mReadShareHistoryCalled) {
            throw new IllegalStateException("No preceding call to #readHistoricalData");
        }
        if (!this.mHistoricalRecordsChanged) {
            return;
        }
        this.mHistoricalRecordsChanged = false;
        if (!TextUtils.isEmpty(this.mHistoryFileName)) {
            new PersistHistoryAsyncTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new ArrayList(this.mHistoricalRecords), this.mHistoryFileName);
        }
    }

    public void setActivitySorter(ActivitySorter activitySorter) {
        synchronized (this.mInstanceLock) {
            if (this.mActivitySorter == activitySorter) {
                return;
            }
            this.mActivitySorter = activitySorter;
            if (sortActivitiesIfNeeded()) {
                notifyChanged();
            }
        }
    }

    public void setHistoryMaxSize(int historyMaxSize) {
        synchronized (this.mInstanceLock) {
            if (this.mHistoryMaxSize == historyMaxSize) {
                return;
            }
            this.mHistoryMaxSize = historyMaxSize;
            pruneExcessiveHistoricalRecordsIfNeeded();
            if (sortActivitiesIfNeeded()) {
                notifyChanged();
            }
        }
    }

    public int getHistoryMaxSize() {
        int i;
        synchronized (this.mInstanceLock) {
            i = this.mHistoryMaxSize;
        }
        return i;
    }

    public int getHistorySize() {
        int size;
        synchronized (this.mInstanceLock) {
            ensureConsistentState();
            size = this.mHistoricalRecords.size();
        }
        return size;
    }

    protected void finalize() throws Throwable {
        super.finalize();
        this.mPackageMonitor.unregister();
    }

    private void ensureConsistentState() {
        boolean stateChanged = loadActivitiesIfNeeded();
        boolean stateChanged2 = stateChanged | readHistoricalDataIfNeeded();
        pruneExcessiveHistoricalRecordsIfNeeded();
        if (stateChanged2) {
            sortActivitiesIfNeeded();
            notifyChanged();
        }
    }

    private boolean sortActivitiesIfNeeded() {
        if (this.mActivitySorter != null && this.mIntent != null && !this.mActivities.isEmpty() && !this.mHistoricalRecords.isEmpty()) {
            this.mActivitySorter.sort(this.mIntent, this.mActivities, Collections.unmodifiableList(this.mHistoricalRecords));
            return true;
        }
        return false;
    }

    private boolean loadActivitiesIfNeeded() {
        if (!this.mReloadActivities || this.mIntent == null) {
            return false;
        }
        this.mReloadActivities = false;
        this.mActivities.clear();
        List<ResolveInfo> resolveInfos = this.mContext.getPackageManager().queryIntentActivities(this.mIntent, 0);
        int resolveInfoCount = resolveInfos.size();
        for (int i = 0; i < resolveInfoCount; i++) {
            ResolveInfo resolveInfo = resolveInfos.get(i);
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (ActivityManager.checkComponentPermission(activityInfo.permission, Process.myUid(), activityInfo.applicationInfo.uid, activityInfo.exported) == 0) {
                this.mActivities.add(new ActivityResolveInfo(resolveInfo));
            }
        }
        return true;
    }

    private boolean readHistoricalDataIfNeeded() {
        if (this.mCanReadHistoricalData && this.mHistoricalRecordsChanged && !TextUtils.isEmpty(this.mHistoryFileName)) {
            this.mCanReadHistoricalData = false;
            this.mReadShareHistoryCalled = true;
            readHistoricalDataImpl();
            return true;
        }
        return false;
    }

    private boolean addHisoricalRecord(HistoricalRecord historicalRecord) {
        boolean added = this.mHistoricalRecords.add(historicalRecord);
        if (added) {
            this.mHistoricalRecordsChanged = true;
            pruneExcessiveHistoricalRecordsIfNeeded();
            persistHistoricalDataIfNeeded();
            sortActivitiesIfNeeded();
            notifyChanged();
        }
        return added;
    }

    private void pruneExcessiveHistoricalRecordsIfNeeded() {
        int pruneCount = this.mHistoricalRecords.size() - this.mHistoryMaxSize;
        if (pruneCount <= 0) {
            return;
        }
        this.mHistoricalRecordsChanged = true;
        for (int i = 0; i < pruneCount; i++) {
            this.mHistoricalRecords.remove(0);
        }
    }

    /* loaded from: classes4.dex */
    public static final class HistoricalRecord {
        public final ComponentName activity;
        public final long time;
        public final float weight;

        public HistoricalRecord(String activityName, long time, float weight) {
            this(ComponentName.unflattenFromString(activityName), time, weight);
        }

        public HistoricalRecord(ComponentName activityName, long time, float weight) {
            this.activity = activityName;
            this.time = time;
            this.weight = weight;
        }

        public int hashCode() {
            int i = 1 * 31;
            ComponentName componentName = this.activity;
            int result = i + (componentName == null ? 0 : componentName.hashCode());
            long j = this.time;
            return (((result * 31) + ((int) (j ^ (j >>> 32)))) * 31) + Float.floatToIntBits(this.weight);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            HistoricalRecord other = (HistoricalRecord) obj;
            ComponentName componentName = this.activity;
            if (componentName == null) {
                if (other.activity != null) {
                    return false;
                }
            } else if (!componentName.equals(other.activity)) {
                return false;
            }
            if (this.time == other.time && Float.floatToIntBits(this.weight) == Float.floatToIntBits(other.weight)) {
                return true;
            }
            return false;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(NavigationBarInflaterView.SIZE_MOD_START);
            builder.append("; activity:").append(this.activity);
            builder.append("; time:").append(this.time);
            builder.append("; weight:").append(new BigDecimal(this.weight));
            builder.append(NavigationBarInflaterView.SIZE_MOD_END);
            return builder.toString();
        }
    }

    /* loaded from: classes4.dex */
    public final class ActivityResolveInfo implements Comparable<ActivityResolveInfo> {
        public final ResolveInfo resolveInfo;
        public float weight;

        public ActivityResolveInfo(ResolveInfo resolveInfo) {
            this.resolveInfo = resolveInfo;
        }

        public int hashCode() {
            return Float.floatToIntBits(this.weight) + 31;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ActivityResolveInfo other = (ActivityResolveInfo) obj;
            if (Float.floatToIntBits(this.weight) == Float.floatToIntBits(other.weight)) {
                return true;
            }
            return false;
        }

        @Override // java.lang.Comparable
        public int compareTo(ActivityResolveInfo another) {
            return Float.floatToIntBits(another.weight) - Float.floatToIntBits(this.weight);
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(NavigationBarInflaterView.SIZE_MOD_START);
            builder.append("resolveInfo:").append(this.resolveInfo.toString());
            builder.append("; weight:").append(new BigDecimal(this.weight));
            builder.append(NavigationBarInflaterView.SIZE_MOD_END);
            return builder.toString();
        }
    }

    /* loaded from: classes4.dex */
    private final class DefaultSorter implements ActivitySorter {
        private static final float WEIGHT_DECAY_COEFFICIENT = 0.95f;
        private final Map<ComponentName, ActivityResolveInfo> mPackageNameToActivityMap;

        private DefaultSorter() {
            this.mPackageNameToActivityMap = new HashMap();
        }

        @Override // android.widget.ActivityChooserModel.ActivitySorter
        public void sort(Intent intent, List<ActivityResolveInfo> activities, List<HistoricalRecord> historicalRecords) {
            Map<ComponentName, ActivityResolveInfo> componentNameToActivityMap = this.mPackageNameToActivityMap;
            componentNameToActivityMap.clear();
            int activityCount = activities.size();
            for (int i = 0; i < activityCount; i++) {
                ActivityResolveInfo activity = activities.get(i);
                activity.weight = 0.0f;
                ComponentName componentName = new ComponentName(activity.resolveInfo.activityInfo.packageName, activity.resolveInfo.activityInfo.name);
                componentNameToActivityMap.put(componentName, activity);
            }
            int i2 = historicalRecords.size();
            int lastShareIndex = i2 - 1;
            float nextRecordWeight = 1.0f;
            for (int i3 = lastShareIndex; i3 >= 0; i3--) {
                HistoricalRecord historicalRecord = historicalRecords.get(i3);
                ComponentName componentName2 = historicalRecord.activity;
                ActivityResolveInfo activity2 = componentNameToActivityMap.get(componentName2);
                if (activity2 != null) {
                    activity2.weight += historicalRecord.weight * nextRecordWeight;
                    nextRecordWeight *= WEIGHT_DECAY_COEFFICIENT;
                }
            }
            Collections.sort(activities);
        }
    }

    private void readHistoricalDataImpl() {
        XmlPullParser parser;
        try {
            FileInputStream fis = this.mContext.openFileInput(this.mHistoryFileName);
            try {
                try {
                    try {
                        try {
                            parser = Xml.newPullParser();
                            parser.setInput(fis, StandardCharsets.UTF_8.name());
                            for (int type = 0; type != 1 && type != 2; type = parser.next()) {
                            }
                        } catch (Throwable th) {
                            if (fis != null) {
                                try {
                                    fis.close();
                                } catch (IOException e) {
                                }
                            }
                            throw th;
                        }
                    } catch (IOException ioe) {
                        Log.m109e(LOG_TAG, "Error reading historical recrod file: " + this.mHistoryFileName, ioe);
                        if (fis == null) {
                            return;
                        }
                        fis.close();
                    }
                } catch (XmlPullParserException xppe) {
                    Log.m109e(LOG_TAG, "Error reading historical recrod file: " + this.mHistoryFileName, xppe);
                    if (fis == null) {
                        return;
                    }
                    fis.close();
                }
                if (!TAG_HISTORICAL_RECORDS.equals(parser.getName())) {
                    throw new XmlPullParserException("Share records file does not start with historical-records tag.");
                }
                List<HistoricalRecord> historicalRecords = this.mHistoricalRecords;
                historicalRecords.clear();
                while (true) {
                    int type2 = parser.next();
                    if (type2 == 1) {
                        if (fis == null) {
                            return;
                        }
                        fis.close();
                    } else if (type2 != 3 && type2 != 4) {
                        String nodeName = parser.getName();
                        if (!TAG_HISTORICAL_RECORD.equals(nodeName)) {
                            throw new XmlPullParserException("Share records file not well-formed.");
                        }
                        String activity = parser.getAttributeValue(null, "activity");
                        long time = Long.parseLong(parser.getAttributeValue(null, "time"));
                        float weight = Float.parseFloat(parser.getAttributeValue(null, "weight"));
                        HistoricalRecord readRecord = new HistoricalRecord(activity, time, weight);
                        historicalRecords.add(readRecord);
                    }
                }
            } catch (IOException e2) {
            }
        } catch (FileNotFoundException e3) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class PersistHistoryAsyncTask extends AsyncTask<Object, Void, Void> {
        private PersistHistoryAsyncTask() {
        }

        /* JADX WARN: Removed duplicated region for block: B:61:0x0129 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        @Override // android.p008os.AsyncTask
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public Void doInBackground(Object... args) {
            Throwable th;
            int i = 0;
            List<HistoricalRecord> historicalRecords = (List) args[0];
            String hostoryFileName = (String) args[1];
            try {
                FileOutputStream fos = ActivityChooserModel.this.mContext.openFileOutput(hostoryFileName, 0);
                XmlSerializer serializer = Xml.newSerializer();
                try {
                    try {
                        try {
                            serializer.setOutput(fos, null);
                            serializer.startDocument(StandardCharsets.UTF_8.name(), true);
                            serializer.startTag(null, ActivityChooserModel.TAG_HISTORICAL_RECORDS);
                            int recordCount = historicalRecords.size();
                            int i2 = 0;
                            while (i2 < recordCount) {
                                HistoricalRecord record = historicalRecords.remove(i);
                                serializer.startTag(null, ActivityChooserModel.TAG_HISTORICAL_RECORD);
                                serializer.attribute(null, "activity", record.activity.flattenToString());
                                List<HistoricalRecord> historicalRecords2 = historicalRecords;
                                try {
                                    serializer.attribute(null, "time", String.valueOf(record.time));
                                    serializer.attribute(null, "weight", String.valueOf(record.weight));
                                    serializer.endTag(null, ActivityChooserModel.TAG_HISTORICAL_RECORD);
                                    i2++;
                                    historicalRecords = historicalRecords2;
                                    i = 0;
                                } catch (IOException e) {
                                    ioe = e;
                                    Log.m109e(ActivityChooserModel.LOG_TAG, "Error writing historical recrod file: " + ActivityChooserModel.this.mHistoryFileName, ioe);
                                    ActivityChooserModel.this.mCanReadHistoricalData = true;
                                    if (fos != null) {
                                        fos.close();
                                    }
                                    return null;
                                } catch (IllegalArgumentException e2) {
                                    iae = e2;
                                    Log.m109e(ActivityChooserModel.LOG_TAG, "Error writing historical recrod file: " + ActivityChooserModel.this.mHistoryFileName, iae);
                                    ActivityChooserModel.this.mCanReadHistoricalData = true;
                                    if (fos != null) {
                                        fos.close();
                                    }
                                    return null;
                                } catch (IllegalStateException e3) {
                                    ise = e3;
                                    Log.m109e(ActivityChooserModel.LOG_TAG, "Error writing historical recrod file: " + ActivityChooserModel.this.mHistoryFileName, ise);
                                    ActivityChooserModel.this.mCanReadHistoricalData = true;
                                    if (fos != null) {
                                        fos.close();
                                    }
                                    return null;
                                }
                            }
                            serializer.endTag(null, ActivityChooserModel.TAG_HISTORICAL_RECORDS);
                            serializer.endDocument();
                            ActivityChooserModel.this.mCanReadHistoricalData = true;
                        } catch (Throwable th2) {
                            th = th2;
                            ActivityChooserModel.this.mCanReadHistoricalData = true;
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (IOException e4) {
                                }
                            }
                            throw th;
                        }
                    } catch (IOException e5) {
                        ioe = e5;
                    } catch (IllegalArgumentException e6) {
                        iae = e6;
                    } catch (IllegalStateException e7) {
                        ise = e7;
                    } catch (Throwable th3) {
                        th = th3;
                        ActivityChooserModel.this.mCanReadHistoricalData = true;
                        if (fos != null) {
                        }
                        throw th;
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e8) {
                }
                return null;
            } catch (FileNotFoundException fnfe) {
                Log.m109e(ActivityChooserModel.LOG_TAG, "Error writing historical recrod file: " + hostoryFileName, fnfe);
                return null;
            }
        }
    }

    /* loaded from: classes4.dex */
    private final class DataModelPackageMonitor extends PackageMonitor {
        private DataModelPackageMonitor() {
        }

        @Override // com.android.internal.content.PackageMonitor
        public void onSomePackagesChanged() {
            ActivityChooserModel.this.mReloadActivities = true;
        }
    }
}
