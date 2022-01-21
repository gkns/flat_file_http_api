package com.example.flat_file_http_api.repositories;

import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import javax.persistence.TypedQuery;

import com.example.flat_file_http_api.models.Session;
import com.example.flat_file_http_api.util.Utils;

import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionDataRepositoryImpl {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    CamelContext camelContext;

    public Session[] findSessionsInTimeRange(String fromDate_, String toDate_) {
        // strip trailing 'Z' since, LocalDateTime is agnostic of timezone.
        // we treat all dates as UTC in this application.
        LocalDateTime fromDate = LocalDateTime.parse(fromDate_.substring(0, fromDate_.length()-1));
        LocalDateTime toDate = LocalDateTime.parse(toDate_.substring(0, fromDate_.length()-1));
        String dateRangeQL = "SELECT s FROM Session s WHERE event_time"
                + " BETWEEN TIMESTAMP('%s') AND TIMESTAMP('%s')"
            .formatted(
                    fromDate.format(Utils.DERBY_DATETIME_FORMATTER),
                    toDate.format(Utils.DERBY_DATETIME_FORMATTER));
        TypedQuery<Session> query = entityManager.createQuery(dateRangeQL, Session.class);
        return query.getResultList().toArray(new Session[0]);
    }
}
