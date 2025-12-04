package com.portal.kids.user.model;

import com.portal.kids.security.Permission;
import lombok.Getter;

import java.util.Set;

@Getter
public enum UserRole {
    USER(Set.of()),
    TRAINER(Set.of(Permission.VIEW_PAYMENTS,
            Permission.VIEW_SUBSCRIPTIONS,
            Permission.ADD_TRAINING,
            Permission.CREATE_CLUB,
            Permission.EDIT_CLUB,
            Permission.EDIT_PAYMENT,
            Permission.EDIT_EVENT,
            Permission.DELETE_EVENT)),
    ADMIN(Set.of(Permission.VIEW_PAYMENTS,
            Permission.VIEW_SUBSCRIPTIONS,
            Permission.VIEW_USERS,
            Permission.ADD_TRAINING,
            Permission.CREATE_CLUB,
            Permission.EDIT_CLUB,
            Permission.EDIT_EVENT,
            Permission.DELETE_EVENT,
            Permission.EDIT_USER,
            Permission.EDIT_PAYMENT));

    private final Set<Permission> permissions;

    UserRole(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}