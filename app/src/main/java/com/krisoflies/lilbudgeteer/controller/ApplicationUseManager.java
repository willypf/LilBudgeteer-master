package com.krisoflies.lilbudgeteer.controller;

import android.app.Activity;
import android.app.AlertDialog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jxl.write.*;


/* Created by Paolo on 3/12/2015. */
public class ApplicationUseManager {
    //Estos metodos solo se utilizan 1 vez para crear los archivo de 1)configuracion 2)transacciones 3)temas
    public static void initializeConfig(String filename, Activity ola) throws IOException, WriteException {
        createFile(filename + "/cfg.add", 1, ola);  //numero de transaccion,permit obs,perdidas,ganancias,saldo total,avisodegasto,MontoInicial
        createFile(filename + "/gc.add", 2, ola);//gasto por categoria matriz(categoria, dinero, %)
        createFile(filename + "/k.add", 3, ola); //opcion de clave,clave misma
        createFile(filename + "/gd.add", 4, ola);//direccion de gdrive,clave gdrive
    }

    public static void updateFile(String filename, String update, Activity act) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(update);
            writer.close();
        } catch (Exception e) {
            sendAlert(e.getMessage(), "Error", act);
        }
    }

    private static void createFile(String filename, int option, Activity ola) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            switch (option) {
                case 1:
                    writer.write("0,0,0,0,0,0,0");
                    break;
                case 2:
                    writer.write("Salary,0,");
                    writer.write("Sell,0,");
                    writer.write("Love,0,");
                    writer.write("Mobility,0,");
                    writer.write("Food,0,");
                    writer.write("Clothes,0,");
                    writer.write("Party,0,");
                    writer.write("Debt,0,");
                    writer.write("Technology,0,");
                    writer.write("Medicine,0,");
                    writer.write("Other,0");
                    break;
                case 3:
                    writer.write("0,0");
                    break;
                case 4:
                    writer.write("0,0");
                    break;
            }
            writer.close();
        } catch (Exception e) {
            sendAlert(e.getMessage(), "Error", ola);
        }
    }

    public static List<String> readFile(String filename) {
        List<String> records = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                Collections.addAll(records, values);
            }

            reader.close();
            return records;
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
            return null;
        }
    }

    public static void sendAlert(String message, String title, Activity act) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(act);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.show();
    }
}