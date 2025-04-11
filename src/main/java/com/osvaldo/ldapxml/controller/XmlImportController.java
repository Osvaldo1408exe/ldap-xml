package com.osvaldo.ldapxml.controller;

import com.osvaldo.ldapxml.config.LdapConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


@RestController
@RequestMapping("/api/xml")
public class XmlImportController {

    private final LdapConnection ldapService;

    public XmlImportController(LdapConnection ldapService) {
        this.ldapService = ldapService;
    }

    @PostMapping("/importar")
    public ResponseEntity<String> importarXml(@RequestParam("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            ldapService.processXml(inputStream);
            return ResponseEntity.ok("XML processado com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar o XML: " + e.getMessage());
        }
    }
}