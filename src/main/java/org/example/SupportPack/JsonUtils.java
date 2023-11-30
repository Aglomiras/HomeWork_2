package org.example.SupportPack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    private static ObjectMapper mapper = new ObjectMapper();

    public static String code(Object o) {
        String s = null;
        try {
            s = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
        return s;
    }
}
