package ru.itmo.soa.entity.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.itmo.soa.entity.Team;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
public class TeamData {
    private String name; //Поле не может быть null

    public Team toTeam() {
        return new Team(null, name, new ArrayList<>());
    }
}