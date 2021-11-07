package ru.itmo.soa.service;


import ru.itmo.soa.entity.Team;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface TeamServiceI {
    List<Team> getTeams();
    Team getTeam(long id);
    Team createTeam(Team team);
    void addHumanToTeam(Long teamId, Long humanId);
    void removeHumanFromTeam(Long teamId, Long humanId);
    List<Team> teamsByHuman(long id);
    void deleteTeam(long id);
}
