package ru.itmo.soa.app.soap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import ru.itmo.soa.app.sd.ServiceDiscovery;
import ru.itmo.soa.entity.data.HumanData;
import ru.itmo.soa.entity.data.PaginationData;

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

    Gson gson = new Gson();


    public PaginationData getAll(TreeMap<String, List<String>> map) {
        WebTarget target = getTarget();
        for (String key : map.keySet())
            for (String param : map.get(key))
                target = target.queryParam(key, param);

        return gson.fromJson(target.request().accept(MediaType.APPLICATION_JSON).get().getEntity().toString(), PaginationData.class);
    }



    public HumanData getOne(Long id) {
        WebTarget target = getTarget();
        Response response = target.path(id.toString()).request().accept(MediaType.APPLICATION_JSON).get();
        if (response.getStatus()==404)
            throw new EntityNotFoundException("Cannot find human with id " + id);
        return  gson.fromJson(response.getEntity().toString(),HumanData.class);
    }


    public List<HumanData> getSoundtrackNameStarts(String soundtrackName) {
        WebTarget target = getTarget();
        return gson.fromJson( target.path(SOUNDTRACK_NAME_STARTS).queryParam(SOUNDTRACK_NAME_PARAM, soundtrackName).request().accept(MediaType.APPLICATION_JSON).get().getEntity().toString(), new TypeToken<List<HumanData>>(){}.getType());
    }

    @GET
    @Path(WEAPON_TYPE_LESS)
    public Long getWeaponTypeLess(@QueryParam(WEAPON_TYPE_PARAM) String weaponType) {
        WebTarget target = getTarget();
        return (Long) target.path(WEAPON_TYPE_LESS).queryParam(WEAPON_TYPE_PARAM, weaponType).request().accept(MediaType.APPLICATION_JSON).get().getEntity();
    }



    public HumanData doPost(HumanData requestData) {
        WebTarget target = getTarget();
        Response response = target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(gson.toJson(requestData), MediaType.APPLICATION_JSON));
            return  gson.fromJson(target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(gson.toJson(requestData), MediaType.APPLICATION_JSON)).getEntity().toString(), HumanData.class);
    }


    public String doPut(Long id, HumanData requestData) {
        WebTarget target = getTarget();
        Response response = target.path(id.toString()).request().accept(MediaType.APPLICATION_JSON).put(Entity.entity(gson.toJson(requestData), MediaType.APPLICATION_JSON));
        if (response.getStatus()==404)
            throw new EntityNotFoundException("Cannot update human with id " + id);
        if (response.getStatus()==400)
            throw new IllegalArgumentException(response.getEntity().toString());
        return response.getEntity().toString();
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

