package builders.dsl.spreadsheet.builder.poi;

import builders.dsl.spreadsheet.builder.api.CellDefinition;
import builders.dsl.spreadsheet.builder.api.ImageCreator;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.util.IOUtils;

import java.io.*;
import java.net.URL;

class PoiImageCreator implements ImageCreator {

    PoiImageCreator(PoiCellDefinition poiCell, int type) {
        this.cell = poiCell;
        this.type = type;
    }

    @Override
    public CellDefinition from(String fileOrUrl) {
        if (fileOrUrl.startsWith("https://") || fileOrUrl.startsWith("http://")) {
            try {
                from(new BufferedInputStream(new URL(fileOrUrl).openStream()));
            } catch (IOException e) {
                throw new RuntimeException("Exception opening image stream: " + fileOrUrl, e);
            }
            return cell;
        }

        try {
            from(new FileInputStream(new File(fileOrUrl)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Image file not found: " + fileOrUrl, e);
        }
        return cell;
    }

    @Override
    public CellDefinition from(InputStream stream) {
        try {
            addPicture(cell.getRow().getSheet().getSheet().getWorkbook().addPicture(IOUtils.toByteArray(stream), type));
        } catch (IOException e) {
            throw new RuntimeException("Exception adding image from stream: " + stream, e);
        }
        return cell;
    }

    @Override
    public CellDefinition from(byte[] imageData) {
        addPicture(cell.getRow().getSheet().getSheet().getWorkbook().addPicture(imageData, type));
        return cell;
    }

    private void addPicture(int pictureIdx) {
        Drawing<?> drawing = cell.getRow().getSheet().getSheet().createDrawingPatriarch();

        CreationHelper helper = cell.getRow().getSheet().getSheet().getWorkbook().getCreationHelper();
        ClientAnchor anchor = helper.createClientAnchor();
        anchor.setCol1(cell.getCell().getColumnIndex());
        anchor.setRow1(cell.getCell().getRowIndex());

        Picture pict = drawing.createPicture(anchor, pictureIdx);
        pict.resize();
    }

    private final PoiCellDefinition cell;
    private final int type;
}
