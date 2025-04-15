package com.ahut.context;

public class BaseContext {

    private static final ThreadLocal<Long> currentId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentRole = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        currentId.set(id);
    }

    public static Long getCurrentId() {
        return currentId.get();
    }

    public static void removeCurrentId() {
        currentId.remove();
    }

    public static void setCurrentRole(String role) {
        currentRole.set(role);
    }

    public static String getCurrentRole() {
        return currentRole.get();
    }

    public static void removeCurrentRole() {
        currentRole.remove();
    }

}
