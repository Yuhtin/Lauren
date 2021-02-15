package com.yuhtin.lauren.core.player.impl;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class PermissionAttachment implements Serializable {

    @Getter private ArrayList<String> permissions = new ArrayList<>();

    public void addPermission(String permission) {
        assertList();
        permissions.add(permission);
    }

    public void removePermission(String permission) {
        assertList();
        permissions.remove(permission);
    }

    public boolean hasPermission(String permission) {
        assertList();
        return permissions.contains(permission);
    }

    public void assertList() {
        if (permissions == null) permissions = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "PermissionAttachment{" +
                "permissions=" + permissions +
                '}';
    }
}
