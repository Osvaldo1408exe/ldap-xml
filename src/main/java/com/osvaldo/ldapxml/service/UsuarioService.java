package com.osvaldo.ldapxml.service;

import com.osvaldo.ldapxml.model.Usuario;
import com.osvaldo.ldapxml.util.LdapConnector;
import com.osvaldo.ldapxml.util.XmlUsuarioParser;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class UsuarioService {

    public void importarUsuarios(File xmlFile) throws Exception {
        List<Usuario> usuarios = XmlUsuarioParser.lerUsuarios(xmlFile);
        for (Usuario u : usuarios) {
            LdapConnector.salvarUsuario(u);
        }
    }
}
