package com.kymokim.spirit.auth.security;

public interface AuthToken<T> {
    boolean validate();
    T getClaims();
}
