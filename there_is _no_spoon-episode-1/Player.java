import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Don't let the machines win. You are humanity's last hope...
 **/
class Player {

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int width = in.nextInt(); // the number of cells on the X axis
		int height = in.nextInt(); // the number of cells on the Y axis
		List<String> lines = new ArrayList<>();
		if (in.hasNextLine()) {
			in.nextLine();
		}
		for (int i = 0; i < height; i++) {
			String line = in.nextLine(); // width characters, each either 0 or .

			lines.add(line);

		}

		for (int i = 0; i < height; i++) {

			String line = lines.get(i);
			System.err.println(line);

			for (int j = 0; j < width; j++) {

				StringBuilder out = new StringBuilder();

				if(String.valueOf(lines.get(i).charAt(j)).equals(Cell.EMPTY)){
					continue;
				}

				out.append(MessageFormat.format("{0} {1}", j, i));

				int iterator = j;
				String cellValue = Cell.EMPTY;
				Cell rightCell = new Cell(Cell.EMPTY, 0, 0);
				while (iterator < width && cellValue.equals(Cell.EMPTY)) {
					iterator++;
					try {
						cellValue = String.valueOf(lines.get(i).charAt(iterator));
					} catch (RuntimeException e) {
						cellValue = Cell.EMPTY;
					}
					System.err.println(cellValue);

				}

				rightCell = new Cell(
					cellValue,
					i,
					iterator
				);
				out.append(" ").append(rightCell.getCoordinates());

				iterator = i;
				cellValue = Cell.EMPTY;
				Cell bellowCell = new Cell(Cell.EMPTY, 0, 0);
				while (iterator < height && cellValue.equals(Cell.EMPTY)) {
					iterator++;
					try {
						cellValue = String.valueOf(lines.get(iterator).charAt(j));
					} catch (RuntimeException e) {
						cellValue = Cell.EMPTY;
					}
					System.err.println(cellValue);
				}

				bellowCell = new Cell(
					cellValue,
					iterator,
					j
				);

				out.append(" ").append(bellowCell.getCoordinates());

				System.out.println(out);
			}

		}

		// Write an action using System.out.println()
		// To debug: System.err.println("Debug messages...");

		// Three coordinates: a node, its right neighbor, its bottom neighbor
		//		System.out.println("0 0 1 0 0 1");

		//		System.out.println(out);
	}

}

class Cell {
	public static final String EMPTY = ".";
	public static final String NODE = "0";
	private static final String UNKNOWN = "-1 -1";

	private String cell;
	private int x;
	private int y;

	public Cell(String cell, int x, int y) {
		this.cell = cell;
		this.x = x;
		this.y = y;
	}

	Boolean isNode() {
		return this.cell.equals(NODE);
	}

	String getCoordinates() {
		if (this.isNode()) {
			return MessageFormat.format("{0} {1}", y, x);
		} else {
			return MessageFormat.format("{0}", UNKNOWN);
		}
	}
}
