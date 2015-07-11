package com.krisoflies.lilbudgeteer.model;

import java.io.IOException;
import java.io.RandomAccessFile;

/*Created by Paolo on 3/10/2015. */
public class Transaction {
    private char type;
    private double amount;
    private String observations;
    private String date;
    private String category;

    //Type
    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    //Amount
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (type == 'e')
            this.amount = amount - 2 * amount;
        else
            this.amount = amount;
    }

    //Observations
    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    //Date
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    //Category
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void write(RandomAccessFile raf) throws IOException {//Orden: Date,Category,Obs, Amount
        StringBuffer sb;
        if (getDate() != null)
            sb = new StringBuffer(getDate());
        else sb = new StringBuffer();
        sb.setLength(12);
        raf.writeChars(sb.toString());

        if (getCategory() != null)
            sb = new StringBuffer(getCategory());
        else sb = new StringBuffer();
        sb.setLength(20);
        raf.writeChars(sb.toString());

        if (getObservations() != null)
            sb = new StringBuffer(getObservations());
        else sb = new StringBuffer();
        sb.setLength(60);
        raf.writeChars(sb.toString());

        raf.writeDouble(getAmount()*(getType()=='i'?1:-1));//se tiene que grabar la transaccion segun su tipo establecido.
    }

    public void read(RandomAccessFile raf) throws IOException {
        char[] temp = new char[12];
        for (int i = 0; i < temp.length; i++) temp[i] = raf.readChar();
        setDate(new String(temp).trim());

        temp = new char[20];
        for (int i = 0; i < temp.length; i++) temp[i] = raf.readChar();
        setCategory(new String(temp).trim());

        temp = new char[60];
        for (int i = 0; i < temp.length; i++) temp[i] = raf.readChar();
        setObservations(new String(temp).trim());

        setAmount(raf.readDouble());

        if(getAmount()>0)setType('i');
        else setType('e');
    }

    public int size() {//el tamanho del archivo
        return 12 + 20 + 60;
    }
}