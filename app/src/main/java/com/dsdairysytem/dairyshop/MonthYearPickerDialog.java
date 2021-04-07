package com.dsdairysytem.dairyshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.fonts.Font;
import android.graphics.fonts.FontFamily;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
//import com.itextpdf.layout.Document;
//import com.itextpdf.text.BaseColor;
//import com.itextpdf.text.Chunk;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Element;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.PageSize;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MonthYearPickerDialog extends DialogFragment {

    private String[] month_name = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
    private static final int MAX_YEAR = 2099;
    private int selected_month, selected_year;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private File pdfFile;
    private int sumAmt = 0;
    Context context;
    Activity activity;
    private static final String TAG = "ClientDetails";
    //private String milkmanName, milkmanMobile;
    Boolean share;
    String exportType;
    FirebaseFirestore db;
    String orders = "", quantity = "";
    Table table;
    Document document;
    OutputStream output;
    int count;
    String shopMobile = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    //String clientMobile = "+919879874562";
    //String milkmanMobile;
    HSSFSheet firstSheet;
    HSSFWorkbook workbook;

    public void setList(Context context,Activity activity, Boolean share,String exportType) {

        this.context = context;
        this.activity = activity;
        this.share = share;
        this.exportType = exportType;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Calendar cal = Calendar.getInstance();

        View dialog = inflater.inflate(R.layout.month_year_picker_dialog, null);
        final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);
        db = FirebaseFirestore.getInstance();

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);

        monthPicker.setValue(cal.get(Calendar.MONTH)+1);

        final int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(year);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(year);

        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        selected_month = monthPicker.getValue();
                        selected_year = yearPicker.getValue();

                        if (share) {
                            if (exportType.equals("pdf")) {
                                try {
                                    createPdfNew();
                                } catch (FileNotFoundException e){
                                    e.printStackTrace();
//                                } catch (DocumentException e) {
//                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    createExcel();
                                } catch (FileNotFoundException e){
                                    e.printStackTrace();
//                                } catch (DocumentException e) {
//                                    e.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                createPdfNew();
                            } catch (FileNotFoundException e){
                                e.printStackTrace();
//                            } catch (DocumentException e) {
//                                e.printStackTrace();
                            }
                        }

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MonthYearPickerDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

//    private void createPdf() throws FileNotFoundException, DocumentException {
//        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/DSDairy");
//        try {
//            if(!docsFolder.isDirectory()) {
//                docsFolder.mkdirs();
//                Log.i(TAG, "Created a new directory for PDF");
//            }
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//        String pdfname = "orderDetails"+month_name[selected_month-1]+".pdf";
//        pdfFile = new File(docsFolder.getAbsolutePath(), pdfname);
//        output = new FileOutputStream(pdfFile);
//        document = new Document(PageSize.A4);
//
//        table = new PdfPTable(new float[]{7, 7, 7, 7, 7, 7});
//        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
//        //table.getDefaultCell().setFixedHeight(50);
//        table.setTotalWidth(PageSize.A4.getWidth());
//        table.setWidthPercentage(100);
//        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
//        //table.addCell("Order");
//        table.addCell("Seller Name");
//        table.addCell("Date");
//        table.addCell("Quantity");
//        table.addCell("Fat Units");
//        table.addCell("Rate per Unit");
//        table.addCell("Amount(Rs)");
//        table.setHeaderRows(1);
//        PdfPCell[] cells = table.getRow(0).getCells();
//        for (int j = 0; j < cells.length; j++) {
//            cells[j].setBackgroundColor(BaseColor.GRAY);
//        }
////        orders = "";
////        quantity = "";
//        sumAmt = 0;
//
//
//        db.collection("DairyShop").document(shopMobile).collection("Orders")
//                .orderBy("timestamp").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful())
//                {
//                    Log.d("SUCCESSFUL","TRUE");
//                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//
//
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTime(documentSnapshot.getTimestamp("timestamp").toDate());
//                        int month = calendar.get(Calendar.MONTH) + 1;
//                        int year = calendar.get(Calendar.YEAR);
//                        Log.d("MONTH",month+"");
//                        Log.d("YEAR",year+"");
//
//                        if (month == selected_month && year == selected_year) {
//                            Log.d("IF CONDITION","TRUE");
//
//
//                            table.addCell(documentSnapshot.getString("SellerName"));
//
//                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                        String date = sdf.format(documentSnapshot.getTimestamp("timestamp").toDate());
//                        table.addCell(date);
//                        table.addCell(documentSnapshot.getDouble("Quantity")+"");
//                        table.addCell(documentSnapshot.getDouble("FatUnits")+"");
//                        table.addCell(documentSnapshot.getDouble("RateperFat")+"");
//                        table.addCell(documentSnapshot.getDouble("Amount") + "");
//                        sumAmt += documentSnapshot.getDouble("Amount");
//                    }
//                    }
//
//                    table.addCell("");
//                    table.addCell("");
//                    table.addCell("");
//                    table.addCell("");
//                    table.addCell("");
//                    table.addCell("Total Amount - Rs "+sumAmt);
//
//                    try {
//                        PdfWriter.getInstance(document, output);
//                    } catch (DocumentException e) {
//                        e.printStackTrace();
//                    }
//                    document.open();
//                    final Font f = new Font(Font.FontFamily.TIMES_ROMAN, 32.0f, Font.NORMAL, BaseColor.BLACK);
//                    final Font g = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.NORMAL, BaseColor.BLUE);
//                    Font h = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.NORMAL, BaseColor.BLACK);
//
//                    Date c = Calendar.getInstance().getTime();
//                    final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
//                    String formattedDate = df.format(c);
//
//                    final Paragraph paragraph = new Paragraph("Date: "+formattedDate);
//                    paragraph.setAlignment(Element.ALIGN_RIGHT);
//
//                    final Paragraph paragraph1 = new Paragraph("Order Details",h);
//                    paragraph1.setAlignment(Element.ALIGN_CENTER);
//
////                    try {
//
//
//
//                        db.collection("DairyShop").document(shopMobile).get()
//                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                        try {
//                                            document.add(new Paragraph("DS-Dairy-System", f));
//                                            document.add(paragraph);
//                                            document.add(Chunk.NEWLINE);
//
//                                            document.add(new Paragraph("Shopkeeper Name - "+documentSnapshot.getString("Name"),g));
//
//                                            document.add(new Paragraph("Shopkeeper Number - "+shopMobile,g));
//                                            document.add(Chunk.NEWLINE);
//                                            document.add(paragraph1);
//                                            document.add(Chunk.NEWLINE);
//                                            Log.d("TABLE SIZE",table.size()+"");
//                                            document.add(table);
//                                            document.close();
//                                        } catch (DocumentException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                });
////                        document.add(new Paragraph("Milkman Name - "+milkmanName,g));
//
////                    } catch (DocumentException e) {
////                        e.printStackTrace();
////                    }
//
//
//
//                    if (share)
//                        sharePdf();
//                    else
//                        previewPdf();
//                }
//            }
//        });
//
//
//            }

    private void previewPdf() {
        PackageManager packageManager = context.getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List lists = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (lists.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider",pdfFile);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } else {
            Toast.makeText(activity, R.string.download_pdf_viewer, Toast.LENGTH_SHORT).show();
        }
    }

    private void sharePdf() {
        File outputFile = new File(Environment.getExternalStorageDirectory() + "/DSDairy/orderDetails"+month_name[selected_month-1]+".pdf");
        Uri uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider",outputFile);

        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_SUBJECT,
                "Sharing File...");
        share.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
        context.startActivity(Intent.createChooser(share, "Share File"));
    }

    private void createExcel() throws FileNotFoundException{
        Log.d("Excel","TRUE");
        workbook = new HSSFWorkbook();
        firstSheet = workbook.createSheet("Order Details");
        HSSFRow row1 = firstSheet.createRow(0);
        HSSFCell cell1 = row1.createCell(4);
        cell1.setCellValue(new HSSFRichTextString("DS-DAIRY-SYSTEM"));

        Date c = Calendar.getInstance().getTime();
        final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        HSSFRow row2 = firstSheet.createRow(2);
        HSSFCell cell2 = row2.createCell(0);
        cell2.setCellValue(new HSSFRichTextString("Date: "+formattedDate));

        db.collection("DairyShop").document(shopMobile).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                HSSFRow row3 = firstSheet.createRow(4);

                HSSFCell cell3 = row3.createCell(0);
                cell3.setCellValue(new HSSFRichTextString("Shopkeeper Name: "+documentSnapshot.getString("Name")));
            }
        });



        HSSFRow row4 = firstSheet.createRow(5);

        HSSFCell cell4 = row4.createCell(0);
        cell4.setCellValue(new HSSFRichTextString("Shopkeeper No.: "+shopMobile));

        HSSFRow row8 = firstSheet.createRow(7);
        HSSFCell cell8 = row8.createCell(3);
        cell8.setCellValue(new HSSFRichTextString("Order Details - "+month_name[selected_month-1]));

        HSSFRow row5 = firstSheet.createRow(9);

        HSSFCell cell5 = row5.createCell(0);
        cell5.setCellValue(new HSSFRichTextString("Seller Name"));
        HSSFCell cell5i = row5.createCell(2);
        cell5i.setCellValue(new HSSFRichTextString("Date"));
        HSSFCell cell5o = row5.createCell(4);
        cell5o.setCellValue(new HSSFRichTextString("Quantity"));
        HSSFCell cell5f = row5.createCell(6);
        cell5f.setCellValue(new HSSFRichTextString("Fat Units"));
        HSSFCell cell5r = row5.createCell(8);
        cell5r.setCellValue(new HSSFRichTextString("Rate per Unit"));
        HSSFCell cell5p = row5.createCell(10);
        cell5p.setCellValue(new HSSFRichTextString("Amount(₹)"));

        orders = "";
        quantity = "";
        sumAmt = 0;
        count = 0;

        db.collection("DairyShop").document(shopMobile).collection("Orders")
                .orderBy("timestamp").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(documentSnapshot.getTimestamp("timestamp").toDate());
                        int month =  calendar.get(Calendar.MONTH) + 1;
                        int year = calendar.get(Calendar.YEAR);

                        if (month == selected_month && year == selected_year)
                        {
                            HSSFRow row6 = firstSheet.createRow(11+count);

                            HSSFCell cell6 = row6.createCell(0);
                            cell6.setCellValue(new HSSFRichTextString(documentSnapshot.getString("SellerName")));

                            HSSFCell cell6o = row6.createCell(2);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            String date = sdf.format(documentSnapshot.getTimestamp("timestamp").toDate());
                            cell6o.setCellValue(new HSSFRichTextString(date));

                            HSSFCell cell6i = row6.createCell(4);
                            cell6i.setCellValue(new HSSFRichTextString(documentSnapshot.getLong("Quantity")+""));


                            HSSFCell cell6f = row6.createCell(6);
                            cell6f.setCellValue(new HSSFRichTextString(documentSnapshot.getLong("FatUnits")+""));

                            HSSFCell cell6r = row6.createCell(8);
                            cell6r.setCellValue(new HSSFRichTextString(documentSnapshot.getLong("RateperFat")+""));

                            HSSFCell cell6p = row6.createCell(10);
                            cell6p.setCellValue(new HSSFRichTextString(String.valueOf(documentSnapshot.getLong("Amount"))));

                            sumAmt += documentSnapshot.getLong("Amount");
                            count++;
                        }
                    }

                    Log.d("COUNT VALUE",count+"");

                    HSSFRow row7 = firstSheet.createRow(11+count);
                    HSSFCell cell7 = row7.createCell(10);
                    cell7.setCellValue(new HSSFRichTextString("Total Amount - Rs "+sumAmt));

                    String fileName = "orderDetails"+month_name[selected_month-1]+".xls"; //Name of the file

                    String extStorageDirectory = Environment.getExternalStorageDirectory()
                            .toString();
                    File folder = new File(extStorageDirectory+"/DSDairy", "Excel");// Name of the folder you want to keep your file in the local storage.
                    folder.mkdir(); //creating the folder
                    File file = new File(folder, fileName);
                    try {
                        file.createNewFile(); // creating the file inside the folder
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    try {
                        FileOutputStream fileOut = new FileOutputStream(file); //Opening the file
                        workbook.write(fileOut); //Writing all your row column inside the file
                        fileOut.close(); //closing the file and done

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    shareExcel();

                }
            }
        });


    }

    private void shareExcel() {
        Log.d("SHARING ","RUNNING");
        File outputFile = new File(Environment.getExternalStorageDirectory() + "/DSDairy/Excel/orderDetails"+month_name[selected_month-1]+".xls");
        Uri uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider",outputFile);

        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/xls");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_SUBJECT,
                "Sharing File...");
        share.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
        context.startActivity(Intent.createChooser(share, "Share File"));
    }

    private void createPdfNew() throws FileNotFoundException {
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/DSDairy");
        try {
            if(!docsFolder.isDirectory()) {
                docsFolder.mkdirs();
                Log.i(TAG, "Created a new directory for PDF");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        String pdfname = "orderDetails"+month_name[selected_month-1]+".pdf";
        pdfFile = new File(docsFolder.getAbsolutePath(), pdfname);
        output = new FileOutputStream(pdfFile);
        //PdfWriter pdfWriter = new PdfWriter("cdv");

        PdfDocument pdf = new PdfDocument(new PdfWriter(pdfFile));
        document = new Document(pdf);

        table = new Table(new float[]{7, 7, 7, 7, 7, 7});
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        //table.getDefaultCell().setFixedHeight(50);
        table.setMaxWidth(PageSize.A4.getWidth());
        table.setMinWidth(PageSize.A4.getWidth());
        //table.setWidthPercentage(100);
        table.setVerticalAlignment(VerticalAlignment.MIDDLE);
        //table.addCell("Order");
        table.addCell("Seller Name");
        table.addCell("Date");
        table.addCell("Quantity");
        table.addCell("Fat Units");
        table.addCell("Rate per Unit");
        table.addCell("Amount(Rs)");
        //table.setHeaderRows(1);
//        PdfPCell[] cells = table.getRow(0).getCells();
//        for (int j = 0; j < cells.length; j++) {
//            cells[j].setBackgroundColor(BaseColor.GRAY);
//        }
//        orders = "";
//        quantity = "";
        sumAmt = 0;


        db.collection("DairyShop").document(shopMobile).collection("Orders")
                .orderBy("timestamp").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    Log.d("SUCCESSFUL","TRUE");
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {


                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(documentSnapshot.getTimestamp("timestamp").toDate());
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int year = calendar.get(Calendar.YEAR);
                        Log.d("MONTH",month+"");
                        Log.d("YEAR",year+"");

                        if (month == selected_month && year == selected_year) {
                            Log.d("IF CONDITION","TRUE");


                            table.addCell(documentSnapshot.getString("SellerName"));

                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            String date = sdf.format(documentSnapshot.getTimestamp("timestamp").toDate());
                            table.addCell(date);
                            table.addCell(documentSnapshot.getDouble("Quantity")+"");
                            table.addCell(documentSnapshot.getDouble("FatUnits")+"");
                            table.addCell(documentSnapshot.getDouble("RateperFat")+"");
                            table.addCell(documentSnapshot.getDouble("Amount") + "");
                            sumAmt += documentSnapshot.getDouble("Amount");
                        }
                    }

                    table.addCell("");
                    table.addCell("");
                    table.addCell("");
                    table.addCell("");
                    table.addCell("");
                    table.addCell("Total Amount - Rs "+sumAmt);

//                    try {
                       // PdfWriter.getInstance(document, output);
//                    } catch (DocumentException e) {
//                        e.printStackTrace();
//                    }
//                    document.open();
//                    final Font f = new Font(FontFamily.TIMES_ROMAN, 32.0f, Font.NORMAL, BaseColor.BLACK);
//                    final Font g = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.NORMAL, BaseColor.BLUE);
//                    Font h = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.NORMAL, BaseColor.BLACK);

                    try {
                        PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
                        document.setFont(font);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    Date c = Calendar.getInstance().getTime();
                    final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    String formattedDate = df.format(c);


                    final Paragraph paragraph = new Paragraph("Date: "+formattedDate);
                    paragraph.setTextAlignment(TextAlignment.RIGHT);

                    final Paragraph paragraph1 = new Paragraph("Order Details");
                    paragraph1.setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20);
//                    try {



                    db.collection("DairyShop").document(shopMobile).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                    try {
                                        document.add(new Paragraph("DS-Dairy-System").setFontSize(32));
                                        document.add(paragraph);
                                        //document.add(NEWLINE);

                                        document.add(new Paragraph("Shopkeeper Name - "+documentSnapshot.getString("Name")).setFontSize(20));

                                        document.add(new Paragraph("Shopkeeper Number - "+shopMobile).setFontSize(20));
                                        //document.add(Chunk.NEWLINE);
                                        document.add(paragraph1);
                                        //document.add(Chunk.NEWLINE);
                                        //Log.d("TABLE SIZE",table.size()+"");
                                        document.add(table);
                                        document.close();
//                                    } catch (DocumentException e) {
//                                        e.printStackTrace();
//                                    }
                                }
                            });
//                        document.add(new Paragraph("Milkman Name - "+milkmanName,g));

//                    } catch (DocumentException e) {
//                        e.printStackTrace();
//                    }



                    if (share)
                        sharePdf();
                    else
                        previewPdf();
                }
            }
        });


    }

}
