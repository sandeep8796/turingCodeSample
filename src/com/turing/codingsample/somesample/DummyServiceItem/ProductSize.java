package com.turing.codingsample.somesample.DummyServiceItem;

public enum ProductSize {
    HEAVY_BULKY_FREIGHT("HeavyBulkyFreight"),
    HEAVY_BULKY_PARCEL("HeavyBulkyParcel"),
    SMALL_SIZE_SHIPMENT("SmallSizeShipment"),
    UNSUPPORTED("Unsupported");

    private final String productSizeDescriptiveName;

    ProductSize(String productSizeDescriptiveName) {
        this.productSizeDescriptiveName = productSizeDescriptiveName;
    }

    public String getProductSizeDescriptiveName() {
        return this.productSizeDescriptiveName;
    }
}
