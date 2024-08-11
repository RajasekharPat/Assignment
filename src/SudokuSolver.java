import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

class SudokuCell{
	
	private int value;
	private ArrayList<Integer> possible_values;
	private int possible_values_index;
		
	SudokuCell(){
		this.value = 0;
		this.possible_values = null;
		this.possible_values_index = -1;
	}
	
	SudokuCell(int value){
		this.value = value;
		this.possible_values = null;
		this.possible_values_index = -1;
	}
	
	void addPossibleValues(ArrayList<Integer> possible_values) {
		this.possible_values = possible_values;
		this.possible_values_index = -1;
	}
	
	int getNextPossibleValue() {
		this.possible_values_index++;
		if(this.possible_values_index < this.possible_values.size())
			return this.possible_values.get(possible_values_index);
		return 0;
		
	}
	
	void resetPossibleValuesIndex() {
		this.possible_values_index = -1;
		
	}
	
	int getValue() {
		if((this.value != 0) || (this.possible_values_index == -1))
			return this.value;
		return this.possible_values.get(this.possible_values_index);
	}
	
	void setValue(int value) {
		this.value = value;
	}
	
	ArrayList<Integer> getPossibleValues(){
		return this.possible_values;
	}
	
	
}

class Sudoku{
	
	private ArrayList<SudokuCell> sudoku_grid;
	private HashMap<Integer, Integer> cellRegionMap;
	private HashMap<Integer, ArrayList<Integer>> regionCellsMap;
	
	Sudoku() {
		this.sudoku_grid = new ArrayList<SudokuCell>();
		this.cellRegionMap = new HashMap<Integer, Integer>();
		this.regionCellsMap = new HashMap<Integer, ArrayList<Integer>>();
	}
	
	void populateGrid(ArrayList<Integer> cellValues) {
		for(int i = 0; i < cellValues.size(); i++) {
		
			int cellValue = cellValues.get(i);
			if(cellValue != 0) {
				SudokuCell cell = new SudokuCell(cellValue);
				sudoku_grid.add(cell);
			}
			else {
				SudokuCell cell = new SudokuCell();
				sudoku_grid.add(cell);
			}
		}
		
		
		setCellPossibleValues();
		
		
		
	}
	
	private void setCellPossibleValues() {
		
		for(int i = 0; i<this.sudoku_grid.size(); i++)
		{
			if(this.sudoku_grid.get(i).getValue() == 0)
				setCellPossibleValues(i);
		}
		
	}

	private void setCellPossibleValues(int cellIndex) {
		ArrayList<Integer> possibleValues = new ArrayList<Integer>();
		
		for(int i = 1; i<=9; i++)
			possibleValues.add(i);
		
		
		for(int i = cellIndex-9; i >= 0; i=i-9) {
			int val = this.sudoku_grid.get(i).getValue();
			if(possibleValues.contains(val)) {
				possibleValues.remove((Object)val);
			}
		}
		for(int i = cellIndex+9; i <= 80; i=i+9) {
			
			int val = this.sudoku_grid.get(i).getValue();
			if(possibleValues.contains(val)) {
				possibleValues.remove((Object)val);
			}
		}
		
		
		int start = (cellIndex/9)*9;
		int end = start+ 9;
		
		for(int i = start; i < end; i++) {
			if(i == cellIndex)
				continue;
			int val = this.sudoku_grid.get(i).getValue();
			if(possibleValues.contains(val)) {
				possibleValues.remove((Object)val);
			}
		}
		
			
		
		int region = cellRegionMap.get(cellIndex);
		ArrayList<Integer> indices = regionCellsMap.get(region);
		for(int i = 0; i < indices.size(); i++) {
			int j = indices.get(i);
			if(j == cellIndex)
				continue;
			int val = this.sudoku_grid.get(j).getValue();
			if(possibleValues.contains(val)) {
				possibleValues.remove((Object)val);
			}
		}
		
		if(possibleValues.size() == 1)
		{
			this.sudoku_grid.get(cellIndex).setValue(possibleValues.get(0));
			
		}
		else
			this.sudoku_grid.get(cellIndex).addPossibleValues(possibleValues);
	}

