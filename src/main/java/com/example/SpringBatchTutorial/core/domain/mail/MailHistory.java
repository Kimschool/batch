package com.example.SpringBatchTutorial.core.domain.mail;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mailhistory")
public class MailHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "date", insertable = false, updatable = false)
    private LocalDate date;

    private String result;

    private int count;

}
