package ru.itmo.soa.service;

import ru.itmo.soa.dao.TeamDao;
import ru.itmo.soa.entity.HumanBeing;
import ru.itmo.soa.entity.Team;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

public class TeamService implements RemoteEJBInterface{
    private final TeamDao teamDao;

    public TeamService() {
        teamDao = new TeamDao();
    }

    public List<Team> getTeams() {
        return teamDao.getTeams();
    }

    public Team getTeam(long id) {
        Optional<Team> team = teamDao.getTeam(id);
        if (team.isPresent()) {
            return team.get();
        }
        throw new EntityNotFoundException(String.format("Team with id %d wasn't found", id));
    }

    public Team createTeam(Team team) {
        return teamDao.createTeam(team);
    }

    public void addHumanToTeam(Long teamId, Long humanId) {
        Optional<Team> team = teamDao.getTeam(teamId);
        Optional<HumanBeing> humanBeing = teamDao.getHuman(humanId);
        if (team.isPresent() && humanBeing.isPresent()) {
            Team teamValue = team.get();
            teamValue.getHumans().add(humanBeing.get());
            teamDao.updateTeam(teamValue);
        } else {
            if (team.isPresent()) {
                throw new EntityNotFoundException(String.format("Human with id %d wasn't found", humanId));
            }
            throw new EntityNotFoundException(String.format("Team with id %d wasn't found", teamId));
        }
    }

    public void removeHumanFromTeam(Long teamId, Long humanId) {
        Optional<Team> team = teamDao.getTeam(teamId);
        Optional<HumanBeing> humanBeing = teamDao.getHuman(humanId);
        if (team.isPresent() && humanBeing.isPresent()) {
            Team teamValue = team.get();
            teamValue.getHumans().remove(humanBeing.get());
            teamDao.updateTeam(teamValue);
        } else {
            if (team.isPresent()) {
                throw new EntityNotFoundException(String.format("Human with id %d wasn't found", humanId));
            }
            throw new EntityNotFoundException(String.format("Team with id %d wasn't found", teamId));
        }
    }

    public List<Team> teamsByHuman(long id) {
        Optional<HumanBeing> humanBeing = teamDao.getHuman(id);
        if (!humanBeing.isPresent()) {
            throw new EntityNotFoundException(String.format("Human with id %d wasn't found", id));
        }
        return teamDao.findTeamsByHumanId(id);
    }

    public void deleteTeam(long id) {
        if (!teamDao.deleteTeam(id)) {
            throw new EntityNotFoundException(String.format("Team with id %d wasn't found", id));
        }
    }
}
