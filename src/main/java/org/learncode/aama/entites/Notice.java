package org.learncode.aama.entites;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long notice_id;
    private String type;
    @Column(length=500)
    private String purpose;
    private String noticeCreator;

}
