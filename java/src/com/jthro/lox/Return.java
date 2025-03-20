package com.jthro.lox;

public class Return extends RuntimeException {
    final Object value;

    Return(Object value) {
        super(null, null, false, false); // Disables jvm stuff that we don't care about
        this.value = value;
    }
}
