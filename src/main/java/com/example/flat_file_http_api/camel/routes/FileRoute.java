package com.example.flat_file_http_api.camel.routes;

import static com.example.flat_file_http_api.util.Utils.CSV_DATAFORMAT_TYPE;
import static com.example.flat_file_http_api.util.Utils.FILE_ROUTE_ID;
import static com.example.flat_file_http_api.util.Utils.INPUT_FILES_DEFAULT_FOLDER_NAME;
import static com.example.flat_file_http_api.util.Utils.JDBC_ROUTE_URL;

import java.io.IOException;

import com.example.flat_file_http_api.camel.processors.TextRecordToDatabaseQueryProcessor;

import javax.sql.DataSource;

import com.example.flat_file_http_api.models.Session;
import com.example.flat_file_http_api.util.Utils;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class FileRoute extends RouteBuilder {
  Logger log = LoggerFactory.getLogger(RouteBuilder.class);

  @Autowired Environment environment;

  @Autowired DataSource dataSource;

  @Override
  public void configure() throws IOException {
    BindyDataFormat dataFormat = new BindyDataFormat();
    dataFormat.setClassType(Session.class);
    // Even though this reads CSV, Bindy doesn't mandate it is CSV -
    // it can be any delimiter separated value (XSV)
    dataFormat.setType(CSV_DATAFORMAT_TYPE);
    // We do not want to block other messages (lines in input file) in case -
    // one of the line is not properly formatted/as per spec.
    // this faulty record will be ignored. We can do custom processing too.
    onException(Exception.class).continued(true);
    String inputFilesLocation = environment.getProperty(INPUT_FILES_DEFAULT_FOLDER_NAME);
    log.info("Input files location is: {}", inputFilesLocation);
    if (Utils.isEmptyDirectory(inputFilesLocation)) {
      log.error("Input files location seems to be empty. See README.md");
    }
    from("file://%s".formatted(inputFilesLocation))
        .routeId(FILE_ROUTE_ID)
        .threads(Runtime.getRuntime().availableProcessors())
        .split(body().tokenize("\n"))
        .streaming()
        .unmarshal(dataFormat)
        .process(new TextRecordToDatabaseQueryProcessor())
        .to(JDBC_ROUTE_URL);
  }
}
