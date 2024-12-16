package com.bappi.userauthservice.model.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum UserState {

    PENDING("Pending"),
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    BLOCKED("Blocked"),

    ENABLE("Enable"),
    DISABLE("Disable")
    ;

    private final String message;

    UserState(String message) {
        this.message = message;
    }

    private static final Map<String, UserState> userStateMap = new HashMap<>();
    static {
        for (UserState userState : UserState.values()) {
            if(!userStateMap.containsKey(userState.getMessage().toLowerCase())) {
                userStateMap.put(userState.getMessage().toLowerCase(), userState);
            }
        }
    }

    public static boolean isValidUserState(String value) {
        try {
            UserState status = UserState.valueOf(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
