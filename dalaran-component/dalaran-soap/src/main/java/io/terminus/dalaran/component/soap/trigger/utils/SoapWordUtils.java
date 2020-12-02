package io.terminus.dalaran.component.soap.trigger.utils;

import io.terminus.dalaran.component.soap.trigger.model.SoapApiParameter;
import io.terminus.dalaran.component.soap.trigger.model.SoapWordApiInfo;
import lombok.val;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth.DXA;

public class SoapWordUtils {

    private static final String HEADING_STYLE_ID = "Heading";

    public static File buildWordFile(List<SoapWordApiInfo> apiInfoList) {
        XWPFDocument doc = new XWPFDocument();
        addHeadingStyle(doc);
        for (SoapWordApiInfo apiInfo : apiInfoList) {
            addParamTable(doc, apiInfo);
        }
        try {
            File outFile = File.createTempFile("dalaran", "word");
            doc.write(new FileOutputStream(outFile));
            return outFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void addParamTable(XWPFDocument doc, SoapWordApiInfo info) {
        XWPFParagraph headingParagraph = doc.createParagraph();
        headingParagraph.createRun().setText(System.lineSeparator() + info.getName() + System.lineSeparator());
        headingParagraph.setStyle(HEADING_STYLE_ID);

        int maxCol = info.getParamLevel() + 2;
        XWPFTable table = doc.createTable(0, maxCol);
        long paramNameWidth = 720L * 3 * info.getParamLevel();
        long tableWidth = paramNameWidth + 720L * 10;
        table.getCTTbl().getTblPr().getTblW().setType(DXA);
        table.getCTTbl().getTblPr().getTblW().setW(BigInteger.valueOf(tableWidth));
        long colWidth = paramNameWidth / info.getParamLevel();
        CTTblGrid grid = table.getCTTbl().getTblGrid();
        if (grid == null) {
            grid = table.getCTTbl().addNewTblGrid();
        }
        for (int i = 0; i < info.getParamLevel(); i++) {
            val addNewGridCol = grid.addNewGridCol();
            addNewGridCol.setW(BigInteger.valueOf(colWidth));
        }
        grid.addNewGridCol().setW(BigInteger.valueOf(720L * 3));
        grid.addNewGridCol().setW(BigInteger.valueOf(720L * 7));

        addKVRow(table, "接口名称", info.getName(), maxCol);
        addKVRow(table, "接口说明", info.getDescription(), maxCol);
        addKVRow(table, "请求路径", info.getPath(), maxCol);
        addKVRow(table, "请求方式", info.getMethod().toString(), maxCol);
        addTitleRow(table, "请求参数", maxCol);
        addParams(table, info.getInput().getSubParameter(), 0, info.getParamLevel());
        addTitleRow(table, "响应参数", maxCol);
        addParams(table, info.getOutput().getSubParameter(), 0, info.getParamLevel());
        table.removeRow(0);

        setTableBorder(table);
    }

    private static void setTableBorder(XWPFTable table) {
        CTTblPr tblPr = table.getCTTbl().getTblPr() != null ? table.getCTTbl().getTblPr() : table.getCTTbl().addNewTblPr();
        CTTblBorders borders = tblPr.isSetTblBorders() ? tblPr.getTblBorders() : tblPr.addNewTblBorders();
        CTBorder topBorder = borders.isSetTop() ? borders.getTop() : borders.addNewTop();
        CTBorder bottomBorder = borders.isSetBottom() ? borders.getBottom() : borders.addNewBottom();
        CTBorder leftBorder = borders.isSetLeft() ? borders.getLeft() : borders.addNewLeft();
        CTBorder rightBorder = borders.isSetRight() ? borders.getRight() : borders.addNewRight();
        setBorder(topBorder);
        setBorder(bottomBorder);
        setBorder(leftBorder);
        setBorder(rightBorder);
        table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "000000");
        table.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "000000");
    }

    private static void setBorder(CTBorder border) {
        border.setVal(STBorder.Enum.forInt(3));
        border.setSz(BigInteger.valueOf(4));
        border.setSpace(BigInteger.valueOf(0));
        border.setColor("000000");
    }

