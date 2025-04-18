package com.osvaldo.ldapxml.controller;

import com.osvaldo.ldapxml.service.XmlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.NamingException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/xml")
public class XmlImportController {

    private final XmlService XmlService;

    public XmlImportController(XmlService XmlService) {
        this.XmlService = XmlService;
    }


    //list all users and groups
    @GetMapping("/ImportEntity")
    public ResponseEntity<List<Map<String, Object>>> listarUsuarios() {
        try {
            List<Map<String, Object>> usuarios = XmlService.listarUsuarios();
            return ResponseEntity.ok(usuarios);
        } catch (NamingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //ADD USER OR GROUP
    @PostMapping("/ImportEntity")
    public ResponseEntity<String> importXml(@RequestParam("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            XmlService.addXml(inputStream);
            return ResponseEntity.ok("XML processado com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar o XML: " + e.getMessage());
        }
    }

    //MODIFY USER
    @PutMapping("/ImportEntity")
    public ResponseEntity<String> importarXml(@RequestParam("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            XmlService.modifyUser(inputStream);
            return ResponseEntity.ok("Usuário alterado com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar o XML: " + e.getMessage());
        }
    }

    //DELETE USER
    @DeleteMapping("/ImportEntity")
    public ResponseEntity<String> deletarEntidade(@RequestParam("file") MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            XmlService.deleteXml(inputStream);
            return ResponseEntity.ok("Entidade apagada com sucesso.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar o XML: " + e.getMessage());
        }
    }
}