package com.shs.crm.service.modelmapper;

import org.modelmapper.spi.NameTokenizer;
import org.modelmapper.spi.NameableType;

public class CustomMapperTokenizer implements NameTokenizer {
    @Override
    public String[] tokenize(String s, NameableType nameableType) {
        return new String[]{s};
    }
}
