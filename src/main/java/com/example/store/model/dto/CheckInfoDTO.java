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
public class CheckInfoDTO {

    @JsonProperty("check_number")
    private int checkNumber;

    @JsonProperty("cash_register_number")
    private Long cashRegisterNumber;

    @JsonProperty("amount_received")
    private float amountReceived;

    @JsonProperty("guest_number")
    private int guestNumber;

    @JsonProperty("table_number")
    private int tableNumber;

    private String waiter;

    @JsonProperty("date_time")
    private long dateTime;

    // 1C
    private String time;

    @JsonProperty("is_return")
    private boolean isReturn;

    @JsonProperty("is_KKM_checked")
    private boolean isKKMChecked;

    @JsonProperty("is_payed")
    private boolean isPayed;

    @JsonProperty("is_payed_by_card")
    private boolean isPayedByCard;

    @JsonProperty("is_delivery")
    private boolean isDelivery;

}
