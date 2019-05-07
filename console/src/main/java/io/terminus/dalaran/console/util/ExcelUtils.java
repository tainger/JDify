package io.terminus.dalaran.console.util;

import io.terminus.dalaran.FieldType;
import io.terminus.dalaran.model.ModelField;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by jingdi on 2019/4/12
 */
public class ExcelUtils {

    private static Map<Integer, Boolean> booleanMap = new HashMap<>();

    public static Map<String, ModelField> parseFirstSheet(InputStream file) throws Exception {
        val workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        return buildModel(sheet);
    }

    public static Map<String, Map<String, ModelField>> parseAllSheet(InputStream file) throws Exception {
        Map<String, Map<String, ModelField>> modelMap = new HashMap<>();
        val workbook = new XSSFWorkbook(file);
        for (Sheet sheet : workbook) {
            val name = sheet.getSheetName();
            val model = buildModel(sheet);
            modelMap.put(name, model);
        }
        return modelMap;
    }

    private static Map<String, ModelField> buildModel(Sheet sheet) {
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            booleanMap.put(i, false);
        }

        Map<String, ModelField> modelFieldMap = new HashMap<>();
        for (Row row : sheet) {
            int currentRowNum = row.getRowNum();
            if (currentRowNum == 0) {
                continue;
            }

            int currentRowLevel = getRowLevel(currentRowNum, sheet);
            if (currentRowLevel != 0) {
                continue;
            }

            int nextRowNum = currentRowNum + 1;
            if (nextRowNum >= sheet.getPhysicalNumberOfRows()) {
                break;
            }
            val field = buildFields(-1, currentRowLevel, currentRowNum, sheet);
            modelFieldMap.putAll(field);
        }
        return modelFieldMap;
    }

    private static Map<String, ModelField> buildFields(int topLevel, int currentRowLevel, int currentRowNum, Sheet sheet) {
        Row currentRow = sheet.getRow(currentRowNum);
        ModelField currentField = new ModelField();
        String columnName = currentRow.getCell(currentRowLevel).getStringCellValue();

        Iterator<Cell> cells = currentRow.cellIterator();
        while (cells.hasNext()) {
            Cell cell = cells.next();
            switch (cell.getColumnIndex()) {
                case 5:
                    currentField.setType(FieldType.valueOf(cell.getStringCellValue().toUpperCase()));
                    break;
                case 7:
                    currentField.setNullable(cell.getBooleanCellValue());
                    break;
                case 8:
                    currentField.setDescription(cell.getStringCellValue());
                    break;
            }
        }

        Map<String, ModelField> fieldMap = new HashMap<>();
        fieldMap.put(columnName, currentField);

        int nextRowNum = currentRowNum + 1;
        if (nextRowNum >= sheet.getPhysicalNumberOfRows()) {
            return fieldMap;
        }

        int nextRowLevel = 0;
        for (Cell cell : sheet.getRow(nextRowNum)) {
            if (StringUtils.isNoneBlank(cell.getStringCellValue())) {
                nextRowLevel = cell.getColumnIndex();
                break;
            }
        }

        if (nextRowLevel <= topLevel) {
            return fieldMap;
        }

        for (int rowNum = nextRowNum; rowNum < sheet.getPhysicalNumberOfRows(); rowNum++) {
            int level = getRowLevel(rowNum, sheet);
            if ((nextRowNum >= rowNum && level <= currentRowLevel) || level - currentRowLevel > 1) {
                break;
            }

            Map<String, ModelField> children = buildFields(currentRowLevel, level, rowNum, sheet);
            if (!booleanMap.get(rowNum)) {
                booleanMap.put(rowNum, true);
                if (currentField.getFields() != null) {
                    if (level == currentRowLevel) {
                        fieldMap.putAll(children);
                    } else {
                        currentField.getFields().putAll(children);
                    }
                } else {
                    if (level == currentRowLevel) {
                        fieldMap.putAll(children);
                    } else {
                        currentField.setFields(children);
                    }
                }
            }
        }
        return fieldMap;
    }

    private static int getRowLevel(int rowNum, Sheet sheet) {
        for (Cell cell : sheet.getRow(rowNum)) {
            if (StringUtils.isNoneBlank(cell.getStringCellValue())) {
                return cell.getColumnIndex();
            }
        }
        return -1;
    }
}
