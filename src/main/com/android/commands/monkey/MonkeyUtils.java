package com.android.commands.monkey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
/* loaded from: classes.dex */
public abstract class MonkeyUtils {
    private static final Date DATE = new Date();
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS ");
    private static PackageFilter sFilter;

    private MonkeyUtils() {
    }

    public static synchronized String toCalendarTime(long time) {
        String format;
        synchronized (MonkeyUtils.class) {
            Date date = DATE;
            date.setTime(time);
            format = DATE_FORMATTER.format(date);
        }
        return format;
    }

    public static PackageFilter getPackageFilter() {
        if (sFilter == null) {
            sFilter = new PackageFilter();
        }
        return sFilter;
    }

    /* loaded from: classes.dex */
    public static class PackageFilter {
        private Set<String> mInvalidPackages;
        private Set<String> mValidPackages;

        private PackageFilter() {
            this.mValidPackages = new HashSet();
            this.mInvalidPackages = new HashSet();
        }

        public void addValidPackages(Set<String> validPackages) {
            this.mValidPackages.addAll(validPackages);
        }

        public void addInvalidPackages(Set<String> invalidPackages) {
            this.mInvalidPackages.addAll(invalidPackages);
        }

        public boolean hasValidPackages() {
            return this.mValidPackages.size() > 0;
        }

        public boolean isPackageValid(String pkg) {
            return this.mValidPackages.contains(pkg);
        }

        public boolean isPackageInvalid(String pkg) {
            return this.mInvalidPackages.contains(pkg);
        }

        public boolean checkEnteringPackage(String pkg) {
            return this.mInvalidPackages.size() > 0 ? !this.mInvalidPackages.contains(pkg) : this.mValidPackages.size() <= 0 || this.mValidPackages.contains(pkg);
        }

        public void dump() {
            if (this.mValidPackages.size() > 0) {
                Iterator<String> it = this.mValidPackages.iterator();
                while (it.hasNext()) {
                    Logger.out.println(":AllowPackage: " + it.next());
                }
            }
            if (this.mInvalidPackages.size() > 0) {
                Iterator<String> it2 = this.mInvalidPackages.iterator();
                while (it2.hasNext()) {
                    Logger.out.println(":DisallowPackage: " + it2.next());
                }
            }
        }
    }
}
