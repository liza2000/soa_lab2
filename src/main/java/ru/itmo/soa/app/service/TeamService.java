package ru.itmo.soa.app.service;

import ru.itmo.soa.app.dao.TeamDao;
import ru.itmo.soa.app.data.TeamHumanRequest;
import ru.itmo.soa.app.entity.HumanBeing;
import ru.itmo.soa.app.entity.Team;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

public class TeamService {
    private final TeamDao teamDao;

    public TeamService() {
        teamDao = new TeamDao();
    }

    public List<Team> getTeams() {
        return teamDao.getTeams();
    }

    public Optional<Team> getTeam(long id) {
        return teamDao.getTeam(id);
    }

    public Team createTeam(Team team) {
        return teamDao.createTeam(team);
    }

    public boolean addHumanToTeam(Long teamId, Long humanId) {
        Optional<Team> team = teamDao.getTeam(teamId);
        Optional<HumanBeing> humanBeing = teamDao.getHuman(humanId);
        if (team.isPresent() && humanBeing.isPresent()) {
            Team teamValue = team.get();
            teamValue.getHumans().add(humanBeing.get());
            teamDao.updateTeam(teamValue);
            return true;
        }
        return false;
    }

    public Response removeHumanFromTeam(Long teamId, Long humanId) {
        Optional<Team> team = teamDao.getTeam(teamId);
        Optional<HumanBeing> humanBeing = teamDao.getHuman(humanId);
        if (team.isPresent() && humanBeing.isPresent()) {
            Team teamValue = team.get();
            teamValue.getHumans().remove(humanBeing.get());
            teamDao.updateTeam(teamValue);
            return Response.ok().build();
        }
        return Response.status(404).build();
    }

    public Response deleteTeam(long id) {
        if (teamDao.deleteTeam(id)) {
            return Response.ok().build();
        } else {
            return Response.status(404).build();
        }
    }
}
