package ru.itmo.soa.app.soap;

import ru.itmo.soa.entity.Team;
import ru.itmo.soa.entity.data.HumanData;
import ru.itmo.soa.entity.data.PaginationData;


import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;

@WebService
public interface HeroesSoapServiceI {

    @WebMethod
    Team[] getAllTeams();

    @WebMethod
    PaginationData findHeroes(boolean realHero);

    @WebMethod
    PaginationData findHeroesNoParam();

    @WebMethod
    Team[] getTeamsByHuman(Long id);

    @WebMethod
    Team createTeam(Team data);

    @WebMethod
    int makeDepressive(Long id);

    @WebMethod
    void addHumanToTeam(Long teamId, Long humanId);

    @WebMethod
    void deleteTeam(Long id);

    @WebMethod
    Team getTeam(Long id);

    @WebMethod
    void deleteHumanFromTeam(Long id, Long humanId);

}