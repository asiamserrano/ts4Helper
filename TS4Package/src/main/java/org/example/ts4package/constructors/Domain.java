package org.example.ts4package.constructors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class Domain {
    public final String value;

    @Override
    public String toString() {
        return this.value;
    }

}