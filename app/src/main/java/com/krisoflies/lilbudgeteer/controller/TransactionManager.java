package com.krisoflies.lilbudgeteer.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;

import com.krisoflies.lilbudgeteer.model.Transaction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

/*Created by Paolo on 3/12/2015.*/
public class TransactionManager {

    public static boolean saveTransaction(String path, Transaction tr, Activity act) throws IOException {//Esto es lectura
        int[] currentLine = obtainCurrentLine(path, act);//currentLine es la data de cfg.add y contiene todos los indicadores monetarios necesarios
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(path + "/tr.add", "rw");//RAF es nuestro archivo randomico de transacciones.
            raf.seek(raf.length());
            tr.write(raf);
            currentLine[0]++;//el numero de transacciones ha aumentado

            updateThemesAcumulator(tr, path, act);
            updateBasicAcumulator(currentLine, tr, path, act);//aqui se hace el recalculo de las ganancias, perdidas, numero de transacciones y saldo final
        } catch (Exception e) {
            ApplicationUseManager.sendAlert(e.getMessage().substring(0, 30), "Error", act);
        }

        if (raf == null) return false;
        else {
            raf.close();
            return true;
        }
    }

    private static void updateThemesAcumulator(Transaction tr, String path, Activity act) {
        String[] header = new String[11];//hardcodeado con 11 temas de gasto.
        int[] values = new int[11];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path + "/gc.add"));
            String[] strData = reader.readLine().split(",");
            for (int i = 1, j = 0; i < strData.length; i += 2, j++) {
                values[j] = Integer.parseInt(strData[i]);
                header[j] = strData[i - 1];
                if (strData[i - 1].equals(tr.getCategory())) {//aumentamos en la cantidad X, para esta categoria
                    values[j] += tr.getAmount() * (tr.getType() == 'i' ? 1 : -1);
                }
            }
            reader.close();//Fin del recambio, ahora guardamos todos los cambios realizados
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/gc.add"));
            for (int i = 0; i < header.length - 1; i++)
                writer.write(header[i] + "," + String.valueOf(values[i]) + ",");

            writer.write(header[values.length - 1] + "," + String.valueOf(values[values.length - 1]));
            writer.close();
        } catch (java.io.IOException e) {
            ApplicationUseManager.sendAlert(e.getMessage().substring(0, 30), "An error has occurred. Please try later.", act);
        }
    }

    private static void updateBasicAcumulator(int[] currentLine, Transaction tr, String path, Activity act) {
        try {
            if (tr.getType() == 'e') {//hubo una salida de dinero
                currentLine[2] -= tr.getAmount();
                currentLine[4] -= tr.getAmount();
            } else {//hubo una entrada de dinero
                currentLine[3] += tr.getAmount();
                currentLine[4] += tr.getAmount();
            }
            updateCurrentLine(path, currentLine, act);
        } catch (Exception e) {
            ApplicationUseManager.sendAlert(e.getMessage().substring(0, 30), "Error", act);
        }
    }

    public static int[] obtainCurrentLine(String path, Activity act) {
        int[] nums = new int[7];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path + "/cfg.add"));
            String[] strData = reader.readLine().split(",");
            for (int i = 0; i < strData.length; i++) {
                nums[i] = Integer.parseInt(strData[i]);
            }
            reader.close();
        } catch (java.io.IOException e) {
            ApplicationUseManager.sendAlert(e.getMessage().substring(0, 30), "Error", act);
        }
        return nums;
    }

    public static void updateCurrentLine(String path, int[] allData, Activity act) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/cfg.add"));
            String update = "";
            for (int i = 0; i < allData.length - 1; i++) update += "" + allData[i] + ",";

            update += allData[allData.length - 1];
            writer.write(update);
            writer.close();
        } catch (java.io.IOException e) {
            ApplicationUseManager.sendAlert(e.getMessage(), "Error", act);
        }
    }

    public static List<String> obtainCategories(String path) {
        return ApplicationUseManager.readFile(path);
    }

    public static boolean dataCorrectness(String strDate, String strQuantity, Activity activity) {
        if (strQuantity == null || strQuantity.equals("")) {
            ApplicationUseManager.sendAlert("An amount of money must be especified", "Input error", activity);
            return false;
        }

        if (strQuantity.equals(".")) {
            ApplicationUseManager.sendAlert("An amount of money must be especified.Not just .", "Input error", activity);
            return false;
        }

        if (strDate == null || strDate.equals("")) {
            ApplicationUseManager.sendAlert("The date of the transaction must be identified", "Input error", activity);
            return false;
        }

        String[] diaVal = strDate.split("/");
        if (diaVal.length < 3) {
            ApplicationUseManager.sendAlert("The date of the transaction must have the following format: dd/MM/yy", "Input error", activity);
            return false;
        }

        return true;
    }

    public static boolean correctPassword(String path, Activity act) {
        int num;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path + "/k.add"));
            String[] strData = reader.readLine().split(",");
            num = Integer.parseInt(strData[0]);
            reader.close();
            if (num > 0) {//significa que el usuario si puso contrasenha y hay que comprobar
                String strResponse = sendPassRequest(act);
                if (strData[1].equals(strResponse)) {
                    return true;
                } else ApplicationUseManager.sendAlert("Wrong Password", "Error", act);
            } else return true;//no puso contrasenha dejarlo pasar.
        } catch (java.io.IOException e) {
            ApplicationUseManager.sendAlert(e.getMessage(), "Error", act);
        }
        return false;
    }

    private static String sendPassRequest(Activity act) {
        final String[] writtenPass = new String[1];
        writtenPass[0] = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle("Input Password");// Set up the input
        final EditText input = new EditText(act);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(input);// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                writtenPass[0] = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

        return writtenPass[0];
    }

    public static String[] obtainLast20(String path, Activity act) throws IOException {
        RandomAccessFile raf = null;
        Transaction tr = new Transaction();
        String[] transactions = new String[20];
        long countdown;
        try {
            raf = new RandomAccessFile(path + "/tr.add", "rw");
            tr.read(raf);
            long trueSize = raf.getFilePointer();
            long numRecords = raf.length() / trueSize;
            countdown = numRecords;
            if (numRecords < 20) {
                raf.seek(0);
                transactions = new String[(int) numRecords];
                if (numRecords == 0) transactions = new String[1];//sobreentiende vacio
            } else raf.seek(numRecords * tr.size());

            while (raf.getFilePointer() < raf.length()) {
                tr.read(raf);
                int n = (int) numRecords - (int) countdown + 1;
                transactions[(int) numRecords - (int) countdown] = "" + n + "| " + tr.getDate() + ", " + tr.getCategory() + ", " + tr.getAmount();
                countdown--;
            }
        } catch (Exception e) {
            ApplicationUseManager.sendAlert(e.getMessage(), "An error has occurred. Please try later.", act);
        }
        if (raf == null) return new String[1];
        else {
            raf.close();
            return transactions;
        }
    }

    public static void eraseTransaction(String path, int position, int totalTransactions, Activity act) {
        RandomAccessFile raf = null;
        Transaction tr = new Transaction(), nulTr = new Transaction();
        nulTr.setCategory("NO");
        try {
            raf = new RandomAccessFile(path + "/tr.add", "rw");// 23 || (20 2 = 19 borra) = 22 ->  20 - 2 + (23-20)+1
            tr.read(raf);
            long trueSize = raf.getFilePointer();
            long numRecords = raf.length() / trueSize;
            //posicion de borrado. Otro ejemplo: 17medida || 17 - 12borrar + 17 - 17 =5  (17 1) (16 2) (15 3) (14 4) (15 5)
            long popPos = ((totalTransactions - position + ((int) numRecords - totalTransactions) + 1) - 1) * trueSize;
            long pushPos = popPos + trueSize;
            if (popPos == 0) {
                raf.setLength(0);
            } else {
                while (pushPos < raf.length()) {
                    raf.seek(pushPos);

                    if (nulTr.getCategory().equals("NO")) nulTr.read(raf);
                    else tr.read(raf);

                    raf.seek(popPos);
                    tr.write(raf);//con esto abremos sobreescrito la antigua posicion
                    pushPos += trueSize;
                    popPos += trueSize;
                }
                raf.seek(0);
                raf.setLength(popPos);//la ultima posicion que debe ser trimeada, por ende el archivo es solo hasta popPos
            }
            //Aqui actualizamos los datos globales.
            int[] currentLine = obtainCurrentLine(path, act);
            currentLine[0]--;//hay una transaccion menos
            if (nulTr.getAmount() < 0) {//hubo una salida de dinero
                currentLine[2] += nulTr.getAmount();
                currentLine[4] += nulTr.getAmount();
            } else {//hubo una entrada de dinero
                currentLine[3] -= nulTr.getAmount();
                currentLine[4] -= nulTr.getAmount();
            }

            nulTr.setAmount(nulTr.getAmount() * -1);
            updateCurrentLine(path, currentLine, act);
            updateThemesAcumulator(nulTr, path, act);//Mando la transacion contraria a los acumuladores
        } catch (Exception e) {
            ApplicationUseManager.sendAlert(e.getMessage(), "An error has occurred. Please try later.", act);
        }

        if (!(raf == null)) {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}