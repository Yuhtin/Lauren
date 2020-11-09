package com.yuhtin.lauren.models.objects;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class PermissionAttachment implements Serializable {

    @Getter private ArrayList<String> permissions = new ArrayList<>();

    public void addPermission(String permission) {
        if (permissions == null) permissions = new ArrayList<>();

        permissions.add(permission);
    }

    public void removePermission(String permission) {
        if (permissions == null) permissions = new ArrayList<>();

        permissions.remove(permission);
    }

    public boolean hasPermission(String permission) {
        if (permissions == null) permissions = new ArrayList<>();

        return permissions.contains(permission);
    }

}
