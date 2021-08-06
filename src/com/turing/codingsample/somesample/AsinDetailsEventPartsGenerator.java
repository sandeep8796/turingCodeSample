package com.turing.codingsample.somesample;

//some of the imports are removed
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * EventPartsGenerator for building the AsinDetails SEA event part.
 *
 * <p>AsinDetails SEA event part contains optional(except asin and marketplaceId) asin attributes, which
 * are important DSI attributes. These attributes are sourced from DummyService api by this
 * EventPartsGenerator. We use a distributed cache to ensure that we don't call CatalogClient(datapath gateway)
 * many times for the same DummyServiceRequest(combination of Asin and Marketplace).</p>
 */
public class AsinDetailsEventPartsGenerator implements EventPartsGenerator {
    private static final String INVOCATIONS_METRIC = "AsinDetailsEventPartsGenerator.Invocations";
    private static final String ERRORS_METRIC = "AsinDetailsEventPartsGenerator.Errors";
    private static final String LATENCY_METRIC = "AsinDetailsEventPartsGenerator.Latency";

    private static final Set<SeaEventPart> EVENT_PARTS_TO_BE_GENERATED = Collections.singleton(
            ContraCogsSeaEventPart.ASIN_DETAILS);
    private static final Set<String> EVENT_PART_NAMES_TO_BE_GENERATED = EVENT_PARTS_TO_BE_GENERATED.stream()
            .map(SeaEventPart::getEventPartName)
            .collect(Collectors.toSet());

    private final EventPartExtractor eventPartExtractor;
    private final CachedDependentService<CatalogItemRequest, AsinAttributes> cachedCatalogClientAccessor;
    private final EventPartBuilder eventPartBuilder;
    private final IonSystem ionSystem;
    private final Aspect dsiCoreAspect;
    private final MetricsClient metricsClient;
    private final boolean areAsinDetailsOptional;

    public AsinDetailsEventPartsGenerator(
            final EventPartExtractor eventPartExtractor,
            final CachedDependentService<CatalogItemRequest, AsinAttributes> cachedCatalogClientAccessor,
            final EventPartBuilder eventPartBuilder,
            final IonSystem ionSystem,
            final DocumentFactory documentFactory,
            final MetricsClient metricsClient,
            final boolean areAsinDetailsOptional) {
        this.eventPartExtractor = eventPartExtractor;
        this.cachedCatalogClientAccessor = cachedCatalogClientAccessor;
        this.eventPartBuilder = eventPartBuilder;
        this.ionSystem = ionSystem;
        this.dsiCoreAspect = documentFactory.aspectOf(ContraCogsSeaEventPart.DSI_CORE.getEventPartName());
        this.metricsClient = metricsClient;
        this.areAsinDetailsOptional = areAsinDetailsOptional;
    }

    @Override
    public List<EventPart> generate(PostedDataNotification postedDataNotification) {
        final long startTime = System.currentTimeMillis();
        metricsClient.countOne(INVOCATIONS_METRIC);

        // Get DistributorShipmentItemCore event part from the PostedDataNotification
        final IonStruct dsiCoreStruct =
                eventPartExtractor.extractEventPart(postedDataNotification, dsiCoreAspect);
        final TypeSafeIonStruct dsiCoreTypeSafeStruct = IonStructs.newTypeSafeStruct(dsiCoreStruct);

        try {
            return generateEventParts(dsiCoreTypeSafeStruct, areAsinDetailsOptional);
        } catch (final Exception ex) {
            metricsClient.countOne(ERRORS_METRIC);
            throw ex;
        } finally {
            final long endTime = System.currentTimeMillis();
            metricsClient.publishLatencyInMillis(LATENCY_METRIC, endTime - startTime);
        }
    }

    private List<EventPart> generateEventParts(final TypeSafeIonStruct dsiStruct, final boolean areAsinDetailsOptional) {
        final int legalEntityId = dsiStruct.getInt(SeaEventPartConstants.LEGAL_ENTITY_ID).intValue();
        final LegalEntity legalEntity = LegalEntity.forId(legalEntityId);
        final Marketplace marketplace = Marketplace.forLegalEntity(legalEntity);

        final long marketplaceId = marketplace.getId();

        final String asin = dsiStruct.getString(SeaEventPartConstants.ISBN)
                .stringValue();
        final CatalogItemRequest catalogItemRequest = CatalogItemRequest.builder()
                .asin(asin)
                .marketplaceId(marketplaceId)
                .build();

        AsinAttributes asinAttributes;
        try {
            asinAttributes = cachedCatalogClientAccessor.call(catalogItemRequest);
        } catch (final IllegalStateException e) {
            if (areAsinDetailsOptional) {
                asinAttributes = AsinAttributes.builder().build();
            } else {
                throw e;
            }
        }

        final IonStruct asinDetails = createAsinDetails(asin, marketplaceId, asinAttributes);

        return Collections.singletonList(eventPartBuilder.build(ContraCogsSeaEventPart.ASIN_DETAILS,
                asinDetails));
    }

    @Override
    public boolean isApplicable(Set<String> completedEventPartNames) {
        final boolean isDsiCoreEventPartPosted = completedEventPartNames.contains(
                ContraCogsSeaEventPart.DSI_CORE.getEventPartName());

        final boolean isAnyEventPartOfInterestAlreadyPostedToSea =
                EVENT_PART_NAMES_TO_BE_GENERATED.stream().anyMatch(completedEventPartNames::contains);

        return isDsiCoreEventPartPosted && !isAnyEventPartOfInterestAlreadyPostedToSea;
    }

    @Override
    public Set<SeaEventPart> getEventParts() {
        return EVENT_PARTS_TO_BE_GENERATED;
    }

    private IonStruct createAsinDetails(final String asin,
                                        final long marketplaceId,
                                        final AsinAttributes asinAttributes) {
        return NullSafeIonStructBuilder.from(ionSystem)
                .withNullableString(SeaEventPartConstants.ASIN, asin)
                .withNullableInt(SeaEventPartConstants.MARKETPLACE_ID, (int)marketplaceId)
                .withNullableInt(SeaEventPartConstants.GL_PRODUCT_GROUP_ID, asinAttributes.getGlProductGroupId())
                .withNullableString(SeaEventPartConstants.CATEGORY_CODE, asinAttributes.getProductCategory())
                .withNullableString(SeaEventPartConstants.SUB_CATEGORY_CODE, asinAttributes.getProductSubcategory())
                .withNullableString(SeaEventPartConstants.BINDING_NAME, asinAttributes.getBinding())
                .withNullableString(SeaEventPartConstants.ITEM_CLASSIFICATION, asinAttributes.getItemClassification())
                .withNullableString(SeaEventPartConstants.BRAND_CODE, asinAttributes.getBrandCode())
                .withNullableString(SeaEventPartConstants.PRODUCT_SIZE, asinAttributes.getProductSize())
                .withNullableString(SeaEventPartConstants.MANUFACTURER_CODE, asinAttributes.getManufacturerCode())
                .build();
    }
}
