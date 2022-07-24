package com.example.store.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PeriodicValueDTO {

    private int id;
    private long date;

    @JsonProperty("string_date")
    private String stringDate;

    private float quantity;
    private String type;

    @JsonProperty("is_deleted")
    private boolean isDeleted;

}
