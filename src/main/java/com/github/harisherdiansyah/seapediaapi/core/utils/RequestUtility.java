package com.github.harisherdiansyah.seapediaapi.core.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RequestUtility {
    public String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress.contains(",") ? ipAddress.split(",")[0].trim() : ipAddress;
    }

    public String getClientDeviceInfo(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
