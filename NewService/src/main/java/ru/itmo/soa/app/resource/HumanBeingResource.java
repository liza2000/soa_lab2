package ru.itmo.soa.app.resource;


import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import ru.itmo.soa.app.soap.HumanBeingSoapServiceI;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Path("/human-being")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HumanBeingResource {

    private static final String WEAPON_TYPE_LESS = "weapon-type-less";
    private static final String SOUNDTRACK_NAME_STARTS = "soundtrack-name-starts";

    private static final String MINUTES_OF_WAITING_PARAM = "minutes-of-waiting";
    private static final String SOUNDTRACK_NAME_PARAM = "soundtrack-name";
    private static final String WEAPON_TYPE_PARAM = "weapon-type";

    JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
    HumanBeingSoapServiceI soapService;

    HumanBeingResource(){
        factoryBean.setServiceClass(HumanBeingSoapServiceI.class);
        factoryBean.setAddress("/soap/service");
        soapService = (HumanBeingSoapServiceI) factoryBean.create();
    }

    @GET
    public Response get(@Context UriInfo ui) {
        MultivaluedMap<String, String> map = ui.getQueryParameters();
        TreeMap<String, List<String>> treeMap = new TreeMap<>();
        for (String key : map.keySet())
            treeMap.put(key,new ArrayList<>(map.get(key)));

        return Response.ok(soapService.getAll(treeMap)).build();
    }


    @GET
    @Path("/{id}")
    public Response getOne(@PathParam("id") Long id) {
        return Response.ok(soapService.getOne(id)).build();
    }

    @GET
    @Path(SOUNDTRACK_NAME_STARTS)
    public Response getSoundtrackNameStarts(@QueryParam(SOUNDTRACK_NAME_PARAM) String soundtrackName) {
        return Response.ok(soapService.getSoundtrackNameStarts(soundtrackName)).build();
    }

    @GET
    @Path(WEAPON_TYPE_LESS)
    public Response getWeaponTypeLess(@QueryParam(WEAPON_TYPE_PARAM) String weaponType) {
        return Response.ok(soapService.getWeaponTypeLess(weaponType)).build();
    }


    @POST
    public Response doPost(@Context HttpServletRequest request) {
        try {
            String requestData = request.getReader().lines().collect(Collectors.joining());
            return (Response)  soapService.doPost(requestData);
        } catch (IOException e) {
            return Response.serverError().build();
        }

    }

    @PUT
    @Path("/{id}")
    public Response doPut(@PathParam("id") Long id, @Context HttpServletRequest request) {
        try {
            String requestData = request.getReader().lines().collect(Collectors.joining());
            return (Response) soapService.doPut(id,requestData);
        } catch (IOException e) {
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response doDelete(@PathParam("id") Long id) {
        return Response.ok(soapService.doDelete(id)).build();
    }

    @DELETE
    public Response doDelete(@QueryParam(MINUTES_OF_WAITING_PARAM) Double minutesOfWaiting) {
        return Response.ok(soapService.doDelete(minutesOfWaiting)).build();
    }

}
