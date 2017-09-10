package com.littletonhandyman.javaee.util;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import java.util.*;
import java.io.Writer;
import java.io.StringWriter;

import org.apache.velocity.*;
import org.apache.velocity.app.*;
import org.apache.velocity.exception.*;
import org.apache.velocity.runtime.*;
import org.apache.velocity.runtime.resource.loader.*;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.littletonhandyman.javaee.util.QuickDOM;

import com.fasterxml.jackson.databind.ObjectMapper;

public class VelocityRenderer {
  
  private final Logger logger = Logger.getLogger(this.getClass().getName());

  //@Inject
  public QuickDOM qd = new QuickDOM();
  public QuickDOM getQd() {
    return qd;
  }
  
  @Inject
  protected ServletContext context;

  @Inject
  protected HttpSession session;
  
  @Inject
  protected HttpServletRequest request;
  
  //protected QuickDOM qd = new QuickDOM();
  protected ObjectMapper mapper = new ObjectMapper();
  
  protected VelocityEngine ve;
  protected VelocityContext vc;
  
  public VelocityRenderer() {
    ve = getEngine();
    vc = new VelocityContext();
    defaultContext();
    resetFluent();
  }
  
  public VelocityEngine getEngine() {
    VelocityEngine engine = new VelocityEngine();
    engine.setProperty(RuntimeConstants.ENCODING_DEFAULT, "UTF-8");
    engine.setProperty(RuntimeConstants.OUTPUT_ENCODING, "UTF-8");
    engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
    engine.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());
    engine.init();
    return engine;
  }

  public VelocityRenderer(VelocityEngine aVe) {
    ve = aVe;
    if (aVe == null) {
      ve = getEngine();
    }
    vc = new VelocityContext();
    defaultContext();
    resetFluent();
  }
  
  protected void defaultContext() {
    for(Object key : vc.getKeys()) {
      vc.remove(key);
    } 
    vc.put("vm", this);
    vc.put("context", context);
    vc.put("session", session);
    vc.put("request", request);
  }
  
  protected void setContext(Map<String, ?> values) {
    defaultContext();
    for (Map.Entry<String, ?> variable : values.entrySet()) {
      vc.put(variable.getKey(), variable.getValue());
    }
  }
  
  protected String render(String key, Object value, String aTemplateName) {
    Map<String, Object> values = new HashMap<String, Object>();
    values.put(key, value);
    return render(values, aTemplateName);
  }

  protected String render(String aTemplateName) {
    return render(new HashMap<String, Object>(), aTemplateName);
  }
  
  protected String render(Map<String, Object> values, String aTemplateName) {
    setContext(values);
    StringWriter wr = new StringWriter();
    Template vt = ve.getTemplate(aTemplateName);
    vt.merge(vc, wr);
    String output = wr.toString();
    wr.flush();
    return output;
  }
  
  
  protected String templateName = null;
  protected Map<String, Object> fluentContext = new HashMap<String, Object>();
  public void resetFluent() {
    fluentContext = new HashMap<String, Object>();
    for(Object key : vc.getKeys()) {
      if (key instanceof String) {
        fluentContext.put((String)key, vc.get((String)key));
      }
    }
  }
  
  public VelocityRenderer build(String aTemplateName) {
    defaultContext();
    resetFluent();
    templateName = aTemplateName;
    return this;
  }
  
  public VelocityRenderer template(String aTemplateName) {
    return (new VelocityRenderer(ve)).build(aTemplateName);
  }
  
  public VelocityRenderer put(String variableName, Object value) {
   fluentContext.put(variableName, value); return this;
  }

  public VelocityRenderer putAll(Map<String, Object> variables) {
   for (Map.Entry<String, Object> entry : variables.entrySet()) {
     fluentContext.put(entry.getKey(), entry.getValue()); 
   } return this;
  }
  public VelocityRenderer sub(String variableName, Map<String, Object> subValues, String aTemplateName) {
    fluentContext.put(variableName, render(subValues, aTemplateName)); return this;
  }


  public String inline(String variableName, Object inlineValue, String aTemplateName) {
    return inline(variableName, inlineValue, aTemplateName, 0);
  }

  public String inline(String variableName, Object inlineValue, String aTemplateName, String indentString) {
    StringWriter wr = new StringWriter();
    Template vt = ve.getTemplate(aTemplateName);
    defaultContext();
    vc.put(variableName, inlineValue);
    vt.merge(vc, wr);
    String result = wr.toString(); wr.flush();
    return result.replaceAll("\n", "\n"+indentString);
  }

  public String inline(String variableName, Object inlineValue, String aTemplateName, int indent) {
    String indentString = "";
    for (int i=0; i < indent; i++) {
      indentString = indentString.concat(" ");
    } return inline(variableName, inlineValue, aTemplateName, indentString);
  }

  public String inlineAll(String variableName, List<Object> inlineValues, String aTemplateName, int indent) {
    String result = "";
    for (Object inlineValue : inlineValues) {
      result = result.concat(inline(variableName, inlineValue, aTemplateName, indent));
    } return result;
  }

  public String inlineAll(String variableName, List<Object> inlineValues, String aTemplateName) {
    return inlineAll(variableName, inlineValues, aTemplateName, 0);
  }

  public VelocityRenderer sub(String variableName, String subValueName, Object subValue, String aTemplateName) {
    fluentContext.put(variableName, render(subValueName, subValue, aTemplateName)); return this;
  }

  public <OT> VelocityRenderer subAll(String variableName, String subValueName, List<OT> subValues, String aTemplateName) {
    StringWriter wr = new StringWriter();
    Template vt = ve.getTemplate(aTemplateName);
    for (OT subValue : subValues) {
     defaultContext();
     vc.put(subValueName, subValue);
     vt.merge(vc, wr);
    } fluentContext.put(variableName, wr.toString()); wr.flush();
    return this;
  }
  public VelocityRenderer sub(String variableName, String aTemplateName) {
    fluentContext.put(variableName, render(aTemplateName)); return this;
  }
  public VelocityRenderer subDirect(String variableName, String templateValue) {
    StringWriter wr = new StringWriter();
    defaultContext();
    for (String contextKey : fluentContext.keySet()) {
      vc.put(contextKey, fluentContext.get(contextKey));
    } ve.evaluate(vc, wr, "subDirect", templateValue);
    fluentContext.put(variableName, wr.toString()); wr.flush(); return this;
  }

  public String render() {
    return render(fluentContext, templateName);
  }

  public String renderString(String template) {
    return renderString(new HashMap<String, Object>(), template);
  }
  
  public String renderString(Map<String, Object> values, String template) {
    if (template != null){
      setContext(values);
      StringWriter wr = new StringWriter();
      if (ve == null) {
        logger.info ("VELOCITY ENGINE NULL!!!!!!!!!!!");
      }
      if (vc == null) {
        logger.info ("VELOCITY CONTEXT NULL!!!!!!!!!!!");
      }
      if (template == null) {
        logger.info ("VELOCITY TEMPLATE NULL!!!!!!!!!!!");
      }
      ve.evaluate(vc, wr, "renderString", template);
      String output = wr.toString();
      wr.flush();
      return output;
    } return null;
  }
  
  public String resolve(String template) {
    return renderString(fluentContext, template);
  }
  
  public String DataLink(String linkTitle, Object o) {
    String json = "";
    try {
      json = mapper.writeValueAsString(o);
    } catch (Exception e) {
      logger.error("DataLink Failed!", e);
    }
    return qd.element("a")
      .attr("class", "datalink")
      .attr("href", "#"+json)
      .jsonAttr("data-link", o)
      .child(resolve(linkTitle)).serialize();
  }  
  
}