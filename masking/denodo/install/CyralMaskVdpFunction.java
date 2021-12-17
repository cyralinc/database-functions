/*
 * Copyright (c) 2015. DENODO Technologies.
 * http://www.denodo.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of DENODO
 * Technologies ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with DENODO.
 */
package com.cyral.masking.function;

import com.denodo.common.custom.annotations.CustomElement;
import com.denodo.common.custom.annotations.CustomElementType;
import com.denodo.common.custom.annotations.CustomExecutor;
import com.denodo.common.custom.elements.CustomElementsUtil;
import com.denodo.common.custom.elements.QueryContext;
import com.denodo.common.custom.annotations.CustomParam;

import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.Json;

import java.io.StringReader;

@CustomElement(type = CustomElementType.VDPFUNCTION, name = "CyralMask")
public class CyralMaskVdpFunction {
    public String defaultConstantNumber = "0";
    public String defaultConstantText = "REDACTED";

    private Character cyralInternalGenerateChar(@CustomParam(name="value")Character value) {
        if (value >= 'a' && value <= 'z') {
            java.util.Random r = new java.util.Random();
            char c = (char)(r.nextInt(26) + 'a');
            return c;
        }

        if (value >= 'A' && value <= 'Z') {
            java.util.Random r = new java.util.Random();
            char c = (char)(r.nextInt(26) + 'a');
            return Character.toUpperCase(c);
        }

        if (value >= '0' && value <= '9') {
            java.util.Random r = new java.util.Random();
            char c = (char)(r.nextInt(10-0) + '0');
            return c;
        }
        return value;
    }

    private String cyralInternalPreserveMask(@CustomParam(name="value")String value) {
        String return_value = "";
        for (int i = 0; i < value.length(); i++) {
            return_value = return_value.concat(Character.toString(cyralInternalGenerateChar(value.charAt(i))));
        }
        return return_value;
    }


    @CustomExecutor
    public Integer cyralMaskInteger(@CustomParam(name="field_value")Integer field_value, @CustomParam(name="mask_type")String mask) {
        JsonObject jsonObj = parseJSON(mask);
        String mask_type = jsonObj.getString("maskFunction");

        if(mask_type.equals("null_mask")) {
          return null;
        }

        if(mask_type.equals("mask")) {
           return Integer.parseInt(cyralInternalPreserveMask(field_value.toString()));
        }

        if(mask_type.equals("constant_mask")) {
            return jsonObj.getJsonArray("args").getJsonNumber(0).intValue(); 
        }

        return field_value; 
    }

    @CustomExecutor
    public byte[] cyralMaskByte(@CustomParam(name="field_value")byte[] field_value, @CustomParam(name="mask_type")String mask) {
        JsonObject jsonObj = parseJSON(mask);
        String mask_type = jsonObj.getString("maskFunction");

        if(mask_type.equals("null_mask")) {
          byte[] temp = null;
          return temp;
        }
        return field_value;
    }

    @CustomExecutor
    public Boolean cyralMaskBool(@CustomParam(name="field_value")Boolean field_value, @CustomParam(name="mask_type")String mask) {
        JsonObject jsonObj = parseJSON(mask);
        String mask_type = jsonObj.getString("maskFunction");

        if(mask_type.equals("null_mask")) {
          return null;
        }
        return field_value;
    }

    @CustomExecutor
    public java.math.BigDecimal cyralMaskBigDecimal(@CustomParam(name="field_value")java.math.BigDecimal field_value, @CustomParam(name="mask_type")String mask) {
        JsonObject jsonObj = parseJSON(mask);
        String mask_type = jsonObj.getString("maskFunction");

        if(mask_type.equals("null_mask")) {
          return null;
        }

        if(mask_type.equals("mask")) {
          java.math.BigDecimal bigDecimal = new java.math.BigDecimal(cyralInternalPreserveMask(field_value.toString()));
          return bigDecimal;
        }

        if(mask_type.equals("constant_mask")) {
          return jsonObj.getJsonArray("args").getJsonNumber(0).bigDecimalValue();
        }

        return field_value;
    }

    @CustomExecutor
    public Double cyralMaskDouble(@CustomParam(name="field_value")Double field_value, @CustomParam(name="mask_type")String mask) {
        JsonObject jsonObj = parseJSON(mask);
        String mask_type = jsonObj.getString("maskFunction");

        if(mask_type.equals("null_mask")) {
          return null;
        }

        if(mask_type.equals("mask")) {
           return Double.valueOf(cyralInternalPreserveMask(field_value+""));
        }
        
        if(mask_type.equals("constant_mask")) {
          return jsonObj.getJsonArray("args").getJsonNumber(0).doubleValue();
        }

        return field_value;
    }

