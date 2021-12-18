package ru.itmo.soa.entity.data;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Data
@XmlRootElement(name = "human")
public class HumanData implements Serializable {
    private Long id;
    private String name;
    private CoordinatesData coordinates;
    private Boolean realHero;
    private Boolean hasToothpick;
    private Float impactSpeed;
    private String soundtrackName;
    private String creationDate;
    private Double minutesOfWaiting;
    private String weaponType;
    private CarData car;
}