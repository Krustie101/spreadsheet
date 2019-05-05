package builders.dsl.spreadsheet.query.poi;

import builders.dsl.spreadsheet.api.Cell;
import builders.dsl.spreadsheet.api.CellStyle;
import builders.dsl.spreadsheet.api.Comment;
import builders.dsl.spreadsheet.impl.Utils;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import builders.dsl.spreadsheet.impl.DefaultCommentDefinition;

import java.util.*;

class PoiCell implements Cell {

    PoiCell(PoiRow row, XSSFCell xssfCell) {
        this.xssfCell = checkNotNull(xssfCell, "Cell");
        this.row = checkNotNull(row, "Row");
    }

    private static <T> T checkNotNull(T o, String what) {
        if (o == null) {
            throw new IllegalArgumentException(what + " cannot be null");
        }

        return o;
    }

    @Override
    public int getColumn() {
        return xssfCell.getColumnIndex() + 1;
    }

    @Override
    public String getColumnAsString() {
        return Utils.toColumn(getColumn());
    }

    @Override
    public <T> T read(Class<T> type) {
        if (CharSequence.class.isAssignableFrom(type)) {
            return type.cast(xssfCell.getStringCellValue());
        }


        if (Date.class.isAssignableFrom(type)) {
            return type.cast(xssfCell.getDateCellValue());
        }


        if (Boolean.class.isAssignableFrom(type)) {
            return type.cast(xssfCell.getBooleanCellValue());
        }


        if (Number.class.isAssignableFrom(type)) {
            Double val = xssfCell.getNumericCellValue();
            return type.cast(val);
        }

        if (xssfCell.getRawValue() == null) {
            return null;
        }

        throw new IllegalArgumentException("Cannot read value " + xssfCell.getRawValue() + " of cell as " + String.valueOf(type));
    }

    @Override
    public Object getValue() {
        switch (xssfCell.getCellType()) {
            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK:
                return "";
            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN:
                return xssfCell.getBooleanCellValue();
            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_ERROR:
                return xssfCell.getErrorCellString();
            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA:
                return xssfCell.getCellFormula();
            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
                return xssfCell.getNumericCellValue();
            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
                return xssfCell.getStringCellValue();
        }
        return xssfCell.getRawValue();
    }

    @Override
    public Comment getComment() {
        XSSFComment comment = xssfCell.getCellComment();
        if (comment == null) {
            return new DefaultCommentDefinition();
        }

        DefaultCommentDefinition definition = new DefaultCommentDefinition();
        definition.author(comment.getAuthor());
        definition.text(comment.getString().getString());
        return definition;
    }

    @Override
    public CellStyle getStyle() {
        XSSFCellStyle cellStyle = xssfCell.getCellStyle();
        return cellStyle != null ? new PoiCellStyle(cellStyle) : null;
    }

    private String generateRefersToFormula() {
        return "\'" + xssfCell.getSheet().getSheetName().replaceAll("'", "\\'") + "\'!" + xssfCell.getReference();
    }

    @Override
    public String getName() {
        Workbook wb = xssfCell.getSheet().getWorkbook();

        List<String> possibleReferences = new ArrayList<String>(Arrays.asList(new CellReference(xssfCell).formatAsString(), generateRefersToFormula()));
        for (Name n : wb.getAllNames()) {
            if (n.getSheetIndex() == -1 || n.getSheetIndex() == wb.getSheetIndex(xssfCell.getSheet())) {
                for (String reference : possibleReferences) {
                    if (normalizeFormulaReference(n.getRefersToFormula()).equalsIgnoreCase(normalizeFormulaReference(reference))) {
                        return n.getNameName();
                    }
                }
            }
        }

        return null;
    }

    public int getColspan() {
        if (row.getSheet().getSheet().getNumMergedRegions() == 0) {
            return 1;
        }

        for (CellRangeAddress candidate : row.getSheet().getSheet().getMergedRegions()) {
            if (candidate.isInRange(getCell().getRowIndex(), getCell().getColumnIndex())) {
                return candidate.getLastColumn() - candidate.getFirstColumn() + 1;
            }
        }
        return 1;
    }

    public int getRowspan() {
        if (row.getSheet().getSheet().getNumMergedRegions() == 0) {
            return 1;
        }

        for (CellRangeAddress candidate : row.getSheet().getSheet().getMergedRegions()) {
            if (candidate.isInRange(getCell().getRowIndex(), getCell().getColumnIndex())) {
                return candidate.getLastRow() - candidate.getFirstRow() + 1;
            }
        }

        return 1;
    }

