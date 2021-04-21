package com.PFE.queue_microservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Reason {

    LATE("late", "The client was late."),
    TURN("turn", "It's the client's turn."),
    CANCELLED("cancelled", "The client cancelled their turn."),
    ALMOST_TURN("almostTurn", "The client's turn is almost up."),
    DONE("done", "The client got served."),
    ADDED("added", "The client got added to the queue.");


    private final String value;
    private final String reasonPhrase;

}
