package com.turing.codingsample.somesample.DummyServiceItem;

import amazon.platform.types.Weight;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AsinPhysicalAttributes {
    private final AsinItemPackageDimension itemPackageDimension;
    private final Weight itemPackageWeight;
}
