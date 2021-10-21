package ru.itmo.soa.app.entity;

import lombok.Getter;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name="TEAM_HUMANS")
    @Getter
    private Set<HumanBeing> humans;
}
