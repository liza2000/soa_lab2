package ru.itmo.soa.app.resource;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import ru.itmo.soa.app.entity.HumanBeing;
import ru.itmo.soa.app.entity.Team;
import ru.itmo.soa.app.entity.data.HumanData;
import ru.itmo.soa.app.service.TeamService;

import javax.naming.InitialContext;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Path("/heroes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HeroesResource {

    private static final String REAL_HERO_PARAM = "real-hero";
    private static final String LIMIT_PARAM = "limit";

    private final Gson gson = new Gson();
    private final TeamService teamService;

    public HeroesResource() {
        teamService = new TeamService();
    }

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
        InitialContext cont = new InitialContext();
        String s = (String) cont.lookup("java:/service2_uri"); // service 2 - payara server
        URI uri = UriBuilder.fromUri(s).build();
        Client client = ClientBuilder.newClient();
        return client.target(uri).path("api").path("human-being").queryParam(LIMIT_PARAM, Integer.MAX_VALUE);
    }
}
