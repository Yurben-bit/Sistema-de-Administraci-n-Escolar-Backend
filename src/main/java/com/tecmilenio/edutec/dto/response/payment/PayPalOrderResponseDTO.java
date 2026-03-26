package com.tecmilenio.edutec.dto.response.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PayPalOrderResponseDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("status")
    private String status;

    @JsonProperty("purchase_units")
    private List<PurchaseUnit> purchaseUnits;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PurchaseUnit {

        @JsonProperty("amount")
        private Amount amount;

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Amount {

            @JsonProperty("currency_code")
            private String currencyCode;

            @JsonProperty("value")
            private String value;
        }
    }
}