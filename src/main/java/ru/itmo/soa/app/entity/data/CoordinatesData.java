package ru.itmo.soa.app.entity.data;

import lombok.Data;

@Data
public class CoordinatesData {
    private Float x; //Значение поля должно быть больше -706
    private Float y; //Поле не может быть null
}
