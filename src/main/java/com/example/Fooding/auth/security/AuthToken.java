package com.example.Fooding.auth.security;

public interface AuthToken<T> {
    boolean validate();
    T getClaims();
}
