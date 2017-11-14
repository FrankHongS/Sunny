package com.hon.sunny.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by Frank_Hon on 2017/11/14.
 * E-mail:hongshuaihua@7xinsheng.com
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityScoped {
}
