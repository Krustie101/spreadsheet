package builders.dsl.spreadsheet.builder.poi;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFName;
import builders.dsl.spreadsheet.impl.AbstractPendingFormula;
import builders.dsl.spreadsheet.impl.Utils;

/**
 * Pending formula is a formula definition which needs to be resolved at the end of the build where all named references
 * are know.
 */
class PoiPendingFormula extends AbstractPendingFormula {

    PoiPendingFormula(PoiCellDefinition cell, String formula) {
        super(cell, formula);
    }

    protected void doResolve(String expandedFormula) {
        getPoiCell().getCell().setCellFormula(expandedFormula);
        getPoiCell().getCell().setCellType(CellType.FORMULA);
    }


    protected String findRefersToFormula(final String name) {
        if (!name.equals(Utils.fixName(name))) {
            throw new IllegalArgumentException("Name " + name + " is not valid Excel name! Suggestion: " + Utils.fixName(name));
        }

        XSSFName nameFound = getPoiCell().getCell().getSheet().getWorkbook().getName(name);
        if (nameFound == null) {
            throw new IllegalArgumentException("Named cell \'" + name + "\' cannot be found!");
        }

        return nameFound.getRefersToFormula();
    }

    private PoiCellDefinition getPoiCell() {
        return (PoiCellDefinition) getCell();
    }

}
