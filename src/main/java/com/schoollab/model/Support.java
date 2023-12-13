package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "supports")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Support {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "from_id")
    private String fromId;

    @Column(name = "name", nullable = false, columnDefinition = "nvarchar")
    private String name;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email", nullable = false, columnDefinition = "nvarchar")
    private String email;

    @Column(name = "title", nullable = false, columnDefinition = "nvarchar")
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "nvarchar")
    private String content;

    @Column(name = "response", nullable = false, columnDefinition = "nvarchar")
    private String response;

    @Column(name = "priority", nullable = false)
    private String priority;

    @Column(name = "create_at", nullable = false)
    private Instant createAt;

    @Column(name = "update_at")
    private Instant updateAt;

    @Column(name = "status", nullable = false, columnDefinition = "nvarchar")
    private String status;
}
