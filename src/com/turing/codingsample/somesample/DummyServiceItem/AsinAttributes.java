package com.turing.codingsample.somesample.DummyServiceItem;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AsinAttributes {
    private final String binding;
    private final String brandCode;
    private final Integer glProductGroupId;
    private final String itemClassification;
    private final String manufacturerCode;
    private final String productSubcategory;
    private final String productCategory;
    private final String productSize;
    private final String specialDeliveryRequirements;
    private final AsinPhysicalAttributes physicalAttribute;
}
