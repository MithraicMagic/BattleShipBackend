package com.bs.epic.battleships.documentation.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Doc {
    String value() default "";
}
