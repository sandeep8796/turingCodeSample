package com.turing.codingsample.somesample.DummyServiceItem;

//some of the imports are removed
import java.io.IOException;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;

/**
 * {@inheritDoc}.
 */
@Slf4j
@AllArgsConstructor
public class DummyServiceRequestProcessor implements DatapathRequestProcessor {
    @NonNull private final String endpoint;
    @NonNull private final IonSystem ionSystem;
    @NonNull private final ProductSizeHelper productSizeHelper;

    @Override
    public String buildRequestUrl(@NonNull final DummyServiceRequest input) {
        return String.format("%s/%s/%s", endpoint, input.getMarketplaceId(), input.getAsin());
    }

    @Override
    public AsinAttributes buildOutput(@NonNull final DummyServiceRequest input,
                                      @NonNull final HttpEntity httpEntity) {
        try (IonReader ionReader = ionSystem.newReader(httpEntity.getContent())) {
            final IonDatagram ionDatagram = ionSystem.getLoader().load(ionReader);
            if (ionDatagram == null || ionDatagram.isEmpty()) {
                final String errMessage = "Incorrect format of ion data from catalogV3.";
                log.error(errMessage);
                throw new MalformedDataException(errMessage);
            }

            final IonStruct struct = (IonStruct) ionDatagram.get(0);
            final Optional<IonStruct> productInfo = Optional.ofNullable(struct)
                    .map(val -> val.get(DatapathConstants.PRODUCT))
                    .filter(this::isNonNullIonValue)
                    .map(IonStruct.class::cast);

            if (!productInfo.isPresent()) {
                final String errMessage = "The 'product' attribute must not be null.";
                log.error(errMessage);
                throw new MalformedDataException(errMessage);
            }

            final IonStruct productInfoStruct = productInfo.get();
            final Long marketplaceId = input.getMarketplaceId();
            return buildAsinAttributesFromStruct(marketplaceId, productInfoStruct);
        } catch (IOException e) {
            final String errorMessage = String.format("Error processing response from datapath for the input: %s", input);
            log.error(errorMessage, e);
            throw new MalformedDataException(errorMessage, e);
        }
    }

    private AsinAttributes buildAsinAttributesFromStruct(final Long marketplaceId,
                                                         final IonStruct productInfoStruct) {
        final Optional<String> glProductGroupType = getProductAttribute(productInfoStruct,
                DatapathConstants.GL_PRODUCT_GROUP);
        final Optional<String> binding = getProductAttribute(productInfoStruct, DatapathConstants.BINDING);
        final Optional<String> productCategory = getProductAttribute(productInfoStruct,
                DatapathConstants.PRODUCT_CATEGORY);
        final Optional<String> productSubCategory = getProductAttribute(productInfoStruct,
                DatapathConstants.PRODUCT_SUBCATEGORY);
        final Optional<String> itemClassification = getProductAttribute(productInfoStruct,
                DatapathConstants.ITEM_CLASSIFICATION);
        final Optional<String> brandCode = getProductAttribute(productInfoStruct, DatapathConstants.BRAND_CODE);
        final Optional<String> specialDeliveryRequirements = getProductAttribute(productInfoStruct,
                DatapathConstants.SPECIAL_DELIVERY_REQUIREMENTS);
        final Optional<String> manufacturerCode = getProductAttribute(productInfoStruct, DatapathConstants.MANUFACTURER);

        final AsinItemPackageDimension asinItemPackageDimension = getItemPackageDimensions(productInfoStruct,
                DatapathConstants.ITEM_PACKAGE_DIMENSIONS);

        final Weight itemPackageWeight = getAsinItemPackageWeight(productInfoStruct,
                DatapathConstants.ITEM_PACKAGE_WEIGHT);

        //building physical attributes
        final AsinPhysicalAttributes asinPhysicalAttributes = AsinPhysicalAttributes.builder()
                .itemPackageDimension(asinItemPackageDimension)
                .itemPackageWeight(itemPackageWeight)
                .build();

        final String categoryCode = productCategory.isPresent() ? productCategory.get() : null;
        final String deliveryRequirement = specialDeliveryRequirements.isPresent() ? specialDeliveryRequirements.get()
                : null;
        final ProductSize productSize = productSizeHelper.deriveProductSize(marketplaceId,
                categoryCode,
                deliveryRequirement,
                asinPhysicalAttributes);

        Integer glProductGroupId =  null;
        if (glProductGroupType.isPresent()) {
            final String glProductGroup = glProductGroupType.get();
            glProductGroupId = GlProductGroupMap.getIdByName(glProductGroup);
            if (glProductGroupId == null) {
                final String errMessage = String.format("Name to Id mapping is not available for "
                        + "glProductGroupType: %s", glProductGroup);
                throw new MalformedDataException(errMessage);
            }
        }

        return AsinAttributes.builder()
                .binding(binding.isPresent() ? binding.get() : null)
                .brandCode(brandCode.isPresent() ? brandCode.get() : null)
                .glProductGroupId(glProductGroupId)
                .itemClassification(itemClassification.isPresent() ? itemClassification.get() : null)
                .manufacturerCode(manufacturerCode.isPresent() ? manufacturerCode.get() : null)
                .productSize(productSize.getProductSizeDescriptiveName())
                .productCategory(productCategory.isPresent() ? productCategory.get() : null)
                .productSubcategory(productSubCategory.isPresent() ? productSubCategory.get() : null)
                .specialDeliveryRequirements(specialDeliveryRequirements.isPresent() ? specialDeliveryRequirements.get()
                        : null)
                .physicalAttribute(asinPhysicalAttributes)
                .build();
    }

