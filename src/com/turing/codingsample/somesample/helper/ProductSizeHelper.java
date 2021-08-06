package com.turing.codingsample.somesample.helper;

import amazon.platform.types.Length;
import amazon.platform.types.Weight;
import com.amazon.contracogseventdataaggregators.external.catalogv3.catalogitem.AsinItemPackageDimension;
import com.amazon.contracogseventdataaggregators.external.catalogv3.catalogitem.AsinPhysicalAttributes;
import com.amazon.contracogseventdataaggregators.external.catalogv3.catalogitem.ProductSize;
import com.amazon.contracogseventdataaggregators.external.catalogv3.datapath.DatapathConstants;

/**
 * Responsible for deriving productSize based on other catalog Attributes.
 */
public class ProductSizeHelper {
    /**
     * This methods computes the product size based on below attributes of asin
     * @param categoryCode of asin
     * @param specialDeliveryRequirement value of asin
     * @param physicalAttribute represents item_package_dimensions and weight
     * @return productSize
     */
    public ProductSize deriveProductSize(final Long marketplaceId,
                                                final String categoryCode,
                                                final String specialDeliveryRequirement,
                                                final AsinPhysicalAttributes physicalAttribute) {
        Weight itemPackageWeight = physicalAttribute.getItemPackageWeight();

        if (marketplaceId == 1) {
            if (itemPackageWeight != null) {
                itemPackageWeight = DimensionHelper.convertToHundrethOfPounds(itemPackageWeight);
                final Double itemPackageWeightValue = itemPackageWeight.getValue();
                if (itemPackageWeightValue >= DatapathConstants.PACKAGE_WEIGHT_FOR_HBF) {
                    return ProductSize.HEAVY_BULKY_FREIGHT;
                }
            }

            if (DatapathConstants.SPECIAL_DELIVERY_REQ_FOR_HBP.contains(specialDeliveryRequirement)
                    || DatapathConstants.CATEGORY_CODE_FOR_HBP.contains(categoryCode)
                    || isSatisfyingPhysicalCriteriaForHbp(physicalAttribute)) {
                return ProductSize.HEAVY_BULKY_PARCEL;
            }

            return ProductSize.SMALL_SIZE_SHIPMENT;
        }
        return ProductSize.UNSUPPORTED;
    }

    private boolean isSatisfyingPhysicalCriteriaForHbp(final AsinPhysicalAttributes asinPhysicalAttributes) {
        final AsinItemPackageDimension packageDimension = asinPhysicalAttributes.getItemPackageDimension();
        //initializing it with zero to avoid null check everytime.
        double itemPackageWeightValue = 0;
        double itemPackageLengthValue = 0;
        double itemPackageHeightValue = 0;
        double itemPackageWidthValue = 0;

        Weight itemPackageWeight = asinPhysicalAttributes.getItemPackageWeight();
        if (itemPackageWeight != null) {
            itemPackageWeight = DimensionHelper.convertToHundrethOfPounds(itemPackageWeight);
            itemPackageWeightValue = itemPackageWeight.getValue();
        }

        Length itemPackageWidth = packageDimension.getItemPackageWidth();
        if (itemPackageWidth != null) {
            itemPackageWidth = DimensionHelper.convertToHundrethOfInches(itemPackageWidth);
            itemPackageWidthValue = itemPackageWidth.getValue();
        }

        Length itemPackageHeight = packageDimension.getItemPackageHeight();
        if (itemPackageHeight != null) {
            itemPackageHeight = DimensionHelper.convertToHundrethOfInches(itemPackageHeight);
            itemPackageHeightValue = itemPackageHeight.getValue();
        }

        Length itemPackageLength = packageDimension.getItemPackageLength();
        if (itemPackageLength != null) {
            itemPackageLength = DimensionHelper.convertToHundrethOfInches(itemPackageLength);
            itemPackageLengthValue = itemPackageLength.getValue();
        }

        final double packageGirth = getPackageGirth(itemPackageLengthValue, itemPackageHeightValue, itemPackageWidthValue);
        if (itemPackageWeightValue >= DatapathConstants.PACKAGE_WEIGHT_FOR_HBP
                || itemPackageLengthValue >= DatapathConstants.PACKAGE_LENGTH_FOR_HBP
                || packageGirth >= DatapathConstants.PACKAGE_GIRTH_FOR_HBP) {
            return true;
        }
        return false;
    }

    private double getPackageGirth(final double itemPackageLength,
                                   final double itemPackageHeight,
                                   final double itemPackageWidth) {
        return (((2 * itemPackageWidth) + (2 * itemPackageHeight)) + itemPackageLength);
    }
}
