

package RBFS;

import java.io.File;

public class Main {

	public static void main(String[] args) {

		// Создаем RecursiveBestFirstSearch object
		RecursiveBestFirstSearch rbfs = new RecursiveBestFirstSearch();

		// Инициализируем головоломку
		char[][] puzzle = {{'2', '8', '1'}, {'3', '6', '4'}, {'7', '0', '5'}};
		//char[][] puzzle = {{'2','5','3'}, {'1','6','0'}, {'7','8','4'}};

//		// Allows args[0] to be used if it contains a test file
//		if (args.length == 1)
//			puzzle = rbfs.createPuzzle(new File(args[0]));

		// Вызываем метод поиска
		rbfs.search(puzzle);
	}
}
