package com.vickey.skillforge.entity;

import com.vickey.skillforge.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "user")
    private List<Skill> skills= new ArrayList<>();

    private String email;

    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

}
