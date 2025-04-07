package com.osvaldo.ldapxml.util;

import com.osvaldo.ldapxml.model.Usuario;
import javax.naming.*;
import javax.naming.directory.*;
import java.util.Hashtable;

public class LdapConnector {

    private static DirContext conectar() throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://localhost:389");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "cn=admin");
        env.put(Context.SECURITY_CREDENTIALS, "suaSenhaAqui");
        return new InitialDirContext(env);
    }

    public static void salvarUsuario(Usuario usuario) throws NamingException {
        DirContext ctx = conectar();

        Attributes attrs = new BasicAttributes();
        attrs.put("objectClass", "inetOrgPerson");
        attrs.put("cn", usuario.getNomeCompleto());
        String[] partes = usuario.getNomeCompleto().split(" ");
        attrs.put("sn", partes.length > 1 ? partes[1] : partes[0]);
        attrs.put("uid", usuario.getLogin());
        attrs.put("telephoneNumber", usuario.getTelefone());

        for (String grupo : usuario.getGrupos()) {
            attrs.put("ou", grupo);
        }

        String dn = "uid=" + usuario.getLogin() + ",ou=usuarios,dc=usuarios,dc=com";
        try {
            ctx.bind(dn, null, attrs);
        } catch (NameAlreadyBoundException e) {
            System.out.println("Usuário já existe: " + usuario.getLogin());
        }

        ctx.close();
    }
}
