package com.tncalculator.calculatorapi.utils;

import com.tncalculator.calculatorapi.domain.dto.UserDTO;
import com.tncalculator.calculatorapi.domain.model.Audit;
import com.tncalculator.calculatorapi.domain.model.Operation;
import com.tncalculator.calculatorapi.domain.model.Record;
import com.tncalculator.calculatorapi.domain.model.User;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    private AssertionUtils() {
    }

    public static void assertUser(User expected, User actual) {
        assertAll("All user fields should match",
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getUsername(), actual.getUsername()),
                () -> assertEquals(expected.getPassword(), actual.getPassword()),
                () -> assertEquals(expected.getUserStatus(), actual.getUserStatus()),
                () -> assertEquals(expected.getBalance(), actual.getBalance()),
                () -> assertAudit(expected.getAudit(), actual.getAudit())
        );
    }

    public static void assertOperation(Operation expected, Operation actual) {
        assertAll("All operation fields should match",
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getType(), actual.getType()),
                () -> assertEquals(expected.getCost(), actual.getCost()),
                () -> assertEquals(expected.getStatus(), actual.getStatus()),
                () -> assertAudit(expected.getAudit(), actual.getAudit())
        );
    }

    public static void assertRecord(Record expected, Record actual) {
        assertAll("All record fields should match",
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertOperation(expected.getOperation(), actual.getOperation()),
                () -> assertUser(expected.getUser(), actual.getUser()),
                () -> assertEquals(expected.getAmount(), actual.getAmount()),
                () -> assertEquals(expected.getUserBalance(), actual.getUserBalance()),
                () -> assertEquals(expected.getOperationResponse(), actual.getOperationResponse()),
                () -> assertAudit(expected.getAudit(), actual.getAudit())
        );
    }

    public static void assertAudit(Audit expected, Audit actual) {
        assertAll("All audit fields should match",
                () -> assertEquals(expected.getCreatedAt(), actual.getCreatedAt()),
                () -> assertEquals(expected.getCreatedBy(), actual.getCreatedBy()),
                () -> assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt()),
                () -> assertEquals(expected.getUpdatedBy(), actual.getUpdatedBy()),
                () -> assertEquals(expected.isDeleted(), actual.isDeleted())
        );
    }

    public static void assertUserDTO(UserDTO expected, UserDTO actual) {
        assertAll("All user DTO fields should match",
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getEmail(), actual.getEmail()),
                () -> assertEquals(expected.getPassword(), actual.getPassword()),
                () -> assertEquals(expected.getStatus(), actual.getStatus()),
                () -> assertEquals(expected.getBalance(), actual.getBalance()),
                () -> assertEquals(expected.getRoles(), actual.getRoles())
        );
    }
}
