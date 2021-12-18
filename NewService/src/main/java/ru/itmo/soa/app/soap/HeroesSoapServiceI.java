package ru.itmo.soa.app.soap;

import ru.itmo.soa.entity.Team;


import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;

@WebService
public
interface HeroesSoapServiceI {

    @WebMethod
    public List<Team> getAllTeams();

    @WebMethod
    public String findHeroes(boolean realHero);

    @WebMethod
    public String findHeroesNoParam();

    @WebMethod
    public List<Team> getTeamsByHuman(Long id);

    @WebMethod
    public Team createTeam(Team data);

    @WebMethod
    public int makeDepressive(Long id);

    @WebMethod
    public void addHumanToTeam(Long teamId, Long humanId);

    @WebMethod
    public void deleteTeam(Long id);

    @WebMethod
    public Team getTeam(Long id);

    @WebMethod
    public void deleteHumanFromTeam(Long id, Long humanId);

}