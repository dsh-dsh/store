package com.example.sklad.model.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListResponse<T> {

    private Integer total;

    @JsonProperty("page_number")
    private Integer pageNumber;

    @JsonProperty("page_size")
    private Integer pageSize;

    private List<T> data;

    public ListResponse(List<T> data, Page<?> page) {
        this.total = page.getTotalPages();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.data = data;
    }
}
