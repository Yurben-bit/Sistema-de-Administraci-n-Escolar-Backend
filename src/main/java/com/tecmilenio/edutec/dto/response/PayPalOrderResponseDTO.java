package com.tecmilenio.edutec.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PayPalOrderResponseDTO {
    private String id;
    private String status;
    @JsonProperty("purchase_units")
    private List<PurchaseUnit> purchaseUnits;

    public PayPalOrderResponseDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<PurchaseUnit> getPurchaseUnits() { return purchaseUnits; }
    public void setPurchaseUnits(List<PurchaseUnit> purchaseUnits) { this.purchaseUnits = purchaseUnits; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PurchaseUnit {
        private Payments payments;
        public PurchaseUnit() {}
        public Payments getPayments() { return payments; }
        public void setPayments(Payments payments) { this.payments = payments; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payments {
        private List<Capture> captures;
        public Payments() {}
        public List<Capture> getCaptures() { return captures; }
        public void setCaptures(List<Capture> captures) { this.captures = captures; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Capture {
        private String id;
        private String status;
        public Capture() {}
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
