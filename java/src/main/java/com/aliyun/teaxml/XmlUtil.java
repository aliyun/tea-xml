package com.aliyun.teaxml;

import com.aliyun.tea.NameInMap;
import com.aliyun.tea.TeaModel;
import com.aliyun.tea.utils.StringUtils;
import com.google.gson.Gson;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlUtil {
    public static Map<String, Object> DeserializeXml(String xmlStr, Class type) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, DocumentException {
        Map<String, Object> result = new HashMap<String, Object>();
        if (StringUtils.isEmpty(xmlStr)) {
            return result;
        }
        Document contentXmlDoc;
        contentXmlDoc = DocumentHelper.parseText(xmlStr);
        Element rootElement = contentXmlDoc.getRootElement();
        if (type != null) {
            Field[] properties = type.getFields();
            Field resultField = null;
            for (Field field : properties) {
                field.setAccessible(true);
                NameInMap nameAnnotation = field.getAnnotation(NameInMap.class);
                String realName = nameAnnotation == null ? field.getName() : nameAnnotation.value();
                if (realName.equals(rootElement.getName())) {
                    resultField = field;
                    break;
                }
            }
            if (null != resultField) {
                if (String.class == resultField.getType()) {
                    result.put(rootElement.getName(), rootElement.getText());
                    return result;
                }
                result.put(rootElement.getName(), getValueFromXml(rootElement, resultField.getType()));
            }
        } else {
            ElementToMap(rootElement, result);
        }

        return result;
    }

    private static Map<String, Object> getValueFromXml(Element element, Class type) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Map<String, Object> nodeDict = new HashMap<>();
        Field[] subFields = type.getFields();
        for (Field subField : subFields) {
            NameInMap nameAnnotation = subField.getAnnotation(NameInMap.class);
            String realName = nameAnnotation == null ? subField.getName() : nameAnnotation.value();
            if (List.class.isAssignableFrom(subField.getType())) {
                List<Element> subElements = element.elements(realName);
                ParameterizedType listGenericType = (ParameterizedType) subField.getGenericType();
                Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                Type listActualTypeArgument = listActualTypeArguments[0];
                Class<?> itemType = null;
                if (listActualTypeArgument instanceof Class) {
                    itemType = (Class<?>) listActualTypeArgument;
                }

                List target = new ArrayList();
                for (int i = 0; i < subElements.size(); i++) {
                    target.add(TeaModel.toModel(getValueFromXml(subElements.get(i), itemType),
                            (TeaModel) itemType.newInstance()));
                }
                nodeDict.put(realName, target);
            } else if (TeaModel.class.isAssignableFrom(subField.getType())) {
                Element selfElement = element.element(realName);
                if (selfElement != null) {
                    nodeDict.put(realName, getValueFromXml(selfElement, subField.getType()));
                }
            } else {
                Element selfElement = element.element(realName);
                if (selfElement != null) {
                    nodeDict.put(realName, castType(subField.getType(), selfElement.getText()));
                }
            }
        }

        return nodeDict;
    }

    private static Object castType(Class clazz, String value) {
        if (boolean.class == clazz || Boolean.class == clazz) {
            return Boolean.parseBoolean(value);
        }
        if (int.class == clazz || Integer.class == clazz) {
            return Integer.parseInt(value);
        }
        if (long.class == clazz || Long.class == clazz) {
            return Long.parseLong(value);
        }
        return value;
    }

    private static Object ElementToMap(Element element, Map<String, Object> map) {
        List<Element> elements = element.elements();
        if (elements.size() == 0) {
            String context = StringUtils.isEmpty(element.getTextTrim()) ? null : element.getTextTrim();
            if (null != map) {
                map.put(element.getName(), context);
            }
            return context;
        } else {
            Map<String, Object> subMap = new HashMap<String, Object>();
            if (null != map) {
                map.put(element.getName(), subMap);
            }
            for (Element elem : elements) {
                if (subMap.containsKey(elem.getName())) {
                    Object o = subMap.get(elem.getName());
                    Class clazz = o.getClass();
                    if (List.class.isAssignableFrom(clazz)) {
                        ((List) o).add(ElementToMap(elem, null));
                    } else if (Map.class.isAssignableFrom(clazz)) {
                        List list = new ArrayList();
                        Map remove = (Map) subMap.remove(elem.getName());
                        list.add(remove);
                        list.add(ElementToMap(elem, null));
                        subMap.put(elem.getName(), list);
                    } else {
                        List list = new ArrayList();
                        Object remove = subMap.remove(elem.getName());
                        list.add(remove);
                        list.add(ElementToMap(elem, null));
                        subMap.put(elem.getName(), list);
                    }
                } else {
                    ElementToMap(elem, subMap);
                }
            }
            return subMap;
        }
    }

    public static String mapToXml(Map<String, Object> map) throws IllegalAccessException {
        if (null == map || map.isEmpty()) {
            return "";
        }
        if (map.size() != 1) {
            throw new RuntimeException("This map does not have a unique root tag");
        }
        String rootName = map.keySet().toArray(new String[]{})[0];
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(rootName);
        setValue(root, rootName, map.get(rootName));
        return document.asXML();
    }

    private static void setValue(Element element, String key, Object value) throws IllegalAccessException {
        Class clazz = value.getClass();
        if (Map.class.isAssignableFrom(clazz)) {
            Map<String, Object> map = (Map<String, Object>) value;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                if (List.class.isAssignableFrom(entry.getValue().getClass())) {
                    setValue(element, entry.getKey(), entry.getValue());
                    continue;
                }
                Element subElement = element.addElement(entry.getKey());
                setValue(subElement, entry.getKey(), entry.getValue());
            }
        } else if (List.class.isAssignableFrom(clazz)) {
            List<Object> list = (List) value;
            for (Object sub : list) {
                if (sub == null) {
                    continue;
                }
                Element subElement = element.addElement(key);
                setValue(subElement, key, sub);
            }
        } else if (TeaModel.class.isAssignableFrom(clazz)) {
            Map<String, Object> map = ((TeaModel) value).toMap();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                if (List.class.isAssignableFrom(entry.getValue().getClass())) {
                    setValue(element, entry.getKey(), entry.getValue());
                    continue;
                }
                Element subElement = element.addElement(entry.getKey());
                setValue(subElement, entry.getKey(), entry.getValue());
            }
        } else {
            element.setText(String.valueOf(value));
        }
    }
}
