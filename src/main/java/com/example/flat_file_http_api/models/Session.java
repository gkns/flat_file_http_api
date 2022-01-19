package com.example.flat_file_http_api.models;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Indexed by timestamp
@Table(name = "session_logs", indexes = { @Index(name = "IDX_TIMESTAMP", columnList = "timestamp") })
@Entity
// Lombok
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@CsvRecord(separator = " ")
public class Session {
    @Id
    @Column(name = "session_id")
    @DataField(pos=3, trim=true, delimiter=" ")
    String sessionId;

    @DataField(pos=1, trim=true, delimiter=" ", pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Column(name = "timestamp")
    LocalDate timeStamp;

    @DataField(pos=2, trim=true, delimiter=" ")
    @Column(name = "username")
    String userName;
}
