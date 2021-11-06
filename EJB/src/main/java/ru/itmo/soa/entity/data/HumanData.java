package ru.itmo.soa.entity.data;

import lombok.Data;

@Data
public class HumanData {
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