package com.github.harisherdiansyah.seapediaapi.core.utils;

public class SecurityConstant {

    public static String[] PUBLIC_GET_ENDPOINTS = {
        "/api/health-check/**",
        "/api/categories/**",
        "/api/products/**",
        "/api/app-review/**",
        "/api/stores/*/categories",
        "/api/stores/*/profile",
        "/api/stores/*/catalog",
        "/api/stores/products/*",
    };
    public static String[] PUBLIC_POST_ENDPOINTS = {
        "/api/authentication/login",
        "/api/authentication/register",
        "/api/authentication/refresh-token",
        "/api/authentication/logout",
        "/api/app-review",
    };
    public static String[] PUBLIC_PATCH_ENDPOINTS = {
        "/api/authentication/reset-password",
    };
}
