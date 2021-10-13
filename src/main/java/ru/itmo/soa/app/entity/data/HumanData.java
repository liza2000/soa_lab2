package ru.itmo.soa.app.entity.data;

import lombok.Data;

@Data
public class HumanData {
    private String name;
    private CoordinatesData coordinates;
    private Boolean realHero;
    private Boolean hasToothpick;
    private Float impactSpeed;
    private String soundtrackName;
    private Double minutesOfWaiting;
    private String weaponType;
    private CarData car;
}