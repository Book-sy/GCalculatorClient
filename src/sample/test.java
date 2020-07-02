package sample;

/**
 * test��
 * Git to�� http://hs.mccspace.com:3000/Qing_ning/untitled/
 *
 * @TIME 2020/7/1 11:54
 * @AUTHOR ��˶~
 */

import java.io.File;
import java.io.FileInputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;


public class test {
    /**
     * ��ȡxls�ļ�����
     *
     * @param file ��Ҫ��ȡ���ļ�����
     * @return �����ļ�����
     */
    public static String xls2String(File file) {
        String result = "";
        try {
            FileInputStream fis = new FileInputStream(file);
            StringBuilder sb = new StringBuilder();
            jxl.Workbook rwb = Workbook.getWorkbook(fis);
            Sheet[] sheet = rwb.getSheets();
            for (int i = 0; i < sheet.length; i++) {
                Sheet rs = rwb.getSheet(i);
                for (int j = 0; j < rs.getRows(); j++) {
                    Cell[] cells = rs.getRow(j);
                    for (int k = 0; k < cells.length; k++) {
                        sb.append(cells[k].getContents());
                        sb.append('\t');
                    }
                    sb.append('\n');
                }
            }
            fis.close();
            result += sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        File file = new File("C:\\Users\\Administrator\\Desktop\\G.xls");
        System.out.println(xls2String(file));
    }
}