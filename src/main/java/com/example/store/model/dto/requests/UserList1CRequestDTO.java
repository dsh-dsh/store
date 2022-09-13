package com.example.store.model.dto.requests;


import com.example.store.model.dto.User1CDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserList1CRequestDTO {
    @JsonProperty("user_dto_list")
    private List<User1CDTO> user1CDTOList;
}
