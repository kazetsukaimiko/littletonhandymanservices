package com.littletonhandyman.javaee.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
 
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.StringWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;
// Helper to generate picoXML
public class QuickDOM {
  
  protected final Logger logger = Logger.getLogger(this.getClass().getName());

  private String elementName;

  // For setting XML Attributes to JSON values
  private ObjectMapper mapper = new ObjectMapper();

  // Document Builder
  private DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
  private DocumentBuilder docBuilder;
  private TransformerFactory transformerFactory;
  private Transformer transformer;
  private Document doc;
  private Element root;
  
  //private Map<String, Attr> attributes = new HashMap<String, Attr>();

  public QuickDOM() {
    //logger.info("QuickDOM init");
    try {
      docBuilder = docFactory.newDocumentBuilder();
      doc = docBuilder.newDocument();
      transformerFactory = TransformerFactory.newInstance();
      transformer = transformerFactory.newTransformer();
    } catch (Exception e) {
      logger.error("Exception!", e);
    }
  }
  
  public QuickDOM element(String anElementName) {
    try {
      //docBuilder = docFactory.newDocumentBuilder();
      //doc = docBuilder.newDocument();
      root = doc.createElement(anElementName);
    } catch (Exception e) {
      logger.error("Reset Failed!", e);
    } return this;
  }
  
  public QuickDOM jsonAttr(String attrName, Object value) {
    try {
      Attr attr = doc.createAttribute(attrName);
      attr.setValue(mapper.writeValueAsString(value));
      root.setAttributeNode(attr); 
    } catch (Exception e) {
      logger.error("jsonAttr Failed!", e);
    } return this;
  }

  public QuickDOM attr(String attrName, String value) {
    try {
      Attr attr = doc.createAttribute(attrName);
      attr.setValue(value);
      root.setAttributeNode(attr); 
    } catch (Exception e) {
      logger.error("attr Failed!", e);
    } return this;
  }
  
  public QuickDOM child(String text) {
    try {
      root.appendChild(doc.createTextNode(text));
    } catch (Exception e) {
      logger.error("child Failed!", e);
    } return this;
  }

  public String serialize(String alt) {
    String value = serialize(); return ((value != null)? value : alt);
  }
  
  public String serialize() {
    // write the content into xml file
    try {
      //doc.appendChild(root);
      //TransformerFactory transformerFactory = TransformerFactory.newInstance();
      //Transformer transformer = transformerFactory.newTransformer();
      StringWriter sr = new StringWriter();
      StreamResult result = new StreamResult(sr);
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      transformer.transform(new DOMSource(root), result);              
      return result.getWriter().toString(); //.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
    } catch (Exception e) {
      logger.error("Serialize Failed!", e);
    } return null;
  }
  
}
