package ru.itmo.soa.app.soap;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;
import java.util.TreeMap;

@WebService
public interface HumanBeingSoapServiceI {

    @WebMethod
    public String getAll(TreeMap<String, List<String>> map);
    
    @WebMethod
    public Object getOne( Long id);

    @WebMethod
    public String getSoundtrackNameStarts( String soundtrackName);
    
    @WebMethod
    public Long getWeaponTypeLess( String weaponType);

    @WebMethod
    public Object doPost(String requestData);

    @WebMethod
    public Object doPut( Long id, String requestData);

    @WebMethod
    public String doDelete( Long id);

    @WebMethod
    public String doDelete( Double minutesOfWaiting);
}
