package org.sample.concurrency.harness;

import org.openjdk.jcstress.annotations.Result;

import java.io.Serializable;
import java.util.Objects;

@Result
public class StringString_Result implements Serializable {
    @sun.misc.Contended
    @jdk.internal.vm.annotation.Contended
    public String r1;

    @sun.misc.Contended
    @jdk.internal.vm.annotation.Contended
    public String r2;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringString_Result that = (StringString_Result) o;
        return Objects.equals(r1, that.r1) &&
                Objects.equals(r2, that.r2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(r1, r2);
    }

    @Override
    public String toString() {
        return r1 + ", " + r2;
    }
}
