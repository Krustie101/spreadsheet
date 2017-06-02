package org.modelcatalogue.spreadsheet.builder.poi

import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.modelcatalogue.spreadsheet.api.AbstractBorderProvider

import org.modelcatalogue.spreadsheet.api.BorderStyle
import org.modelcatalogue.spreadsheet.api.Color
import org.modelcatalogue.spreadsheet.api.Keywords

class PoiBorderDefinition extends AbstractBorderProvider {

    private final XSSFCellStyle xssfCellStyle

    private XSSFColor color
    private BorderStyle borderStyle

    PoiBorderDefinition(XSSFCellStyle xssfCellStyle) {
        this.xssfCellStyle = xssfCellStyle
    }

    @Override
    PoiBorderDefinition style(BorderStyle style) {
        borderStyle = style
        return this
    }

    @Override
    PoiBorderDefinition color(String hexColor) {
        color = PoiCellStyleDefinition.parseColor(hexColor)
        return this
    }

    @Override
    PoiBorderDefinition color(Color colorPreset) {
        color colorPreset.hex
        return this
    }

    protected void applyTo(Keywords.BorderSide location) {
        switch (location) {
            case Keywords.PureBorderSide.BOTTOM:
                if (borderStyle) {
                    xssfCellStyle.setBorderBottom(poiBorderStyle)
                }
                if (color) {
                    xssfCellStyle.setBottomBorderColor(color)
                }
                break
            case Keywords.PureBorderSide.TOP:
                if (borderStyle) {
                    xssfCellStyle.setBorderTop(poiBorderStyle)
                }
                if (color) {
                    xssfCellStyle.setTopBorderColor(color)
                }
                break
            case Keywords.BorderSideAndVerticalAlignment.LEFT:
                if (borderStyle) {
                    xssfCellStyle.setBorderLeft(poiBorderStyle)
                }
                if (color) {
                    xssfCellStyle.setLeftBorderColor(color)
                }
                break
            case Keywords.BorderSideAndVerticalAlignment.RIGHT:
                if (borderStyle) {
                    xssfCellStyle.setBorderRight(poiBorderStyle)
                }
                if (color) {
                    xssfCellStyle.setRightBorderColor(color)
                }
                break
            default:
                throw new IllegalArgumentException("$location is not supported!")
        }

    }

    private org.apache.poi.ss.usermodel.BorderStyle getPoiBorderStyle() {
        switch (borderStyle) {
            case BorderStyle.NONE:
                return org.apache.poi.ss.usermodel.BorderStyle.NONE
            case BorderStyle.THIN:
                return org.apache.poi.ss.usermodel.BorderStyle.THIN
            case BorderStyle.MEDIUM:
                return org.apache.poi.ss.usermodel.BorderStyle.MEDIUM
            case BorderStyle.DASHED:
                return org.apache.poi.ss.usermodel.BorderStyle.DASHED
            case BorderStyle.DOTTED:
                return org.apache.poi.ss.usermodel.BorderStyle.DOTTED
            case BorderStyle.THICK:
                return org.apache.poi.ss.usermodel.BorderStyle.THICK
            case BorderStyle.DOUBLE:
                return org.apache.poi.ss.usermodel.BorderStyle.DOUBLE
            case BorderStyle.HAIR:
                return org.apache.poi.ss.usermodel.BorderStyle.HAIR
            case BorderStyle.MEDIUM_DASHED:
                return org.apache.poi.ss.usermodel.BorderStyle.MEDIUM_DASHED
            case BorderStyle.DASH_DOT:
                return org.apache.poi.ss.usermodel.BorderStyle.DASH_DOT
            case BorderStyle.MEDIUM_DASH_DOT:
                return org.apache.poi.ss.usermodel.BorderStyle.MEDIUM_DASH_DOT
            case BorderStyle.DASH_DOT_DOT:
                return org.apache.poi.ss.usermodel.BorderStyle.DASH_DOT_DOT
            case BorderStyle.MEDIUM_DASH_DOT_DOT:
                return org.apache.poi.ss.usermodel.BorderStyle.MEDIUM_DASH_DOT_DOTC
            case BorderStyle.SLANTED_DASH_DOT:
                return org.apache.poi.ss.usermodel.BorderStyle.SLANTED_DASH_DOT
        }
    }

}
