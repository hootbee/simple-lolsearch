//package com.example.simple_lolsearch.security.filter;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//@Order(1)
//public class BasicDDoSProtectionFilter implements Filter {
//
//    private final Map<String, List<Long>> requestCounts = new ConcurrentHashMap<>();
//    private final Set<String> blockedIPs = ConcurrentHashMap.newKeySet();
//
//    private static final int MAX_REQUESTS = 10;
//    private static final long TIME_WINDOW = 10_000; // 10초
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        String clientIP = getClientIP(httpRequest);
//
//        if (isBlocked(clientIP) || isRateLimited(clientIP)) {
//            ((HttpServletResponse) response).setStatus(429);
//            return;
//        }
//
//        chain.doFilter(request, response);
//    }
//
//    private boolean isRateLimited(String clientIP) {
//        long now = System.currentTimeMillis();
//        List<Long> requests = requestCounts.computeIfAbsent(clientIP, k -> new ArrayList<>());
//
//        requests.removeIf(time -> now - time > TIME_WINDOW);
//
//        if (requests.size() >= MAX_REQUESTS) {
//            blockedIPs.add(clientIP);
//            return true;
//        }
//
//        requests.add(now);
//        return false;
//    }
//
//    private boolean isBlocked(String clientIP) {
//        return blockedIPs.contains(clientIP);
//    }
//
//    private String getClientIP(HttpServletRequest request) {
//        String xForwardedFor = request.getHeader("X-Forwarded-For");
//        return (xForwardedFor != null) ? xForwardedFor.split(",")[0] : request.getRemoteAddr();
//    }
//}
