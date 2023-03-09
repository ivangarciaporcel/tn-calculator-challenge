package com.tncalculator.calculatorapi.exceptions;

public class ForbiddenServiceException extends Exception {

    private final transient Object[] args;

    public ForbiddenServiceException(String message, Object[]args) {
        super(message);
        this.args = args;
    }

    public Object[] getArgs() {
        return this.args;
    }
}
