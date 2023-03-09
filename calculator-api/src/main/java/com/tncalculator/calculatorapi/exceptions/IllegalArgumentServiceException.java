package com.tncalculator.calculatorapi.exceptions;

public class IllegalArgumentServiceException extends Exception {

    private final transient Object[] args;

    public IllegalArgumentServiceException(String message, Object[]args) {
        super(message);
        this.args = args;
    }

    public Object[] getArgs() {
        return this.args;
    }
}