    private Optional<String> getProductAttribute(@Nonnull final IonStruct productInfo,
                                                 final String attributeName) {
        final Optional<IonStruct> firstAttribute = getTheFirstStructForKey(productInfo,
                attributeName);
        return firstAttribute
                .map(val -> val.get(DatapathConstants.VALUE))
                .map(str -> str.toString())
                .map(str -> StringUtils.replace(str, "\"", StringUtils.EMPTY));
    }

    private AsinItemPackageDimension getItemPackageDimensions(final IonStruct productInfo,
                                                              final String attributeName) {
        final Optional<IonStruct> firstDim = getTheFirstStructForKey(productInfo, DatapathConstants.ITEM_PACKAGE_DIMENSIONS);
        final AsinItemPackageDimension asinItemPackageDimension;

        if (firstDim.isPresent()) {
            asinItemPackageDimension = AsinItemPackageDimension.builder()
                    .itemPackageLength(getTheLength(firstDim.get(), DatapathConstants.LENGTH))
                    .itemPackageHeight(getTheLength(firstDim.get(), DatapathConstants.HEIGHT))
                    .itemPackageWidth(getTheLength(firstDim.get(), DatapathConstants.WIDTH))
                    .build();
        } else {
            final String infoMessage = "Item package dimension not present in catalog data, building empty dimension";
            log.info(infoMessage);
            asinItemPackageDimension = AsinItemPackageDimension.builder().build();
        }
        return asinItemPackageDimension;
    }

    private Optional<IonStruct> getTheFirstStructForKey(final IonStruct productInfo,
                                                        final String key) {
        final IonList data = (IonList) productInfo.get(key);
        if (data == null) {
            final String infoMessage = String.format("Returning empty optional as attribute: %s not present in response",
                    key);
            log.info(infoMessage);
            return Optional.empty();
        }
        return data.stream().findFirst().map(IonStruct.class::cast);
    }

    private Weight getAsinItemPackageWeight(final IonStruct productInfo,
                                            final String attributeName) {
        final Optional<IonStruct> firstWeight = getTheFirstStructForKey(productInfo, DatapathConstants.ITEM_PACKAGE_WEIGHT);
        Weight itemPackageWeight = null;
        if (firstWeight.isPresent()) {
            itemPackageWeight = getTheWeight(firstWeight.get());
        } else {
            final String infoMessage = "Item package weight not present in catalog data, returning null value";
            log.info(infoMessage);
        }
        return itemPackageWeight;
    }

    private Weight getTheWeight(final IonStruct ionStruct) {

        final Optional<IonStruct> weightNormalized =
                Optional.ofNullable(ionStruct)
                        .map(ionData -> ionData.get(DatapathConstants.NORMALIZED_VALUE))
                        .filter(this::isNonNullIonValue)
                        .map(IonStruct.class::cast);

        final Optional<IonDecimal> weightVal = weightNormalized
                .map(val -> val.get(DatapathConstants.VALUE))
                .map(IonDecimal.class::cast);

        final Optional<String> weightUnit = weightNormalized
                .map(unit -> unit.get(DatapathConstants.UNIT))
                .map(str -> str.toString());

        if (!weightVal.isPresent() || !weightUnit.isPresent()) {
            final String infoMessage = "Weight values missing in catalog data";
            log.info(infoMessage);
            return null;
        }

        /*In catalogV3 response, complete name of the unit is written, however in Weight class
        we need short name of the unit.
        */
        final String newUnit = DatapathConstants.WEIGHT_SHORT_UNIT.get(weightUnit.get());
        if (newUnit == null) {
            final String errMessage = String.format("Weight Unit mapping is not available for unit: %s",
                    weightUnit.get());
            throw new MalformedDataException(errMessage);
        }
        return DimensionHelper.getWeight(weightVal.get().doubleValue(), newUnit);
    }

    private Length getTheLength(final IonStruct ionStruct, final String dimension) {

        final Optional<IonStruct> lenNormalized = Optional.ofNullable(ionStruct)
                .map(ionData -> ionData.get(dimension))
                .filter(this::isNonNullIonValue)
                .map(IonStruct.class::cast)
                .map(ionNormalisedValue -> ionNormalisedValue.get(DatapathConstants.NORMALIZED_VALUE))
                .filter(this::isNonNullIonValue)
                .map(IonStruct.class::cast);

        final Optional<IonDecimal> lenVal = lenNormalized
                .map(val -> val.get(DatapathConstants.VALUE))
                .map(IonDecimal.class::cast);

        final Optional<String> lenUnit = lenNormalized
                .map(unit -> unit.get(DatapathConstants.UNIT))
                .map(str -> str.toString());

        if (!lenVal.isPresent() || !lenUnit.isPresent()) {
            final String infoMessage = "Item Length is missing in catalog data, returning null";
            log.info(infoMessage);
            return null;
        }

        /*In catalogV3 response, complete name of the unit is written, however in Length cla
        we need short name of the unit.
        */
        final String newUnit = DatapathConstants.LENGTH_SHORT_UNIT.get(lenUnit.get());
        if (newUnit == null) {
            final String errMessage = String.format("Length Unit mapping is not available for unit: %s",
                    lenUnit.get());
            throw new MalformedDataException(errMessage);
        }
        return DimensionHelper.getLength(lenVal.get().doubleValue(), newUnit);
    }

    private boolean isNonNullIonValue(final IonValue ionValue) {
        return ionValue != null && !ionValue.isNullValue();
    }
}
