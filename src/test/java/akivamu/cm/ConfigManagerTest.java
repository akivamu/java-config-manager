package akivamu.cm;

import com.akivamu.jsonmerger.JsonMerger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class ConfigManagerTest {
    private static Gson gson = new Gson();
    private static final JsonParser jsonParser = new JsonParser();

    @Test
    public void test_compare_using_json_merger() {
        compareUsingJsonMerger("/flat/origin.json", "/flat/ext.json");
        compareUsingJsonMerger("/nested/origin.json", "/nested/ext.json");
        compareUsingJsonMerger("/nested2/origin.json", "/nested2/ext.json");
    }

    @Test
    public void should_throw_error_when_invalid_file() {
        ConfigManager.clear();

        // Null
        try {
            File file = null;
            ConfigManager.load(file);
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
        }

        // Not exist file
        try {
            File file = new File("asd");
            ConfigManager.load(file);
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
        }

        // Invalid file content
        try {
            File file = new File(this.getClass().getClassLoader().getResource("invalid.json").getFile());
            ConfigManager.load(file);
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void should_throw_error_when_invalid_json_string() {
        ConfigManager.clear();
        // Empty json string
        try {
            ConfigManager.load("");
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
        }

        // Invalid json string
        try {
            ConfigManager.load("asdasdasd");
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void should_able_to_convert_to_object() {
        ConfigManager.clear();

        JsonObject originJsonObject = TestUtil.readJsonObjectFromResources("/flat/origin.json");
        JsonObject updateJsonObject = TestUtil.readJsonObjectFromResources("/flat/ext.json");
        ConfigManager.clear();
        ConfigManager.load(originJsonObject.toString());
        ConfigManager.load(updateJsonObject.toString());

        SampleConfig sampleConfig = ConfigManager.getObject(SampleConfig.class);
        Assert.assertNotNull(sampleConfig);
        Assert.assertEquals("jsonmerger.com", sampleConfig.host);
        Assert.assertEquals(true, sampleConfig.tls);

        // Compare content
        JsonObject reproduce = jsonParser.parse(gson.toJson(sampleConfig)).getAsJsonObject();
        Assert.assertTrue(reproduce.equals(ConfigManager.getObject()));
    }

    @Test
    public void should_able_to_convert_to_nested_object() {
        ConfigManager.clear();

        JsonObject originJsonObject = TestUtil.readJsonObjectFromResources("/nested/origin.json");
        JsonObject updateJsonObject = TestUtil.readJsonObjectFromResources("/nested/ext.json");
        ConfigManager.clear();
        ConfigManager.load(originJsonObject.toString());
        ConfigManager.load(updateJsonObject.toString());

        SampleConfig sampleConfig = ConfigManager.getObject("web", SampleConfig.class);
        Assert.assertNotNull(sampleConfig);
        Assert.assertEquals("jsonmerger.com", sampleConfig.host);
        Assert.assertEquals(true, sampleConfig.tls);

        // Compare content
        JsonObject reproduce = jsonParser.parse(gson.toJson(ConfigManager.getObject())).getAsJsonObject();
        Assert.assertTrue(reproduce.equals(ConfigManager.getObject()));
    }

    private void compareUsingJsonMerger(String path1, String path2) {
        JsonObject originJsonObject = TestUtil.readJsonObjectFromResources(path1);
        JsonObject updateJsonObject = TestUtil.readJsonObjectFromResources(path2);

        JsonMerger merger = new JsonMerger();
        JsonObject result = merger.merge(originJsonObject, updateJsonObject);

        ConfigManager.clear();
        ConfigManager.load(originJsonObject.toString());
        ConfigManager.load(updateJsonObject.toString());

        Assert.assertTrue(ConfigManager.getObject().equals(result));
    }

    public static class SampleConfig {
        public String host;
        public String note;
        public int port;
        public boolean tls;
        public String additional;
    }

    public static class NestedSampleConfig {
        public NestedSampleConfig web;
        public String flatProp;
        public String flatPropExtOnly;
        public String flatPropOriginOnly;
    }
}
