package com.android.server.firewall;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.net.INetd;
import android.os.Binder;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.XmlUtils;
import com.android.server.EventLogTags;
import com.android.server.IntentResolver;
import com.android.server.LocalServices;
import com.android.server.p011pm.Computer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class IntentFirewall {
    public static final File RULES_DIR = new File(Environment.getDataSystemDirectory(), "ifw");
    public static final HashMap<String, FilterFactory> factoryMap;
    public final AMSInterface mAms;
    public final FirewallHandler mHandler;
    public final RuleObserver mObserver;
    public PackageManagerInternal mPackageManager;
    public FirewallIntentResolver mActivityResolver = new FirewallIntentResolver();
    public FirewallIntentResolver mBroadcastResolver = new FirewallIntentResolver();
    public FirewallIntentResolver mServiceResolver = new FirewallIntentResolver();

    /* loaded from: classes.dex */
    public interface AMSInterface {
        int checkComponentPermission(String str, int i, int i2, int i3, boolean z);

        Object getAMSLock();
    }

    static {
        FilterFactory[] filterFactoryArr = {AndFilter.FACTORY, OrFilter.FACTORY, NotFilter.FACTORY, StringFilter.ACTION, StringFilter.COMPONENT, StringFilter.COMPONENT_NAME, StringFilter.COMPONENT_PACKAGE, StringFilter.DATA, StringFilter.HOST, StringFilter.MIME_TYPE, StringFilter.SCHEME, StringFilter.PATH, StringFilter.SSP, CategoryFilter.FACTORY, SenderFilter.FACTORY, SenderPackageFilter.FACTORY, SenderPermissionFilter.FACTORY, PortFilter.FACTORY};
        factoryMap = new HashMap<>(24);
        for (int i = 0; i < 18; i++) {
            FilterFactory filterFactory = filterFactoryArr[i];
            factoryMap.put(filterFactory.getTagName(), filterFactory);
        }
    }

    public IntentFirewall(AMSInterface aMSInterface, Handler handler) {
        this.mAms = aMSInterface;
        this.mHandler = new FirewallHandler(handler.getLooper());
        File rulesDir = getRulesDir();
        rulesDir.mkdirs();
        readRulesDir(rulesDir);
        RuleObserver ruleObserver = new RuleObserver(rulesDir);
        this.mObserver = ruleObserver;
        ruleObserver.startWatching();
    }

    public PackageManagerInternal getPackageManager() {
        if (this.mPackageManager == null) {
            this.mPackageManager = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        }
        return this.mPackageManager;
    }

    public boolean checkStartActivity(Intent intent, int i, int i2, String str, ApplicationInfo applicationInfo) {
        return checkIntent(this.mActivityResolver, intent.getComponent(), 0, intent, i, i2, str, applicationInfo.uid);
    }

    public boolean checkService(ComponentName componentName, Intent intent, int i, int i2, String str, ApplicationInfo applicationInfo) {
        return checkIntent(this.mServiceResolver, componentName, 2, intent, i, i2, str, applicationInfo.uid);
    }

    public boolean checkBroadcast(Intent intent, int i, int i2, String str, int i3) {
        return checkIntent(this.mBroadcastResolver, intent.getComponent(), 1, intent, i, i2, str, i3);
    }

    public boolean checkIntent(FirewallIntentResolver firewallIntentResolver, ComponentName componentName, int i, Intent intent, int i2, int i3, String str, int i4) {
        List<Rule> queryIntent = firewallIntentResolver.queryIntent(getPackageManager().snapshot(), intent, str, false, 0);
        if (queryIntent == null) {
            queryIntent = new ArrayList<>();
        }
        firewallIntentResolver.queryByComponent(componentName, queryIntent);
        boolean z = false;
        boolean z2 = false;
        for (int i5 = 0; i5 < queryIntent.size(); i5++) {
            Rule rule = queryIntent.get(i5);
            if (rule.matches(this, componentName, intent, i2, i3, str, i4)) {
                z |= rule.getBlock();
                z2 |= rule.getLog();
                if (z && z2) {
                    break;
                }
            }
        }
        if (z2) {
            logIntent(i, intent, i2, str);
        }
        return !z;
    }

    public static void logIntent(int i, Intent intent, int i2, String str) {
        ComponentName component = intent.getComponent();
        String str2 = null;
        String flattenToShortString = component != null ? component.flattenToShortString() : null;
        IPackageManager packageManager = AppGlobals.getPackageManager();
        int i3 = 0;
        if (packageManager != null) {
            try {
                String[] packagesForUid = packageManager.getPackagesForUid(i2);
                if (packagesForUid != null) {
                    i3 = packagesForUid.length;
                    str2 = joinPackages(packagesForUid);
                }
            } catch (RemoteException e) {
                Slog.e("IntentFirewall", "Remote exception while retrieving packages", e);
            }
        }
        EventLogTags.writeIfwIntentMatched(i, flattenToShortString, i2, i3, str2, intent.getAction(), str, intent.getDataString(), intent.getFlags());
    }

    public static String joinPackages(String[] strArr) {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (String str : strArr) {
            if (sb.length() + str.length() + 1 < 150) {
                if (z) {
                    z = false;
                } else {
                    sb.append(',');
                }
                sb.append(str);
            } else if (sb.length() >= 125) {
                return sb.toString();
            }
        }
        if (sb.length() != 0 || strArr.length <= 0) {
            return null;
        }
        String str2 = strArr[0];
        return str2.substring((str2.length() - 150) + 1) + '-';
    }

    public static File getRulesDir() {
        return RULES_DIR;
    }

    public final void readRulesDir(File file) {
        FirewallIntentResolver[] firewallIntentResolverArr = new FirewallIntentResolver[3];
        for (int i = 0; i < 3; i++) {
            firewallIntentResolverArr[i] = new FirewallIntentResolver();
        }
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file2 : listFiles) {
                if (file2.getName().endsWith(".xml")) {
                    readRules(file2, firewallIntentResolverArr);
                }
            }
        }
        Slog.i("IntentFirewall", "Read new rules (A:" + firewallIntentResolverArr[0].filterSet().size() + " B:" + firewallIntentResolverArr[1].filterSet().size() + " S:" + firewallIntentResolverArr[2].filterSet().size() + ")");
        synchronized (this.mAms.getAMSLock()) {
            this.mActivityResolver = firewallIntentResolverArr[0];
            this.mBroadcastResolver = firewallIntentResolverArr[1];
            this.mServiceResolver = firewallIntentResolverArr[2];
        }
    }

    public final void readRules(File file, FirewallIntentResolver[] firewallIntentResolverArr) {
        ArrayList arrayList = new ArrayList(3);
        for (int i = 0; i < 3; i++) {
            arrayList.add(new ArrayList());
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                try {
                    try {
                        XmlPullParser newPullParser = Xml.newPullParser();
                        newPullParser.setInput(fileInputStream, null);
                        XmlUtils.beginDocument(newPullParser, "rules");
                        int depth = newPullParser.getDepth();
                        while (XmlUtils.nextElementWithin(newPullParser, depth)) {
                            String name = newPullParser.getName();
                            int i2 = name.equals("activity") ? 0 : name.equals(INetd.IF_FLAG_BROADCAST) ? 1 : name.equals("service") ? 2 : -1;
                            if (i2 != -1) {
                                Rule rule = new Rule();
                                List list = (List) arrayList.get(i2);
                                try {
                                    rule.readFromXml(newPullParser);
                                    list.add(rule);
                                } catch (XmlPullParserException e) {
                                    Slog.e("IntentFirewall", "Error reading an intent firewall rule from " + file, e);
                                }
                            }
                        }
                        try {
                            fileInputStream.close();
                        } catch (IOException e2) {
                            Slog.e("IntentFirewall", "Error while closing " + file, e2);
                        }
                        for (int i3 = 0; i3 < arrayList.size(); i3++) {
                            List list2 = (List) arrayList.get(i3);
                            FirewallIntentResolver firewallIntentResolver = firewallIntentResolverArr[i3];
                            for (int i4 = 0; i4 < list2.size(); i4++) {
                                Rule rule2 = (Rule) list2.get(i4);
                                for (int i5 = 0; i5 < rule2.getIntentFilterCount(); i5++) {
                                    firewallIntentResolver.addFilter(null, rule2.getIntentFilter(i5));
                                }
                                for (int i6 = 0; i6 < rule2.getComponentFilterCount(); i6++) {
                                    firewallIntentResolver.addComponentFilter(rule2.getComponentFilter(i6), rule2);
                                }
                            }
                        }
                    } catch (IOException e3) {
                        Slog.e("IntentFirewall", "Error reading intent firewall rules from " + file, e3);
                        try {
                            fileInputStream.close();
                        } catch (IOException e4) {
                            Slog.e("IntentFirewall", "Error while closing " + file, e4);
                        }
                    }
                } catch (XmlPullParserException e5) {
                    Slog.e("IntentFirewall", "Error reading intent firewall rules from " + file, e5);
                    try {
                        fileInputStream.close();
                    } catch (IOException e6) {
                        Slog.e("IntentFirewall", "Error while closing " + file, e6);
                    }
                }
            } catch (Throwable th) {
                try {
                    fileInputStream.close();
                } catch (IOException e7) {
                    Slog.e("IntentFirewall", "Error while closing " + file, e7);
                }
                throw th;
            }
        } catch (FileNotFoundException unused) {
        }
    }

    public static Filter parseFilter(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        String name = xmlPullParser.getName();
        FilterFactory filterFactory = factoryMap.get(name);
        if (filterFactory == null) {
            throw new XmlPullParserException("Unknown element in filter list: " + name);
        }
        return filterFactory.newFilter(xmlPullParser);
    }

    /* loaded from: classes.dex */
    public static class Rule extends AndFilter {
        public boolean block;
        public boolean log;
        public final ArrayList<ComponentName> mComponentFilters;
        public final ArrayList<FirewallIntentFilter> mIntentFilters;

        public Rule() {
            this.mIntentFilters = new ArrayList<>(1);
            this.mComponentFilters = new ArrayList<>(0);
        }

        @Override // com.android.server.firewall.FilterList
        public Rule readFromXml(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
            this.block = Boolean.parseBoolean(xmlPullParser.getAttributeValue(null, "block"));
            this.log = Boolean.parseBoolean(xmlPullParser.getAttributeValue(null, "log"));
            super.readFromXml(xmlPullParser);
            return this;
        }

        @Override // com.android.server.firewall.FilterList
        public void readChild(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
            String name = xmlPullParser.getName();
            if (name.equals("intent-filter")) {
                FirewallIntentFilter firewallIntentFilter = new FirewallIntentFilter(this);
                firewallIntentFilter.readFromXml(xmlPullParser);
                this.mIntentFilters.add(firewallIntentFilter);
            } else if (name.equals("component-filter")) {
                String attributeValue = xmlPullParser.getAttributeValue(null, "name");
                if (attributeValue == null) {
                    throw new XmlPullParserException("Component name must be specified.", xmlPullParser, null);
                }
                ComponentName unflattenFromString = ComponentName.unflattenFromString(attributeValue);
                if (unflattenFromString == null) {
                    throw new XmlPullParserException("Invalid component name: " + attributeValue);
                }
                this.mComponentFilters.add(unflattenFromString);
            } else {
                super.readChild(xmlPullParser);
            }
        }

        public int getIntentFilterCount() {
            return this.mIntentFilters.size();
        }

        public FirewallIntentFilter getIntentFilter(int i) {
            return this.mIntentFilters.get(i);
        }

        public int getComponentFilterCount() {
            return this.mComponentFilters.size();
        }

        public ComponentName getComponentFilter(int i) {
            return this.mComponentFilters.get(i);
        }

        public boolean getBlock() {
            return this.block;
        }

        public boolean getLog() {
            return this.log;
        }
    }

    /* loaded from: classes.dex */
    public static class FirewallIntentFilter extends IntentFilter {
        public final Rule rule;

        public FirewallIntentFilter(Rule rule) {
            this.rule = rule;
        }
    }

    /* loaded from: classes.dex */
    public static class FirewallIntentResolver extends IntentResolver<FirewallIntentFilter, Rule> {
        public final ArrayMap<ComponentName, Rule[]> mRulesByComponent;

        @Override // com.android.server.IntentResolver
        public IntentFilter getIntentFilter(FirewallIntentFilter firewallIntentFilter) {
            return firewallIntentFilter;
        }

        @Override // com.android.server.IntentResolver
        public boolean isPackageForFilter(String str, FirewallIntentFilter firewallIntentFilter) {
            return true;
        }

        @Override // com.android.server.IntentResolver
        public void sortResults(List<Rule> list) {
        }

        public FirewallIntentResolver() {
            this.mRulesByComponent = new ArrayMap<>(0);
        }

        @Override // com.android.server.IntentResolver
        public boolean allowFilterResult(FirewallIntentFilter firewallIntentFilter, List<Rule> list) {
            return !list.contains(firewallIntentFilter.rule);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.server.IntentResolver
        public FirewallIntentFilter[] newArray(int i) {
            return new FirewallIntentFilter[i];
        }

        @Override // com.android.server.IntentResolver
        public Rule newResult(Computer computer, FirewallIntentFilter firewallIntentFilter, int i, int i2, long j) {
            return firewallIntentFilter.rule;
        }

        public void queryByComponent(ComponentName componentName, List<Rule> list) {
            Rule[] ruleArr = this.mRulesByComponent.get(componentName);
            if (ruleArr != null) {
                list.addAll(Arrays.asList(ruleArr));
            }
        }

        public void addComponentFilter(ComponentName componentName, Rule rule) {
            this.mRulesByComponent.put(componentName, (Rule[]) ArrayUtils.appendElement(Rule.class, this.mRulesByComponent.get(componentName), rule));
        }
    }

    /* loaded from: classes.dex */
    public final class FirewallHandler extends Handler {
        public FirewallHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            IntentFirewall.this.readRulesDir(IntentFirewall.getRulesDir());
        }
    }

    /* loaded from: classes.dex */
    public class RuleObserver extends FileObserver {
        public RuleObserver(File file) {
            super(file.getAbsolutePath(), 968);
        }

        @Override // android.os.FileObserver
        public void onEvent(int i, String str) {
            if (str == null || !str.endsWith(".xml")) {
                return;
            }
            IntentFirewall.this.mHandler.removeMessages(0);
            IntentFirewall.this.mHandler.sendEmptyMessageDelayed(0, 250L);
        }
    }

    public boolean checkComponentPermission(String str, int i, int i2, int i3, boolean z) {
        return this.mAms.checkComponentPermission(str, i, i2, i3, z) == 0;
    }

    public boolean signaturesMatch(int i, int i2) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return getPackageManager().checkUidSignaturesForAllUsers(i, i2) == 0;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }
}
