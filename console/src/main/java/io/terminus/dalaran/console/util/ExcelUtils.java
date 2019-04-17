package io.terminus.dalaran.console.util;

import io.terminus.dalaran.model.schema.structure.FieldType;
import io.terminus.dalaran.model.schema.structure.ModelField;
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

    public Map<String, Map<String, ModelField>> parse(InputStream file) throws Exception {
        Map<String, Map<String, ModelField>> objs = new HashMap<>();
        val workbook = new XSSFWorkbook(file);
        for (Sheet sheet : workbook) {
            Map<String, ModelField> obj = new HashMap<>();
            val name = sheet.getSheetName();
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
                if (nextRowNum >= sheet.getPhysicalNumberOfRows() ) {
                    break;
                }
                val field = build(-1, currentRowLevel, currentRowNum, sheet);
                obj.putAll(field);
            }
            objs.put(name, obj);
        }
        return objs;
    }

    private  Map<String, ModelField> build(int topLevel, int currentRowLevel, int currentRowNum, Sheet sheet) {
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
            if (level <= currentRowLevel || level - currentRowLevel > 1) {
                break;
            }

            Map<String, ModelField> children = build(currentRowLevel, level, rowNum, sheet);
            if (currentField.getFields() != null) {
                currentField.getFields().putAll(children);
            } else {
                currentField.setFields(children);
            }
        }

        return fieldMap;
    }

    private int getRowLevel(int rowNum, Sheet sheet) {
        for (Cell cell : sheet.getRow(rowNum)) {
            if (StringUtils.isNoneBlank(cell.getStringCellValue())) {
                return cell.getColumnIndex();
            }
        }
        return -1;
    }
}
