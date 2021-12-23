package ru.itmo.soa.app.soap;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import ru.itmo.soa.app.sd.ServiceDiscovery;
import ru.itmo.soa.entity.HumanBeing;
import ru.itmo.soa.entity.Team;
import ru.itmo.soa.entity.data.HumanData;
import ru.itmo.soa.entity.data.PaginationData;
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

@WebService(endpointInterface = "ru.itmo.soa.app.soap.HeroesSoapServiceI", serviceName = "heroes-soap")
public class HeroesSoapService implements HeroesSoapServiceI {

    private static final String REAL_HERO_PARAM = "real-hero";
    private static final String LIMIT_PARAM = "limit";
    private TeamServiceI teamService = lookupRemoteStatelessBean();
    private final Gson gson = new Gson();

    public HeroesSoapService() {
        System.out.println("HeroesSoapService created!");
    }

    public void setTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

    public Team[] getAllTeams() {
        System.out.println();
        return teamService.getTeams().toArray(new Team[0]);
    }

    public PaginationData findHeroes(boolean realHero) {
        if (realHero) {
            return getTarget()
                    .queryParam(REAL_HERO_PARAM, true)
                    .request().accept(MediaType.APPLICATION_JSON).get(PaginationData.class);
        } else {
            return getTarget().request().accept(MediaType.APPLICATION_JSON).get(PaginationData.class);
        }
    }

    public PaginationData findHeroesNoParam() {
        return getTarget().request().accept(MediaType.APPLICATION_JSON).get(PaginationData.class);
    }

    public Team getTeam(Long id) {
        return teamService.getTeam(id);
    }

    public Team[] getTeamsByHuman(Long id) {
        return teamService.teamsByHuman(id).toArray(new Team[0]);
    }

    public Team createTeam(Team data) {
        return teamService.createTeam(data);
    }

    public int makeDepressive(Long id) {
        Team team = teamService.getTeam(id);
        for (HumanBeing human : team.getHumans()) {
            Response response = getTarget().path(String.format("%s", human.getId())).request().accept(MediaType.APPLICATION_JSON).get();
            if (response.getStatus() != 200)
                return -1;

            HumanData data = response.readEntity(HumanData.class);
            data.setImpactSpeed(-500.0f);
            getTarget().path(String.format("%s", human.getId())).request().accept(MediaType.APPLICATION_JSON).put(Entity.entity(gson.toJson(data), MediaType.APPLICATION_JSON));
        }
        return 0;
    }

    public void addHumanToTeam(Long teamId, Long humanId) {
        teamService.addHumanToTeam(teamId, humanId);
    }

    public void deleteTeam(Long id) {
        teamService.deleteTeam(id);
    }

    public void deleteHumanFromTeam(Long id, Long humanId) {
        teamService.removeHumanFromTeam(id, humanId);
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
