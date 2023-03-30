package org.example;

import lombok.Getter;
import lombok.Setter;

abstract class Entry {
    @Getter
    @Setter
    protected String name;

    @Getter
    @Setter
    protected Directory parent;

    public Entry(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
    }

}
