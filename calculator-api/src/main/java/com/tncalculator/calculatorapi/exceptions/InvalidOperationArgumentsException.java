package com.tncalculator.calculatorapi.exceptions;

public class InvalidOperationArgumentsException extends Exception {

    private final transient Object[] args;

    public InvalidOperationArgumentsException(String message, Object[]args) {
        super(message);
        this.args = args;
    }

    public Object[] getArgs() {
        return this.args;
    }
}
