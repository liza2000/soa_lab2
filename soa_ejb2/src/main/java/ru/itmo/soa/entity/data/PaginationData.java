package ru.itmo.soa.entity.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@XmlRootElement(name = "pagination-data")
public class PaginationData {

    private final int pageSize;
    private final int pageIndex;
    private final long totalItems;
    private final List<HumanData> list;
    public PaginationData() {
        pageSize = 0;
        pageIndex = 0;
        totalItems = 0;
        list = new ArrayList<>();
    }
}