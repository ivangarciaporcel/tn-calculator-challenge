package com.tncalculator.calculatorapi.services;

import com.tncalculator.calculatorapi.domain.model.Record;
import com.tncalculator.calculatorapi.domain.model.User;
import com.tncalculator.calculatorapi.domain.model.UserStatus;
import com.tncalculator.calculatorapi.exceptions.NotFoundException;
import com.tncalculator.calculatorapi.repository.RecordRepository;
import com.tncalculator.calculatorapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.tncalculator.calculatorapi.constants.MessageConstants.CURRENT_USER_NOT_FOUND;
import static com.tncalculator.calculatorapi.constants.MessageConstants.ID_NOT_FOUND;
import static com.tncalculator.calculatorapi.security.SecurityUtils.getAuthUserDetails;

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

    public Page<Record> listByCurrentUser(Pageable pageable) throws NotFoundException {
        User user = getAuthenticatedUser();
        return recordRepository.listByUser(user.getId(), pageable);
    }

    private User getAuthenticatedUser() throws NotFoundException {
        UserDetails userDetails = getAuthUserDetails();
        String userName = userDetails.getUsername();
        return userRepository.findByUsernameAndUserStatus(userName, UserStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException(CURRENT_USER_NOT_FOUND, new Object[]{User.class.getSimpleName(), userName}));
    }

}
