package ru.itmo.soa.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.itmo.soa.entity.data.CarData;
import ru.itmo.soa.entity.data.TeamData;

import javax.persistence.*;
import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    ArrayList<HumanBeing> humans;

    public void update(TeamData teamData) {
        this.name = teamData.getName();
    }
}
