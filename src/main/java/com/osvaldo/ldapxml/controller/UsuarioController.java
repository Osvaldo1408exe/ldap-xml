package com.osvaldo.ldapxml.controller;

import com.osvaldo.ldapxml.config.LdapConnection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UsuarioController {

    @GetMapping("/usuarios")
    public List<String> getUsuarios() {
        return LdapConnection.listarUsuarios();
    }
}
