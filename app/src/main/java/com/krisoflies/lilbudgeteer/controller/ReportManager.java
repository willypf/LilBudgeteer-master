package com.krisoflies.lilbudgeteer.controller;

import android.app.Activity;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.krisoflies.lilbudgeteer.ReportActivity;
import com.krisoflies.lilbudgeteer.model.Transaction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;

/*Created by Paolo on 4/3/2015. */
public class ReportManager {

    public static String initialDate;

    public static void createTempReport(String savePath, Activity act) {
        File Excel_Rep = new File(savePath + "/LilBudgeteer.xls"); //Esto es momentaneo solo en memoria para llevarlo al DB
        WritableWorkbook workbook;//Creando el workbook
        try {
            workbook = basicTransactionWorkbook(savePath, Excel_Rep, act);//A partir de aqui son las opciones del usuario, habilitar indicadores acumulados
            indicatorExpansionWorkbook(workbook, savePath, act);//La expansison de indicadores
            workbook.write();//La parte de dropbox
            workbook.close();//cierro el workbook, grabando los datos en memoria
        } catch (Exception e) {
            ApplicationUseManager.sendAlert(e.getMessage(), "An error has occurred. Please try later.", act);
        }
    }

    public static boolean saveReport(DbxAccountManager mDbxAcctMgr, String savePath, ReportActivity act) {
        //The following is the Dropbox part of the action
        File Excel_Rep = new File(savePath + "/LilBudgeteer.xls");
        DbxFileSystem dbxFs;
        DbxFile ExcDropFile = null;
        try {
            dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd.hhmmss");
            DbxPath dbxPath = new DbxPath(DbxPath.ROOT, "Budget Report -" + sdf.format(date) + ".xls");
            ExcDropFile = dbxFs.create(dbxPath);
            ExcDropFile.writeFromExistingFile(Excel_Rep, false);
        } catch (Exception unauthorized) {
            ApplicationUseManager.sendAlert(unauthorized.getMessage(), "An error has occurred. Please try later.", act);
        }
        if (ExcDropFile != null) ExcDropFile.close();
        else return false;
        return true;
    }

    private static WritableWorkbook basicTransactionWorkbook(String savePath, File excel_Rep, Activity activity)
            throws IOException, WriteException {
        WritableWorkbook workbook = null;
        try {
            workbook = Workbook.createWorkbook(excel_Rep);
            WritableSheet sheet = workbook.createSheet("Transactions", 0);
            //Estos son items propios del excel
            List<Label> l = new ArrayList<>();
            List<Number> n = new ArrayList<>();
            //Estos son las transacciones
            Transaction tr = new Transaction();

            RandomAccessFile raf = new RandomAccessFile(savePath + "/tr.add", "rw");
            tr.read(raf);
            long size = raf.getFilePointer();
            raf.seek(0);
            //cabeceras de la principal
            // Create cell font and format
            WritableFont cellFont = new WritableFont(WritableFont.COURIER, 16);
            cellFont.setBoldStyle(WritableFont.BOLD);
            WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
            sheet.addCell(new Label(1, 1, "Nro.", cellFormat));
            sheet.addCell(new Label(2, 1, "Categoria", cellFormat));
            sheet.addCell(new Label(3, 1, "Cantidad", cellFormat));
            sheet.addCell(new Label(4, 1, "Observaciones", cellFormat));

            for (int i = 0, j = 0; i < raf.length(); i += size, j++) {
                tr.read(raf);
                if (i == 0) initialDate = tr.getDate();
                n.add(new Number(1, j + 2, j + 1));
                l.add(new Label(2, j + 2, tr.getCategory().trim()));
                n.add(new Number(3, j + 2, tr.getAmount()));
                l.add(new Label(4, j + 2, tr.getObservations().trim()));
            }

            for (int i = 0; i < n.size(); i++) sheet.addCell(n.get(i));
            for (int i = 0; i < l.size(); i++) sheet.addCell(l.get(i));
        } catch (Exception e) {
            ApplicationUseManager.sendAlert(e.getMessage(), "An error has occurred. Please try later.", activity);
        }
        return workbook;
    }

