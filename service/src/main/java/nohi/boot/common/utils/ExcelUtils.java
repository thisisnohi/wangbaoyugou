package nohi.boot.common.utils;


import lombok.extern.slf4j.Slf4j;
import nohi.boot.common.utils.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author NOHI
 * 2021-02-16 19:45
 **/
@Slf4j
public class ExcelUtils {
    /**
     * 创建 Pattern 对象
     */
    static Pattern pattern = Pattern.compile("[A-Z]+\\d+");
    static String NaN = "NaN";
    static String HSSFWorkbook = "HSSFWorkbook";
    static String POINT_ZERO = ".0";
    static int ZIMU_LEN = 26;

    /**
     * 设置样式
     *
     * @param styleRow
     * @param row
     * @return
     */
    public static Row setRowStyleFromListFirstRow(Row styleRow, Row row) {
        if (null != styleRow && null != row) {
            int styleRowNum = styleRow.getRowNum();
            int editRowNum = row.getRowNum();

            for (int i = 0; i < styleRow.getSheet().getNumMergedRegions(); i++) {
                CellRangeAddress ranage = styleRow.getSheet().getMergedRegion(i);
                int firstRow = ranage.getFirstRow();
                int loastRow = ranage.getLastRow();

                if (styleRowNum == ranage.getFirstRow()) {
                    CellRangeAddress newRanage = new CellRangeAddress(firstRow + (editRowNum - styleRowNum), loastRow + (editRowNum - styleRowNum), ranage.getFirstColumn(), ranage.getLastColumn());
                    row.getSheet().addMergedRegion(newRanage);
                }
            }
            row.setHeight(styleRow.getHeight());
            for (int i = 0; i < styleRow.getLastCellNum(); i++) {
                Cell from = getCell(styleRow, i);
                Cell to = getCell(row, i);
                CellStyle oldStyle = from.getCellStyle();
                to.setCellStyle(oldStyle);
                String ss = getCellValue(from);
                to.setCellValue(ss == null ? "" : ss.trim());

                //设置下拉框等验证信息
                setValidatation(from, to);
                copyCellValue(from, to);
            }
        }
        return row;
    }


    public static Cell getCell(Row row, int rol) {
        if (rol >= row.getLastCellNum()) {
            return row.createCell(rol);
        } else {
            if (null == row.getCell(rol)) {
                return row.createCell(rol);
            }
            return row.getCell(rol);
        }
    }

