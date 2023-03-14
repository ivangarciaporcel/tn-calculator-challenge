package com.tncalculator.calculatorapi.utils;

import com.tncalculator.calculatorapi.exceptions.IllegalArgumentServiceException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.tncalculator.calculatorapi.constants.AuditConstants.CREATED_AT;
import static com.tncalculator.calculatorapi.constants.AuditConstants.UPDATED_AT;
import static com.tncalculator.calculatorapi.constants.MessageConstants.INVALID_FILTER_FORMAT;
import static com.tncalculator.calculatorapi.constants.MessageConstants.SORT_PROPERTY_NOT_VALID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageUtils {

    public static final List<String> AUDIT_SORT_FIELDS = List.of(CREATED_AT, UPDATED_AT);

    public static Map<String, String> getFilters(String[] filterArray) {
        Map<String, String> filters = new HashMap<>();
        if (filterArray.length > 0) {
            if (filterArray[0].contains(",")) {
                for (String filter : filterArray) {
                    String[] splitFilter = filter.split(",");
                    checkArgument(splitFilter.length == 2, INVALID_FILTER_FORMAT);
                    filters.put(splitFilter[0], splitFilter[1]);
                }
            } else {
                checkArgument(filterArray.length == 2, INVALID_FILTER_FORMAT);
                filters.put(filterArray[0], filterArray[1]);
            }
        }
        return filters;
    }

    public static List<Sort.Order> getSortOrders(String[] sort, List<String> allowedFields) throws IllegalArgumentServiceException {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")) {
            for (String sortOrder : sort) {
                String[] splitSort = sortOrder.split(",");
                if (!allowedFields.contains(splitSort[0])) {
                    throw new IllegalArgumentServiceException(SORT_PROPERTY_NOT_VALID, new Object[]{splitSort[0]});
                }
                String property = parseProperty(splitSort[0]);
                orders.add(new Sort.Order(getSortDirection(splitSort[1]), property));
            }
        } else {
            if (!allowedFields.contains(sort[0])) {
                throw new IllegalArgumentServiceException(SORT_PROPERTY_NOT_VALID, new Object[]{sort[0]});
            }
            String property = parseProperty(sort[0]);
            orders.add(new Sort.Order(getSortDirection(sort[1]), property));
        }
        return orders;
    }

    private static String parseProperty(String property) {
        if (AUDIT_SORT_FIELDS.contains(property)) {
            property = "audit." + property;
        }
        return property;
    }

    private static Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }
}
