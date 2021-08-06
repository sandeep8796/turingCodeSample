package com.amazon.contracogseventdataaggregators.external.DummyService;

//some of the imports are removed
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsinDetailsEventPartsGeneratorTest {
    @Mock private EventPartExtractor eventPartExtractor;
    @Mock private EventPartBuilder eventPartBuilder;
    @Mock private CachedDependentService<CatalogItemRequest, AsinAttributes> cachedCatalogItemAccessor;
    @Mock private MerchantMarketplaceBasedCsiFilter merchantMarketplaceBasedCsiFilter;
    @Mock private MetricsClient metricsClient;


    private final IonSystem ionSystem = IonSystemBuilder.standard().build();
    private final DocumentFactory documentFactory = Documents.createFactory("META-INF/DocumentModelConfig.properties");
    private final Aspect dsiCoreAspect = documentFactory.aspectOf("DistributorShipmentItemCore");

    private AsinDetailsEventPartsGenerator subject;

    @BeforeEach
    void setup() {
        subject = new AsinDetailsEventPartsGenerator(eventPartExtractor, cachedCatalogItemAccessor,
                eventPartBuilder, ionSystem, documentFactory, metricsClient, true);
    }

    @Test
    void testGetEventParts() {
        final Set<SeaEventPart> expectedEventParts = Collections.singleton(
                ContraCogsSeaEventPart.ASIN_DETAILS);

        Assertions.assertEquals(expectedEventParts, subject.getEventParts());
    }

    @Test
    void testGenerate_OnSuccess_ReturnsEventParts() {
        final PostedDataNotification postedDataNotification = mock(PostedDataNotification.class);
        final EventPart asinDetailsEventPart = mock(EventPart.class);

        final TypeSafeIonStruct dsiCoreEventPart = IonStructs.newTypeSafeStruct(IonTestDataBuilder.createDsiCore());
        System.out.println(dsiCoreEventPart.toString());
        final AsinAttributes asinAttributes = AsinAttributes.builder()
                .brandCode(IonTestDataBuilder.BRAND_CODE)
                .productCategory(IonTestDataBuilder.CATEGORY_CODE)
                .productSubcategory(IonTestDataBuilder.SUB_CATEGORY_CODE)
                .manufacturerCode(IonTestDataBuilder.MANUFACTURER_CODE)
                .itemClassification(IonTestDataBuilder.ITEM_CLASSIFICATION)
                .binding(IonTestDataBuilder.BINDING_NAME)
                .glProductGroupId(IonTestDataBuilder.GL_PRODUCT_GROUP_ID)
                .productSize(IonTestDataBuilder.PRODUCT_SIZE)
                .build();
        final IonStruct asinAttributesDetails = NullSafeIonStructBuilder.from(ionSystem)
                .withString("asin", IonTestDataBuilder.ISBN)
                .withInt("marketplaceId", 1)
                .withString("brandCode", IonTestDataBuilder.BRAND_CODE)
                .withString("categoryCode", IonTestDataBuilder.CATEGORY_CODE)
                .withString("subCategoryCode", IonTestDataBuilder.SUB_CATEGORY_CODE)
                .withString("manufacturerCode", IonTestDataBuilder.MANUFACTURER_CODE)
                .withString("itemClassification", IonTestDataBuilder.ITEM_CLASSIFICATION)
                .withString("bindingName", IonTestDataBuilder.BINDING_NAME)
                .withInt("glProductGroupId", IonTestDataBuilder.GL_PRODUCT_GROUP_ID)
                .withString("productSize", IonTestDataBuilder.PRODUCT_SIZE)
                .build();

        final int legalEntityId = dsiCoreEventPart.getInt(SeaEventPartConstants.LEGAL_ENTITY_ID).intValue();
        final LegalEntity legalEntity = LegalEntity.forId(legalEntityId);
        final Marketplace marketplace = Marketplace.forLegalEntity(legalEntity);
        final long marketplaceId = marketplace.getId();
        final CatalogItemRequest catalogItemRequest = CatalogItemRequest.builder()
                .asin(IonTestDataBuilder.ISBN)
                .marketplaceId(marketplaceId)
                .build();

        when(eventPartExtractor.extractEventPart(postedDataNotification, dsiCoreAspect))
                .thenReturn(dsiCoreEventPart);
        when(cachedCatalogItemAccessor.call(catalogItemRequest))
                .thenReturn(asinAttributes);
        when(eventPartBuilder.build(ContraCogsSeaEventPart.ASIN_DETAILS, asinAttributesDetails))
                .thenReturn(asinDetailsEventPart);

        final List<EventPart> actualEventParts = subject.generate(postedDataNotification);

        Assertions.assertEquals(Arrays.asList(asinDetailsEventPart), actualEventParts);
        verify(metricsClient).countOne("AsinDetailsEventPartsGenerator.Invocations");
        verify(metricsClient).publishLatencyInMillis(
                Mockito.eq("AsinDetailsEventPartsGenerator.Latency"),
                Mockito.any());
    }

    @Test
    void testGenerate_OnSuccess_WhenIllegalStateExceptionOnAsinDetailsOptional_ReturnsEventParts() {
        final PostedDataNotification postedDataNotification = mock(PostedDataNotification.class);
        final EventPart asinDetailsEventPart = mock(EventPart.class);

        final TypeSafeIonStruct dsiCoreEventPart = IonStructs.newTypeSafeStruct(IonTestDataBuilder.createDsiCore());
        final AsinAttributes asinAttributes = AsinAttributes.builder().build();
        final IonStruct asinAttributesDetails = NullSafeIonStructBuilder.from(ionSystem)
                .withNullableString("asin", IonTestDataBuilder.ISBN)
                .withNullableInt("marketplaceId", 1)
                .withNullableString("brandCode", null)
                .withNullableString("categoryCode", null)
                .withNullableString("subCategoryCode", null)
                .withNullableString("manufacturerCode", null)
                .withNullableString("itemClassification", null)
                .withNullableString("bindingName", null)
                .withNullableInt("glProductGroupId", null)
                .withNullableString("productSize", null)
                .build();

        final int legalEntityId = dsiCoreEventPart.getInt(SeaEventPartConstants.LEGAL_ENTITY_ID).intValue();
        final LegalEntity legalEntity = LegalEntity.forId(legalEntityId);
        final Marketplace marketplace = Marketplace.forLegalEntity(legalEntity);
        final long marketplaceId = marketplace.getId();
        final CatalogItemRequest catalogItemRequest = CatalogItemRequest.builder()
                .asin(IonTestDataBuilder.ISBN)
                .marketplaceId(marketplaceId)
                .build();

        when(eventPartExtractor.extractEventPart(postedDataNotification, dsiCoreAspect))
                .thenReturn(dsiCoreEventPart);
        when(cachedCatalogItemAccessor.call(catalogItemRequest))
                .thenThrow(IllegalStateException.class);
        when(eventPartBuilder.build(ContraCogsSeaEventPart.ASIN_DETAILS, asinAttributesDetails))
                .thenReturn(asinDetailsEventPart);

        final List<EventPart> actualEventParts = subject.generate(postedDataNotification);

        Assertions.assertEquals(Arrays.asList(asinDetailsEventPart), actualEventParts);
        verify(metricsClient).countOne("AsinDetailsEventPartsGenerator.Invocations");
        verify(metricsClient).publishLatencyInMillis(
                Mockito.eq("AsinDetailsEventPartsGenerator.Latency"),
                Mockito.any());
    }

    @Test
    void testGenerate_OnFailure_WhenIllegalStateExceptionOnAsinDetailsNotOptional_throwIllegalStateException() {
        final PostedDataNotification postedDataNotification = mock(PostedDataNotification.class);
        final EventPart asinDetailsEventPart = mock(EventPart.class);

        final TypeSafeIonStruct dsiCoreEventPart = IonStructs.newTypeSafeStruct(IonTestDataBuilder.createDsiCore());
        final AsinAttributes asinAttributes = AsinAttributes.builder().build();
        final IonStruct asinAttributesDetails = NullSafeIonStructBuilder.from(ionSystem)
                .withNullableString("asin", IonTestDataBuilder.ISBN)
                .withNullableInt("marketplaceId", 1)
                .withNullableString("brandCode", null)
                .withNullableString("categoryCode", null)
                .withNullableString("subCategoryCode", null)
                .withNullableString("manufacturerCode", null)
                .withNullableString("itemClassification", null)
                .withNullableString("bindingName", null)
                .withNullableInt("glProductGroupId", null)
                .withNullableString("productSize", null)
                .build();

        final int legalEntityId = dsiCoreEventPart.getInt(SeaEventPartConstants.LEGAL_ENTITY_ID).intValue();
        final LegalEntity legalEntity = LegalEntity.forId(legalEntityId);
        final Marketplace marketplace = Marketplace.forLegalEntity(legalEntity);
        final long marketplaceId = marketplace.getId();
        final CatalogItemRequest catalogItemRequest = CatalogItemRequest.builder()
                .asin(IonTestDataBuilder.ISBN)
                .marketplaceId(marketplaceId)
                .build();
        subject = new AsinDetailsEventPartsGenerator(eventPartExtractor, cachedCatalogItemAccessor,
                eventPartBuilder, ionSystem, documentFactory, metricsClient, false);

        when(eventPartExtractor.extractEventPart(postedDataNotification, dsiCoreAspect))
                .thenReturn(dsiCoreEventPart);
        when(cachedCatalogItemAccessor.call(catalogItemRequest))
                .thenThrow(IllegalStateException.class);

        Assertions.assertThrows(IllegalStateException.class, () -> subject.generate(postedDataNotification));
    }

    @Test
    void testGenerate_OnCatalogServiceCallFailure_ThrowsDependentServiceFailureException() {
        final PostedDataNotification postedDataNotification = mock(PostedDataNotification.class);
        final TypeSafeIonStruct dsiCoreEventPart = IonStructs.newTypeSafeStruct(IonTestDataBuilder.createDsiCore());

        final int legalEntityId = dsiCoreEventPart.getInt(SeaEventPartConstants.LEGAL_ENTITY_ID).intValue();
        final LegalEntity legalEntity = LegalEntity.forId(legalEntityId);
        final Marketplace marketplace = Marketplace.forLegalEntity(legalEntity);
        final long marketplaceId = marketplace.getId();

        final CatalogItemRequest catalogItemRequest = CatalogItemRequest.builder()
                .asin(IonTestDataBuilder.ISBN)
                .marketplaceId(marketplaceId)
                .build();

        when(eventPartExtractor.extractEventPart(postedDataNotification, dsiCoreAspect))
                .thenReturn(dsiCoreEventPart);

        doThrow(DependentServiceFailureException.class)
                .when(cachedCatalogItemAccessor).call(catalogItemRequest);

        Assertions.assertThrows(DependentServiceFailureException.class,
                () -> subject.generate(postedDataNotification));

        verify(metricsClient).countOne("AsinDetailsEventPartsGenerator.Invocations");
        verify(metricsClient).countOne("AsinDetailsEventPartsGenerator.Errors");
        verify(metricsClient).publishLatencyInMillis(
                Mockito.eq("AsinDetailsEventPartsGenerator.Latency"),
                Mockito.any());
    }

    @Test
    void testIsApplicable_WhenDsiCoreEventPartIsPosted_ButAsinDetailsIsNot_ReturnsTrue() {
        Assertions.assertTrue(subject.isApplicable(Collections.singleton("DistributorShipmentItemCore")));
    }

    @Test
    void testIsApplicable_WhenDsiCoreAndAsinDetailsEventPartArePosted_ReturnsFalse() {
        Assertions.assertFalse(subject.isApplicable(
                Sets.newHashSet("DistributorShipmentItemCore", "AsinDetails")));
    }

    @Test
    void testIsApplicable_WhenDsiCoreIsNotPosted_ReturnsFalse() {
        Assertions.assertFalse(subject.isApplicable(Collections.emptySet()));
    }
}
