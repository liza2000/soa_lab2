package ru.itmo.soa.entity.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class CarData implements Serializable {
    private Long id;
    private String name;
}
