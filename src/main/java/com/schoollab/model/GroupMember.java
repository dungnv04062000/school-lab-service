package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "group_members")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class GroupMember {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "group_id", nullable = false)
    private String groupId;

    @Column(name = "member_id", nullable = false)
    private String memberId;
}
