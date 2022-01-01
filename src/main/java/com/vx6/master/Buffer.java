package com.vx6.master;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

import java.util.LinkedList;
import java.util.Queue;

public class Buffer {
    private int lt = 0, ht = 0, size = 0;
    private Queue<Message> buf = new LinkedList<>();
    private final String address;
    private final AddressBook addressBook;
    private EventBus eb;
    private boolean pushedBack = false;

    public Buffer(EventBus eb, String address, int size) throws Exception {
        if (size < 3) {
            throw new Exception("Buffer size must be greater than " + size + " (min = 3)");
        }
        this.eb = eb;
        addressBook = null;
        this.address = address;
        this.size = size;
        thresholds();
    }

    public Buffer(EventBus eb, AddressBook addressBook, int size) throws Exception {
        if (size < 3) {
            throw new Exception("Buffer size must be greater than " + size + " (min = 3)");
        }
        this.eb = eb;
        this.addressBook = addressBook;
        this.address = null;
        this.size = size;
        thresholds();
    }

    private void thresholds() {
        lt = Math.round(size / 3);
        ht = Math.round(size * 2 / 3);
    }

    public Message<?> getMessage() {
        if (buf.size() == lt && pushedBack) {
            addressBook.getPushBackAddresses().forEach(adrs -> eb.publish(adrs, "Resume", new DeliveryOptions().addHeader("cmd", "resume")));
            eb.publish(addressBook.getTrigger(), "Buffer is hungry!", new DeliveryOptions().addHeader("cmd", "buffer_drained"));
            pushedBack = false;
        }
        return buf.poll();
    }

    public boolean putMessage(Message<?> msg) {
        if (buf.size() == ht && !pushedBack) {
            addressBook.getPushBackAddresses().forEach(adrs -> eb.publish(adrs, "Pause", new DeliveryOptions().addHeader("cmd", "pause")));
            eb.publish(addressBook.getTrigger(), "Buffer is filling!", new DeliveryOptions().addHeader("cmd", "buffer_filled"));
            pushedBack = true;
        }
        return buf.add(msg);
    }

    public int getBuffSize() {
        return buf.size();
    }

    public void setSize(int size) {
        this.size = size;
        thresholds();
    }

    public int getLt() {
        return lt;
    }

    public int getHt() {
        return ht;
    }

    public int getSize() {
        return size;
    }

    public boolean isPushedBack() {
        return pushedBack;
    }
}
