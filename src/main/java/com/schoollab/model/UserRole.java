package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "UserRole")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class UserRole {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "role_id")
    private int roleId;
}