    @CustomExecutor
    public Float cyralMaskFloat(@CustomParam(name="field_value")Float field_value, @CustomParam(name="mask_type")String mask) {
        JsonObject jsonObj = parseJSON(mask);
        String mask_type = jsonObj.getString("maskFunction");

        if(mask_type.equals("null_mask")) {
          return null;
        }

        if(mask_type.equals("mask")) {
           return Float.parseFloat(cyralInternalPreserveMask(field_value.toString()));
        }
        
        if(mask_type.equals("constant_mask")) {
          return (float) jsonObj.getJsonArray("args").getJsonNumber(0).doubleValue();
        }

        return field_value;
    }

    @CustomExecutor
    public java.time.Duration cyralMaskTimeDuration(@CustomParam(name="field_value")java.time.Duration field_value, @CustomParam(name="mask_type")String mask) {
        JsonObject jsonObj = parseJSON(mask);
        String mask_type = jsonObj.getString("maskFunction");

        if(mask_type.equals("null_mask")) {
          return null;
        }
        return field_value;
    }

    @CustomExecutor
    public java.time.Period cyralMaskTimePeriod(@CustomParam(name="field_value")java.time.Period field_value, @CustomParam(name="mask_type")String mask) {
        JsonObject jsonObj = parseJSON(mask);
        String mask_type = jsonObj.getString("maskFunction");

        if(mask_type.equals("null_mask")) {
          return null;
        }
        return field_value;
    }

    @CustomExecutor
    public java.time.LocalDate cyralMaskLocalDate(@CustomParam(name="field_value")java.time.LocalDate field_value, @CustomParam(name="mask_type")String mask) {
        JsonObject jsonObj = parseJSON(mask);
        String mask_type = jsonObj.getString("maskFunction");

        if(mask_type.equals("null_mask")) {
          return null;
        }
        return field_value;
    }

    @CustomExecutor
    public Long cyralMaskLong(@CustomParam(name="field_value")Long field_value, @CustomParam(name="mask_type")String mask) {
        JsonObject jsonObj = parseJSON(mask);
        String mask_type = jsonObj.getString("maskFunction");

        if(mask_type.equals("null_mask")) {
          return null;
        }

        if(mask_type.equals("mask")) {
           return Long.parseLong(cyralInternalPreserveMask(field_value.toString()));
        }

        if(mask_type.equals("constant_mask")) {
          return jsonObj.getJsonArray("args").getJsonNumber(0).longValue();
        }

        return field_value;
    }

    @CustomExecutor
    public java.time.LocalTime cyralMaskLocalTime(@CustomParam(name="field_value")java.time.LocalTime field_value, @CustomParam(name="mask_type")String mask) {
        JsonObject jsonObj = parseJSON(mask);
        String mask_type = jsonObj.getString("maskFunction");

        if(mask_type.equals("null_mask")) {
          return null;
        }
        return field_value;
    }

    @CustomExecutor
    public java.time.LocalDateTime cyralMaskLocalDateTime(@CustomParam(name="field_value")java.time.LocalDateTime field_value, @CustomParam(name="mask_type")String mask) {
        JsonObject jsonObj = parseJSON(mask);
        String mask_type = jsonObj.getString("maskFunction");

        if(mask_type.equals("null_mask")) {
          return null;
        }
        return field_value;
    }

    @CustomExecutor
    public java.time.ZonedDateTime cyralMaskZonedDateTime(@CustomParam(name="field_value")java.time.ZonedDateTime field_value, @CustomParam(name="mask_type")String mask) {
        JsonObject jsonObj = parseJSON(mask);
        String mask_type = jsonObj.getString("maskFunction");

        if(mask_type.equals("null_mask")) {
          return null;
        }
        return field_value;
    }

    @CustomExecutor
    public java.util.Calendar cyralMaskCalendar(@CustomParam(name="field_value")java.util.Calendar field_value, @CustomParam(name="mask_type")String mask) {
        JsonObject jsonObj = parseJSON(mask);
        String mask_type = jsonObj.getString("maskFunction");

        if(mask_type.equals("null_mask")) {
          return null;
        }
        return field_value;
    }

    @CustomExecutor
    public String cyralMaskString(@CustomParam(name="field_value")String field_value, @CustomParam(name="mask")String mask) {
        JsonObject jsonObj = parseJSON(mask);
        String mask_type = jsonObj.getString("maskFunction");

        if(mask_type.equals("null_mask")) {
          return null;
        }

        if(mask_type.equals("mask")) {
          return cyralInternalPreserveMask(field_value);
        }

        if(mask_type.equals("constant_mask")) {
          String mask_param = jsonObj.getJsonArray("args").getString(0);

          // Let's make sure we got a param
          if(mask_param.isEmpty()) {
            mask_param = defaultConstantText;
          }

          return mask_param;
        }
        return field_value;
    }

    private JsonObject parseJSON(String json_value) {
        return Json.createReader(new StringReader(json_value)).readObject();
    }
}