    private static void indicatorExpansionWorkbook(WritableWorkbook workbook, String path, Activity act) {
        //Este codigo se acerca a su version funcional
        try {
            WritableSheet sheet = workbook.createSheet("Indicators", 1);

            Calendar currentDate = Calendar.getInstance(); //Get the current date
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy"); //format it as per your requirement
            String dateNow = formatter.format(currentDate.getTime());

            //cabeceras fila 1
            List<Label> l = new ArrayList<>();
            l.add(new Label(2, 2, "Number of transactions"));//cabeceras del reporte sheet.addCell(l);
            l.add(new Label(2, 3, "Total costs"));
            l.add(new Label(2, 4, "Total income"));
            l.add(new Label(2, 6, "Initial date"));
            //caberas fila 2
            l.add(new Label(6, 2, "Minimun box"));
            l.add(new Label(6, 3, "Money left"));
            l.add(new Label(6, 6, "End date"));

            //Cabecera de la parte acumulativa
            l.add(new Label(3, 8, "Category"));
            l.add(new Label(3, 9, "Salary"));
            l.add(new Label(3, 10, "Sell"));
            l.add(new Label(3, 11, "Love"));
            l.add(new Label(3, 12, "Mobility"));//la fecha inicial
            l.add(new Label(3, 13, "Food"));//la fecha final
            l.add(new Label(3, 14, "Clothes"));
            l.add(new Label(3, 15, "Party"));
            l.add(new Label(3, 16, "Debt"));
            l.add(new Label(3, 17, "Equipment"));
            l.add(new Label(3, 18, "Medicine"));
            l.add(new Label(3, 19, "Other"));
            l.add(new Label(3, 20, "Adjustment"));
            l.add(new Label(2, 22, "Conclusion"));
            l.add(new Label(4, 8, "Amount"));
            l.add(new Label(5, 8, "Percentage"));

            int[] currentLine = TransactionManager.obtainCurrentLine(path, act);
            //Valores de las variables
            //Nro Transacciones, costos totales, ingresos totales, fecha Inicial
            l.add(new Label(4, 2, "" + currentLine[0]));
            l.add(new Label(4, 3, "" + currentLine[2]));
            l.add(new Label(4, 4, "" + currentLine[3]));
            l.add(new Label(4, 6, initialDate));
            //Caja minima, zon de peligro,fecha fin
            l.add(new Label(8, 2, "" + currentLine[6]));
            l.add(new Label(8, 3, "" + currentLine[4]));
            l.add(new Label(8, 6, dateNow));

            if (currentLine[2] > currentLine[3])
                l.add(new Label(4, 22, "Too much expending, sends you on bankrupcy sooner or later."));
            else l.add(new Label(4, 22, "You're save."));


            double totalPerc = 0.0;
            double[] percentages = new double[11];
            int[] values = new int[11];
            DecimalFormat df = new DecimalFormat("#.##");
            //ACUMULADORES DE LAS CATEGORIAS. SACO EL PROMEDIO DE ELLAS PARA SACAR LA MAYOR  Y EL ACUMUL PORCENTUAL

            BufferedReader reader = new BufferedReader(new FileReader(path + "/gc.add"));//Obtengo los valores
            String[] strData = reader.readLine().split(",");  //Anotados en el reader.
            for (int i = 1, j = 0; i < strData.length; i += 2, j++)
                values[j] = Integer.parseInt(strData[i]);

            for (int j = 0; j < 11; j++) {
                if (values[j] < 0) percentages[j] = values[j] / currentLine[2]; //sale de perdida
                else percentages[j] = values[j] / currentLine[3]; //sale de ganancia
                l.add(new Label(4, 9 + j, "" + values[j]));
                l.add(new Label(5, 9 + j, df.format(percentages[j]).toString()));//ponderado
                totalPerc += percentages[j];
            }
            //Habra un ajuste que es necesario controlar de aquellos patrones que sigan tanto salidas como entradas //descuentos
            l.add(new Label(5, 20, df.format(1-totalPerc).toString()));//ponderado
            l.add(new Label(7, 20, "Reason: in some categories, you have income and expense at the same time."));//ponderado

            //finalmente meto toda la data en la sheet
            for (int i = 0; i < l.size(); i++) sheet.addCell(l.get(i));
        } catch (Exception e) {
            ApplicationUseManager.sendAlert(e.getMessage(), "An error has occurred. Please try later.", act);
        }
    }

    public static void deleteTempReport(String savePath, Activity act) {
        try {
            File Excel_Rep = new File(savePath + "/LilBudgeteer.xls");
            if (!Excel_Rep.delete()) throw new Exception();
        } catch (Exception e) {
            ApplicationUseManager.sendAlert("No se pudo borrar.", "Error", act);
        }
    }
}