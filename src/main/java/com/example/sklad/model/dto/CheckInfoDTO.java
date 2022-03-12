package com.example.sklad.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckInfoDTO {

    @JsonProperty("check_number")
    private long checkNumber;

    @JsonProperty("cash_register_number")
    private String cashRegisterNumber;

    @JsonProperty("amount_received")
    private double amountReceived;

    @JsonProperty("guest_number")
    private int guestNumber;

    @JsonProperty("table_number")
    private int tableNumber;

    private String waiter;

    private LocalDateTime time;

    @JsonProperty("is_return")
    private boolean isReturn;

    @JsonProperty("is_KKM_checked")
    private boolean isKKMChecked;

    @JsonProperty("is_payed")
    private boolean isPayed;

    @JsonProperty("is_payed_by_card")
    private boolean isPayedByCard;

}
