package ru.ok.java.api.request.serializer.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ru.ok.java.api.HttpMethodType;
import ru.ok.java.api.Scope;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpPreamble {
    boolean hasFormat() default false;

    boolean hasGeolocation() default false;

    boolean hasSessionKey() default false;

    boolean hasTargetUrl() default false;

    boolean hasUserId() default false;

    HttpMethodType httpType() default HttpMethodType.GET;

    Scope signType() default Scope.SESSION;

    boolean useHttps() default false;
}
