package ru.itmo.soa.entity;

import lombok.Getter;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Set;

@Entity
@XmlRootElement
public class Team implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name="TEAM_HUMANS",
            joinColumns=
            @JoinColumn(name="TEAM_ID", referencedColumnName="ID"),
            inverseJoinColumns=
            @JoinColumn(name="HUMAN_ID", referencedColumnName="ID", columnDefinition = "bigint NOT NULL constraint human_being_id_fk references human_being on delete cascade")
    )
    @Getter
    private Set<HumanBeing> humans;
}
