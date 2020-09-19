package com.bs.epic.battleships.documentation.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(OnErrors.class)
public @interface OnError {
    Class<?> value();
    int code();
    String desc() default "";
}
