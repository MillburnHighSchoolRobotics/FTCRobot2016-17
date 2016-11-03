package com.I2CUtils;

import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cDevice;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

/**
 * Created by ethachu19 on 11/1/2016.
 */

public abstract class I2CSensor implements I2cController.I2cPortReadyCallback {
    //The maximum number of registers that a ReadRequest or WriteRequest can have
    private static final int MAX_LENGTH = 26;

    //A request for a read or write
    public abstract class SensorRequest {
        //The first register being controlled
        private final int register;
        //The number of consecutive registers being controlled
        private final int length;
        //For example, controlling registers 5 to 15 would require register = 5, length = 11

        //The value of System.nanoTime() when the action was performed
        protected long actionTimestamp;

        public SensorRequest(int register, int length) {
            if (length > MAX_LENGTH) throw new IllegalArgumentException("Length must be no more than " + Integer.toString(MAX_LENGTH));
            this.register = register;
            this.length = length;
            this.resetActionTimer();
        }

        protected int getRegister() {
            return this.register;
        }
        protected int getLength() {
            return this.length;
        }
        //Send the request to the I2C controller, return whether or not it was successful
        public abstract void sendRequest();
        //Add the request to the end of the queue if it isn't yet there
        protected void addToQueue() {
            if (!I2CSensor.this.requests.contains(this)) I2CSensor.this.requests.add(this);
        }
        //Reset the amount time since the last action
        protected void resetActionTimer() {
            this.actionTimestamp = System.nanoTime();
        }
        //Returns the number of nanoseconds since the last action
        public long nanosSinceAction() {
            return System.nanoTime() - this.actionTimestamp;
        }
    }
    //A request for a read
    public class SensorReadRequest extends SensorRequest {
        //The last data read (null if no data has been read yet)
        private byte[] readData;

        public SensorReadRequest(int register, int length) {
            super(register, length);
            this.readData = null;
        }

        //Begin constantly polling these registers
        public void startReading() {
            this.addToQueue();
        }
        //Stop constantly polling these registers
        public void stopReading() {
            I2CSensor.this.requests.remove(this);
        }
        public void sendRequest() {
            I2CSensor.this.device.enableI2cReadMode(I2CSensor.this.getWrappedAddress(), this.getRegister(), this.getLength());
            I2CSensor.this.lastRead = this;
        }
        //Record new data that has been received
        public void setReadData() {
            this.resetActionTimer();
            this.readData = I2CSensor.this.device.getCopyOfReadBuffer();
        }
        //Whether we have gotten read data at least once
        public boolean hasReadData() {
            return this.readData != null;
        }
        //Get the data that has been read
        public byte[] getReadData() {
            return this.readData;
        }
    }
    //A request for a write
    public class SensorWriteRequest extends SensorRequest {
        //The data to be written
        private byte[] writeData;
        //Whether or not the most recent data have been sent
        private boolean sent;

        public SensorWriteRequest(int register, int length) {
            super(register, length);
            this.writeData = null;
            this.sent = true;
        }

        //Set the data to be written (returns whether or not it was different (and set)
        public boolean setWriteData(byte[] newData) {
            if (this.getLength() != newData.length) throw new IllegalArgumentException("Expected length " + Integer.toString(this.getLength()) + ", got " + Integer.toString(newData.length));
            if (Arrays.equals(this.writeData, newData)) return false;
            else {
                this.writeData = newData;
                this.sent = false;
                this.addToQueue();
                return true;
            }
        }
        public void sendRequest() {
            this.resetActionTimer();
            I2CSensor.this.device.enableI2cWriteMode(I2CSensor.this.getWrappedAddress(), this.getRegister(), this.writeData.length);
            I2CSensor.this.device.copyBufferIntoWriteBuffer(this.writeData);
            this.sent = true;
            I2CSensor.this.lastRead = null; //there is no read request being responded to
        }
        //Whether the most recent changed value has been sent
        public boolean wasSent() {
            return this.sent;
        }
    }
    //Automatically calculates the length necessary to read all the registers between the two specified ones (inclusive)
    protected SensorReadRequest makeReadRequest(int minRegister, int maxRegister) {
        return new SensorReadRequest(minRegister, maxRegister - minRegister + 1);
    }

    //The device being controlled (wrapped)
    private I2cDevice device;
    //The queue of requests to be processed
    //Read requests cycle to the back of the queue when processed, while write requests are taken out
    private Queue<SensorRequest> requests;
    //The read request processed in the last ready cycle
    private SensorReadRequest lastRead;
    //The I2C address (7-bit version)
    protected abstract int getAddress(); //essentially static, but must be able to be overridden

    public I2CSensor(I2cDevice device) {
        this.device = device;
        this.requests = new ArrayDeque<>();
        this.lastRead = null;
        this.device.registerForI2cPortReadyCallback(this);
    }

    //The I2C address as an I2cAddr
    private I2cAddr getWrappedAddress() {
        return I2cAddr.create7bit(this.getAddress());
    }
    public synchronized void portIsReady(int port) {
        if (this.lastRead != null) this.lastRead.setReadData(); //record the read data
        if (!this.requests.isEmpty()) { //if there are commands to process
            SensorRequest nextRequest = this.requests.poll(); //get the request from the top of the queue
            nextRequest.sendRequest(); //run the code necessary to process the request
            this.device.setI2cPortActionFlag(); //we always want to do the action
            this.device.writeI2cCacheToController(); //set new desired register
            if (nextRequest instanceof SensorReadRequest) {
                this.requests.add(nextRequest); //reads need to be issued repeatedly
                this.device.readI2cCacheFromController(); //don't waste time performing a read if it isn't necessary
            }
        }
        this.readyCallback();
    }
    //Can be overridden in subclasses to provide a callback function when an I2C request is completed
    protected void readyCallback() {}
}
