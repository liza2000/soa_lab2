package ru.itmo.soa.app.entity;

import lombok.Getter;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name="TEAM_HUMANS")
    @Getter
    private Set<HumanBeing> humans;
}
