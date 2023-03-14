package com.tncalculator.calculatorapi.utils;

import com.tncalculator.calculatorapi.exceptions.IllegalArgumentServiceException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.tncalculator.calculatorapi.constants.MessageConstants.CANNOT_FIND_VALUE_ENUM;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnumUtils {

    public static <E extends Enum<E>> E valueOf(Class<E> e, String value) throws IllegalArgumentServiceException {
        E result;
        try {
            result = Enum.valueOf(e, value.toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentServiceException(CANNOT_FIND_VALUE_ENUM, new Object[]{value, e.getSimpleName()});
        }
        return result;
    }
}
