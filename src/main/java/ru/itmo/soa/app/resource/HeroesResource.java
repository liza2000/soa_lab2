package ru.itmo.soa.app.resource;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import ru.itmo.soa.app.entity.HumanBeing;
import ru.itmo.soa.app.entity.Team;
import ru.itmo.soa.app.entity.data.HumanData;
import ru.itmo.soa.app.sd.ServiceDiscovery;
import ru.itmo.soa.app.service.RemoteEJBInterface;
import ru.itmo.soa.service.TeamService;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.*;
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

@Path("/heroes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HeroesResource {

    private static final String REAL_HERO_PARAM = "real-hero";
    private static final String LIMIT_PARAM = "limit";

    private final Gson gson = new Gson();
    private final RemoteEJBInterface teamService = lookupRemoteStatelessBean();


    @GET
    public Response getAllTeams() {
        List<Team> teams = teamService.getTeams();
        return Response.ok(gson.toJson(teams)).build();
    }


    @GET
    @Path("/search/{real-hero-only}")
    public Response findHeroes(@PathParam("real-hero-only") boolean realHero) {
        if (realHero) {
            return getTarget()
                    .queryParam(REAL_HERO_PARAM, true)
                    .request().accept(MediaType.APPLICATION_JSON).get();
        } else {
            return getTarget().request().accept(MediaType.APPLICATION_JSON).get();
        }
    }

    @GET
    @Path("/search")
    public Response findHeroesNoParam() {
        return getTarget().request().accept(MediaType.APPLICATION_JSON).get();
    }

    @GET
    @Path("/{id}")
    public Response getTeam(@PathParam("id") Long id) {
        return Response.ok(gson.toJson(teamService.getTeam(id))).build();
    }

    @GET
    @Path("/teams-by-human/{id}")
    public Response getTeamByHuman(@PathParam("id") Long id) {
        return Response.ok(gson.toJson(teamService.teamsByHuman(id))).build();
    }

    @POST
    public Response createTeam(String data) {
        Team saved = teamService.createTeam(gson.fromJson(data, Team.class));
        return Response.ok(gson.toJson(saved)).build();
    }

    @POST
    @Path("/team/{team-id}/make-depressive")
    public Response makeDepressive(@PathParam("team-id") Long id) {
        Team team = teamService.getTeam(id);
        for (HumanBeing human : team.getHumans()) {
            Response response = getTarget().path(String.format("%s", human.getId())).request().accept(MediaType.APPLICATION_JSON).get();
            if (response.getStatus() != 200) {
                return Response.status(500).build();
            }
            HumanData data = response.readEntity(HumanData.class);
            data.setImpactSpeed(-500.0f);
            getTarget().path(String.format("%s", human.getId())).request().accept(MediaType.APPLICATION_JSON).put(Entity.entity(gson.toJson(data), MediaType.APPLICATION_JSON));
        }
        return Response.ok().build();
    }

    @PUT
    @Path("/{id}/{human-id}")
    public Response addHumanToTeam(@PathParam("id") Long teamId, @PathParam("human-id") Long humanId) {
        teamService.addHumanToTeam(teamId, humanId);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTeam(@PathParam("id") Long id) {
        teamService.deleteTeam(id);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}/{human-id}")
    public Response deleteHumanFromTeam(@PathParam("id") Long id, @PathParam("human-id") Long humanId) {
        teamService.removeHumanFromTeam(id, humanId);
        return Response.ok().build();
    }

    @SneakyThrows
    private WebTarget getTarget() {
        // service 2 - payara server
        URI uri = UriBuilder.fromUri(ServiceDiscovery.getUriFromConsul()).build();
        Client client = ClientBuilder.newClient();
        return client.target(uri).path("api").path("human-being").queryParam(LIMIT_PARAM, Integer.MAX_VALUE);
    }

    private static RemoteEJBInterface lookupRemoteStatelessBean()  {
        final Hashtable<String,String> jndiProperties = new Hashtable<>();
        jndiProperties.put(javax.naming.Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        try {
            final javax.naming.Context context = new InitialContext(jndiProperties);
            // The app name is the application name of the deployed EJBs. This is typically the ear name
            // without the .ear suffix. However, the application name could be overridden in the application.xml of the
            // EJB deployment on the server.
            // Since we haven't deployed the application as a .ear, the app name for us will be an empty string
            final String appName = "";
            // This is the module name of the deployed EJBs on the server. This is typically the jar name of the
            // EJB deployment, without the .jar suffix, but can be overridden via the ejb-jar.xml
            // In this example, we have deployed the EJBs in a jboss-as-ejb-remote-app.jar, so the module name is
            // jboss-as-ejb-remote-app
            final String moduleName = "soa_ejb2-1";
            // AS7 allows each deployment to have an (optional) distinct name. We haven't specified a distinct name for
            // our EJB deployment, so this is an empty string
            final String distinctName = "";
            // The EJB name which by default is the simple class name of the bean implementation class
            final String beanName = TeamService.class.getSimpleName();
            // the remote view fully qualified class name
            final String viewClassName = RemoteEJBInterface.class.getName();
            // let's do the lookup

            return (RemoteEJBInterface) context.lookup("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
        }catch (NamingException e){
            System.out.println("не получилось (");
            return null;
        }
    }
}
