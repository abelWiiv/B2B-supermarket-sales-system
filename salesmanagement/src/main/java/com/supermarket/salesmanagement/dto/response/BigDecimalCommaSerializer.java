package com.supermarket.salesmanagement.dto.response;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BigDecimalCommaSerializer extends JsonSerializer<BigDecimal> {
    private static final DecimalFormat formatter = new DecimalFormat("#,##0.00");

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            gen.writeString(formatter.format(value));
        } else {
            gen.writeNull();
        }
    }
}