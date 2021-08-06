package com.turing.codingsample.somesample.datapath;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DatapathConstants {
    public static final String HEADER_CLIENT_ID = "x-client-id";
    public static final String HEADER_SERVICE = "Service";
    public static final String HEADER_OPERATION = "Operation";

    public static final String AAA_SERVICE_NAME = "DatapathApiGateway";
    public static final String OPERATION_QUERY = "Query";
    public static final String VALID_RESPONSE_FILE_PATH = "tst/com/amazon/contracogseventdataaggregators/external/"
            + "DummyService/resource/DummyServiceResponse";
    public static final String RESPONSE_FILE_WITHOUT_DIMENSION_PATH = "tst/com/amazon/contracogseventdataaggregators"
            + "/external/DummyService/resource/DummyServiceResponseWithNoDimension";
    public static final String RESPONSE_FILE_WITH_NEW_GL_PATH = "tst/com/amazon/contracogseventdataaggregators/external"
            + "/DummyService/resource/DummyServiceResponseWithNewGlProductGroupType";
    public static final String RESPONSE_FILE_WITH_NEW_LENGTH_UNIT_PATH = "tst/com/amazon/contracogseventdataaggregators/external"
            + "/DummyService/resource/DummyServiceResponseWithNewLengthUnit";
    public static final String RESPONSE_FILE_WITH_NEW_WEIGHT_UNIT_PATH = "tst/com/amazon/contracogseventdataaggregators/external"
            + "/DummyService/resource/DummyServiceResponseWithNewWeightUnit";
    //Ion attributes
    public static final String VALUE = "value";
    public static final String PRODUCT = "product";
    public static final String UNIT = "unit";
    public static final String NORMALIZED_VALUE = "normalized_value";
    public static final String WIDTH = "width";
    public static final String LENGTH = "length";
    public static final String HEIGHT = "height";
    public static final String GL_PRODUCT_GROUP = "gl_product_group_type";
    public static final String BINDING = "binding";
    public static final String PRODUCT_CATEGORY = "product_category";
    public static final String PRODUCT_SUBCATEGORY = "product_subcategory";
    public static final String BRAND_CODE = "brand_code";
    public static final String ITEM_CLASSIFICATION = "item_classification";
    public static final String SPECIAL_DELIVERY_REQUIREMENTS = "special_delivery_requirements";
    public static final String ITEM_PACKAGE_DIMENSIONS = "item_package_dimensions";
    public static final String ITEM_PACKAGE_WEIGHT = "item_package_weight";
    public static final String MANUFACTURER = "manufacturer";

    //ProductSize related details
    public static final List<String> CATEGORY_CODE_FOR_HBP = ImmutableList.copyOf(new ArrayList<>(
            Arrays.asList("50400100", "56400100")));
    public static final List<String> SPECIAL_DELIVERY_REQ_FOR_HBP = ImmutableList.copyOf(new ArrayList<>(
            Arrays.asList("white_glove_service", "heavy_bulky_override")));
    public static final double PACKAGE_WEIGHT_FOR_HBP = 50;
    public static final double PACKAGE_WEIGHT_FOR_HBF = 150;
    public static final double PACKAGE_LENGTH_FOR_HBP = 108;
    public static final double PACKAGE_GIRTH_FOR_HBP = 165;

    // Length Unit Mapping
    public static final ImmutableMap<String, String> LENGTH_SHORT_UNIT = ImmutableMap.<String, String>builder()
            .put("millimeters", "MM")
            .put("centimeters", "CM")
            .put("meters", "M")
            .put("kilometers", "KM")
            .put("feet", "FT")
            .put("inches", "IN")
            .put("miles", "MI")
            .put("decimeters", "DM")
            .put("yards", "YD")
            .put("angstrom", "AN")
            .put("hundredthsinches", "HUN_IN")
            .put("micrometers", "UM")
            .put("picometers", "PM")
            .put("nanometers", "NM")
            .put("mils", "ML")
            .build();

    //Weight Unit Mapping
    public static final ImmutableMap<String, String> WEIGHT_SHORT_UNIT = ImmutableMap.<String, String>builder()
            .put("milligrams", "mg")
            .put("grams", "g")
            .put("kilograms", "kg")
            .put("pounds", "lb")
            .put("hundrethspounds", "hth_lb")
            .put("ounces", "oz")
            .put("tons", "t")
            .put("stones", "st")
            .build();
}
