package com.lomicron.utils.parsing.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.lomicron.utils.parsing.tokenizer.Date;

import java.io.IOException;

public class DateSerializer extends StdSerializer<Date> {

    @SuppressWarnings("WeakerAccess")
    public DateSerializer(Class<Date> t) {
        super(t);
    }

    @Override
    public void serialize(
            Date value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {

        jgen.writeString(value.toString());
    }
}
