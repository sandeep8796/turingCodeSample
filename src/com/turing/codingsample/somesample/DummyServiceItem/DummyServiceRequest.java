package com.turing.codingsample.somesample.DummyServiceItem;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class DummyServiceRequest {
    @NonNull private final Long marketplaceId;
    @NonNull private final String asin;
}