	private void populateMaps() {
		
		for(int i=0,j=1;j<=9;j++)
		{
			ArrayList<Integer> values = new ArrayList<Integer>();
			cellRegionMap.put(i,j);
			cellRegionMap.put(i+1,j);
			cellRegionMap.put(i+2,j);
			
			cellRegionMap.put(i+9,j);
			cellRegionMap.put(i+10,j);
			cellRegionMap.put(i+11,j);
			
			cellRegionMap.put(i+18,j);
			cellRegionMap.put(i+19,j);
			cellRegionMap.put(i+20,j);
			
			values.add(i);
			values.add(i+1);
			values.add(i+2);
			
			values.add(i+9);
			values.add(i+10);
			values.add(i+11);
			
			values.add(i+18);
			values.add(i+19);
			values.add(i+20);
			
			regionCellsMap.put(j, values);
			
			
			if(j%3 ==0)
				i = i + 21;
			else
				i = i + 3;
			
			
		}
		  
	}

	boolean solve() {
		Stack<Integer> processing_cells = new Stack<Integer>();
		Stack<Integer> storage_cells = this.getEmptyCells();
		
		if(storage_cells.empty())
			return true;
		
		int cellIndex = storage_cells.peek();
		storage_cells.pop();
		processing_cells.push(cellIndex);
		
		while(!(processing_cells.empty())) {
			
			SudokuCell cell = this.sudoku_grid.get(cellIndex);
			int cell_possible_value = cell.getNextPossibleValue();
			boolean is_valid = isValid(cellIndex, cell_possible_value);
			if(is_valid == true) {
				if(storage_cells.empty())
					return true;
				cellIndex = storage_cells.peek();
				storage_cells.pop();
				processing_cells.push(cellIndex);
				
			}
			else {
				if(cell_possible_value == 0) {
					
					processing_cells.pop();
					storage_cells.push(cellIndex);
					cell.resetPossibleValuesIndex();
					if(processing_cells.empty())
						return false;
					cellIndex = processing_cells.peek();
					
				}
				else {
					continue;
				}
			}
		}
		return false;
		
	}

	private boolean isValid(int cellIndex, int cell_possible_value) {
		if(cell_possible_value == 0)
			return false;
		
		for(int i = cellIndex-9; i >= 0; i=i-9) {
			int val = this.sudoku_grid.get(i).getValue();
			if(cell_possible_value == val) {
				return false;
			}
		}
		for(int i = cellIndex+9; i <= 80; i=i+9) {
			
			int val = this.sudoku_grid.get(i).getValue();
			if(cell_possible_value == val) {
				return false;
			}
		}
		
		
		int start = (cellIndex/9)*9;
		int end = start+ 9;
		
		for(int i = start; i < end; i++) {
			if(i == cellIndex)
				continue;
			int val = this.sudoku_grid.get(i).getValue();
			if(cell_possible_value == val) {
				return false;
			}
		}
		
			
		
		int region = cellRegionMap.get(cellIndex);
		ArrayList<Integer> indices = regionCellsMap.get(region);
		for(int i = 0; i < indices.size(); i++) {
			int j = indices.get(i);
			if(j == cellIndex)
				continue;
			int val = this.sudoku_grid.get(j).getValue();
			if(cell_possible_value == val) {
				return false;
			}
		}
		
		return true;
		
	}

	private Stack<Integer> getEmptyCells() {
		Stack<Integer> empty_cells = new Stack<Integer>();
		for(int i = 80; i >= 0; i--) {
			if(this.sudoku_grid.get(i).getValue() == 0)
			{
				empty_cells.push(i);
			}
		}
		return empty_cells;
	}

