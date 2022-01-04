package ru.itmo.soa.app.soap;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import ru.itmo.soa.app.sd.ServiceDiscovery;
import ru.itmo.soa.entity.data.HumanData;
import ru.itmo.soa.entity.data.PaginationData;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.TreeMap;

@WebService(endpointInterface = "ru.itmo.soa.app.soap.HumanBeingSoapServiceI", serviceName = "human-being-soap")
public class HumanBeingSoapService implements HumanBeingSoapServiceI {
    private static final String WEAPON_TYPE_LESS = "weapon-type-less";
    private static final String SOUNDTRACK_NAME_STARTS = "soundtrack-name-starts";

    private static final String MINUTES_OF_WAITING_PARAM = "minutes-of-waiting";
    private static final String SOUNDTRACK_NAME_PARAM = "soundtrack-name";
    private static final String WEAPON_TYPE_PARAM = "weapon-type";

    private final Gson gson = new Gson();

    public HumanBeingSoapService() {}

    public PaginationData getAll(TreeMap<String, String[]> map) {
        WebTarget target = getTarget();
        for (String key : map.keySet()) {
            for (String param : map.get(key)) {
                target = target.queryParam(key, param);
            }
        }
        Invocation.Builder accept = target.request().accept(MediaType.APPLICATION_JSON);
        return accept.get(PaginationData.class);
    }

    public HumanData getOne(Long id) {
        WebTarget target = getTarget();
        Response response = target.path(id.toString()).request().accept(MediaType.APPLICATION_JSON).get();
        if (response.getStatus() == 404)
            throw new EntityNotFoundException("Cannot find human with id " + id);
        return response.readEntity(HumanData.class);
    }


    public HumanData[] getSoundtrackNameStarts(String soundtrackName) {
        WebTarget target = getTarget();
        return target.path(SOUNDTRACK_NAME_STARTS).queryParam(SOUNDTRACK_NAME_PARAM, soundtrackName).request().accept(MediaType.APPLICATION_JSON)
                .get(HumanData[].class);
    }

    @GET
    @Path(WEAPON_TYPE_LESS)
    public Long getWeaponTypeLess(@QueryParam(WEAPON_TYPE_PARAM) String weaponType) {
        WebTarget target = getTarget();
        Invocation.Builder request = target.path(WEAPON_TYPE_LESS).queryParam(WEAPON_TYPE_PARAM, weaponType).request();
        return request.accept(MediaType.TEXT_PLAIN).get().readEntity(Long.class);
    }


    public HumanData doPost(HumanData requestData) {
        WebTarget target = getTarget();
        return target.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(gson.toJson(requestData), MediaType.APPLICATION_JSON)).readEntity(HumanData.class);
    }


    public String doPut(Long id, HumanData requestData) {
        WebTarget target = getTarget();
        Response response = target.path(id.toString()).request().accept(MediaType.APPLICATION_JSON).put(Entity.entity(gson.toJson(requestData), MediaType.APPLICATION_JSON));
        if (response.getStatus() == 404)
            throw new EntityNotFoundException("Cannot update human with id " + id);
        if (response.getStatus() == 400)
            throw new IllegalArgumentException(response.getEntity().toString());
        return response.readEntity(String.class);
    }


    public String doDelete(Long id) {
        WebTarget target = getTarget();
        Response response = target.path(id.toString()).request().accept(MediaType.APPLICATION_JSON).delete();
        if (response.getStatus() == 404)
            throw new EntityNotFoundException("Cannot update human with id " + id);
        return response.readEntity(String.class);
    }


    public String doDeleteByMinutes(Double minutesOfWaiting) {
        WebTarget target = getTarget();
        return target.queryParam(MINUTES_OF_WAITING_PARAM, minutesOfWaiting).request().accept(MediaType.APPLICATION_JSON).delete().readEntity(String.class);
    }

    @SneakyThrows
    private WebTarget getTarget() {
        URI uri = UriBuilder.fromUri(ServiceDiscovery.getUriFromConsul()).build();
        Client client = ClientBuilder.newClient();
        return client.target(uri).path("api").path("human-being");
    }
}

