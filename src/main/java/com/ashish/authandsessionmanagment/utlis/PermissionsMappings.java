package com.ashish.authandsessionmanagment.utlis;

import com.ashish.authandsessionmanagment.entities.enums.Permissions;
import com.ashish.authandsessionmanagment.entities.enums.Roles;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ashish.authandsessionmanagment.entities.enums.Permissions.*;
import static com.ashish.authandsessionmanagment.entities.enums.Roles.*;

public class PermissionsMappings {

    public static Map<Roles, Set<Permissions>> getPermissionsMappings() {
        return Map.of(
                USER, Set.of(
                        USER_VIEW,
                        POST_VIEW,
                        USER_UPDATE
                ),
                CREATOR, Set.of(
                        USER_VIEW,
                        USER_UPDATE,
                        POST_CREATE,
                        POST_VIEW,
                        POST_UPDATE,
                        POST_DELETE
                ),
                ADMIN, Set.of(
                        USER_VIEW,
                        USER_UPDATE,
                        USER_DELETE,
                        POST_CREATE,
                        POST_VIEW,
                        POST_UPDATE,
                        POST_DELETE
                )
        );
    }

    public static Set<SimpleGrantedAuthority> mapRolesToAuthorities(Roles role) {
        return getPermissionsMappings().get(role).stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toSet());
    }
}
