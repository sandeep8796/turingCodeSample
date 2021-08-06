package com.turing.codingsample.somesample.helper;

import amazon.platform.types.Length;
import amazon.platform.types.LengthUnit;
import amazon.platform.types.Weight;
import amazon.platform.types.WeightUnit;
import lombok.NonNull;

/**
 * Helper class to manage dimensions and weight attributes of asin
 */
public class DimensionHelper {
    /**
     * This methods takes value and unit and return an object of Length
     * @param value a double value
     * @param unit valid unit for the above value
     * @return Object of Length
     */
    public static Length getLength(final double value,
                                   @NonNull final String unit) {
        final Length length = new Length(value, unit);
        return length;
    }

    /**
     * This methods takes value and unit and return an object of Weight
     * @param value a double value
     * @param unit valid unit for the above value
     * @return Object of Weight
     */
    public static Weight getWeight(final double value,
                                   @NonNull final String unit) {
        final Weight weight = new Weight(value, unit);
        return weight;
    }

    /**
     * This method is responsible of Converting given object to HundrethOfInches unit object
     * @param originalLength original length object on which conversion will take place
     * @return new converted object with new value and unit
     */
    public static Length convertToHundrethOfInches(@NonNull final Length originalLength) {
        return originalLength.convert(LengthUnit.HUNDREDTHS_INCHES);
    }

    /**
     * This method is responsible of Converting given object to HundrethOfPounds unit object
     * @param originalWeight original weight object on which conversion will take place
     * @return new converted object with new value and unit
     */
    public static Weight convertToHundrethOfPounds(@NonNull final Weight originalWeight) {
        return originalWeight.convert(WeightUnit.HUNDRETH_POUNDS);
    }
}
