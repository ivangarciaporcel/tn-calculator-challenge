package com.tncalculator.calculatorapi.exceptions;

public class InvalidRolesException extends Exception {

    private final transient Object[] args;

    public InvalidRolesException(String message, Object[]args) {
        super(message);
        this.args = args;
    }

    public Object[] getArgs() {
        return this.args;
    }
}


