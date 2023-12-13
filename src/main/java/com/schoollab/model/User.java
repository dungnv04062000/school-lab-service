package com.schoollab.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "Users")
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "image_url")
    private String imageUrl;

    @NotNull
    @Column(name = "register_at")
    private Instant registerAt;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "gender")
    private String gender;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "update_at")
    private Instant updateAt;

    @Column(name = "campus_id", nullable = false)
    private String campusId;

    @Column(name = "address_id")
    private String addressId;
}
