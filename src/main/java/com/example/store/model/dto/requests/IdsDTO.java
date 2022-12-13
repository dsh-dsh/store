package com.example.store.model.dto.requests;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class IdsDTO {
    private List<Integer> ids;
}
