package org.projects.ts4.utility.constructors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class DomainImpl implements Domain {
    public final String value;

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }

}