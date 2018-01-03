package org.embulk.filter.strip_html_tags;

import org.embulk.spi.*;
import org.embulk.filter.strip_html_tags.StripHtmlTagsFilterPlugin.PluginTask;

import java.util.List;

public class ColumnVisitorImpl implements ColumnVisitor{

    private final PluginTask task;
    private final Schema inputSchema;
    private final Schema outputSchema;
    private final PageReader pageReader;
    private final PageBuilder pageBuilder;
    private final PlainTextizer plainTextizer;

    ColumnVisitorImpl(PluginTask task, Schema inputSchema, Schema outputSchema, PageReader pageReader, PageBuilder pageBuilder) {
        this.task = task;
        this.inputSchema = inputSchema;
        this.outputSchema = outputSchema;
        this.pageReader = pageReader;
        this.pageBuilder = pageBuilder;
        this.plainTextizer = new PlainTextizer();
    }

    @Override
    public void booleanColumn(Column column) {
        if (pageReader.isNull(column)) {
            pageBuilder.setNull(column);
        }
        else {
            pageBuilder.setBoolean(column, pageReader.getBoolean(column));
        }
    }

    @Override
    public void longColumn(Column column) {
        if (pageReader.isNull(column)) {
            pageBuilder.setNull(column);
        }
        else {
            pageBuilder.setLong(column, pageReader.getLong(column));
        }
    }

    @Override
    public void doubleColumn(Column column) {
        if (pageReader.isNull(column)) {
            pageBuilder.setNull(column);
        }
        else {
            pageBuilder.setLong(column, pageReader.getLong(column));
        }
    }

    @Override
    public void stringColumn(Column column) {
        if (pageReader.isNull(column)) {
            pageBuilder.setNull(column);
            return;
        }

        String value = pageReader.getString(column);
        if (isTargetColumn(column)) {
            pageBuilder.setString(column, stripTags(value));
        }
        else {
            pageBuilder.setString(column, value);
        }
    }

    @Override
    public void timestampColumn(Column column) {
        if (pageReader.isNull(column)) {
            pageBuilder.setNull(column);
        }
        else {
            pageBuilder.setTimestamp(column, pageReader.getTimestamp(column));
        }
    }

    @Override
    public void jsonColumn(Column column) {
        if (pageReader.isNull(column)) {
            pageBuilder.setNull(column);
        }
        else {
            pageBuilder.setJson(column, pageReader.getJson(column));
        }
    }

    private boolean isTargetColumn(Column column) {
        List<String> targetColumns = task.getColumns();
        String columnName = column.getName();
        return targetColumns.contains(columnName);
    }

    private String stripTags(String str) {
        return plainTextizer.execute(str);
    }
}
