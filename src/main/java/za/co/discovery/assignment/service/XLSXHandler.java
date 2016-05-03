package za.co.discovery.assignment.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.discovery.assignment.entity.Vertex;
import za.co.discovery.assignment.model.EdgeModel;
import za.co.discovery.assignment.model.TrafficModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class XLSXHandler {
    private File file;

    @Autowired
    public XLSXHandler(File file) {
        this.file = file;
    }

    public Map<String, Vertex> readVertexes() {
        Map<String, Vertex> vertexMap = new LinkedHashMap<>();
        try {
            FileInputStream inputStream = new FileInputStream(this.file);

            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = firstSheet.iterator();

            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                // skip header
                if (nextRow.getRowNum() == 0) {
                    continue;
                }
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                Vertex vertex = new Vertex();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int columnIndex = cell.getColumnIndex();
                    switch (columnIndex + 1) {
                        case 1:
                            vertex.setId((String) getCellValue(cell));
                            break;
                        case 2:
                            vertex.setName((String) getCellValue(cell));
                            break;
                    }
                }
                vertexMap.put(vertex.getId(), vertex);
            }

            workbook.close();
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger("discovery").log(Level.SEVERE, "An Exception occurred while reading vertices data: " + ex);
            System.exit(1);
        }
        return vertexMap;
    }

    public List<EdgeModel> readEdges() {
        List<EdgeModel> edges = new ArrayList<>();
        try {
            FileInputStream inputStream = new FileInputStream(this.file);

            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet firstSheet = workbook.getSheetAt(1);
            Iterator<Row> iterator = firstSheet.iterator();

            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                // skip header
                if (nextRow.getRowNum() == 0) {
                    continue;
                }
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                EdgeModel edge = new EdgeModel();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int columnIndex = cell.getColumnIndex();
                    switch (columnIndex + 1) {
                        case 1:
                            edge.setId(String.valueOf((int) cell.getNumericCellValue()));
                            break;
                        case 2:
                            edge.setSource(cell.getStringCellValue());
                            break;
                        case 3:
                            edge.setDestination(cell.getStringCellValue());
                            break;
                        case 4:
                            edge.setWeight((float) cell.getNumericCellValue());
                            break;
                    }
                }

                edges.add(edge);
            }

            workbook.close();
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger("discovery").log(Level.SEVERE, "An Exception occurred while reading edges data: " + ex);
            System.exit(1);
        }
        return edges;
    }

    public List<TrafficModel> readTraffics() {
        List<TrafficModel> traffics = new ArrayList<>();
        try {
            FileInputStream inputStream = new FileInputStream(this.file);

            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet thirdSheet = workbook.getSheetAt(2);
            Iterator<Row> iterator = thirdSheet.iterator();

            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                // skip header
                if (nextRow.getRowNum() == 0) {
                    continue;
                }
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                TrafficModel traffic = new TrafficModel();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int columnIndex = cell.getColumnIndex();
                    switch (columnIndex + 1) {
                        case 1:
                            traffic.setId(String.valueOf((int) cell.getNumericCellValue()));
                            break;
                        case 2:
                            traffic.setSource((String) getCellValue(cell));
                            break;
                        case 3:
                            traffic.setDestination((String) getCellValue(cell));
                            break;
                        case 4:
                            traffic.setWeight((float) cell.getNumericCellValue());
                            break;
                    }
                }
                traffics.add(traffic);
            }

            workbook.close();
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger("discovery").log(Level.SEVERE, "An Exception occurred while reading traffics data: " + ex);
            System.exit(1);
        }

        return traffics;
    }

    private Object getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();

            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();

            case Cell.CELL_TYPE_NUMERIC:
                return cell.getNumericCellValue();
        }

        return null;
    }
}
