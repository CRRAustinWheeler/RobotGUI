package com.coderedrobotics.dashboard.dashboard;

import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;

/**
 * Custom policy for the security-test.
 *
 * @author Michael
 */
class PluginPolicy extends Policy {

    /**
     * Returns {@link AllPermission} for any code sources that do not end in
     * “/rogue.jar” and an empty set of permissions for code sources that do end
     * in “/rogue.jar”, denying access to all local resources to the rogue
     * plugin.
     *
     * @param codeSource The code source to get the permission for
     * @return The permissions for the given code source
     */
    @Override
    public PermissionCollection getPermissions(CodeSource codeSource) {
        Permissions p = new Permissions();
        p.add(new AllPermission());
        return p;
    }
}
