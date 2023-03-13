package com.tncalculator.calculatorapi.unit.services;

import com.tncalculator.calculatorapi.domain.dto.OperationPartialDTO;
import com.tncalculator.calculatorapi.domain.dto.OperationResultDTO;
import com.tncalculator.calculatorapi.domain.mapper.OperationMapper;
import com.tncalculator.calculatorapi.domain.model.Record;
import com.tncalculator.calculatorapi.domain.model.*;
import com.tncalculator.calculatorapi.exceptions.NotFoundException;
import com.tncalculator.calculatorapi.operations.CalculatorOperation;
import com.tncalculator.calculatorapi.operations.OperationsFactory;
import com.tncalculator.calculatorapi.repository.OperationRepository;
import com.tncalculator.calculatorapi.repository.RecordRepository;
import com.tncalculator.calculatorapi.repository.UserRepository;
import com.tncalculator.calculatorapi.services.OperationService;
import com.tncalculator.calculatorapi.unit.SecurityMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.tncalculator.calculatorapi.constants.OperationConstants.*;
import static com.tncalculator.calculatorapi.domain.model.OperationResponse.APPROVED;
import static com.tncalculator.calculatorapi.domain.model.OperationResponse.DENIED;
import static com.tncalculator.calculatorapi.domain.model.Role.USER_ADMIN;
import static com.tncalculator.calculatorapi.utils.AssertionUtils.assertOperation;
import static com.tncalculator.calculatorapi.utils.AssertionUtils.assertRecord;
import static com.tncalculator.calculatorapi.utils.EntityBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class OperationServiceTest extends SecurityMock {

    private OperationRepository operationRepository;
    private RecordRepository recordRepository;
    private UserRepository userRepository;
    private OperationMapper operationMapper;
    private OperationService operationService;

    private OperationsFactory operationsFactory;

    @BeforeEach
    public void setUp() {
        operationRepository = mock(OperationRepository.class);
        recordRepository = mock(RecordRepository.class);
        userRepository = mock(UserRepository.class);
        operationMapper = mock(OperationMapper.class);
        operationsFactory = mock(OperationsFactory.class);
        operationService = new OperationService(operationRepository, recordRepository, userRepository,
                operationMapper, operationsFactory);
    }

    @AfterEach
    public void tearDown() {
        reset(operationRepository, recordRepository, operationRepository, operationMapper);
    }

    @Test
    public void testCreateOperationAndStatusInVerification() {
        Operation toCreate = operation("NEW", OperationStatus.APPROVED);
        assertThrows(IllegalArgumentException.class,
                () -> operationService.create(toCreate));
    }

    @Test
    public void testCreateOperationWithAlreadyCreatedType() {
        Operation toCreate = operation("NEW", OperationStatus.IN_VERIFICATION);
        when(operationRepository.existsByTypeNotDeleted(toCreate.getType())).thenReturn(true);
        assertThrows(IllegalArgumentException.class,
                () -> operationService.create(toCreate));
    }

    @Test
    public void testCreateOperation() {
        Operation toCreate = operation("NEW", OperationStatus.IN_VERIFICATION);
        when(operationRepository.existsByTypeNotDeleted(toCreate.getType())).thenReturn(false);
        when(operationRepository.save(toCreate)).thenReturn(toCreate);

        Operation createdOperation = operationService.create(toCreate);
        assertOperation(toCreate, createdOperation);
    }

    @Test
    public void testFindOperationByIdAndNotFound() {
        UUID operationId = UUID.randomUUID();
        when(operationRepository.findByIdNotDeleted(operationId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> operationService.findById(operationId));
    }

    @Test
    public void testFindOperationById() throws Exception {
        UUID operationId = UUID.randomUUID();
        Operation existentOperation = operation("ADDITION", OperationStatus.APPROVED);
        when(operationRepository.findByIdNotDeleted(operationId)).thenReturn(Optional.of(existentOperation));
        Operation foundOperation = operationService.findById(operationId);
        assertOperation(existentOperation, foundOperation);
    }

    @Test
    public void testUpdateOperationNotFound() {
        UUID operationId = UUID.randomUUID();
        Operation toUpdate = operation("ADDITION", OperationStatus.APPROVED);

        when(operationRepository.findByIdNotDeleted(operationId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> operationService.update(operationId, toUpdate));
    }

    @Test
    public void testUpdateOperationWithDifferentType() {
        UUID operationId = UUID.randomUUID();
        Operation toUpdate = operation("SUBTRACTION", OperationStatus.APPROVED);
        Operation existentOperation = operation("ADDITION", OperationStatus.APPROVED);

        when(operationRepository.findByIdNotDeleted(operationId)).thenReturn(Optional.of(existentOperation));
        assertThrows(IllegalArgumentException.class,
                () -> operationService.update(operationId, toUpdate));
    }

    @Test
    public void testUpdateOperation() throws Exception {
        UUID operationId = UUID.randomUUID();
        Operation toUpdate = operation("ADDITION", OperationStatus.DEPRECATED);
        Operation existentOperation = operation("ADDITION", OperationStatus.APPROVED);

        when(operationRepository.findByIdNotDeleted(operationId)).thenReturn(Optional.of(existentOperation));
        when(operationRepository.save(existentOperation)).thenReturn(toUpdate);
        Operation updatedOperation = operationService.update(operationId, toUpdate);
        assertOperation(toUpdate, updatedOperation);
    }

    @Test
    public void testPatchOperationNotFound() {
        UUID operationId = UUID.randomUUID();
        OperationPartialDTO partial = mock(OperationPartialDTO.class);

        when(operationRepository.findByIdNotDeleted(operationId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> operationService.patch(operationId, partial));
    }

    @Test
    public void testPatchOperation() throws Exception {
        UUID operationId = UUID.randomUUID();
        OperationPartialDTO partial = mock(OperationPartialDTO.class);
        Operation existentOperation = operation("ADDITION", OperationStatus.APPROVED);

        when(operationRepository.findByIdNotDeleted(operationId)).thenReturn(Optional.of(existentOperation));
        when(operationRepository.save(existentOperation)).thenReturn(existentOperation);
        Operation updatedOperation = operationService.patch(operationId, partial);
        assertOperation(existentOperation, updatedOperation);
    }

    @Test
    public void testDeleteOperationNotFound() {
        UUID operationId = UUID.randomUUID();

        when(operationRepository.findByIdNotDeleted(operationId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> operationService.delete(operationId));
    }

    @Test
    public void testDeleteOperation() throws Exception {
        UUID operationId = UUID.randomUUID();
        Operation existentOperation = operation("ADDITION", OperationStatus.APPROVED);

        when(operationRepository.findByIdNotDeleted(operationId)).thenReturn(Optional.of(existentOperation));
        operationService.delete(operationId);

        ArgumentCaptor<Operation> operationCaptor = ArgumentCaptor.forClass(Operation.class);
        verify(operationRepository, times(1)).save(operationCaptor.capture());
        Operation deletedOperation = operationCaptor.getValue();
        assertTrue(deletedOperation.getAudit().isDeleted());
    }

    @Test
    public void testListOperations() {
        Pageable pageable = mock(Pageable.class);
        Page<Operation> result = Page.empty();
        when(operationRepository.listNotDeleted(pageable)).thenReturn(result);

        Page<Operation> pagedList = operationService.list(pageable);
        assertTrue(pagedList.isEmpty());
        verify(operationRepository, times(1)).listNotDeleted(pageable);
    }

    @Test
    public void testCalculateOperationNotFound() {
        UUID operationId = UUID.randomUUID();

        when(operationRepository.findByIdNotDeleted(operationId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> operationService.calculate(operationId, Map.of()));
    }

    @Test
    public void testCalculateOperationDeprecated() {
        UUID operationId = UUID.randomUUID();
        Operation existentOperation = operation("ADDITION", OperationStatus.DEPRECATED);

        when(operationRepository.findByIdNotDeleted(operationId)).thenReturn(Optional.of(existentOperation));
        assertThrows(IllegalArgumentException.class,
                () -> operationService.calculate(operationId, Map.of()));
    }

    @Test
    public void testCalculateOperationInVerification() {
        UUID operationId = UUID.randomUUID();
        Operation existentOperation = operation("ADDITION", OperationStatus.IN_VERIFICATION);

        when(operationRepository.findByIdNotDeleted(operationId)).thenReturn(Optional.of(existentOperation));
        assertThrows(IllegalArgumentException.class,
                () -> operationService.calculate(operationId, Map.of()));
    }

    @Test
    public void testCalculateOperationAndLoggedUserNotFound() {
        UUID operationId = UUID.randomUUID();
        Operation existentOperation = operation("ADDITION", OperationStatus.APPROVED);

        when(operationRepository.findByIdNotDeleted(operationId)).thenReturn(Optional.of(existentOperation));
        when(userRepository.findByUsernameAndUserStatus(userDetails.getUsername(), UserStatus.ACTIVE)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> operationService.calculate(operationId, Map.of()));
    }

    @Test
    public void testCalculateOperationAndNotEnoughUserBalance() throws Exception {
        UUID operationId = UUID.randomUUID();
        Operation existentOperation = operation(ADDITION, OperationStatus.APPROVED);
        existentOperation.setCost(10.0);

        User loggedUser = user(userDetails.getUsername(), Set.of(USER_ADMIN));
        loggedUser.setBalance(5.0);

        when(operationRepository.findByIdNotDeleted(operationId)).thenReturn(Optional.of(existentOperation));
        when(userRepository.findByUsernameAndUserStatus(userDetails.getUsername(), UserStatus.ACTIVE)).thenReturn(Optional.of(loggedUser));
        OperationResultDTO operationResult = operationService.calculate(operationId, Map.of(
                FIRST_NUMBER, 1.0, SECOND_NUMBER, 2.0
        ));
        assertTrue(StringUtils.isBlank((String) operationResult.getResult()));
        assertEquals(DENIED, operationResult.getOperationResponse());

        verify(userRepository, never()).save(any());

        // verify Record information
        ArgumentCaptor<Record> recordCaptor = ArgumentCaptor.forClass(Record.class);
        verify(recordRepository, times(1)).save(recordCaptor.capture());
        Record savedRecord = recordCaptor.getValue();
        Record expectedRecord = record(existentOperation, loggedUser, existentOperation.getCost(), loggedUser.getBalance(), DENIED);
        assertNotNull(savedRecord);
        assertRecord(expectedRecord, savedRecord);
    }

    @Test
    public void testCalculateOperation() throws Exception {
        UUID operationId = UUID.randomUUID();
        Operation existentOperation = operation(ADDITION, OperationStatus.APPROVED);
        existentOperation.setCost(10.0);

        User loggedUser = user(userDetails.getUsername(), Set.of(USER_ADMIN));
        loggedUser.setBalance(50.0);

        double expectedBalance = loggedUser.getBalance() - existentOperation.getCost();
        when(operationRepository.findByIdNotDeleted(operationId)).thenReturn(Optional.of(existentOperation));
        when(userRepository.findByUsernameAndUserStatus(userDetails.getUsername(), UserStatus.ACTIVE)).thenReturn(Optional.of(loggedUser));
        CalculatorOperation calculatorOperation = mock(CalculatorOperation.class);
        when(calculatorOperation.calculate(any())).thenReturn(3.0);
        when(operationsFactory.getOperation(existentOperation.getType())).thenReturn(calculatorOperation);
        OperationResultDTO operationResult = operationService.calculate(operationId, Map.of(
                FIRST_NUMBER, 1.0, SECOND_NUMBER, 2.0
        ));
        assertEquals(3.0, operationResult.getResult());
        assertEquals(APPROVED, operationResult.getOperationResponse());

        // verify balance updated for user
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals(expectedBalance, capturedUser.getBalance());

        // verify Record information
        ArgumentCaptor<Record> recordCaptor = ArgumentCaptor.forClass(Record.class);
        verify(recordRepository, times(1)).save(recordCaptor.capture());
        Record savedRecord = recordCaptor.getValue();
        Record expectedRecord = record(existentOperation, loggedUser, existentOperation.getCost(), expectedBalance, APPROVED);
        assertNotNull(savedRecord);
        assertRecord(expectedRecord, savedRecord);
    }
}
