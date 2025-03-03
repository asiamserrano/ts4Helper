package org.ts4.pkg.constructors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class Domain {
    public final String value;

    @Override
    public String toString() {
        return this.value;
    }

}