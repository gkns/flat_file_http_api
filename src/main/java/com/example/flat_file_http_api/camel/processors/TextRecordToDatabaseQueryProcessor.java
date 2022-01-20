package com.example.flat_file_http_api.camel.processors;

import com.example.flat_file_http_api.models.Session;
import com.example.flat_file_http_api.util.Utils;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class TextRecordToDatabaseQueryProcessor implements Processor {
    @Override
    public void process(Exchange msg) throws Exception {
        Session sessionInfo = msg.getIn().getBody(Session.class);
        String query =
                ("INSERT INTO session_logs (session_id, email, event_time)"
                        + " VALUES (\'%s\', \'%s\', \'%s\')")
                        .formatted(
                                sessionInfo.getSessionId(),
                                sessionInfo.getEmail(),
                                sessionInfo.getEventTime().format(Utils.DERBY_DATETIME_FORMATTER));
        System.out.println(query);
        msg.getIn().setBody(query);
    }
}
