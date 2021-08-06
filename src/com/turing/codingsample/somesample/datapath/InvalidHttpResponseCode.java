package com.turing.codingsample.somesample.datapath;

public class InvalidHttpResponseCode extends RuntimeException {
    public InvalidHttpResponseCode(final String message) {
        super(message);
    }
}