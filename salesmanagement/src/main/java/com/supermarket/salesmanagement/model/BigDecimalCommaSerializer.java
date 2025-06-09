package com.supermarket.salesmanagement.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BigDecimalCommaSerializer extends StdSerializer<BigDecimal> {

    private static final DecimalFormat formatter = new DecimalFormat("#,##0.00");

    public BigDecimalCommaSerializer() {
        super(BigDecimal.class);
    }

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value != null) {
            gen.writeString(formatter.format(value));
        } else {
            gen.writeNull();
        }
    }
}