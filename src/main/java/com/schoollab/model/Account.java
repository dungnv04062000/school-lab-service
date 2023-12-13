package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Table(name = "Accounts")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Account {

    @Id
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Column(name = "salt", nullable = false)
    private String salt;

    @NotNull
    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @NotNull
    @Column(name = "is_verify", nullable = false)
    private Boolean isVerify;

    @Column(name = "update_at")
    private Instant updateAt;

}
