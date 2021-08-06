package com.amazon.contracogseventdataaggregators.external.catalogV3.catalogitem;

//some of the imports are removed
import org.apache.http.HttpEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CatalogItemRequestProcessorTest {
    private static final String ASIN = "Test123";
    private static final long MARKETPLACE_ID = 1L;
    private static final String ENDPOINT= "https://test.com/catalog/item";
    private static final String EXPECTED_URL= "https://test.com/catalog/item/1/Test123";

    private final IonSystem ionSystem = IonSystemBuilder.standard().build();
    private CatalogItemRequestProcessor subject;

    private final CatalogItemRequest catalogItemRequest = CatalogItemRequest.builder()
            .asin(ASIN)
            .marketplaceId(MARKETPLACE_ID)
            .build();

    @Mock private HttpEntity httpEntity;

    @BeforeEach
    void setUp() {
        final ProductSizeHelper productSizeHelper = new ProductSizeHelper();
        subject = new CatalogItemRequestProcessor(ENDPOINT, ionSystem, productSizeHelper);
    }

    @Test
    void testBuildRequestUrl_OnSuccess_ReturnsURL() {
        final String actual = subject.buildRequestUrl(catalogItemRequest);
        Assertions.assertEquals(actual, EXPECTED_URL);
    }

    @Test
    void testBuildRequestUrl_OnNullRequest_ThrowIllegalArgumentException() {
        final CatalogItemRequest catalogItemRequest = null;
        Assertions.assertThrows(IllegalArgumentException.class, () -> subject.buildRequestUrl(catalogItemRequest));
    }

    @Test
    void testBuildOutput_OnSuccess_ReturnsAsinDetails() throws IOException {
        when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(
                buildIonPayload(new String(Files.readAllBytes(Paths.get(DatapathConstants.VALID_RESPONSE_FILE_PATH))))));

        AsinAttributes asinDetails = subject.buildOutput(catalogItemRequest, httpEntity);
        Assertions.assertEquals(asinDetails.getBinding(), "apparel");
        Assertions.assertEquals(asinDetails.getProductCategory(), "19307700");
        Assertions.assertEquals(asinDetails.getProductSubcategory(), "19307721");
        Assertions.assertEquals(asinDetails.getBrandCode(), "CRIHM");
        Assertions.assertEquals(asinDetails.getGlProductGroupId(), 193);
        Assertions.assertEquals(asinDetails.getItemClassification(), "base_product");
        Assertions.assertEquals(asinDetails.getManufacturerCode(), "Criba");
        Assertions.assertEquals(asinDetails.getProductSize(), ProductSize.HEAVY_BULKY_PARCEL.getProductSizeDescriptiveName());
        Assertions.assertEquals(asinDetails.getSpecialDeliveryRequirements(), "Testing");
        final AsinPhysicalAttributes asinPhysicalAttributes = asinDetails.getPhysicalAttribute();
        Assertions.assertEquals(asinPhysicalAttributes.getItemPackageWeight().getValue(), 0.0881849048);
        final AsinItemPackageDimension asinItemPackageDimension = asinPhysicalAttributes.getItemPackageDimension();
        Assertions.assertEquals(asinItemPackageDimension.getItemPackageLength().getValue(), 5.9055118050);
        Assertions.assertEquals(asinItemPackageDimension.getItemPackageWidth().getValue(), 2.2834645646);
        Assertions.assertEquals(asinItemPackageDimension.getItemPackageHeight().getValue(), 1.5748031480);
    }

    @Test
    void testBuildOutput_OnFailure_Given_New_GL_ThrowsMalformedDataException() throws IOException {
        when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(
                buildIonPayload(new String(Files.readAllBytes(Paths.get(DatapathConstants.RESPONSE_FILE_WITH_NEW_GL_PATH))))));
        Assertions.assertThrows(MalformedDataException.class,
                () -> subject.buildOutput(catalogItemRequest, httpEntity));
    }

    @Test
    void testBuildOutput_OnFailure_Given_New_LengthUnit_ThrowsMalformedDataException() throws IOException {
        when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(
                buildIonPayload(new String(Files.readAllBytes(Paths.get(DatapathConstants.RESPONSE_FILE_WITH_NEW_LENGTH_UNIT_PATH))))));

        Assertions.assertThrows(MalformedDataException.class,
                () -> subject.buildOutput(catalogItemRequest, httpEntity));
    }

    @Test
    void testBuildOutput_OnFailure_Given_New_WeightUnit_ThrowsMalformedDataException() throws IOException {
        when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(
                buildIonPayload(new String(Files.readAllBytes(Paths.get(DatapathConstants.RESPONSE_FILE_WITH_NEW_WEIGHT_UNIT_PATH))))));

        Assertions.assertThrows(MalformedDataException.class,
                () -> subject.buildOutput(catalogItemRequest, httpEntity));
    }

    @Test
    void testBuildOutput_OnSuccess_WithoutDimension_ReturnsAsinDetails() throws IOException {
        when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(
                buildIonPayload(new String(Files.readAllBytes(Paths.get(DatapathConstants.RESPONSE_FILE_WITHOUT_DIMENSION_PATH))))));

        AsinAttributes asinDetails = subject.buildOutput(catalogItemRequest, httpEntity);
        final AsinPhysicalAttributes asinPhysicalAttributes = asinDetails.getPhysicalAttribute();
        Assertions.assertNull(asinPhysicalAttributes.getItemPackageDimension().getItemPackageHeight());
        Assertions.assertNull(asinPhysicalAttributes.getItemPackageDimension().getItemPackageLength());
        Assertions.assertNull(asinPhysicalAttributes.getItemPackageDimension().getItemPackageWidth());
        Assertions.assertNull(asinPhysicalAttributes.getItemPackageWeight());
    }

    @Test
    public void testBuildOutput_Given_NullResponse_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class,
                ()-> subject.buildOutput(catalogItemRequest, null));
    }

    @Test
    public void testBuildOutput_Given_NoProduct_ThrowsMalformedDataException() throws IOException {
        when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(buildIonPayload("{}")));
        Assertions.assertThrows(MalformedDataException.class,
                () -> subject.buildOutput(catalogItemRequest, httpEntity));
    }

    @Test
    public void testBuildOutput_Given_NullIonDataGram_ThrowsMalformedDataException() throws IOException {
        when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(buildIonPayload("")));
        Assertions.assertThrows(MalformedDataException.class,
                () -> subject.buildOutput(catalogItemRequest, httpEntity));
    }

    @Test
    public void testBuildOutput_OnInvalidStruct_ThrowsMalformedDataException() throws IOException {
        when(httpEntity.getContent()).thenThrow(IOException.class);
        Assertions.assertThrows(MalformedDataException.class,
                () -> subject.buildOutput(catalogItemRequest, httpEntity));
    }

    @Test
    public void testBuildOutput_Given_EmptyAttributeValue_ReturnsEmpty() throws IOException {
        when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(
                buildIonPayload("{product: {binding:[],product_category:[]," +
                        "product_subcategory:[]}}")));

        final AsinAttributes result = subject.buildOutput(catalogItemRequest, httpEntity);
        //we are returning AsinAttributes object with default attribute value
        Assertions.assertEquals(result.getBinding(), null);
        Assertions.assertEquals(result.getProductCategory(), null);
        Assertions.assertEquals(result.getProductSubcategory(), null);
    }

    @Test
    public void testBuildOutput_Given_NoStructNoValue_ReturnEmpty() throws IOException {
        when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(
                buildIonPayload("{product: {binding:[{}],product_category:[{}]," +
                        "product_subcategory:[{}]}}")));

        final AsinAttributes result = subject.buildOutput(catalogItemRequest, httpEntity);
        //we are returning AsinAttributes object with default attribute value
        Assertions.assertEquals(result.getBinding(), null);
        Assertions.assertEquals(result.getProductCategory(), null);
        Assertions.assertEquals(result.getProductSubcategory(), null);
    }

    private byte[] buildIonPayload(final String text) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (final IonWriter writer = IonTextWriterBuilder.standard().build(out)) {
            final IonReader reader = IonReaderBuilder.standard().build(text);
            writer.writeValues(reader);
        }
        return out.toByteArray();
    }
}
