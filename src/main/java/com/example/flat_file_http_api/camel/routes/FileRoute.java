package com.example.flat_file_http_api.camel.routes;

import com.example.flat_file_http_api.camel.processors.TextRecordToDatabaseQueryProcessor;

import javax.sql.DataSource;

import com.example.flat_file_http_api.models.Session;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class FileRoute extends RouteBuilder {
    @Autowired
    Environment environment;

    @Autowired
    DataSource dataSource;

    @Override
    public void configure() {
        BindyDataFormat dataFormat = new BindyDataFormat();
        dataFormat.setClassType(Session.class);
        dataFormat.setType("Csv");
        from("file://%s".formatted(environment.getProperty("input_files_folder")))
                .threads(Runtime.getRuntime().availableProcessors())
                .split(body().tokenize("\n"))
                .streaming()
                .unmarshal(dataFormat)
                .process(new TextRecordToDatabaseQueryProcessor())
                .to("jdbc:dataSource");
    }
}
