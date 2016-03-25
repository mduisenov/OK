package ru.ok.android.bus.annotation;

import android.support.annotation.AnyRes;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {
    @AnyRes
    int on() default 0;

    @AnyRes
    int to();
}
