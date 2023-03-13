package com.tncalculator.calculatorapi.unit.services;

import com.tncalculator.calculatorapi.domain.model.*;
import com.tncalculator.calculatorapi.domain.model.Record;
import com.tncalculator.calculatorapi.exceptions.NotFoundException;
import com.tncalculator.calculatorapi.repository.RecordRepository;
import com.tncalculator.calculatorapi.repository.UserRepository;
import com.tncalculator.calculatorapi.services.RecordService;
import com.tncalculator.calculatorapi.unit.SecurityMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.tncalculator.calculatorapi.domain.model.Role.USER_ADMIN;
import static com.tncalculator.calculatorapi.utils.AssertionUtils.assertRecord;
import static com.tncalculator.calculatorapi.utils.EntityBuilders.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class RecordServiceTest extends SecurityMock {

    private RecordRepository recordRepository;
    private UserRepository userRepository;

    private RecordService recordService;

    @BeforeEach
    public void setUp() {
        recordRepository = mock(RecordRepository.class);
        userRepository = mock(UserRepository.class);
        recordService = new RecordService(recordRepository, userRepository);
    }

    @AfterEach
    public void tearDown() {
        reset(recordRepository, userRepository);
    }

    @Test
    public void testGetRecordByIdAndLoggedUserNotFound() {
        UUID recordId = UUID.randomUUID();

        when(userRepository.findByUsernameAndUserStatus(userDetails.getUsername(), UserStatus.ACTIVE)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> recordService.findById(recordId));
    }

    @Test
    public void testGetRecordByIdNotFound() {
        UUID recordId = UUID.randomUUID();
        User loggedUser = user(userDetails.getUsername(), Set.of(USER_ADMIN));

        when(userRepository.findByUsernameAndUserStatus(userDetails.getUsername(), UserStatus.ACTIVE)).thenReturn(Optional.of(loggedUser));
        when(recordRepository.findByUserAndId(loggedUser.getId(), recordId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> recordService.delete(recordId));
    }

    @Test
    public void testGetRecordById() throws NotFoundException {
        UUID recordId = UUID.randomUUID();
        User loggedUser = user(userDetails.getUsername(), Set.of(USER_ADMIN));
        Record existentRecord = record(operation("OperationType", OperationStatus.APPROVED), loggedUser);

        when(userRepository.findByUsernameAndUserStatus(userDetails.getUsername(), UserStatus.ACTIVE)).thenReturn(Optional.of(loggedUser));
        when(recordRepository.findByUserAndId(loggedUser.getId(), recordId)).thenReturn(Optional.of(existentRecord));
        Record foundRecord = recordService.findById(recordId);
        assertRecord(existentRecord, foundRecord);
    }

    @Test
    public void testDeleteRecordAndLoggedUserNotFound() {
        UUID recordId = UUID.randomUUID();

        when(userRepository.findByUsernameAndUserStatus(userDetails.getUsername(), UserStatus.ACTIVE)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> recordService.delete(recordId));
    }

    @Test
    public void testDeleteRecordNotFound() {
        UUID recordId = UUID.randomUUID();
        User loggedUser = user(userDetails.getUsername(), Set.of(USER_ADMIN));

        when(userRepository.findByUsernameAndUserStatus(userDetails.getUsername(), UserStatus.ACTIVE)).thenReturn(Optional.of(loggedUser));
        when(recordRepository.findByUserAndId(loggedUser.getId(), recordId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> recordService.delete(recordId));
    }

    @Test
    public void testDeleteRecord() throws NotFoundException {
        UUID recordId = UUID.randomUUID();
        User loggedUser = user(userDetails.getUsername(), Set.of(USER_ADMIN));
        Record existentRecord = record(mock(Operation.class), loggedUser);

        when(userRepository.findByUsernameAndUserStatus(userDetails.getUsername(), UserStatus.ACTIVE)).thenReturn(Optional.of(loggedUser));
        when(recordRepository.findByUserAndId(loggedUser.getId(), recordId)).thenReturn(Optional.of(existentRecord));
        recordService.delete(recordId);

        ArgumentCaptor<Record> recordCaptor = ArgumentCaptor.forClass(Record.class);
        verify(recordRepository, times(1)).save(recordCaptor.capture());
        Record deletedRecord = recordCaptor.getValue();
        assertTrue(deletedRecord.getAudit().isDeleted());
    }

    @Test
    public void testListByCurrentUserAndLoggedUserNotFound() {
        Pageable pageable = Pageable.unpaged();

        when(userRepository.findByUsernameAndUserStatus(userDetails.getUsername(), UserStatus.ACTIVE)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> recordService.listByCurrentUser(pageable));
    }

    @Test
    public void testListByCurrentUser() throws NotFoundException {
        Pageable pageable = Pageable.unpaged();
        User loggedUser = user(userDetails.getUsername(), Set.of(USER_ADMIN));
        Page<Record> result = Page.empty();

        when(userRepository.findByUsernameAndUserStatus(userDetails.getUsername(), UserStatus.ACTIVE)).thenReturn(Optional.of(loggedUser));
        when(recordRepository.listByUser(loggedUser.getId(), pageable)).thenReturn(result);
        Page<Record> pagedList = recordService.listByCurrentUser(pageable);
        assertTrue(pagedList.isEmpty());
        verify(recordRepository, times(1)).listByUser(loggedUser.getId(), pageable);
    }
}
