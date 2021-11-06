package ru.itmo.soa.service;


import ru.itmo.soa.entity.Team;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface RemoteEJBInterface {

    public List<Team> getTeams();

    public Team getTeam(long id);

    public Team createTeam(Team team);

    public void addHumanToTeam(Long teamId, Long humanId);

    public void removeHumanFromTeam(Long teamId, Long humanId);

    public List<Team> teamsByHuman(long id);

    public void deleteTeam(long id);
}
