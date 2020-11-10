package com.yuhtin.lauren.models.objects;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class PermissionAttachment implements Serializable {

    @Getter private ArrayList<String> permissions = new ArrayList<>();

    public void addPermission(String permission) {
        checkList();
        permissions.add(permission);
    }

    public void removePermission(String permission) {
        checkList();
        permissions.remove(permission);
    }

    public boolean hasPermission(String permission) {
        checkList();
        return permissions.contains(permission);
    }

    public void checkList() {
        if (permissions == null) permissions = new ArrayList<>();
    }

}
