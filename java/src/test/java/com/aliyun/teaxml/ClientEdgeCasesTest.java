package com.aliyun.teaxml;

import com.aliyun.tea.NameInMap;
import com.aliyun.tea.TeaModel;
import org.dom4j.DocumentException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClientEdgeCasesTest {
    public static class TextResponse extends TeaModel {
        @NameInMap("root")
        public String text;
    }

    public static class ModelResponse extends TeaModel {
        public Values root;
    }

    public static class Values extends TeaModel {
        public boolean enabled;
        public int count;
        public long total;
        public List<Boolean> flags;
        public List<Integer> numbers;
        public List<Long> totals;
        public Values missing;
    }

    @Test
    public void parsesEmptyAndStringResponses() {
        Assert.assertTrue(Client.parseXml(null, null).isEmpty());
        Assert.assertTrue(Client.parseXml("", TextResponse.class).isEmpty());
        Assert.assertEquals(Collections.<String, Object>singletonMap("root", "你好 & < >"),
                Client.parseXml("<root>你好 &amp; &lt; &gt;</root>", TextResponse.class));
        Assert.assertTrue(Client.parseXml("<unmatched>value</unmatched>", TextResponse.class).isEmpty());
    }

    @Test
    public void parsesTypedScalarsAndLists() {
        Map root = (Map) Client.parseXml("<root><enabled>true</enabled><count>42</count>"
                + "<total>4294967296</total><flags>true</flags><flags>false</flags>"
                + "<numbers>1</numbers><numbers>2</numbers><totals>4294967296</totals></root>",
                ModelResponse.class).get("root");
        Assert.assertEquals(true, root.get("enabled"));
        Assert.assertEquals(42, root.get("count"));
        Assert.assertEquals(4294967296L, root.get("total"));
        Assert.assertEquals(Arrays.asList(true, false), root.get("flags"));
        Assert.assertEquals(Arrays.asList(1, 2), root.get("numbers"));
        Assert.assertEquals(Collections.singletonList(4294967296L), root.get("totals"));
        Assert.assertFalse(root.containsKey("missing"));
    }

    @Test
    public void preservesRepeatedScalarsAndObjectsInOrder() {
        Map root = (Map) Client.parseXml("<root><item>first</item><item>second</item><item>third</item>"
                + "<object><name>one</name></object><object><name>two</name></object>"
                + "<object><name>three</name></object></root>", null).get("root");
        Assert.assertEquals(Arrays.asList("first", "second", "third"), root.get("item"));
        List objects = (List) root.get("object");
        Assert.assertEquals(3, objects.size());
        Assert.assertEquals(Collections.singletonMap("name", "one"), objects.get(0));
        Assert.assertEquals(Collections.singletonMap("name", "three"), objects.get(2));
    }

    @Test
    public void serializesModelsAndSkipsNullListEntries() {
        Values values = new Values();
        values.enabled = true;
        values.count = 42;
        values.total = 4294967296L;
        values.flags = Arrays.asList(true, null, false);
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("root", values);
        String xml = Client.toXML(body);
        Map root = (Map) Client.parseXml(xml, null).get("root");
        Assert.assertEquals("true", root.get("enabled"));
        Assert.assertEquals("42", root.get("count"));
        Assert.assertEquals("4294967296", root.get("total"));
        Assert.assertEquals(Arrays.asList("true", "false"), root.get("flags"));
        Assert.assertFalse(root.containsKey("missing"));
    }

    @Test
    public void escapesTextWhenSerializing() {
        String text = "你好 & < > \" ' &amp;";
        String xml = Client.toXML(Collections.<String, Object>singletonMap("root", text));
        Assert.assertTrue(xml.contains("&amp;"));
        Assert.assertTrue(xml.contains("&lt;"));
        Assert.assertEquals(Collections.<String, Object>singletonMap("root", text), Client.parseXml(xml, null));
    }

    @Test
    public void wrapsParsingAndSerializationErrors() {
        RuntimeException parsing = Assert.assertThrows(RuntimeException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() {
                        Client.parseXml("<root>", null);
                    }
                });
        Assert.assertTrue(parsing.getCause() instanceof DocumentException);
        final Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("first", "one");
        body.put("second", "two");
        RuntimeException serialization = Assert.assertThrows(RuntimeException.class, new ThrowingRunnable() {
            @Override
            public void run() {
                Client.toXML(body);
            }
        });
        Assert.assertEquals("This map does not have a unique root tag", serialization.getCause().getMessage());
    }
}
