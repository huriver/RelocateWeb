package com.ahut.context;

public class BaseContext {

    private static final ThreadLocal<Long> currentId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUserRole = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        currentId.set(id);
    }

    public static Long getCurrentId() {
        return currentId.get();
    }

    public static void setCurrentUserRole(String role) {
        currentUserRole.set(role);
    }

    public static String getCurrentUserRole() {
        return currentUserRole.get();
    }

    public static void remove() {
        currentId.remove();
        currentUserRole.remove();
    }

}
