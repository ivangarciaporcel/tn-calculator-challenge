package com.tncalculator.calculatorapi.services;

import com.tncalculator.calculatorapi.domain.model.OperationResponse;
import com.tncalculator.calculatorapi.domain.model.Record;
import com.tncalculator.calculatorapi.domain.model.User;
import com.tncalculator.calculatorapi.domain.model.UserStatus;
import com.tncalculator.calculatorapi.exceptions.NotFoundException;
import com.tncalculator.calculatorapi.repository.RecordRepository;
import com.tncalculator.calculatorapi.repository.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.tncalculator.calculatorapi.constants.MessageConstants.*;
import static com.tncalculator.calculatorapi.domain.model.Record.*;
import static com.tncalculator.calculatorapi.security.SecurityUtils.getAuthUserDetails;
import static com.tncalculator.calculatorapi.utils.EnumUtils.valueOf;

@Service
public class RecordService {

    private final RecordRepository recordRepository;
    private final UserRepository userRepository;

    @Autowired
    public RecordService(RecordRepository recordRepository, UserRepository userRepository) {
        this.recordRepository = recordRepository;
        this.userRepository = userRepository;
    }

    public Record findById(UUID id) throws NotFoundException {
        User user = getAuthenticatedUser();
        return recordRepository.findByUserAndId(user.getId(), id).orElseThrow(
                () -> new NotFoundException(ID_NOT_FOUND, new Object[]{Record.class.getSimpleName(), id.toString()}));
    }

    public void delete(UUID id) throws NotFoundException {
        User user = getAuthenticatedUser();
        Record record = recordRepository.findByUserAndId(user.getId(), id).orElseThrow(
                () -> new NotFoundException(ID_NOT_FOUND, new Object[]{Record.class.getSimpleName(), id.toString()}));
        record.markAsDeleted();
        recordRepository.save(record);
    }

    @SneakyThrows
    public Page<Record> listByCurrentUser(Pageable pageable, Map<String, String> filters) {
        User user = getAuthenticatedUser();
        UUID userId = user.getId();
        if (filters.isEmpty()) {
            return recordRepository.listByUser(userId, pageable);
        }
        Set<String> fieldsToFilter = filters.keySet();
        checkArgument(FILTER_FIELDS.containsAll(fieldsToFilter), INVALID_RECORD_FILTERS);

        if (fieldsToFilter.containsAll(FILTER_FIELDS)) {
            return recordRepository.listByOperationTypeAndResponseAndCurrentUser(filters.get(FIELD_OPERATION_TYPE),
                    valueOf(OperationResponse.class, filters.get(FIELD_OPERATION_RESPONSE)), userId, pageable);
        } else if (fieldsToFilter.contains(FIELD_OPERATION_TYPE)) {
            return recordRepository.listByOperationTypeAndCurrentUser(filters.get(FIELD_OPERATION_TYPE), userId, pageable);
        } else { // it only contains FIELD_OPERATION_RESPONSE
            return recordRepository.listByOperationResponseAndCurrentUser(valueOf(OperationResponse.class, filters.get(FIELD_OPERATION_RESPONSE)), userId, pageable);
        }
    }

    private User getAuthenticatedUser() throws NotFoundException {
        UserDetails userDetails = getAuthUserDetails();
        String userName = userDetails.getUsername();
        return userRepository.findByUsernameAndUserStatus(userName, UserStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(CURRENT_USER_NOT_FOUND, new Object[]{User.class.getSimpleName(), userName}));
    }

}
