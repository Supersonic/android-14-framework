package android.test;

import android.util.Log;
import dalvik.system.DexFile;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
@Deprecated
/* loaded from: classes.dex */
public class ClassPathPackageInfoSource {
    private static final ClassLoader CLASS_LOADER = ClassPathPackageInfoSource.class.getClassLoader();
    private static String[] apkPaths;
    private static ClassPathPackageInfoSource classPathSource;
    private final ClassLoader classLoader;
    private final SimpleCache<String, ClassPathPackageInfo> cache = new SimpleCache<String, ClassPathPackageInfo>() { // from class: android.test.ClassPathPackageInfoSource.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.test.SimpleCache
        public ClassPathPackageInfo load(String pkgName) {
            return ClassPathPackageInfoSource.this.createPackageInfo(pkgName);
        }
    };
    private final String[] classPath = getClassPath();

    private ClassPathPackageInfoSource(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setApkPaths(String[] apkPaths2) {
        apkPaths = apkPaths2;
    }

    public static ClassPathPackageInfoSource forClassPath(ClassLoader classLoader) {
        if (classPathSource == null) {
            classPathSource = new ClassPathPackageInfoSource(classLoader);
        }
        return classPathSource;
    }

    public Set<Class<?>> getTopLevelClassesRecursive(String packageName) {
        ClassPathPackageInfo packageInfo = this.cache.get(packageName);
        return packageInfo.getTopLevelClassesRecursive();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ClassPathPackageInfo createPackageInfo(String packageName) {
        Set<String> subpackageNames = new TreeSet<>();
        Set<String> classNames = new TreeSet<>();
        Set<Class<?>> topLevelClasses = new HashSet<>();
        findClasses(packageName, classNames, subpackageNames);
        for (String className : classNames) {
            if (!className.endsWith(".R") && !className.endsWith(".Manifest")) {
                try {
                    ClassLoader classLoader = this.classLoader;
                    if (classLoader == null) {
                        classLoader = CLASS_LOADER;
                    }
                    topLevelClasses.add(Class.forName(className, false, classLoader));
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    Log.w("ClassPathPackageInfoSource", "Cannot load class. Make sure it is in your apk. Class name: '" + className + "'. Message: " + e.getMessage(), e);
                }
            }
        }
        return new ClassPathPackageInfo(packageName, subpackageNames, topLevelClasses);
    }

    private void findClasses(String packageName, Set<String> classNames, Set<String> subpackageNames) {
        String[] strArr;
        String[] strArr2;
        for (String entryName : this.classPath) {
            File classPathEntry = new File(entryName);
            if (classPathEntry.exists()) {
                try {
                    if (entryName.endsWith(".apk")) {
                        findClassesInApk(entryName, packageName, classNames, subpackageNames);
                    } else {
                        for (String apkPath : apkPaths) {
                            File file = new File(apkPath);
                            scanForApkFiles(file, packageName, classNames, subpackageNames);
                        }
                    }
                } catch (IOException e) {
                    throw new AssertionError("Can't read classpath entry " + entryName + ": " + e.getMessage());
                }
            }
        }
    }

    private void scanForApkFiles(File source, String packageName, Set<String> classNames, Set<String> subpackageNames) throws IOException {
        if (source.getPath().endsWith(".apk")) {
            findClassesInApk(source.getPath(), packageName, classNames, subpackageNames);
            return;
        }
        File[] files = source.listFiles();
        if (files != null) {
            for (File file : files) {
                scanForApkFiles(file, packageName, classNames, subpackageNames);
            }
        }
    }

    private void findClassesInApk(String apkPath, String packageName, Set<String> classNames, Set<String> subpackageNames) throws IOException {
        try {
            DexFile dexFile = new DexFile(apkPath);
            Enumeration<String> apkClassNames = dexFile.entries();
            while (apkClassNames.hasMoreElements()) {
                String className = apkClassNames.nextElement();
                if (className.startsWith(packageName)) {
                    String subPackageName = packageName;
                    int lastPackageSeparator = className.lastIndexOf(46);
                    if (lastPackageSeparator > 0) {
                        subPackageName = className.substring(0, lastPackageSeparator);
                    }
                    if (subPackageName.length() > packageName.length()) {
                        subpackageNames.add(subPackageName);
                    } else if (isToplevelClass(className)) {
                        classNames.add(className);
                    }
                }
            }
        } catch (IOException e) {
        }
    }

    private static boolean isToplevelClass(String fileName) {
        return fileName.indexOf(36) < 0;
    }

    private static String[] getClassPath() {
        String classPath = System.getProperty("java.class.path");
        String separator = System.getProperty("path.separator", ":");
        return classPath.split(Pattern.quote(separator));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ClassPathPackageInfo {
        private final String packageName;
        private final Set<String> subpackageNames;
        private final Set<Class<?>> topLevelClasses;

        private ClassPathPackageInfo(String packageName, Set<String> subpackageNames, Set<Class<?>> topLevelClasses) {
            this.packageName = packageName;
            this.subpackageNames = Collections.unmodifiableSet(subpackageNames);
            this.topLevelClasses = Collections.unmodifiableSet(topLevelClasses);
        }

        private Set<ClassPathPackageInfo> getSubpackages() {
            Set<ClassPathPackageInfo> info = new HashSet<>();
            for (String name : this.subpackageNames) {
                info.add((ClassPathPackageInfo) ClassPathPackageInfoSource.this.cache.get(name));
            }
            return info;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Set<Class<?>> getTopLevelClassesRecursive() {
            Set<Class<?>> set = new HashSet<>();
            addTopLevelClassesTo(set);
            return set;
        }

        private void addTopLevelClassesTo(Set<Class<?>> set) {
            set.addAll(this.topLevelClasses);
            for (ClassPathPackageInfo info : getSubpackages()) {
                info.addTopLevelClassesTo(set);
            }
        }

        public boolean equals(Object obj) {
            if (obj instanceof ClassPathPackageInfo) {
                ClassPathPackageInfo that = (ClassPathPackageInfo) obj;
                return this.packageName.equals(that.packageName);
            }
            return false;
        }

        public int hashCode() {
            return this.packageName.hashCode();
        }
    }
}
