package com.tncalculator.calculatorapi.exceptions;

public class NotFoundException extends Exception {

    private final transient Object[] args;

    public NotFoundException(String message, Object[]args) {
        super(message);
        this.args = args;
    }

    public Object[] getArgs() {
        return this.args;
    }
}

