package com.osvaldo.ldapxml.util;

import com.osvaldo.ldapxml.model.Usuario;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.File;
import java.util.*;

public class XmlUsuarioParser {

    public static List<Usuario> lerUsuarios(File xmlFile) throws Exception {
        List<Usuario> usuarios = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList addNodes = (NodeList) xpath.evaluate("/usuarios/add", doc, XPathConstants.NODESET);

        for (int i = 0; i < addNodes.getLength(); i++) {
            Usuario user = new Usuario();
            NodeList atributos = ((Element) addNodes.item(i)).getElementsByTagName("add-attr");

            List<String> grupos = new ArrayList<>();
            for (int j = 0; j < atributos.getLength(); j++) {
                Element attr = (Element) atributos.item(j);
                String nomeAttr = attr.getAttribute("attr-name");
                NodeList valores = attr.getElementsByTagName("value");

                switch (nomeAttr) {
                    case "Nome Completo":
                        user.setNomeCompleto(valores.item(0).getTextContent());
                        break;
                    case "Login":
                        user.setLogin(valores.item(0).getTextContent());
                        break;
                    case "Telefone":
                        user.setTelefone(valores.item(0).getTextContent());
                        break;
                    case "Grupo":
                        for (int k = 0; k < valores.getLength(); k++) {
                            grupos.add(valores.item(k).getTextContent());
                        }
                        break;
                }
            }
            user.setGrupos(grupos);
            usuarios.add(user);
        }

        return usuarios;
    }
}
