package com.shs.crm.service.modelmapper;

import org.hibernate.Hibernate;
import org.modelmapper.spi.ValueReader;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomValueReader implements ValueReader<Object> {
    @Override
    public Object get(Object source, String memberName) {
        try {
            PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(source.getClass(),memberName);
            return pd.getReadMethod().invoke(source);
        }
        catch(Exception e) {
            return null;
        }
    }

    @Override
    public Member<Object> getMember(Object source, String memberName) {
        final Object value = get(source, memberName);
        Class<?> memberType = value != null ? value.getClass() : Object.class;
        return new Member<Object>(memberType) {

            @Override
            public Object get(Object source, String memberName) {
                return CustomValueReader.this.get(source, memberName);
            }
        };
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Collection<String> memberNames(Object source) {
        List<String> properties = new ArrayList<>();
        List<Class> classes = new ArrayList<>();

        var propertyDescriptors = BeanUtils.getPropertyDescriptors(source.getClass());

        for (PropertyDescriptor pd : propertyDescriptors) {
            boolean shouldSkip = !(pd.getReadMethod() != null && !"class".equals(pd.getName()));
            if(shouldSkip){
                continue;
            }

            boolean valid = true;
            Object fieldValue = null;
             try{
                 fieldValue = pd.getReadMethod().invoke(source);
             }
             catch(Exception e) {
                 valid = false;
             }
            boolean isInited = valid && Hibernate.isInitialized(fieldValue);
             if (isInited){
                 properties.add(pd.getName());
             }
        }
        return properties;
    }

    @Override
    public String toString() {
        return "Costum";
    }
}
