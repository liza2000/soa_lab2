package ru.itmo.soa.entity.data;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Data
@XmlRootElement
public class CarData implements Serializable {
    private Long id;
    private String name;
}
