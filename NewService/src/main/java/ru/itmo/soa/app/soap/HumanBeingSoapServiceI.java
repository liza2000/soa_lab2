package ru.itmo.soa.app.soap;

import ru.itmo.soa.entity.data.HumanData;
import ru.itmo.soa.entity.data.PaginationData;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;
import java.util.TreeMap;

@WebService
public interface HumanBeingSoapServiceI {

    @WebMethod
    public PaginationData getAll(TreeMap<String, List<String>> map);
    
    @WebMethod
    public HumanData getOne( Long id);

    @WebMethod
    public List<HumanData> getSoundtrackNameStarts( String soundtrackName);
    
    @WebMethod
    public Long getWeaponTypeLess( String weaponType);

    @WebMethod
    public Object doPost(HumanData requestData);

    @WebMethod
    public String doPut( Long id, HumanData requestData);

    @WebMethod
    public String doDelete( Long id);

    @WebMethod
    public String doDelete( Double minutesOfWaiting);
}
