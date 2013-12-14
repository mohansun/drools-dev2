package org.mohans.dev.action;

import org.mohans.dev.message.Message;

public class Action {
    public void performAction(Message message) {
        message.printMessage();
    }
}