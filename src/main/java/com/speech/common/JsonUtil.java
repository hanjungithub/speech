package com.speech.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.text.SimpleDateFormat;


/**
 * @author wangbo
 */
@Slf4j
public class JsonUtil {
    private static ObjectMapper objectMapper=new ObjectMapper();
    static{
        //对象的所有字段全部列入
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);
        //取消日期默认转换timestamps形式
        objectMapper.configure(Feature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        //忽略空bean转json的错误，空bean就是没有getset方法
        objectMapper.configure(Feature.FAIL_ON_EMPTY_BEANS, false);
        //序列化的时候统一日期
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        //在反序列化的时候json对象存在，实体类对象不存在的错误进行忽略
        objectMapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }
    /**
     * 序列化成String
     * @param obj
     * @return
     */
    public static <T> String objtoString(T obj){
        if(obj==null){
            return null;
        }
        try {
            return obj instanceof String ?(String) obj :objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("序列化成String失败",e);
            return null;
        }
    }
    /**
     * 优雅的序列化成String
     * @param obj
     * @return
     */
    public static <T> String objtoStringPretty(T obj){
        if(obj==null){
            return null;
        }
        try {
            return obj instanceof String ?(String) obj :objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("序列化成String失败",e);
            return null;
        }
    }

    /**
     * 把string反序列成对象
     * @param str
     * @param clazz
     * @return
     */
    public static <T> T stringToobj(String str,Class<T> clazz){
        if(StringUtils.isEmpty(str)||clazz==null){
            return null;
        }
        try {
            return clazz.equals(String.class)?(T)str:objectMapper.readValue(str, clazz);
        } catch (Exception e) {
            log.warn("反序列化失败",e);
            return null;
        }


    }

    /**
     * 反序列化泛型集合
     * @param str
     * @param typeReference
     * @return
     */
    public static <T> T stringToobj(String str,TypeReference<T> typeReference){
        if(StringUtils.isEmpty(str)||typeReference==null){
            return null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class)?str:objectMapper.readValue(str, typeReference));
        } catch (Exception e) {
            log.warn("反序列化泛型集合失败",e);
            return null;
        }
    }

    /**
     * 反序列化泛型集合（有选择性的）
     * @param str
     * @return
     */
    public static <T> T stringToobj(String str,Class<?> collectionClass,Class<?>... elementClasse){
        JavaType javaType=objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasse);
        try {
            return objectMapper.readValue(str, javaType);
        } catch (Exception e) {
            log.warn("有选择性反序列化泛型集合失败",e);
            return null;
        }
    }


}