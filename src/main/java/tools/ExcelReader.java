package tools;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import tools.objects.MyRow;
public class ExcelReader {

	public ExcelReader(){
		
	}
	public List<MyRow> readBooksFromExcelFile(String excelFilePath) throws IOException {
	    List<MyRow> listRows = new ArrayList<MyRow>();
	    FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
	 
	    Workbook workbook = new XSSFWorkbook(inputStream);
	    Sheet firstSheet = workbook.getSheetAt(0);
	    Iterator<Row> iterator = firstSheet.iterator();
	    while (iterator.hasNext()) {
	        Row nextRow = iterator.next();
	        Iterator<Cell> cellIterator = nextRow.cellIterator();
	        MyRow aRow = new MyRow();
	        aRow.setInput(new LinkedList<Queue<String>>());
	        
	        while (cellIterator.hasNext()) {
	            Cell nextCell = cellIterator.next();
	            int columnIndex = nextCell.getColumnIndex();
	 
	            switch (columnIndex%2) {
	            case 1://columnas impares
	            	aRow.setAction((String) getCellValue(nextCell));
	                break;
	            case 0://columnas pares
	            	aRow.addColToInput(getInputValues(String.valueOf(getCellValue(nextCell))));
	            }
	 
	 
	        }
	        listRows.add(aRow);
	    }
	 
	    workbook.close();
	    inputStream.close();
	 
	    return listRows;
	}
	private Queue<String> getInputValues(String input){
		Queue<String> values = new LinkedList<String>();
		String[] parts = input.split(" ; ");
        for (String temp: parts){
        	values.add(temp);
        }
		return values;
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

