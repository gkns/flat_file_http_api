package com.example.flat_file_http_api.camel.processors;

import com.example.flat_file_http_api.models.Session;
import com.example.flat_file_http_api.util.Utils;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.util.StringUtils;


public class TextRecordToDatabaseQueryProcessor implements Processor {
    @Override
    public void process(Exchange msg) {
        Session sessionInfo = msg.getIn().getBody(Session.class);
        if (StringUtils.hasText(sessionInfo.getSessionId()) ||
                StringUtils.hasText(sessionInfo.getEmail()) ||
                StringUtils.hasText(sessionInfo.getEventTime().toString())) {
            msg.getIn().setBody("");
        }
        String query =
                ("INSERT INTO session_logs (session_id, email, event_time)"
                        + " VALUES (\'%s\', \'%s\', \'%s\')")
                        .formatted(
                                sessionInfo.getSessionId(),
                                sessionInfo.getEmail(),
                                sessionInfo.getEventTime().format(Utils.DERBY_DATETIME_FORMATTER));
        msg.getIn().setBody(query);
    }
}
