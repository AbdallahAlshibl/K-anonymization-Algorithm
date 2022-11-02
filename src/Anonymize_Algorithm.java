import java.io.*;
import java.util.*;

public class Anonymize_Algorithm {
	public static int k;
	public static int count;
	private int n;
	private int attribute;
	private File file;
	private ArrayList<String> attributesList;

	// class constructor.
	public Anonymize_Algorithm(int k,int attribute, File file) {
		super();
		this.k = k;
		this.attribute = attribute;
		this.file = file;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int getAttribute() {
		return attribute;
	}

	// method to count how many attributes in the table ...
	// assumed the attributes are in the first line and not integer ...
	public void setAttribute() {
		try {
			Scanner scanner;
			scanner = new Scanner(new BufferedReader(new FileReader(file)));
			int i = 0;
			while (!scanner.hasNextInt()) {
				i++;
			}
			scanner.close();
			this.attribute = i;

		} catch (FileNotFoundException e) {
			System.out.println("File is not founded, please check again ...");
		}
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	// method to get an ArrayList that has the data attributes.
	public ArrayList<String> getAttributeList() {
		return this.attributesList;
	}

	public void setAttributesList() {
		ArrayList<String> attributesList = new ArrayList<String>();
		try {
			Scanner scanner;
			scanner = new Scanner(new BufferedReader(new FileReader(file)));
			int i = 0;
			while (i < attribute) {
				attributesList.add(scanner.next());
				i++;
			}
			scanner.close();
			this.attributesList = new ArrayList<String>(attributesList);

		} catch (FileNotFoundException e) {
			System.out.println("File is not founded, please check again ...");
		}
		this.attributesList = new ArrayList<String>(attributesList);
	}

	@Override
	public String toString() {
		return "Anonymize_Algorithm [k=" + k + "]";
	}

	// method to create a multidimensional ArrayList contains table's data.
	public ArrayList<ArrayList<String>> getDataList() {
		ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
		try {
			Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)));
			scanner.nextLine();
			int i = 0;
			int row = 0;
			while (scanner.hasNextLine()) {
				dataList.add(new ArrayList<String>());
				for (int x = 0; x < attribute; x++) {
					dataList.get(row).add(scanner.next());
				}
				row++;
				i++;
			}
			setN(i);
			scanner.close();
			return dataList;
		} catch (FileNotFoundException e) {
			System.out.println("File is not founded, please check again ...");
		}
		return dataList;
	}

	// method to sort the arrayList based on the given column ...
	public static ArrayList<ArrayList<String>> sortDataList(ArrayList<ArrayList<String>> List, int columnNumber) {
		int column = columnNumber; // age column = 2 --- salary column = 7 ...
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>(List);
		// Convert data into an object array
		Object[] temp = data.toArray();

		Arrays.sort(temp, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				// Get inner arrays to be compared by type-casting
				ArrayList<String> temp1 = (ArrayList<String>) o1;
				ArrayList<String> temp2 = (ArrayList<String>) o2;

				// Then compare those inner arrays' indexes
				return temp1.get(column).compareTo(temp2.get(column));
			}
		});

		// Clear the old one and add the ordered list into
		data.clear();

		for (Object o : temp) {
			data.add((ArrayList<String>) o);
		}
		return data;
	}

	public ArrayList<ArrayList<String>> getMissedrecord(ArrayList<ArrayList<String>> output, int column) {
		ArrayList<ArrayList<String>> finalList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>(sortDataList(getDataList(), column));
		for (int x = 0; x < output.size(); x++) {
			int z = Integer.parseInt(output.get(x).get(0));
			for (int y = 0; y < temp.size(); y++) {
				if (Integer.parseInt(temp.get(y).get(0)) == z) {
					temp.remove(y);
				}
			}
		}
		if (temp.isEmpty()) {
			return finalList;
		}
		checkMissed(temp, finalList, column);
		return finalList;
	}

	// recursive helper method for createAnonymizedTable method based on age ...
	public static void checkMissed(ArrayList<ArrayList<String>> input, ArrayList<ArrayList<String>> output,
			int column) {

		ArrayList<ArrayList<String>> tempList = new ArrayList<ArrayList<String>>(input);
		ArrayList<ArrayList<String>> LHS = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> temp2 = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> RHS = new ArrayList<ArrayList<String>>();
		ArrayList<Integer> ageFrequency = new ArrayList<Integer>();

		int ageMedian;

		// get arrayList contains age frequency ...
		for (int x = 0; x < input.size(); x++) {
			ageFrequency.add(Integer.parseInt(input.get(x).get(column)));
		}
//			System.out.println(ageFrequency);

		// calculate the median for even or odd records ...
		if (ageFrequency.size() % 2 == 0) {
			ageMedian = ageFrequency.get((ageFrequency.size() / 2) - 1);
			// System.out.println(ageMedian);
		} else {
			ageMedian = ageFrequency.get(((ageFrequency.size() + 1) / 2) - 1);
		}

		int x = 0;

		// divide and get LHS table ...
		for (x = 0; ageMedian >= Integer.parseInt(input.get(x).get(column)) && x < input.size() - 1; x++) {

			LHS.add(input.get(x));
		}

		// divide and get RHS table ...
		while (x < input.size()) {
			RHS.add(input.get(x));
			x++;
		}

		// compare each side with value of K to check no allowable cut case (based on
		// AGE) ...
		if (LHS.size() < k && RHS.size() < k) {
			tempList = generalize(tempList, column);
			output.addAll(tempList);
			count++;
		} else {
			if (LHS.size() >= k && RHS.size() < k) {
				checkMissed(LHS, output, column);
			} else {
				if (LHS.size() < k && RHS.size() >= k) {
					checkMissed(RHS, output, column);
				} else {
					if (LHS.size() >= k && RHS.size() >= k) {
						checkMissed(LHS, output, column);
						checkMissed(RHS, output, column);
					} else {

					}
				}

			}
		}

	}

	// recursive method to create the anonymized table then write it back to a new
	// file ...
	public void createAnonymizedTable() {
		ArrayList<ArrayList<String>> finalList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> sortData = new ArrayList<ArrayList<String>>(sortDataList(getDataList(), 2));

		createAnonymizedTable(sortData, finalList);

		setAttributesList();

		finalList.addAll(getMissedrecord(finalList, 3));
		finalList.addAll(getMissedrecord(finalList, 4));
		finalList.addAll(getMissedrecord(finalList, 5));

		finalList.addAll(getMissedrecord(finalList, 6));
		finalList.addAll(getMissedrecord(finalList, 7));
		finalList.addAll(getMissedrecord(finalList, 1));

		createFile(finalList);
	}

	// recursive helper method for createAnonymizedTable method based on age ...
	public static void createAnonymizedTable(ArrayList<ArrayList<String>> input, ArrayList<ArrayList<String>> output) {

		ArrayList<ArrayList<String>> tempList = new ArrayList<ArrayList<String>>(input);
		ArrayList<ArrayList<String>> LHS = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> temp2 = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> RHS = new ArrayList<ArrayList<String>>();
		ArrayList<Integer> ageFrequency = new ArrayList<Integer>();

		int ageMedian;

		// get arrayList contains age frequency ...
		for (int x = 0; x < input.size(); x++) {
			ageFrequency.add(Integer.parseInt(input.get(x).get(2)));
		}
//		System.out.println(ageFrequency);

		// calculate the median for even or odd records ...
		if (ageFrequency.size() % 2 == 0) {
			ageMedian = ageFrequency.get((ageFrequency.size() / 2) - 1);
		} else {
			ageMedian = ageFrequency.get(((ageFrequency.size() + 1) / 2) - 1);
		}

		int x = 0;

		// divide and get LHS table ...
		for (x = 0; ageMedian >= Integer.parseInt(input.get(x).get(2)) && x < input.size() - 1; x++) {

			LHS.add(input.get(x));
		}

		// divide and get RHS table ...
		while (x < input.size()) {
			RHS.add(input.get(x));
			x++;
		}

		// compare each side with value of K to check no allowable cut case (based on
		// AGE) ...
		if (LHS.size() < k && RHS.size() < k) {
			tempList = generalize(tempList, 2);
			output.addAll(tempList);
			count++;
		} else {
			if (LHS.size() >= k && RHS.size() < k) {
				createAnonymizedTable(LHS, output);
			} else {
				if (LHS.size() < k && RHS.size() >= k) {
					createAnonymizedTable(RHS, output);
				} else {
					if (LHS.size() >= k && RHS.size() >= k) {
						createAnonymizedTable(LHS, output);
						createAnonymizedTable(RHS, output);
					} else {

					}
				}

			}
		}

	}

	// method to generalize cells before adding to the arrayList based on given
	// column number ...
	public static ArrayList<ArrayList<String>> generalize(ArrayList<ArrayList<String>> List, int columnNumber) {
		List = sortDataList(List, columnNumber);
		String min = List.get(0).get(columnNumber);
		String max = List.get(List.size() - 1).get(columnNumber);
		String str = "[" + min + "-" + max + "]";
		for (int x = 0; x < List.size(); x++) {
			List.get(x).set(columnNumber, str);
		}
		return List;
	}

	// method to create an output file then write in the file ...
	public void createFile(ArrayList<ArrayList<String>> List) {
		try {
			File outputFile = new File("Task2-output-k=" + k + ".txt");
			FileWriter myWriter = new FileWriter(outputFile);
			myWriter.write(getAttributeList().toString() + "\n");
			int x = 0;
			while (x < List.size()) {
				myWriter.write(List.get(x).toString() + "\n");
				x++;
			}
			myWriter.close();
			if (k == 15) {
				System.out.println("output file is created ... number of equivalent classes = " + --count);
			} else {
				System.out.println("output file is created ... number of equivalent classes = " + count);
			}
		} catch (IOException e) {
			System.out.println("An error occurred when created the file.");
			e.printStackTrace();
		}
	}

}
