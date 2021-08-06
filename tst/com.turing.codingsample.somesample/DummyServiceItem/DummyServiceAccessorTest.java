package com.amazon.contracogseventdataaggregators.external.DummyService.catalogitem;

//some of the imports are removed
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CatalogClientAccessorTest {
    private static final String ASIN = "Test123";
    private static final long MARKETPLACE_ID = 1L;
    private static final String BINDING = "consumer_electronics";
    private static final String CATEGORY = "Test_Category";
    private static final String SUB_CATEGORY = "Test_SubCategory";

    @Mock private DatapathClient datapathClient;
    @Mock private MetricsClient metricsClient;
    @Mock private CatalogItemRequestProcessor catalogItemRequestProcessor;
    @InjectMocks private CatalogClientAccessor subject;

    @Test
    void testGetCatalogItem_OnSuccess_ReturnsAsinDetails() throws Exception {
        final CatalogItemRequest catalogItemRequest = CatalogItemRequest.builder()
                .asin(ASIN)
                .marketplaceId(MARKETPLACE_ID)
                .build();
        final AsinAttributes expected = buildExpectedAsinDetails();
        when(datapathClient.callWithRetry(catalogItemRequest, catalogItemRequestProcessor, metricsClient)).thenReturn(expected);
        AsinAttributes actual = subject.call(catalogItemRequest);
        Assertions.assertEquals(expected.getBinding(), actual.getBinding());
        Assertions.assertEquals(expected.getProductCategory(), actual.getProductCategory());
        Assertions.assertEquals(expected.getProductSubcategory(), actual.getProductSubcategory());
    }

    @Test
    void testGetCatalogItem_On_IllegalStateException_ThrowsIllegalStateException() throws Exception {
        final CatalogItemRequest catalogItemRequest = CatalogItemRequest.builder()
                .asin(ASIN)
                .marketplaceId(MARKETPLACE_ID)
                .build();
        when(datapathClient.callWithRetry(catalogItemRequest, catalogItemRequestProcessor, metricsClient)).thenThrow(IllegalStateException.class);
        Assertions.assertThrows(IllegalStateException.class,
                () -> subject.call(catalogItemRequest));
    }

    @Test
    void testGetCatalogItem_On_IOException_ThrowsDependentServiceFailureException() throws Exception {
        final CatalogItemRequest catalogItemRequest = CatalogItemRequest.builder()
                .asin(ASIN)
                .marketplaceId(MARKETPLACE_ID)
                .build();
        when(datapathClient.callWithRetry(catalogItemRequest, catalogItemRequestProcessor, metricsClient)).thenThrow(IOException.class);
        Assertions.assertThrows(DependentServiceFailureException.class,
                () -> subject.call(catalogItemRequest));
    }

    @Test
    void testGetCatalogItem_On_NullInput_ThrowsIllegalArgumentException() throws Exception {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> subject.call(null));
    }

    private AsinAttributes buildExpectedAsinDetails() {
        return AsinAttributes.builder()
                .binding(BINDING)
                .productCategory(CATEGORY)
                .productSubcategory(SUB_CATEGORY)
                .build();
    }
}
