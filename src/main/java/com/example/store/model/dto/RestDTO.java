package com.example.store.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RestDTO {
    private StorageDTO storage;
    private float quantity;
}
