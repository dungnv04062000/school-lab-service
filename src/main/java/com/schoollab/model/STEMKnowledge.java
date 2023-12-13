package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "stem_knowledge")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class STEMKnowledge {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "science", columnDefinition = "nvarchar", nullable = false)
    private String science;

    @Column(name = "technology", columnDefinition = "nvarchar", nullable = false)
    private String technology;

    @Column(name = "engineering", columnDefinition = "nvarchar", nullable = false)
    private String engineering;

    @Column(name = "mathematics", columnDefinition = "nvarchar", nullable = false)
    private String mathematics;

    @Column(name = "create_at", nullable = false)
    private Instant createAt;

    @Column(name = "update_at")
    private Instant updateAt;

}
