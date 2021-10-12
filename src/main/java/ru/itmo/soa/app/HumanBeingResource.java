package ru.itmo.soa.app;

import lombok.SneakyThrows;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/heroes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HumanBeingResource extends Application{

//    Удалить все объекты, значение поля minutesOfWaiting которого эквивалентно заданному.
//    Вернуть количество объектов, значение поля weaponType которых меньше заданного.
//    Вернуть массив объектов, значение поля soundtrackName которых начинается с заданной подстроки.

    private static final String WEAPON_TYPE_LESS = "weapon-type-less";
    private static final String SOUNDTRACK_NAME_STARTS = "soundtrack-name-starts";

    private static final String MINUTES_OF_WAITING_PARAM = "minutes-of-waiting";
    private static final String SOUNDTRACK_NAME_PARAM = "soundtrack-name";
    private static final String WEAPON_TYPE_PARAM = "weapon-type";


    @SneakyThrows
    @GET
    public Response get(@Context UriInfo ui) {
        WebTarget target = getTarget();
        MultivaluedMap<String, String> map = ui.getQueryParameters(true);
        for (String key: map.keySet()) {
            target = target.queryParam(key, map.get(key).toArray());
        }
        System.out.println(target.queryParam(WEAPON_TYPE_PARAM, "123").getUri().toURL().toString());
        return target.request().accept(MediaType.APPLICATION_JSON).get();
    }


    @GET
    @Path("/{id}")
    public Response getOne(@PathParam("id") Long id) {
        WebTarget target = getTarget();
        return target.path(id.toString()).request().accept(MediaType.APPLICATION_JSON).get();
    }

    @GET
    @Path(SOUNDTRACK_NAME_STARTS)
    public Response getSoundtrackNameStarts(@QueryParam(SOUNDTRACK_NAME_PARAM) String soundtrackName) {
        WebTarget target = getTarget();
        return target.path(SOUNDTRACK_NAME_STARTS).queryParam(SOUNDTRACK_NAME_PARAM, soundtrackName).request().accept(MediaType.APPLICATION_JSON).get();
    }

    @GET
    @Path(WEAPON_TYPE_LESS)
    public Response getWeaponTypeLess(@QueryParam(WEAPON_TYPE_PARAM) String weaponType) {
        WebTarget target = getTarget();
        return target.path(WEAPON_TYPE_LESS).queryParam(WEAPON_TYPE_PARAM, weaponType).request().accept(MediaType.APPLICATION_JSON).get();
    }


    @POST
    public Response doPost(String data) {
        WebTarget target = getTarget();
//        try {
//            String requestData = request.getReader().lines().collect(Collectors.joining());
            return target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(data, MediaType.APPLICATION_JSON));
//        }catch (IOException e){
//            return Response.serverError().build();
//        }

    }

    @PUT
    @Path("/{id}")
    public Response doPut(@PathParam("id") Long id, String data) {
        WebTarget target = getTarget();
//        try {
//            String requestData = request.getReader().lines().collect(Collectors.joining());
            return target.path(id.toString()).request().accept(MediaType.APPLICATION_JSON).put(Entity.entity(data, MediaType.APPLICATION_JSON));
//        } catch (IOException e) {
//            return Response.serverError().build();
//        }
    }

    @DELETE
    @Path("/{id}")
    public Response doDelete(@PathParam("id") Long id) {
        WebTarget target = getTarget();
        return target.path(id.toString()).request().accept(MediaType.APPLICATION_JSON).delete();
    }

    @DELETE
    public Response doDelete(@QueryParam(MINUTES_OF_WAITING_PARAM) Double minutesOfWaiting) {
        WebTarget target = getTarget();
        return target.queryParam(MINUTES_OF_WAITING_PARAM, minutesOfWaiting).request().accept(MediaType.APPLICATION_JSON).delete();
    }

    @SneakyThrows
    private WebTarget getTarget(){
        InitialContext cont = new InitialContext();
        String s = (String) cont.lookup("java:/service2_uri");
        URI uri = UriBuilder.fromUri(s).build();
        Client client = ClientBuilder.newClient();
        return client.target(uri).path("api").path("human-being");
    }
}
