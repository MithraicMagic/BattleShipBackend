package com.bs.epic.battleships.documentation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface OnError {
    Class<?> value();
    int code();
}
