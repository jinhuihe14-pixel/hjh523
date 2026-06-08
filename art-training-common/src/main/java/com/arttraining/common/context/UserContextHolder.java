package com.arttraining.common.context;

public class UserContextHolder {

    private static final ThreadLocal<UserContext> USER_CONTEXT = new ThreadLocal<>();

    public static void set(UserContext userContext) {
        USER_CONTEXT.set(userContext);
    }

    public static UserContext get() {
        return USER_CONTEXT.get();
    }

    public static void remove() {
        USER_CONTEXT.remove();
    }

    public static Long getUserId() {
        UserContext context = get();
        return context != null ? context.getUserId() : null;
    }

    public static String getUsername() {
        UserContext context = get();
        return context != null ? context.getUsername() : null;
    }

    public static String getRealName() {
        UserContext context = get();
        return context != null ? context.getRealName() : null;
    }

    public static Long getCampusId() {
        UserContext context = get();
        return context != null ? context.getCampusId() : null;
    }

    public static String getCampusCode() {
        UserContext context = get();
        return context != null ? context.getCampusCode() : null;
    }
}
