package com.example.sports_booking.exception;

public class SlotOverlapException extends RuntimeException {
    public SlotOverlapException(String message) {
        super(message);
    }

    public SlotOverlapException(String message, Throwable cause) {
        super(message, cause);
    }
}
