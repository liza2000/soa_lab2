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
    PaginationData getAll(TreeMap<String, String[]> map);

    @WebMethod
    HumanData getOne(Long id);

    @WebMethod
    HumanData[] getSoundtrackNameStarts(String soundtrackName);

    @WebMethod
    Long getWeaponTypeLess(String weaponType);

    @WebMethod
    Object doPost(HumanData requestData);

    @WebMethod
    String doPut(Long id, HumanData requestData);

    @WebMethod
    String doDelete(Long id);

    @WebMethod
    String doDeleteByMinutes(Double minutesOfWaiting);
}
