package ru.itmo.soa.entity.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class CoordinatesData implements Serializable {
    private Long id;
    private Float x;
    private Float y;
}
