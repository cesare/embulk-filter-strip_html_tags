package org.embulk.filter.strip_html_tags;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import org.embulk.EmbulkTestRuntime;
import org.embulk.config.ConfigLoader;
import org.embulk.config.ConfigSource;
import org.embulk.config.TaskSource;
import org.embulk.spi.*;
import org.embulk.spi.util.Pages;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static org.embulk.spi.type.Types.*;
import static org.hamcrest.Matchers.*;

import org.embulk.filter.strip_html_tags.StripHtmlTagsFilterPlugin.PluginTask;

import java.util.List;

public class TestStripHtmlTagsFilterPlugin
{
    @Rule
    public EmbulkTestRuntime runtime = new EmbulkTestRuntime();

    @Test
    public void testDefaultConfig() {
        String configYaml = "type: strip_html_tags\n";
        ConfigSource config = getConfigFromYaml(configYaml);
        PluginTask task = config.loadConfig(PluginTask.class);

        Assert.assertThat(task.getColumns(), is(empty()));
    }

    @Test
    public void testConfig() {
        String configYaml = ""
                + "type: strip_html_tags\n"
                + "columns:\n"
                + "  - foo\n"
                + "  - bar\n"
                ;
        ConfigSource config = getConfigFromYaml(configYaml);
        PluginTask task = config.loadConfig(PluginTask.class);

        Assert.assertThat(task.getColumns(), is(contains("foo", "bar")));
    }

    @Test
    public void testStringColumns() {
        String configYaml =""
                + "type: strip_html_tags\n"
                + "columns:\n"
                + "  - foo\n"
                ;
        ConfigSource config = getConfigFromYaml(configYaml);

        final Schema inputSchema = Schema.builder()
                .add("foo", STRING)
                .add("bar", STRING)
                .build();

        final StripHtmlTagsFilterPlugin plugin = new StripHtmlTagsFilterPlugin();
        plugin.transaction(config, inputSchema, new FilterPlugin.Control() {
            @Override
            public void run(TaskSource taskSource, Schema outputSchema) {
                TestPageBuilderReader.MockPageOutput mockPageOutput = new TestPageBuilderReader.MockPageOutput();
                PageOutput pageOutput = plugin.open(taskSource, inputSchema, outputSchema, mockPageOutput);

                String foo = "<p>test foo</p>";
                String bar = "<div>test bar</div>";
                for (Page page : PageTestUtils.buildPage(runtime.getBufferAllocator(), inputSchema, foo, bar)) {
                    pageOutput.add(page);
                }
                pageOutput.finish();
                pageOutput.close();

                List<Object[]> records = Pages.toObjects(outputSchema, mockPageOutput.pages);
                Object[] record = records.get(0);
                Assert.assertEquals("test foo", record[0]);
                Assert.assertEquals("<div>test bar</div>", record[1]);
            }
        });
    }

    @Test
    public void testNullColumns() {
        String configYaml =""
                + "type: strip_html_tags\n"
                + "columns:\n"
                + "  - foo\n"
                ;
        ConfigSource config = getConfigFromYaml(configYaml);

        final Schema inputSchema = Schema.builder()
                .add("foo", STRING)
                .add("bar", STRING)
                .build();

        final StripHtmlTagsFilterPlugin plugin = new StripHtmlTagsFilterPlugin();
        plugin.transaction(config, inputSchema, new FilterPlugin.Control() {
            @Override
            public void run(TaskSource taskSource, Schema outputSchema) {
                TestPageBuilderReader.MockPageOutput mockPageOutput = new TestPageBuilderReader.MockPageOutput();
                PageOutput pageOutput = plugin.open(taskSource, inputSchema, outputSchema, mockPageOutput);

                for (Page page : PageTestUtils.buildPage(runtime.getBufferAllocator(), inputSchema, null, null)) {
                    pageOutput.add(page);
                }
                pageOutput.finish();
                pageOutput.close();

                List<Object[]> records = Pages.toObjects(outputSchema, mockPageOutput.pages);
                Object[] record = records.get(0);
                Assert.assertNull(record[0]);
                Assert.assertNull(record[1]);
            }
        });
    }

    private ConfigSource getConfigFromYaml(String yaml) {
        ConfigLoader loader = new ConfigLoader(Exec.getModelManager());
        return loader.fromYamlString(yaml);
    }
}
