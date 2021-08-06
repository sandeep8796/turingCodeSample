package com.turing.codingsample.somesample.helper;

//some of the imports are removed
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductSizeHelperTest {
    private static final String CATEGORY_CODE_1 = "50400100";
    private static final String CATEGORY_CODE_2 = "55400100";
    private static final String DELIVERY_REQ_1 = "white_glove_service";
    private static final String DELIVERY_REQ_2 = "black_glove_service";

    private AsinPhysicalAttributes asinPhysicalAttributes;
    private AsinItemPackageDimension asinItemPackageDimension;
    private ProductSizeHelper subject;

    @BeforeEach
    void setup() {
        asinItemPackageDimension = AsinItemPackageDimension.builder()
                .itemPackageHeight(new Length(5, LengthUnit.INCHES))
                .itemPackageLength(new Length(2, LengthUnit.INCHES))
                .itemPackageWidth(new Length(1, LengthUnit.INCHES))
                .build();
        asinPhysicalAttributes = AsinPhysicalAttributes.builder()
                .itemPackageDimension(asinItemPackageDimension)
                .itemPackageWeight(new Weight(15, WeightUnit.POUNDS))
                .build();
        subject = new ProductSizeHelper();
       }

    @Test
    void testForHeavyBulkyFreight() {
        final ProductSize actual = subject.deriveProductSize(1L, CATEGORY_CODE_1, DELIVERY_REQ_1, asinPhysicalAttributes);
        Assertions.assertEquals(ProductSize.HEAVY_BULKY_FREIGHT, actual);
    }

    @Test
    void testForUnsupportedProductSize() {
        final ProductSize actual = subject.deriveProductSize(2L, CATEGORY_CODE_1, DELIVERY_REQ_1, asinPhysicalAttributes);
        Assertions.assertEquals(ProductSize.UNSUPPORTED, actual);
    }

    @Test
    void testWithHBPCategoryCode() {
        asinPhysicalAttributes = AsinPhysicalAttributes.builder()
                .itemPackageDimension(asinItemPackageDimension)
                .itemPackageWeight(new Weight(0.2, WeightUnit.POUNDS))
                .build();
        final ProductSize actual = subject.deriveProductSize(1L, CATEGORY_CODE_1, DELIVERY_REQ_2, asinPhysicalAttributes);
        Assertions.assertEquals(ProductSize.HEAVY_BULKY_PARCEL, actual);
    }

    @Test
    void testWithHBPDeliveryReq() {
        asinPhysicalAttributes = AsinPhysicalAttributes.builder()
                .itemPackageDimension(asinItemPackageDimension)
                .itemPackageWeight(new Weight(0.2, WeightUnit.POUNDS))
                .build();
        final ProductSize actual = subject.deriveProductSize(1L, CATEGORY_CODE_2, DELIVERY_REQ_1, asinPhysicalAttributes);
        Assertions.assertEquals(ProductSize.HEAVY_BULKY_PARCEL, actual);
    }

    @Test
    void testWithHBPItemPackageWeight() {
        asinPhysicalAttributes = AsinPhysicalAttributes.builder()
                .itemPackageDimension(asinItemPackageDimension)
                .itemPackageWeight(new Weight(0.5, WeightUnit.POUNDS))
                .build();
        final ProductSize actual = subject.deriveProductSize(1L, CATEGORY_CODE_2, DELIVERY_REQ_2, asinPhysicalAttributes);
        Assertions.assertEquals(ProductSize.HEAVY_BULKY_PARCEL, actual);
    }
}