	boolean validate(ArrayList<Integer> vals) {
		int[] values = {0,0,0,0,0,0,0,0,0,0};
		if(vals.size() != 81)
			return false;
		
		populateMaps();
		
		for(int i = 0; i <= 80; i = (i+9)) {
			
			values[vals.get(i)] += vals.get(i);
			values[vals.get(i+1)] += vals.get(i+1);
			values[vals.get(i+2)] += vals.get(i+2);
			
			values[vals.get(i+3)] += vals.get(i+3);
			values[vals.get(i+4)] += vals.get(i+4);
			values[vals.get(i+5)] += vals.get(i+5);
			
			values[vals.get(i+6)] += vals.get(i+6);
			values[vals.get(i+7)] += vals.get(i+7);
			values[vals.get(i+8)] += vals.get(i+8);
			
			for(int j = 0; j < 10; j++)
			{
				if(values[j] > j)
					return false;
				values[j] = 0;
			}
		}
		
		for(int i = 0; i <= 8; i++) {
			
			values[vals.get(i)] += vals.get(i);
			values[vals.get(i+9)] += vals.get(i+9);
			values[vals.get(i+18)] += vals.get(i+18);
			
			values[vals.get(i+27)] += vals.get(i+27);
			values[vals.get(i+36)] += vals.get(i+36);
			values[vals.get(i+45)] += vals.get(i+45);
			
			values[vals.get(i+54)] += vals.get(i+54);
			values[vals.get(i+63)] += vals.get(i+63);
			values[vals.get(i+72)] += vals.get(i+72);
			
			for(int j = 0; j < 10; j++)
			{
				if(values[j] > j)
					return false;
				values[j] = 0;
			}
		}
		
		for(int i = 1; i<=9; i++) {
			ArrayList<Integer> indices = regionCellsMap.get(i);
			
			values[vals.get(indices.get(0))] += vals.get(indices.get(0));
			values[vals.get(indices.get(1))] += vals.get(indices.get(1));
			values[vals.get(indices.get(2))] += vals.get(indices.get(2));
			
			values[vals.get(indices.get(3))] += vals.get(indices.get(3));
			values[vals.get(indices.get(4))] += vals.get(indices.get(4));
			values[vals.get(indices.get(5))] += vals.get(indices.get(5));
			
			values[vals.get(indices.get(6))] += vals.get(indices.get(6));
			values[vals.get(indices.get(7))] += vals.get(indices.get(7));
			values[vals.get(indices.get(8))] += vals.get(indices.get(8));
			
			for(int j = 0; j < 10; j++)
			{
				if(values[j] > j)
					return false;
				values[j] = 0;
			}
			
		}
		
		
		return true;
	}

	void displaySolution() {
		
		for(int i = 0; i<80; i= (i+ 9)){
			System.out.print(this.sudoku_grid.get(i).getValue() + "  ");
			System.out.print(this.sudoku_grid.get(i+1).getValue() + "  ");
			System.out.print(this.sudoku_grid.get(i+2).getValue() + " |  ");
			
			System.out.print(this.sudoku_grid.get(i+3).getValue() + "  ");
			System.out.print(this.sudoku_grid.get(i+4).getValue() + "  ");
			System.out.print(this.sudoku_grid.get(i+5).getValue() + " |  ");
			
			System.out.print(this.sudoku_grid.get(i+6).getValue() + "  ");
			System.out.print(this.sudoku_grid.get(i+7).getValue() + "  ");
			System.out.print(this.sudoku_grid.get(i+8).getValue() + "\n");
			
			if((i == 18) || (i == 45))
				System.out.print("--------|----------|---------\n");
			
		}
		
	}


}

public class SudokuSolver{
	
	
	public static ArrayList<Integer> getVals(){
		ArrayList<Integer> vals = new ArrayList<Integer>();
		
		String currentPath = "";
		try {
			currentPath = new java.io.File(".").getCanonicalPath();
		} catch (IOException e) {
			System.out.println("Unable to find Path");
			return vals;
		}
		
		File file = new File(currentPath + "\\input.txt");
	        Scanner sc = null;
			try {
				sc = new Scanner(file);
			} catch (FileNotFoundException e) {
				System.out.println("Input File not found");
				return vals;
			}
			if(sc != null) {
				int lineNo = 1;
		        while (sc.hasNextLine() && lineNo <= 9) {
		        	String line = sc.nextLine();
		            String[] cells = line.split(",");
			    int colNo = 1;
		            for(int i = 0; i<cells.length; i++) {
			            int val = -1;
			            try {
			               val = Integer.parseInt(cells[i]);
			            }
			            catch (NumberFormatException e) {
			            	e.printStackTrace();
			            }
			            if((val >= 0) && (val <= 9))
				    {
			            	if(colNo <= 9){
						vals.add(val);
						colNo++;
					}
				    }
		            }
		            lineNo++;
		        }
		        sc.close();
			}
		
		return vals;
	}
	
	public static void main(String args[])
	{
		System.out.println("Hi");
		
		Sudoku sudo = new Sudoku();
		ArrayList<Integer> vals = SudokuSolver.getVals();
			
		
		boolean status = sudo.validate(vals);
		
		if(status == false) {
			System.out.println("Not Valid Input");
			return;
		}
		
		
		sudo.populateGrid(vals);
		
		status = sudo.solve();
		
		
		if(status == true)
		{
			System.out.println("Done");
			sudo.displaySolution();
		}
		else
			System.out.println("Not Done");
	}
}
