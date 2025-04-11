package com.osvaldo.ldapxml.service;

import com.osvaldo.ldapxml.model.Usuario;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class XmlUsuarioReader {

    public static List<Usuario> lerUsuarios(InputStream xmlInputStream) {
        List<Usuario> usuarios = new ArrayList<>();

        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlInputStream);


            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();
            NodeList addNodes = (NodeList) xpath.evaluate("//add[@class-name='Usuario']",
                    doc, XPathConstants.NODESET);

            System.out.println("Quantidade de usuários encontrados: " + addNodes.getLength());


            String regexNome = "^[\\p{L}0-9 ]+$";
            String regexLogin = "^[a-zA-Z0-9]+$";
            String regexTelefone = "^[0-9]+$";

            for (int i = 0; i < addNodes.getLength(); i++) {
                Node addNode = addNodes.item(i);
                NodeList attrNodes = ((Element) addNode).getElementsByTagName("add-attr");

                Usuario usuario = new Usuario();
                List<String> grupos = new ArrayList<>();

                for (int j = 0; j < attrNodes.getLength(); j++) {
                    Element attrElement = (Element) attrNodes.item(j);
                    String attrName = attrElement.getAttribute("attr-name");
                    NodeList valores = attrElement.getElementsByTagName("value");

                    if (valores.getLength() == 0) continue;

                    String valor = valores.item(0).getTextContent();

                    switch (attrName) {
                        case "Nome Completo":
                            if (valor.matches(regexNome)) {
                                usuario.setNomeCompleto(valor);
                            } else {
                                System.out.println("Nome inválido: " + valor);
                            }
                            break;

                        case "Login":
                            if (valor.matches(regexLogin)) {
                                usuario.setLogin(valor);
                            } else {
                                System.out.println("Login inválido: " + valor);
                            }
                            break;

                        case "Telefone":
                            String telefoneLimpo = valor.replaceAll("[^0-9]", "");
                            if (telefoneLimpo.matches(regexTelefone)) {
                                usuario.setTelefone(telefoneLimpo);
                            } else {
                                System.out.println("Telefone inválido: " + valor);
                            }
                            break;

                        case "Grupo":
                            for (int k = 0; k < valores.getLength(); k++) {
                                grupos.add(valores.item(k).getTextContent());
                            }
                            break;
                    }
                }

                usuario.setGrupos(grupos);

                if (usuario.getNomeCompleto() != null &&
                        usuario.getLogin() != null &&
                        usuario.getTelefone() != null) {
                    usuarios.add(usuario);
                } else {
                    System.out.println("Usuário ignorado por dados inválidos.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return usuarios;
    }
}
