package main.cl.dagserver.infra.adapters.operators;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONObject;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.Dagmap;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;

@Operator(args={"filePath", "mode", "sheetName","startRow", "startColumn"}, optionalv={"xcom","endRow", "endColumn"})
public class ExcelOperator extends OperatorStage {

    @Override
    public List<Dagmap> call() throws DomainException {
        try {
            log.debug(this.getClass() + " init " + this.name);
            log.debug("args");
            log.debug(this.args);
            log.debug("optionalv");
            log.debug(this.optionals);
            log.debug(this.getClass() + " end " + this.name);

            String filePath = this.args.getProperty("filePath");
            String mode = this.args.getProperty("mode");

            if ("read".equalsIgnoreCase(mode)) {
                return readExcel(filePath);
            } else if ("write".equalsIgnoreCase(mode)) {
                return writeExcel(filePath);
            } else {
                throw new IllegalArgumentException("Unsupported mode: " + mode);
            }
        } catch (Exception e) {
            throw new DomainException(e);
        }
    }

    private List<Dagmap> readExcel(String filePath) {
        List<Dagmap> result = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = getWorkbook(filePath, fis)) {

            String sheetName = this.args.getProperty("sheetName");
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet != null) {
                int startRow = Integer.parseInt(this.args.getProperty("startRow", "0"));
                int endRow = Integer.parseInt(this.optionals.getProperty("endRow", String.valueOf(sheet.getLastRowNum())));
                int startColumn = Integer.parseInt(this.args.getProperty("startColumn", "0"));
                int endColumn = Integer.parseInt(this.optionals.getProperty("endColumn", String.valueOf(getMaxColumnIndex(sheet))));

                for (int i = startRow; i <= endRow; i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        Dagmap rowData = new Dagmap();
                        for (int j = startColumn; j <= endColumn; j++) {
                            Cell cell = row.getCell(j);
                            String columnName = "Column" + (j + 1);
                            if (cell != null) {
                                rowData.put(columnName, cell.toString());
                            } else {
                                rowData.put(columnName, "");
                            }
                        }
                        result.add(rowData);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error reading Excel file: " + filePath, e);
        }

        return result;
    }

    private int getMaxColumnIndex(Sheet sheet) {
        int maxColumnIndex = 0;
        for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                int lastCellIndex = row.getLastCellNum();
                if (lastCellIndex > maxColumnIndex) {
                    maxColumnIndex = lastCellIndex;
                }
            }
        }
        return maxColumnIndex - 1; // Convertimos a base cero
    }

    @SuppressWarnings("unchecked")
    private List<Dagmap> writeExcel(String filePath) throws DomainException {
        List<Dagmap> result = new ArrayList<>();
        Dagmap statusD = new Dagmap();
        try {
            Workbook workbook = getWorkbook(filePath, null);
            Sheet sheet = workbook.createSheet(this.args.getProperty("sheetName"));

            List<Dagmap> data = (List<Dagmap>) this.xcom.get(this.optionals.getProperty("xcom"));
            int startRow = Integer.parseInt(this.args.getProperty("startRow", "0"));
            int startColumn = Integer.parseInt(this.args.getProperty("startColumn", "0"));
            Boolean includeTitles = Boolean.parseBoolean(this.args.getProperty("includeTitles", "true"));
            Integer realStart = startRow;
            List<Map<String, Object>> rowDataObject = (List<Map<String, Object>>) data.get(0).get("output");
            if(includeTitles) {
            	Row row = sheet.createRow(realStart);
            	Map<String, Object> rowData = rowDataObject.get(0);
            	var keys = new ArrayList<>(rowData.keySet());
            	int cellIndex = startColumn;
            	for (int i = 0; i < keys.size(); i++) {
					String key = keys.get(i);
					Cell cell = row.createCell(cellIndex++);
	                cell.setCellValue(key);
				}
            }
            realStart ++;
            for (int i = 0; i < rowDataObject.size(); i++) {
                Row row = sheet.createRow(realStart + i);
                Map<String, Object> rowData = (Map<String, Object>) rowDataObject.get(i);
                int cellIndex = startColumn;
                for (Map.Entry<String, Object> entry : rowData.entrySet()) {
                   Cell cell = row.createCell(cellIndex++);
                   cell.setCellValue(entry.getValue().toString());
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            workbook.close();
            statusD.put("status", "OK");
            result.add(statusD);
            return result;
        } catch (Exception e) {
            throw new DomainException(e);
        }
    }

    @SuppressWarnings("resource")
	private Workbook getWorkbook(String filePath, FileInputStream fis) throws Exception {
        if (filePath.toLowerCase().endsWith(".xlsx")) {
            return (fis != null) ? WorkbookFactory.create(fis) : new XSSFWorkbook();
        } else if (filePath.toLowerCase().endsWith(".xls")) {
            return (fis != null) ? new HSSFWorkbook(fis) : new HSSFWorkbook();
        } else {;
            throw new IllegalArgumentException("Unsupported file extension: " + filePath);
        }
    }

    @Override
    public String getIconImage() {
        return "excel.png";
    }

    @Override
    public JSONObject getMetadataOperator() {
        MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.ExcelOperator");
        metadata.setParameter("filePath", "text");
        metadata.setParameter("mode", "list", List.of("read", "write"));
        metadata.setParameter("sheetName", "text");
        metadata.setParameter("startRow", "number");
        metadata.setParameter("startColumn", "number");
        metadata.setParameter("includeTitles", "list", List.of("true", "false"));
        metadata.setOpts("xcom", "xcom");
        metadata.setOpts("endRow", "number");
        metadata.setOpts("endColumn", "number");
        return metadata.generate();
    }
}
