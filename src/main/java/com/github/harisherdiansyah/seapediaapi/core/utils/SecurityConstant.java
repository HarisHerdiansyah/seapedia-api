package com.github.harisherdiansyah.seapediaapi.core.utils;

public class SecurityConstant {
    public static String[] PUBLIC_GET_ENDPOINTS = {
            "/api/health-check/**",
    };
    public static String[] PUBLIC_POST_ENDPOINTS = {
            "/api/app-review/**",
            "/api/authentication/login",
            "/api/authentication/register",
            "/api/authentication/reset-password",
    };
}
