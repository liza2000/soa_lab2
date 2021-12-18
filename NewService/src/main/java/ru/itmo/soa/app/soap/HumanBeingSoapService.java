package ru.itmo.soa.app.soap;

import lombok.SneakyThrows;
import ru.itmo.soa.app.sd.ServiceDiscovery;

import javax.jws.WebService;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.TreeMap;

@WebService(endpointInterface = "ru.itmo.soa.app.soap.HumanBeingSoapServiceI",
        serviceName = "HumanBeingSoap")
public class HumanBeingSoapService implements HumanBeingSoapServiceI{
    private static final String WEAPON_TYPE_LESS = "weapon-type-less";
    private static final String SOUNDTRACK_NAME_STARTS = "soundtrack-name-starts";

    private static final String MINUTES_OF_WAITING_PARAM = "minutes-of-waiting";
    private static final String SOUNDTRACK_NAME_PARAM = "soundtrack-name";
    private static final String WEAPON_TYPE_PARAM = "weapon-type";


    public String getAll(TreeMap<String, List<String>> map) {
        WebTarget target = getTarget();
        for (String key : map.keySet())
            for (String param : map.get(key))
                target = target.queryParam(key, param);

        return (String) target.request().accept(MediaType.APPLICATION_JSON).get().getEntity();
    }



    public Object getOne(Long id) {
        WebTarget target = getTarget();
        Response response = target.path(id.toString()).request().accept(MediaType.APPLICATION_JSON).get();
        if (response.getStatus()==404)
            throw new EntityNotFoundException("Cannot find human with id " + id);
        return  response.getEntity();
    }


    public String getSoundtrackNameStarts(String soundtrackName) {
        WebTarget target = getTarget();
        return (String) target.path(SOUNDTRACK_NAME_STARTS).queryParam(SOUNDTRACK_NAME_PARAM, soundtrackName).request().accept(MediaType.APPLICATION_JSON).get().getEntity();
    }

    @GET
    @Path(WEAPON_TYPE_LESS)
    public Long getWeaponTypeLess(@QueryParam(WEAPON_TYPE_PARAM) String weaponType) {
        WebTarget target = getTarget();
        return (Long) target.path(WEAPON_TYPE_LESS).queryParam(WEAPON_TYPE_PARAM, weaponType).request().accept(MediaType.APPLICATION_JSON).get().getEntity();
    }



    public Object doPost(String requestData) {
        WebTarget target = getTarget();
            return  target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(requestData, MediaType.APPLICATION_JSON));
    }


    public Object doPut(Long id, String requestData) {
        WebTarget target = getTarget();
        Response response = target.path(id.toString()).request().accept(MediaType.APPLICATION_JSON).put(Entity.entity(requestData, MediaType.APPLICATION_JSON));
        if (response.getStatus()==404)
            throw new EntityNotFoundException("Cannot update human with id " + id);
        return response;
    }


    public String doDelete(Long id) {
        WebTarget target = getTarget();
        Response response = target.path(id.toString()).request().accept(MediaType.APPLICATION_JSON).delete();
        if (response.getStatus()==404)
            throw new EntityNotFoundException("Cannot update human with id " + id);
        return (String) response.getEntity();
    }


    public String doDelete(Double minutesOfWaiting) {
        WebTarget target = getTarget();
        return (String) target.queryParam(MINUTES_OF_WAITING_PARAM, minutesOfWaiting).request().accept(MediaType.APPLICATION_JSON).delete().getEntity();

    }


    @SneakyThrows
    private WebTarget getTarget() {
        URI uri = UriBuilder.fromUri(ServiceDiscovery.getUriFromConsul()).build();
        Client client = ClientBuilder.newClient();
        return client.target(uri).path("api").path("human-being");
    }
}

