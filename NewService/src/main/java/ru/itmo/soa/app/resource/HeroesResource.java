package ru.itmo.soa.app.resource;

import com.google.gson.Gson;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import ru.itmo.soa.app.sd.ServiceDiscovery;
import ru.itmo.soa.app.soap.HeroesSoapServiceI;
import ru.itmo.soa.entity.Team;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/heroes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HeroesResource {

    private final Gson gson = new Gson();
    JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
    HeroesSoapServiceI soapService = null;

    HeroesResource(){
        factoryBean.setServiceClass(HeroesSoapServiceI.class);
        factoryBean.setAddress("http://localhost:8080/soap/heroes-service");
        soapService = (HeroesSoapServiceI) factoryBean.create();
    }

    @GET
    public Response getAllTeams() {
        List<Team> teams = soapService.getAllTeams();
        return Response.ok(gson.toJson(teams)).build();
    }

    @GET
    @Path("/search/{real-hero-only}")
    public Response findHeroes(@PathParam("real-hero-only") boolean realHero) {
        return Response.ok(gson.toJson(soapService.findHeroes(realHero))).build();
    }

    @GET
    @Path("/search")
    public Response findHeroesNoParam() {
        return Response.ok(gson.toJson(soapService.findHeroesNoParam())).build();
    }

    @GET
    @Path("/{id}")
    public Response getTeam(@PathParam("id") Long id) {
        return Response.ok(gson.toJson(soapService.getTeam(id))).build();
    }

    @GET
    @Path("/teams-by-human/{id}")
    public Response getTeamsByHuman(@PathParam("id") Long id) {
        return Response.ok(gson.toJson(soapService.getTeamsByHuman(id))).build();
    }

    @POST
    public Response createTeam(String data) {
        Team saved = soapService.createTeam(gson.fromJson(data, Team.class));
        return Response.ok(gson.toJson(saved)).build();
    }

    @POST
    @Path("/team/{team-id}/make-depressive")
    public Response makeDepressive(@PathParam("team-id") Long id) {
        if (soapService.makeDepressive(id) == 0)
            return Response.ok().build();
        return Response.serverError().build();
    }

    @PUT
    @Path("/{id}/{human-id}")
    public Response addHumanToTeam(@PathParam("id") Long teamId, @PathParam("human-id") Long humanId) {
        soapService.addHumanToTeam(teamId, humanId);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTeam(@PathParam("id") Long id) {
        soapService.deleteTeam(id);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}/{human-id}")
    public Response deleteHumanFromTeam(@PathParam("id") Long id, @PathParam("human-id") Long humanId) {
        soapService.deleteHumanFromTeam(id, humanId);
        return Response.ok().build();
    }

}
