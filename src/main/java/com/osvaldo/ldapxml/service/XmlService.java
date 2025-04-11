package com.osvaldo.ldapxml.service;

import com.osvaldo.ldapxml.config.LdapConnection;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class XmlService {

    public void addXml(InputStream xmlInputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlInputStream);

        Element root = document.getDocumentElement();
        String className = root.getAttribute("class-name");

        if (root.getNodeName().equals("add")) {
            if (className.equalsIgnoreCase("Usuario")) {
                adicionarUsuario(root);
            } else if (className.equalsIgnoreCase("Grupo")) {
                adicionarGrupo(root);
            }
        }
    }


    public void modifyUser(InputStream xmlInputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlInputStream);
        Element root = document.getDocumentElement();

        String className = root.getAttribute("class-name");
        if (root.getNodeName().equals("modify")) {
            if (className.equalsIgnoreCase("Usuario")) {
                modificarUsuario(root);
            }
        }

    }

    public void deleteXml(InputStream xmlInputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlInputStream);

        Element root = document.getDocumentElement();
        String className = root.getAttribute("class-name");

        if (root.getNodeName().equals("delete")) {
            if (className.equalsIgnoreCase("Usuario")) {
                deletarUsuario(root);
            } else if (className.equalsIgnoreCase("Grupo")) {
                deletarGrupo(root);
            }
        }
    }


    private void adicionarUsuario(Element root) throws NamingException {
        Attributes attrs = new BasicAttributes(true);
        Attribute objClass = new BasicAttribute("objectClass");
        objClass.add("inetOrgPerson");

        String uid = null;
        List<String> grupos = new ArrayList<>();

        NodeList attrList = root.getElementsByTagName("add-attr");
        for (int i = 0; i < attrList.getLength(); i++) {
            Element attrElement = (Element) attrList.item(i);
            String attrName = attrElement.getAttribute("attr-name");

            NodeList values = attrElement.getElementsByTagName("value");
            for (int j = 0; j < values.getLength(); j++) {
                String value = values.item(j).getTextContent();

                switch (attrName.toLowerCase()) {
                    case "login":
                        uid = value.toLowerCase();
                        attrs.put("uid", uid);
                        attrs.put("cn", value);
                        attrs.put("sn", value);
                        break;
                    case "nome completo":
                        attrs.put("displayName", value);
                        break;
                    case "telefone":
                        attrs.put("telephoneNumber", value);
                        break;
                    case "grupo":
                        grupos.add(value);
                        break;
                }
            }
        }

        attrs.put(objClass);

        String userDn = "uid=" + uid + ",ou=user,dc=local,dc=com";
        LdapConnection.getContext().bind(userDn, null, attrs);

        // add user in groups
        for (String grupo : grupos) {
            String grupoDn = "cn=" + grupo + ",ou=groups,dc=local,dc=com";
            Attribute memberAttr = new BasicAttribute("member", userDn);
            ModificationItem[] mods = new ModificationItem[]{
                    new ModificationItem(DirContext.ADD_ATTRIBUTE, memberAttr)
            };

            try {
                LdapConnection.getContext().modifyAttributes(grupoDn, mods);
            } catch (NameNotFoundException e) {
                System.err.println("Grupo " + grupo + " não encontrado: " + grupoDn);
            }
        }
    }


    private void adicionarGrupo(Element root) throws NamingException {
        Attributes attrs = new BasicAttributes(true);
        Attribute objClass = new BasicAttribute("objectClass");
        objClass.add("groupOfNames");

        String cn = null;

        NodeList attrList = root.getElementsByTagName("add-attr");
        for (int i = 0; i < attrList.getLength(); i++) {
            Element attrElement = (Element) attrList.item(i);
            String attrName = attrElement.getAttribute("attr-name");

            NodeList values = attrElement.getElementsByTagName("value");
            for (int j = 0; j < values.getLength(); j++) {
                String value = values.item(j).getTextContent();
                if (attrName.equalsIgnoreCase("Identificador")) {
                    cn = value;
                    attrs.put("cn", cn);
                } else if (attrName.equalsIgnoreCase("Descricao")) {
                    attrs.put("description", value);
                }
            }
        }


        attrs.put("member", "cn=admin");

        attrs.put(objClass);

        String dn = "cn=" + cn + ",ou=groups,dc=local,dc=com";
        LdapConnection.getContext().bind(dn, null, attrs);
    }

    private void modificarUsuario(Element root) throws NamingException {
        String login = root.getElementsByTagName("association").item(0).getTextContent().toLowerCase();
        String dn = "uid=" + login + ",ou=user,dc=local,dc=com";

        DirContext ctx = LdapConnection.getContext();


        NodeList modifyAttrs = root.getElementsByTagName("modify-attr");
        for (int i = 0; i < modifyAttrs.getLength(); i++) {
            Element attr = (Element) modifyAttrs.item(i);
            String attrName = attr.getAttribute("attr-name");


            NodeList removeNodes = attr.getElementsByTagName("remove-value");
            for (int j = 0; j < removeNodes.getLength(); j++) {
                Element removeElement = (Element) removeNodes.item(j);
                NodeList values = removeElement.getElementsByTagName("value");

                for (int k = 0; k < values.getLength(); k++) {
                    String value = values.item(k).getTextContent();
                    ModificationItem[] mods = new ModificationItem[1];
                    Attribute modAttr = new BasicAttribute(attrName, value);
                    mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, modAttr);
                    ctx.modifyAttributes(dn, mods);
                }
            }


            NodeList addNodes = attr.getElementsByTagName("add-value");
            for (int j = 0; j < addNodes.getLength(); j++) {
                Element addElement = (Element) addNodes.item(j);
                NodeList values = addElement.getElementsByTagName("value");

                for (int k = 0; k < values.getLength(); k++) {
                    String value = values.item(k).getTextContent();
                    ModificationItem[] mods = new ModificationItem[1];
                    Attribute modAttr = new BasicAttribute(attrName, value);
                    mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, modAttr);
                    ctx.modifyAttributes(dn, mods);
                }
            }
        }

        ctx.close();
    }



    private void deletarUsuario(Element root) throws NamingException {
        String login = root.getElementsByTagName("association").item(0).getTextContent().toLowerCase();
        String dn = "uid=" + login + ",ou=user,dc=local,dc=com";

        DirContext ctx = LdapConnection.getContext();
        ctx.unbind(dn);
        ctx.close();
    }

    private void deletarGrupo(Element root) throws NamingException {
        String identificador = root.getElementsByTagName("association").item(0).getTextContent();
        String dn = "cn=" + identificador + ",ou=groups,dc=local,dc=com";

        DirContext ctx = LdapConnection.getContext();
        ctx.unbind(dn);
        ctx.close();
    }



}
