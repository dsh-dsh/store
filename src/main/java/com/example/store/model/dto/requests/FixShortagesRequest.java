package com.example.store.model.dto.requests;

import com.example.store.model.responses.ShortageResponseLine;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FixShortagesRequest {
    int docId;
    List<ShortageResponseLine> shortages;
}
