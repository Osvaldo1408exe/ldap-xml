package com.osvaldo.ldapxml.config;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class LdapConnection {

    private static DirContext getContext() throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://localhost:389");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "cn=admin"); // ou outro admin
        env.put(Context.SECURITY_CREDENTIALS, "123");

        return new InitialDirContext(env);
    }

    public static List<String> listarUsuarios() {
        List<String> usuarios = new ArrayList<>();
        String baseDn = "dc=usuarios,dc=com";
        String filtro = "(objectClass=inetOrgPerson)";

        try {
            DirContext ctx = getContext();

            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            NamingEnumeration<SearchResult> results = ctx.search(baseDn, filtro, searchControls);

            while (results.hasMore()) {
                SearchResult result = results.next();
                Attributes attrs = result.getAttributes();

                String uid = attrs.get("uid") != null ? attrs.get("uid").get().toString() : "sem uid";
                usuarios.add(uid);
            }

            ctx.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }

        return usuarios;
    }
}
