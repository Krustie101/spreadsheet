package org.modelcatalogue.spreadsheet.query.poi;

import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.modelcatalogue.spreadsheet.api.Keywords;
import org.modelcatalogue.spreadsheet.api.Page;

class PoiPage implements Page {

    PoiPage(PoiSheet sheet) {
        this.printSetup = sheet.getSheet().getPrintSetup();
    }

    @Override
    public Keywords.Orientation getOrientation() {
        switch (printSetup.getOrientation()) {
            case DEFAULT:
                return null;
            case PORTRAIT:
                return Keywords.Orientation.PORTRAIT;
            case LANDSCAPE:
                return Keywords.Orientation.LANDSCAPE;
        }
        return null;
    }

    @Override
    public Keywords.Paper getPaper() {
        switch (printSetup.getPaperSizeEnum()) {
            case LETTER_PAPER:
                return Keywords.Paper.LETTER;
            case LETTER_SMALL_PAPER:
                return Keywords.Paper.LETTER_SMALL;
            case TABLOID_PAPER:
                return Keywords.Paper.TABLOID;
            case LEDGER_PAPER:
                return Keywords.Paper.LEDGER;
            case LEGAL_PAPER:
                return Keywords.Paper.LEGAL;
            case STATEMENT_PAPER:
                return Keywords.Paper.STATEMENT;
            case EXECUTIVE_PAPER:
                return Keywords.Paper.EXECUTIVE;
            case A3_PAPER:
                return Keywords.Paper.A3;
            case A4_PAPER:
                return Keywords.Paper.A4;
            case A4_SMALL_PAPER:
                return Keywords.Paper.A4_SMALL;
            case A5_PAPER:
                return Keywords.Paper.A5;
            case B4_PAPER:
                return Keywords.Paper.B4;
            case B5_PAPER:
                return Keywords.Paper.B5;
            case FOLIO_PAPER:
                return Keywords.Paper.FOLIO;
            case QUARTO_PAPER:
                return Keywords.Paper.QUARTO;
            case STANDARD_PAPER_10_14:
                return Keywords.Paper.STANDARD_10_14;
            case STANDARD_PAPER_11_17:
                return Keywords.Paper.STANDARD_11_17;
        }
        return null;
    }

    final XSSFPrintSetup getPrintSetup() {
        return printSetup;
    }

    private final XSSFPrintSetup printSetup;
}