    /**
     * 取得单元格值
     *
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {
        String value = null;
        if (null == cell) {
            return "";
        }
        switch (cell.getCellType()) {
            // 数值型
            case Cell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    value = DateUtil.getJavaDate(cell.getNumericCellValue()).toString();
                } else {
                    //纯数字

                    value = String.valueOf(cell.getNumericCellValue());
                    //如果数值类型的，如果小数点后只有一个0,取整
                    if (null != value) {
                        int index = value.lastIndexOf(".");
                        if (index >= 1) {
                            String prefix = value.substring(index);
                            if (POINT_ZERO.equals(prefix)) {
                                return value.substring(0, index);
                            }
                        }
                    }
                }
                break;
            // 字符串型
            case Cell.CELL_TYPE_STRING:
                value = cell.getRichStringCellValue().toString();
                break;
            //公式型
            case Cell.CELL_TYPE_FORMULA:
                //读公式计算值
                try {
                    value = cell.getStringCellValue();
                    //如果获取的数据值为非法值,则转换为获取字符串
                    if (value.equals(NaN)) {
                        value = cell.getRichStringCellValue().toString();
                    }
                } catch (Exception e) {
                    log.error("sheetName:" + cell.getSheet().getSheetName() + ",rowIndex:" + cell.getRowIndex() + ",columnIndex:" + cell.getColumnIndex());
                    log.error(e.getMessage(), e);
                    try {
                        value = String.valueOf(cell.getNumericCellValue());
                    } catch (Exception a) {
                        log.error(a.getMessage(), a);
                    }
                }

                break;
            //布尔
            case Cell.CELL_TYPE_BOOLEAN:
                value = " " + cell.getBooleanCellValue();
                break;
            // 空值
            case Cell.CELL_TYPE_BLANK:
                value = "";
                System.out.print(value);
                break;
            // 故障
            case Cell.CELL_TYPE_ERROR:
                value = "";
                break;
            default:
                value = cell.getRichStringCellValue().toString();
        }
        return value;
    }

    /**
     * 获取合并单元格的值
     *
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    public static String getMergedRegionValue(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();

        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();

            if (row >= firstRow && row <= lastRow) {

                if (column >= firstColumn && column <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell);
                }
            }
        }

        return null;
    }

    /**
     * 判断指定的单元格是否是合并单元格
     *
     * @param sheet
     * @param row    行下标
     * @param column 列下标
     * @return
     */
    public static boolean isMergedRegion(Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param obj
     * @param value
     * @return
     * @throws Exception
     */
    public static void setValue(Object obj, String fileName, Class dateType, Object value)
            throws Exception {
        if (null == obj) {
            log.error("对象[" + obj + "] 为空，filename[" + fileName + "]");
            return;
        }
        if (null == fileName) {
            log.error("对象[" + obj + "] 对应的字段的字段名为空");
            return;
        }

        if (null == value) {
            log.error("对象[" + obj + "] 对应的字段[" + fileName + "]的值为空");
            return;
        }
        //用正则，点是正则的关键字，必须转义
        String[] vm = fileName.split("\\.");
        int index = fileName.indexOf(".");

        if (index == -1) {
            Method method = Clazz.getMethod(obj.getClass(), fileName, "set", dateType);
            method.invoke(obj, value);
        } else {
            Method method = Clazz.getMethod(obj.getClass(), vm[0], "get", null);
            Object temp = method.invoke(obj);
            if (null == temp) {
                try {
                    temp = method.getReturnType().newInstance();
                } catch (InstantiationException e) {
                    log.error(e.getMessage(), e);
                    throw new Exception("实例[" + method.getReturnType() + "]失败");
                }
            }

            //循环嵌套调用
            setValue(temp, fileName.substring(index + 1), dateType, value);

            //该对象的set方法
            method = Clazz.getMethod(obj.getClass(), vm[0], "set", temp.getClass());
            method.invoke(obj, temp);
        }
    }

    public static Row getRow(Sheet sheet, int row) {
        if (row > sheet.getLastRowNum()) {
            return sheet.createRow(row);
        } else {
            if (null == sheet.getRow(row)) {
                return sheet.createRow(row);
            }
            return sheet.getRow(row);
        }
    }

    /**
     * 设置样式
     */
    public static Row setRowStyle(Row styleRow, Row row) {
        if (null != styleRow && null != row) {
            int styleRowNum = styleRow.getRowNum();
            int editRowNum = row.getRowNum();

            for (int i = 0; i < styleRow.getSheet().getNumMergedRegions(); i++) {
                CellRangeAddress ranage = styleRow.getSheet().getMergedRegion(i);
                int firstRow = ranage.getFirstRow();
                int loastRow = ranage.getLastRow();

                if (styleRowNum == ranage.getFirstRow()) {
                    CellRangeAddress newRanage = new CellRangeAddress(firstRow + (editRowNum - styleRowNum), loastRow + (editRowNum - styleRowNum), ranage.getFirstColumn(), ranage.getLastColumn());
                    row.getSheet().addMergedRegion(newRanage);
                }
            }
            row.setHeight(styleRow.getHeight());

            Drawing drawing = row.getSheet().createDrawingPatriarch();
            Workbook workbook = row.getSheet().getWorkbook();

            for (int i = 0; i < styleRow.getLastCellNum(); i++) {
                copyCell(ExcelUtils.getCell(styleRow, i), ExcelUtils.getCell(row, i), drawing, workbook.getClass());
            }
        }
        return row;
    }

    /**
     * 拷贝单元格样式
     * @param fromCell 样式单元格
     * @param toCell  需要设置的单元格
     */
    public static void copyCellStyle(Cell fromCell, Cell toCell) {
        // 设置下拉框等验证信息
        setValidatation(fromCell, toCell);

        fromCell.getSheet().createDrawingPatriarch();
        // 同一个excel可以直接复制cellStyle
        if (fromCell.getSheet().getWorkbook() == toCell.getSheet().getWorkbook()) {
            toCell.setCellStyle(fromCell.getCellStyle());
        } else {
            // 不同的excel之间，需要调用cloneStyleFrom方法
            CellStyle newCellStyle = toCell.getSheet().getWorkbook().createCellStyle();
            newCellStyle.cloneStyleFrom(fromCell.getCellStyle());
            toCell.setCellStyle(newCellStyle);
        }
        copyCellValue(fromCell, toCell);
        Comment fromComment = fromCell.getCellComment();
        if (fromComment != null) {
            copyComment(toCell.getSheet().getWorkbook().getClass(), fromComment, toCell.getSheet().getDrawingPatriarch(), fromCell, toCell);
        }
    }

    public static void setValidatation(Cell fromCell, Cell toCell) {
        //拷贝验证
        if (null != fromCell && null != toCell && fromCell.getSheet() instanceof XSSFSheet) {
            List<XSSFDataValidation> validationList = getDataValidationConstraint((XSSFSheet) fromCell.getSheet(), fromCell.getRowIndex(), fromCell.getColumnIndex());
            if (null != validationList) {

                for (XSSFDataValidation item : validationList) {
                    DataValidationConstraint dvc = item.getValidationConstraint();
                    int validationType = dvc.getValidationType();
                    int operatorType = dvc.getOperator();
                    String vMin = dvc.getFormula1();
                    String vMax = dvc.getFormula2();
                    DataValidationConstraint dvConstraint = null;
                    DataValidationHelper dvHelper = toCell.getSheet().getDataValidationHelper();
                    if (validationType == DataValidationConstraint.ValidationType.TEXT_LENGTH) {
                        dvConstraint = dvHelper.createTextLengthConstraint(operatorType, vMin, vMax);
                    } else if (validationType == DataValidationConstraint.ValidationType.DECIMAL) {
                        dvConstraint = dvHelper.createDecimalConstraint(operatorType, vMin, vMax);
                    } else if (validationType == DataValidationConstraint.ValidationType.INTEGER) {
                        dvConstraint = dvHelper.createIntegerConstraint(operatorType, vMin, vMax);
                    } else if (validationType == DataValidationConstraint.ValidationType.DATE) {
                        dvConstraint = dvHelper.createDateConstraint(operatorType, vMin, vMax, null);
                    } else if (validationType == DataValidationConstraint.ValidationType.TIME) {
                        dvConstraint = dvHelper.createTimeConstraint(operatorType, vMin, vMax);
                    } else if (validationType == DataValidationConstraint.ValidationType.FORMULA) {
                        dvConstraint = dvHelper.createFormulaListConstraint(dvc.getFormula1());
                    } else if (validationType == DataValidationConstraint.ValidationType.LIST) {
                        if (null != vMin) {
                            vMin = vMin.replaceAll("\"", "");
                            String[] dlist = vMin.split(",");
                            dvConstraint = dvHelper.createExplicitListConstraint(dlist);
                        }
                    }
                    if (null == dvConstraint) {
                        continue;
                    }
                    CellRangeAddressList addressList = new CellRangeAddressList(toCell.getRowIndex(), toCell.getRowIndex(), toCell.getColumnIndex(), toCell.getColumnIndex());
                    DataValidation validation = dvHelper.createValidation(dvConstraint, addressList);
                    //设置出错提示信息
                    validation.setSuppressDropDownArrow(true);
                    validation.setShowErrorBox(true);
//					setDataValidationErrorMessage(validation, errorTitle, errorMsg);
                    toCell.getSheet().addValidationData(validation);
                }
            }
        }

    }

    private static void copyCell(Cell fromCell, Cell toCell, Drawing drawing, Class<? extends Workbook> clazz) {
        //设置下拉框等验证信息
        setValidatation(fromCell, toCell);

        fromCell.getSheet().createDrawingPatriarch();
        // 同一个excel可以直接复制cellStyle
        if (fromCell.getSheet().getWorkbook() == toCell.getSheet().getWorkbook()) {
            toCell.setCellStyle(fromCell.getCellStyle());
        } else {
            // 不同的excel之间，需要调用cloneStyleFrom方法
            CellStyle newCellStyle = toCell.getSheet().getWorkbook().createCellStyle();
            newCellStyle.cloneStyleFrom(fromCell.getCellStyle());
            toCell.setCellStyle(newCellStyle);
        }
        copyCellValue(fromCell, toCell);
        Comment fromComment = fromCell.getCellComment();
        if (fromComment != null) {
            copyComment(clazz, fromComment, drawing, fromCell, toCell);
        }
    }

    public static void copyCellValue(Cell from, Cell to) {
        switch (from.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                to.setCellValue(from.getStringCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                to.setCellValue(from.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_BLANK:
                to.setCellType(Cell.CELL_TYPE_BLANK);
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                to.setCellValue(from.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_ERROR:
                to.setCellErrorValue(from.getErrorCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                String formula = from.getCellFormula();
                if (null != formula) {
                    int fromRow = from.getRowIndex();
                    int fromCol = from.getColumnIndex();
                    int toRow = to.getRowIndex();
                    int toCol = to.getColumnIndex();
                    Matcher m = pattern.matcher(formula);
                    while (m.find()) {
                        String str = m.group(0);
                        String ziMu = null;
                        String indexStr = null;

                        if (null != str) {
                            indexStr = str.replaceAll("[^\\d]", "");
                            ziMu = str.replaceAll("\\d", "");
                        }
                        if (null != indexStr) {
                            int index = Integer.valueOf(indexStr);

                            if (index < (toRow + 1)) {
                                formula = formula.replaceAll(str, ziMu + (toRow + 1));
                            }
                        }
                    }
                    // 解决从bak文件解析问题
                    formula = formula.replaceAll("_bak!", "!");
                }
                to.setCellFormula(formula);
                break;
            default:
                break;
        }
    }

    private static void copyComment(Class<? extends Workbook> clazz, Comment fromComment, Drawing drawing, Cell fromCell, Cell toCell) {
        Comment newComment = drawing.createCellComment(createClientAnchor(clazz));
        newComment.setAuthor(fromComment.getAuthor());
        newComment.setColumn(fromComment.getColumn());
        newComment.setRow(fromComment.getRow());
        newComment.setString(fromComment.getString());
        newComment.setVisible(fromComment.isVisible());
        toCell.setCellComment(newComment);
    }

    private static ClientAnchor createClientAnchor(Class<? extends Workbook> clazz) {
        ClientAnchor aClientAnchor = null;
        String s = clazz.getSimpleName();
        if (s.equals(HSSFWorkbook)) {
            aClientAnchor = new HSSFClientAnchor();

        } else {
            aClientAnchor = new XSSFClientAnchor();

        }
        return aClientAnchor;
    }

    public static List<XSSFDataValidation> getDataValidationConstraint(XSSFSheet sheet, int row, int col) {
        if (null == sheet) {
            return null;
        }

        List<XSSFDataValidation> rs = new ArrayList<XSSFDataValidation>();
        List<XSSFDataValidation> validations = sheet.getDataValidations();
        if (null != validations) {
            for (XSSFDataValidation item : validations) {
                CellRangeAddressList cral = item.getRegions();
                CellRangeAddress[] craArray = cral.getCellRangeAddresses();
                boolean isIn = false;
                for (CellRangeAddress cra : craArray) {
                    int fc = cra.getFirstColumn();
                    int fr = cra.getFirstRow();
                    int tc = cra.getLastColumn();
                    int tr = cra.getLastRow();

                    if (row >= fr && row <= tr && col >= fc && col <= tc) {
                        isIn = true;
                        break;
                    }
                }

                if (isIn) {
                    rs.add(item);
                }
            }
        }
        return rs;
    }

    public String getColString(int colIndex) {
        int first = colIndex / ZIMU_LEN;
        int index = colIndex % ZIMU_LEN;

        // 首字母
        String rs = String.valueOf((char) ('A' + (first - 1)));
        if (colIndex < ZIMU_LEN) {
            return String.valueOf((char) ('A' + index));
        } else {
            // 首字母;
            return rs + String.valueOf((char) ('A' + index));
        }
    }


    /**
     * 设置值
     */
    public static void setValue(Cell cell, Object rs) {
        if (rs == null) {
            cell.setCellValue("");
            return;
        }
        if (rs instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) rs).doubleValue());
        } else if (rs instanceof Date) {
            cell.setCellValue((Date) rs);
        } else if (rs instanceof Timestamp) {
            cell.setCellValue((Timestamp) rs);
        } else if (rs instanceof LocalDateTime) {
            cell.setCellValue(DateUtils.localDateTimeToDate((LocalDateTime) rs));
        } else if (rs instanceof Integer) {
            cell.setCellValue((Integer) rs);
        } else {
            cell.setCellValue(rs.toString());
        }
    }
}
