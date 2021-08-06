package com.turing.codingsample.somesample.helper;

//some of the imports are removed
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DimensionHelperTest {

    @Test
    void testGetLength() {
        final Length length = DimensionHelper.getLength(12, LengthUnit.INCHES);
        Assertions.assertEquals(12, length.getValue());
        Assertions.assertEquals("IN", length.getUnit());
    }

    @Test
    void testGetWeight() {
        final Weight weight = DimensionHelper.getWeight(12, WeightUnit.POUNDS);
        Assertions.assertEquals(12, weight.getValue());
        Assertions.assertEquals("lb", weight.getUnit());
    }

    @Test
    void testConvertToHundrethOfPounds() {
        final Weight weight = DimensionHelper.getWeight(12, WeightUnit.POUNDS);
        final Weight newWeight = DimensionHelper.convertToHundrethOfPounds(weight);
        Assertions.assertEquals(1200, newWeight.getValue());
        Assertions.assertEquals("hth_lb", newWeight.getUnit());
    }

    @Test
    void testConvertToHundrethOfInches() {
        final Length length = DimensionHelper.getLength(12, LengthUnit.INCHES);
        final Length newLength = DimensionHelper.convertToHundrethOfInches(length);
        Assertions.assertEquals(1200, newLength.getValue());
        Assertions.assertEquals("HUN_IN", newLength.getUnit());
    }

    @Test
    void testGetLenghtWithNullInput_ThrowIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> DimensionHelper.getLength(12, null));
    }

    @Test
    void testGetWeightWithNullInput_ThrowIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> DimensionHelper.getWeight(12, null));
    }

    @Test
    void testConvertToHundrethOfInchesWithNullObject_ThrowIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> DimensionHelper.convertToHundrethOfInches(null));
    }

    @Test
    void testConvertToHundrethOfPoundsWithNullObject_ThrowIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> DimensionHelper.convertToHundrethOfPounds(null));
    }
    
}