    private static void addParams(XWPFTable table, Map<String, SoapApiParameter> params, int currentLevel, int maxLevel) {
        for (Map.Entry<String, SoapApiParameter> param : params.entrySet()) {
            XWPFTableRow row = table.createRow();
            for (int i = 1; i < maxLevel; i++) {
                row.createCell();
            }
            row.getCell(currentLevel).setText(param.getKey());
            mergeCellHorizontally(row, currentLevel, maxLevel - 1);
            row.createCell().setText(param.getValue().getType().toString());
            row.createCell().setText(param.getValue().getDescription());
            if (!param.getValue().getType().isBasicType()) {
                addParams(table, param.getValue().getSubParameter(), currentLevel + 1, maxLevel);
            }
        }
    }

    private static void addTitleRow(XWPFTable table, String title, int maxCol) {
        XWPFTableRow row = table.createRow();
        row.getCell(0).setText(title);
        row.getCell(0).setColor("999999");
        // create empty cell
        for (int i = 1; i < maxCol; i++) {
            row.createCell();
        }
        mergeCellHorizontally(row, 0, maxCol - 1);
    }

    private static void addKVRow(XWPFTable table, String name, String value, int maxCol) {
        XWPFTableRow row = table.createRow();
        row.getCell(0).setText(name);
        row.getCell(0).setColor("999999");
        row.createCell().setText(value);
        // create empty cell
        for (int i = 2; i < maxCol; i++) {
            row.createCell();
        }
        mergeCellHorizontally(row, 1, maxCol - 1);
    }

    private static void mergeCellHorizontally(XWPFTableRow row, int fromCol, int toCol) {
        for (int colIndex = fromCol; colIndex <= toCol; colIndex++) {
            XWPFTableCell cell = row.getCell(colIndex);
            CTHMerge hMerge = CTHMerge.Factory.newInstance();
            if (colIndex == fromCol) {
                // The first merged cell is set with RESTART merge value
                hMerge.setVal(STMerge.RESTART);
            } else {
                // Cells which join (merge) the first one, are set with CONTINUE
                hMerge.setVal(STMerge.CONTINUE);
                // and the content should be removed
                for (int i = cell.getParagraphs().size(); i > 0; i--) {
                    cell.removeParagraph(0);
                }
                cell.addParagraph();
            }
            // Try getting the TcPr. Not simply setting an new one every time.
            CTTcPr tcPr = cell.getCTTc().getTcPr();
            if (tcPr != null) {
                tcPr.setHMerge(hMerge);
            } else {
                // only set an new TcPr if there is not one already
                tcPr = CTTcPr.Factory.newInstance();
                tcPr.setHMerge(hMerge);
                cell.getCTTc().setTcPr(tcPr);
            }
        }
    }

    private static void addHeadingStyle(XWPFDocument doc) {
        CTStyle ctStyle = CTStyle.Factory.newInstance();
        ctStyle.setStyleId(HEADING_STYLE_ID);

        CTString styleName = CTString.Factory.newInstance();
        styleName.setVal(HEADING_STYLE_ID);
        ctStyle.setName(styleName);

        CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
        indentNumber.setVal(BigInteger.valueOf(1));

        // lower number > style is more prominent in the formats bar
        ctStyle.setUiPriority(indentNumber);

        CTOnOff onOff = CTOnOff.Factory.newInstance();
        ctStyle.setUnhideWhenUsed(onOff);

        ctStyle.setQFormat(onOff);

        CTPPr ppr = CTPPr.Factory.newInstance();
        ppr.setOutlineLvl(indentNumber);
        ctStyle.setPPr(ppr);

        CTHpsMeasure size = CTHpsMeasure.Factory.newInstance();
        size.setVal(new BigInteger(String.valueOf(36)));
        CTHpsMeasure size2 = CTHpsMeasure.Factory.newInstance();
        size2.setVal(new BigInteger("24"));

        CTRPr rpr = CTRPr.Factory.newInstance();
        rpr.setSz(size);
        rpr.setSzCs(size2);
        ctStyle.setRPr(rpr);

        XWPFStyle style = new XWPFStyle(ctStyle);
        XWPFStyles styles = doc.createStyles();

        style.setType(STStyleType.PARAGRAPH);
        styles.addStyle(style);
    }
}
