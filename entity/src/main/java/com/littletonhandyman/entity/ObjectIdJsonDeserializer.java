package com.littletonhandyman.entity;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

//import javax.xml.bind.annotation.adapters.XmlAdapter;
//import com.fasterxml.jackson.databind.JsonGenerator;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class ObjectIdJsonDeserializer extends JsonDeserializer<ObjectId> {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public ObjectId deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
	  ObjectCodec oc = jsonParser.getCodec();
	  JsonNode jsonNode = oc.readTree(jsonParser);
	  return unmarshall(jsonNode.textValue());
	}

	public ObjectId unmarshall( String s ) {
	  //logger.info("UnMarshalling...");
	  if (s == null) {
	    return null;
	  } return new ObjectId(s);
	}
	public String marshal( ObjectId id ) {
	  //logger.info("Marshalling...");
		return id.toString();
	}
}  
