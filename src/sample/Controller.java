package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import json.JSONObject;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {

    @FXML
    private TextField name;

    @FXML
    private TextField mail;

    @FXML
    private VBox vbox;

    @FXML
    private Label num;

    @FXML
    private Label g;

    @FXML
    private Label sum;

    @FXML
    private TextField failPath;

    @FXML
    private Button re;

    @FXML
    void chooseFail(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(vbox.getScene().getWindow());
        if (file != null) {
            failPath.setText(file.getPath());
        }
    }

    @FXML
    void enter(ActionEvent event) {
        try {
            FileInputStream fis = new FileInputStream(failPath.getText());
            jxl.Workbook rwb = Workbook.getWorkbook(fis);
            Sheet[] sheet = rwb.getSheets();
            StringBuffer sb = new StringBuffer();
            double sum = 0;
            double fen = 0;
            int num = 0;
            for (int i = 0; i < sheet.length; i++) {
                Sheet rs = rwb.getSheet(i);
                for (int j = 0; j < rs.getRows(); j++) {
                    Cell[] cells = rs.getRow(j);
                    if(cells.length > 3 && cells[0].getContents().equals("")){
                        sb.append(cells[1].getContents()+"\t"+cells[2].getContents()+"\t"+cells[3].getContents()+"%");
                        num++;
                        fen += Double.parseDouble(cells[3].getContents());
                        sum +=  Double.parseDouble(cells[3].getContents())*Double.parseDouble(cells[2].getContents());
                    }
                }
            }
            fis.close();
            this.num.setText("纳入计算科目数量:"+num);
            this.sum.setText("您的学分绩点之和:"+new DecimalFormat("0.00").format(sum));
            g.setText("您的平均学分绩点:"+new DecimalFormat("0.00").format(sum/fen));

            JSONObject json = new JSONObject();
            json.put("name",name.getText());
            json.put("email",mail.getText());
            json.put("num",num);
            json.put("sum",new DecimalFormat("0.00").format(sum));
            json.put("G",new DecimalFormat("0.00").format(sum/fen));
            json.put("g",sb.toString());


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress("192.144.253.104", 1313));

                        PrintWriter pw = new PrintWriter(socket.getOutputStream());
                        pw.println("ml "+json.toString());
                        pw.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        failPath.setText(System.getProperty("user.dir")+"\\G.xls");
        re.setDisable(true);
        mail.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                canEnter(name.getText(),t1);
            }
        });
        name.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                canEnter(t1,mail.getText());
            }
        });

    }

    private void canEnter(String name,String mail){
        Pattern regex = Pattern.compile("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
        Matcher matcher = regex.matcher(mail);
        if (matcher.matches() && !name.equals("") && !failPath.equals(""))
            re.setDisable(false);
    }


    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("选择文件");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("标准Excel表格(Powered by HanShuo)", "*.xls")
        );
    }
}
