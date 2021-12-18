package ru.itmo.soa.entity.data;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Data
@XmlRootElement(name = "coordinates")
public class CoordinatesData implements Serializable {
    private Long id;
    private Float x;
    private Float y;
}