    protected XSSFCell getCell() {
        return xssfCell;
    }

    @Override
    public PoiRow getRow() {
        return row;
    }


    @Override
    public Cell getAbove() {
        PoiRow row = this.row.getAbove();
        if (row == null) {
            return null;
        }

        PoiCell existing = (PoiCell) row.getCellByNumber(getColumn());

        if (existing != null) {
            return existing;
        }


        return createCellIfExists(getCell(row.getRow(), getColumn() - 1));
    }

    @Override
    public Cell getBelow() {
        PoiRow row = this.row.getBelow(getRowspan());
        if (row == null) {
            return null;
        }

        PoiCell existing = (PoiCell) row.getCellByNumber(getColumn());

        if (existing != null) {
            return existing;
        }


        return createCellIfExists(getCell(row.getRow(), getColumn() - 1));
    }

    @Override
    public Cell getLeft() {
        if (getColumn() == 1) {
            return null;
        }

        PoiCell existing = (PoiCell) row.getCellByNumber(getColumn() - 1);

        if (existing != null) {
            return existing;
        }


        return createCellIfExists(getCell(row.getRow(), getColumn() - 2));
    }

    @Override
    public Cell getRight() {
        if (getColumn() + getColspan() > row.getRow().getLastCellNum()) {
            return null;
        }

        PoiCell existing = (PoiCell) row.getCellByNumber(getColumn() + getColspan());

        if (existing != null) {
            return existing;
        }


        return createCellIfExists(getCell(row.getRow(), getColumn() + getColspan() - 1));
    }

    @Override
    public Cell getAboveLeft() {
        PoiRow row = this.row.getAbove();
        if (row == null) {
            return null;
        }

        if (getColumn() == 1) {
            return null;
        }

        PoiCell existing = (PoiCell) row.getCellByNumber(getColumn() - 1);

        if (existing != null) {
            return existing;
        }


        return createCellIfExists(getCell(row.getRow(), getColumn() - 2));
    }

    @Override
    public Cell getAboveRight() {
        PoiRow row = this.row.getAbove();
        if (row == null) {
            return null;
        }

        if (getColumn() == row.getRow().getLastCellNum()) {
            return null;
        }

        PoiCell existing = (PoiCell) row.getCellByNumber(getColumn() + 1);

        if (existing != null) {
            return existing;
        }


        return createCellIfExists(getCell(row.getRow(), getColumn()));
    }

    @Override
    public Cell getBelowLeft() {
        PoiRow row = this.row.getBelow();
        if (row == null) {
            return null;
        }

        if (getColumn() == 1) {
            return null;
        }

        PoiCell existing = (PoiCell) row.getCellByNumber(getColumn() - 1);

        if (existing != null) {
            return existing;
        }


        return createCellIfExists(getCell(row.getRow(), getColumn() - 2));
    }

    @Override
    public Cell getBelowRight() {
        PoiRow row = this.row.getBelow();
        if (row == null) {
            return null;
        }

        if (getColumn() == row.getRow().getLastCellNum()) {
            return null;
        }

        PoiCell existing = (PoiCell) row.getCellByNumber(getColumn() + 1);

        if (existing != null) {
            return existing;
        }


        return createCellIfExists(getCell(row.getRow(), getColumn()));
    }

    private static XSSFCell getCell(final XSSFRow row, final int column) {
        XSSFCell cell = row.getCell(column);
        if (cell != null) {
            return cell;
        }

        if (row.getSheet().getNumMergedRegions() == 0) {
            return null;
        }

        CellRangeAddress address = null;

        for (CellRangeAddress candidate: row.getSheet().getMergedRegions()) {
            if (candidate.isInRange(row.getRowNum(), column)) {
                address = candidate;
                break;
            }
        }

        if (address == null) {
            return null;
        }

        return row.getSheet().getRow(address.getFirstRow()).getCell(address.getFirstColumn());
    }

    private static String normalizeFormulaReference(String ref) {
        return ref.replace("$", "").replace("'", "");
    }

    private PoiCell createCellIfExists(XSSFCell cell) {
        if (cell != null) {
            final PoiRow number = row.getSheet().getRowByNumber(cell.getRowIndex() + 1);
            return new PoiCell(number != null ? number : row.getSheet().createRowWrapper(cell.getRowIndex() + 1), cell);
        }

        return null;
    }

    @Override
    public String toString() {
        return "Cell[" + row.getSheet().getName() + "!" + getColumnAsString() + String.valueOf(row.getNumber()) + "]=" + String.valueOf(getValue());
    }

    private final PoiRow row;
    private final XSSFCell xssfCell;
}
