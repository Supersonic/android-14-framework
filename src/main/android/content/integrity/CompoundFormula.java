package android.content.integrity;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public final class CompoundFormula extends IntegrityFormula implements Parcelable {
    public static final int AND = 0;
    public static final Parcelable.Creator<CompoundFormula> CREATOR = new Parcelable.Creator<CompoundFormula>() { // from class: android.content.integrity.CompoundFormula.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CompoundFormula createFromParcel(Parcel in) {
            return new CompoundFormula(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CompoundFormula[] newArray(int size) {
            return new CompoundFormula[size];
        }
    };
    public static final int NOT = 2;

    /* renamed from: OR */
    public static final int f46OR = 1;
    private final int mConnector;
    private final List<IntegrityFormula> mFormulas;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Connector {
    }

    public CompoundFormula(int connector, List<IntegrityFormula> formulas) {
        Preconditions.checkArgument(isValidConnector(connector), "Unknown connector: %d", Integer.valueOf(connector));
        validateFormulas(connector, formulas);
        this.mConnector = connector;
        this.mFormulas = Collections.unmodifiableList(formulas);
    }

    CompoundFormula(Parcel in) {
        this.mConnector = in.readInt();
        int length = in.readInt();
        Preconditions.checkArgument(length >= 0, "Must have non-negative length. Got %d", Integer.valueOf(length));
        this.mFormulas = new ArrayList(length);
        for (int i = 0; i < length; i++) {
            this.mFormulas.add(IntegrityFormula.readFromParcel(in));
        }
        int i2 = this.mConnector;
        validateFormulas(i2, this.mFormulas);
    }

    public int getConnector() {
        return this.mConnector;
    }

    public List<IntegrityFormula> getFormulas() {
        return this.mFormulas;
    }

    @Override // android.content.integrity.IntegrityFormula
    public int getTag() {
        return 0;
    }

    @Override // android.content.integrity.IntegrityFormula
    public boolean matches(final AppInstallMetadata appInstallMetadata) {
        switch (getConnector()) {
            case 0:
                return getFormulas().stream().allMatch(new Predicate() { // from class: android.content.integrity.CompoundFormula$$ExternalSyntheticLambda0
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean matches;
                        matches = ((IntegrityFormula) obj).matches(AppInstallMetadata.this);
                        return matches;
                    }
                });
            case 1:
                return getFormulas().stream().anyMatch(new Predicate() { // from class: android.content.integrity.CompoundFormula$$ExternalSyntheticLambda1
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean matches;
                        matches = ((IntegrityFormula) obj).matches(AppInstallMetadata.this);
                        return matches;
                    }
                });
            case 2:
                return !getFormulas().get(0).matches(appInstallMetadata);
            default:
                throw new IllegalArgumentException("Unknown connector " + getConnector());
        }
    }

    @Override // android.content.integrity.IntegrityFormula
    public boolean isAppCertificateFormula() {
        return getFormulas().stream().anyMatch(new Predicate() { // from class: android.content.integrity.CompoundFormula$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean isAppCertificateFormula;
                isAppCertificateFormula = ((IntegrityFormula) obj).isAppCertificateFormula();
                return isAppCertificateFormula;
            }
        });
    }

    @Override // android.content.integrity.IntegrityFormula
    public boolean isAppCertificateLineageFormula() {
        return getFormulas().stream().anyMatch(new Predicate() { // from class: android.content.integrity.CompoundFormula$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean isAppCertificateLineageFormula;
                isAppCertificateLineageFormula = ((IntegrityFormula) obj).isAppCertificateLineageFormula();
                return isAppCertificateLineageFormula;
            }
        });
    }

    @Override // android.content.integrity.IntegrityFormula
    public boolean isInstallerFormula() {
        return getFormulas().stream().anyMatch(new Predicate() { // from class: android.content.integrity.CompoundFormula$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean isInstallerFormula;
                isInstallerFormula = ((IntegrityFormula) obj).isInstallerFormula();
                return isInstallerFormula;
            }
        });
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.mFormulas.size() == 1) {
            sb.append(String.format("%s ", connectorToString(this.mConnector)));
            sb.append(this.mFormulas.get(0).toString());
        } else {
            for (int i = 0; i < this.mFormulas.size(); i++) {
                if (i > 0) {
                    sb.append(String.format(" %s ", connectorToString(this.mConnector)));
                }
                sb.append(this.mFormulas.get(i).toString());
            }
        }
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CompoundFormula that = (CompoundFormula) o;
        if (this.mConnector == that.mConnector && this.mFormulas.equals(that.mFormulas)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mConnector), this.mFormulas);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mConnector);
        dest.writeInt(this.mFormulas.size());
        for (IntegrityFormula formula : this.mFormulas) {
            IntegrityFormula.writeToParcel(formula, dest, flags);
        }
    }

    private static void validateFormulas(int connector, List<IntegrityFormula> formulas) {
        switch (connector) {
            case 0:
            case 1:
                Preconditions.checkArgument(formulas.size() >= 2, "Connector %s must have at least 2 formulas", connectorToString(connector));
                return;
            case 2:
                Preconditions.checkArgument(formulas.size() == 1, "Connector %s must have 1 formula only", connectorToString(connector));
                return;
            default:
                return;
        }
    }

    private static String connectorToString(int connector) {
        switch (connector) {
            case 0:
                return "AND";
            case 1:
                return "OR";
            case 2:
                return "NOT";
            default:
                throw new IllegalArgumentException("Unknown connector " + connector);
        }
    }

    private static boolean isValidConnector(int connector) {
        return connector == 0 || connector == 1 || connector == 2;
    }
}
