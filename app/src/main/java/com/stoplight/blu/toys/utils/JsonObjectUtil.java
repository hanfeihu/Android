package com.stoplight.blu.toys.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Map;

public class JsonObjectUtil {
 
    /**
     * object转换成JSON 
     * @param object
     * @return
     */
    public static String convertMapToJson(Object object) {
        if (null == object) {
            return null;
        }
        return JSON.toJSONString(object,
                SerializerFeature.DisableCircularReferenceDetect);
    }
 
    /**
     * JSON转换成Map 
     * @param json
     * @return
     */
    public static Map<String, Object> convertJsonToMap(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        JSONArray jsonarray = JSON.parseArray(json);
        Map<String, Object> map = null;
        if (jsonarray.size() > 1) {
            JSONObject jsonObject = jsonarray.getJSONObject(1);
            map = (Map<String, Object>) JSONObject.toJavaObject(jsonObject, Map.class);
        }
        return map;
    }

 
    public static boolean isEmpty(JSONObject json) {
        return json == null || json.isEmpty();
    }
 
    public static boolean isEmpty(JSONArray array) {
        return array == null || array.isEmpty();
    }
 
    public static JSONObject getDataResult(JSONObject json) {
        JSONObject data = null;
 
        JSONObject tempData = json.getJSONObject("data");
        if (tempData == null || tempData.isEmpty()) {
            return null;
        }
 
        if (tempData.containsKey("data")) {
            try {
                data = tempData.getJSONObject("data");
            } catch (Exception e) { 
                data = null;
            }
        }
        return data;
    }
 
}