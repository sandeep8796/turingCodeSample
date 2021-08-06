package com.turing.codingsample.somesample.DummyServiceItem;

import amazon.platform.types.Length;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AsinItemPackageDimension {
    private final Length itemPackageLength;
    private final Length itemPackageHeight;
    private final Length itemPackageWidth;
}
