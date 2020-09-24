package com.lomicron.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class ObjectNodeDeserializer extends StdDeserializer<ObjectNode> {

    public ObjectNodeDeserializer() {
        this(null);
    }

    public ObjectNodeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ObjectNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        if (node.isNull()) return null;
        else return (ObjectNode) node;
    }
}
