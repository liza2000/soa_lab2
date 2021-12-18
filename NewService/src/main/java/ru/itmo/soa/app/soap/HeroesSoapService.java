package ru.itmo.soa.app.soap;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import ru.itmo.soa.app.sd.ServiceDiscovery;
import ru.itmo.soa.entity.HumanBeing;
import ru.itmo.soa.entity.Team;
import ru.itmo.soa.entity.data.HumanData;
import ru.itmo.soa.service.TeamService;
import ru.itmo.soa.service.TeamServiceI;

import javax.ejb.EJBException;
import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Hashtable;
import java.util.List;

@WebService(endpointInterface = "ru.itmo.soa.app.soap.HeroesSoapServiceI",
        serviceName = "HeroesSoap")
public class HeroesSoapService implements HeroesSoapServiceI {

    private static final String REAL_HERO_PARAM = "real-hero";
    private static final String LIMIT_PARAM = "limit";
    private TeamServiceI teamService = lookupRemoteStatelessBean();
    private final Gson gson = new Gson();

    public void setTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

    @Override
    public List<Team> getAllTeams() {
        return teamService.getTeams();
    }

    @Override
    public String findHeroes(boolean realHero) {
        if (realHero) {
            return (String)getTarget()
                    .queryParam(REAL_HERO_PARAM, true)
                    .request().accept(MediaType.APPLICATION_JSON).get().getEntity();
        } else {
            return (String)getTarget().request().accept(MediaType.APPLICATION_JSON).get().getEntity();
        }
    }

    @Override
    public String findHeroesNoParam() {
        return (String)getTarget().request().accept(MediaType.APPLICATION_JSON).get().getEntity();
    }

    @Override
    public Team getTeam(Long id) {
        return teamService.getTeam(id);
    }

    @Override
    public List<Team> getTeamsByHuman(Long id) {
        return teamService.teamsByHuman(id);
    }

    @Override
    public Team createTeam(Team data) {
       return teamService.createTeam(data);
    }

    @Override
    public int makeDepressive(Long id) {
        Team team = teamService.getTeam(id);
        for (HumanBeing human : team.getHumans()) {
            Response response = getTarget().path(String.format("%s", human.getId())).request().accept(MediaType.APPLICATION_JSON).get();
            if (response.getStatus() != 200)
                return -1;

            HumanData data = (HumanData) response.getEntity();
            data.setImpactSpeed(-500.0f);
            getTarget().path(String.format("%s", human.getId())).request().accept(MediaType.APPLICATION_JSON).put(Entity.entity(gson.toJson(data), MediaType.APPLICATION_JSON));
        }
        return 0;
    }

    @Override
    public void addHumanToTeam(Long teamId, Long humanId) {
        teamService.addHumanToTeam(teamId, humanId);
    }

    @Override
    public void deleteTeam(Long id) {
        teamService.deleteTeam(id);
    }

    @Override
    public void deleteHumanFromTeam(Long id, Long humanId) {
        teamService.removeHumanFromTeam(id,humanId);
    }

    @SneakyThrows
    private WebTarget getTarget() {
        // service 2 - payara server
        URI uri = UriBuilder.fromUri(ServiceDiscovery.getUriFromConsul()).build();
        Client client = ClientBuilder.newClient();
        return client.target(uri).path("api").path("human-being").queryParam(LIMIT_PARAM, Integer.MAX_VALUE);
    }

    private static TeamServiceI lookupRemoteStatelessBean() {
        final Hashtable<String, String> jndiProperties = new Hashtable<>();
        jndiProperties.put(javax.naming.Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        try {
            final javax.naming.Context context = new InitialContext(jndiProperties);
            final String appName = "global";
            final String moduleName = "NewService";
            final String distinctName = "";
            final String beanName = "TeamService";
            final String viewClassName = TeamServiceI.class.getName();
            return (TeamServiceI) context.lookup("java:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
        } catch (NamingException e) {
            System.out.println("не получилось (");
            return new TeamServiceI() {
                @Override
                public List<Team> getTeams() {
                    throw new EJBException("ejb is not available");
                }

                @Override
                public Team getTeam(long id) {
                    throw new EJBException("ejb is not available");
                }

                @Override
                public Team createTeam(Team team) {
                    throw new EJBException("ejb is not available");
                }

                @Override
                public void addHumanToTeam(Long teamId, Long humanId) {
                    throw new EJBException("ejb is not available");
                }

                @Override
                public void removeHumanFromTeam(Long teamId, Long humanId) {
                    throw new EJBException("ejb is not available");
                }

                @Override
                public List<Team> teamsByHuman(long id) {
                    throw new EJBException("ejb is not available");
                }

                @Override
                public void deleteTeam(long id) {
                    throw new EJBException("ejb is not available");
                }
            };
        }
    }
}
