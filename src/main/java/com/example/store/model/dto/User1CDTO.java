package com.example.store.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User1CDTO extends UserDTO{

    private String phone;

    @JsonProperty("birth_date")
    private long birthDate;

    @JsonProperty("parent_id")
    private int parentId;

    @JsonProperty("is_node")
    private boolean isNode;

}
