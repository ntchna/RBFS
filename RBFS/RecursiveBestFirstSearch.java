package RBFS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecursiveBestFirstSearch {

	// Инициализируем предел итерации, чтобы предотвратить переполнение стека
	int iterationLimit = 0;

	// Static final Declarations
	private static final int dimension = 3;
	private static final Double INFINITY = Double.POSITIVE_INFINITY;

	// Снизу, слева, сверху, справа
	// Эти массивы помогут поменять местами значения дочерних головоломок
	int[] row = { 1, 0, -1, 0 };
	int[] col = { 0, -1, 0, 1 };

	// Решение головоломки ( цель)
	char[][] goal = { {'1','2','3'}, {'4','0','5'}, {'6','7','8'} };
	//char[][] goal = { {'0','1','2'}, {'3','4','5'}, {'6','7','8'} };

	// FileInputStream declaration
	//private FileInputStream fis;

	/*** PUBLIC METHODS ***/

	/* search
	 *
	 * Returns void.
	 * Принимает исходную головоломку, находит координаты пустой ячейки
	 * Создает узел с координатами путой ячейки, вызывает rbfs, печатает путь
	 * и успешен ли алгоритм
	 */
	public void search(char[][] initPuzzle) {

		int[] coordinates = getBlankSpaceCoordinates(initPuzzle);

		Node p = new Node(initPuzzle, coordinates[0], coordinates[1], coordinates[0], coordinates[1], null);
		Node n = p;

		SearchResult sr = rbfs(p, n, (double) p.cost, INFINITY);

		printPath(sr.getSolution());

		if (sr.getOutcome() == SearchResult.SearchOutcome.SOLUTION_FOUND)
			System.out.println("Solution Found");
		else
			System.out.println("RBFS Failure");

	}


	/*** PRIVATE METHODS ***/

	// rbfs

	private SearchResult rbfs(Node p, Node c, Double fNode, Double fLimit) {

		// Этот предел увеличивается до тех пор, пока не будет достигнут
		iterationLimit++;

		if (c.cost == 0) return new SearchResult(c, fLimit);

		List<Node> successors = expandNode(c);

		double[] f = new double[successors.size()];

		if (successors.size() == 0) return new SearchResult(null, INFINITY);

		for (int s = 0; s < successors.size(); s++) {
			// f[s] <- max(g(s) + h(s), f[node])
			if (successors.get(s).cost < fNode)
				f[s] = Math.min(successors.get(s).cost, fNode);
			else
				f[s] = Math.max(successors.get(s).cost, fNode);
		}

		// RBFS Должен успешно завершится до переполнения стека. Ограничено до 1000 итераций
		while (iterationLimit < 1000) {

			// best <- the lowest f-value node in successors
			int bestIndex = getBestFValueIndex(f);
			// if f[best] > f_limit then return failure, f[best]
			if (f[bestIndex] > fLimit)
				return new SearchResult(null, f[bestIndex]);

			// alternative <- the second-lowest f-value among successors
			int altIndex = getNextBestFValueIndex(f, bestIndex);
			// result, f[best] <- RBFS(problem, best, min(f_limit, alternative))
			SearchResult sr = rbfs(p, successors.get(bestIndex), f[bestIndex], Math.min(fLimit, f[altIndex]));
			f[bestIndex] = sr.getFCostLimit();

			// if result <> failure then return result
			if (sr.getOutcome() == SearchResult.SearchOutcome.SOLUTION_FOUND) {
				return sr;
			}
		}

		return new SearchResult(null, INFINITY);
	}

	/* heuristic
	 *
	 * Returns an integer value and takes the initial and goal puzzles as parameters.
	 * Метод вызывает метод manDis для каждой плитки в исходной головоломке
	 * что не соответствует цели.
	 * Возвращает  эвристическое значение суммы манхэттенского расстояния каждой плитки
	 * от цели.
	 */
	private int heuristic(char[][] initPuzzle, char[][] goal) {

		int heu = 0;
		int n = initPuzzle.length;

		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				if (initPuzzle[i][j] != '0' && initPuzzle[i][j] != goal[i][j])
					heu = heu + manDis(initPuzzle, i, j);

		return heu;
	}

	/* manDis
	 *
	 * Returns an integer value and takes the initial puzzle and the mismatching tile coordinates as parameters.
	 * Возвращает либо 1, либо абсолютное значение разницы каждой координаты x и y исходной головоломки
	 * минус координаты i и j того места, где должна быть плитка.
	 * Вернет манхэттенское расстояние для плитки, которая находится не на своем месте.
	 */
	private int manDis(char[][] puzzle, int x, int y) {

		int n = puzzle.length;
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				if (puzzle[i][j] != '0' && puzzle[x][y] == goal[i][j]) 	// Once it finds where the mismatched tile should be
					return Math.abs(x - i) + Math.abs(y - j);			// Return the absolute value of the difference in coordinates

		return 1;
	}

	/* getBlankSpaceCoordinates
	 *
	 * Returns an array of integers (max size is 2).
	 * Находит и возвращает координаты x и y пустой плитки исходной головоломки.
	 */
	private int[] getBlankSpaceCoordinates(char[][] initPuzzle) {
		int[] coordinates = {0,0};
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++)
				if (initPuzzle[i][j] == '0') {
					coordinates[0] = i;
					coordinates[1] = j;
				}

		return coordinates;
	}

	/* printPath
	 *
	 * Returns void.
	 * Выводит головоломку каждого дочернего узла, который должен создать корневой узел, чтобы
	 * найти решение.
	 */
	private void printPath(Node root) {
		if (root == null) { return; }
		printPath(root.parent);
		printPuzzle(root.puzzle);
		System.out.println();
	}

	/* printPuzzle
	 *
	 * Returns void.
	 * Вывод каждой ячейки.
	 */
	private void printPuzzle(char[][] Puzzle) {
		for (int i = 0; i < Puzzle.length; i++) {
			for (int j = 0; j < Puzzle.length; j++)
				System.out.print(Puzzle[i][j] + " ");
			System.out.println();
		}
	}

	/* isSafe
	 *
	 * Returns a boolean.
	 * Индексы x и y никогда не покидают своих границ.
	 */
	private boolean isSafe(int x, int y) { return (x >= 0 && x < dimension && y >= 0 && y < dimension); }

	/* expandNode
	 *
	 * Returns a List of Nodes.
	 * Расширяет текущий узел n, создавая его дочерние элементы и добавляя его в список узлов nl.
	 * Получает эвристическое значение стоимости каждого ребенка.*/
	private List<Node> expandNode(Node n) {

		List<Node> nl = new ArrayList<Node>();

		// There is a maximum of 4 children
		for (int i = 0; i < 4; i++)
			if (isSafe(n.x + row[i], n.y + col[i])) {
				switch (i) {

					// If n has a swappable tile below
					case 0:
						n.down = new Node(n.puzzle, n.x, n.y, n.x + row[i], n.y + col[i], n);
						n.down.cost = heuristic(n.down.puzzle, goal);
						nl.add(n.down);
						break;
					// If n has a swappable tile to the left

					case 1:
						n.left = new Node(n.puzzle, n.x, n.y, n.x + row[i], n.y + col[i], n);
						n.left.cost = heuristic(n.left.puzzle, goal);
						nl.add(n.left);
						break;

					// If n has a swappable tile above
					case 2:
						n.top = new Node(n.puzzle, n.x, n.y, n.x + row[i], n.y + col[i], n);
						n.top.cost = heuristic(n.top.puzzle, goal);
						nl.add(n.top);
						break;

					// If n has a swappable tile to the right
					case 3:
						n.right = new Node(n.puzzle, n.x, n.y, n.x + row[i], n.y + col[i], n);
						n.right.cost = heuristic(n.right.puzzle, goal);
						nl.add(n.right);
						break;
				}
			}

		return nl;
	}

	/* getBestFValueIndex
	 *
	 * Returns an integer.
	 * Возвпащает индекс для наименьшего значения массива f.
	 */
	private int getBestFValueIndex(double[] f) {
		int lidx = 0;
		Double lowestSoFar = INFINITY;

		for (int i = 0; i < f.length; i++) {
			if (f[i] < lowestSoFar) {
				lowestSoFar = f[i];
				lidx = i;
			}
		}

		return lidx;
	}

	/* getNextBestFValueIndex
	 *
	 * Returns an integer.
	 * Возвпащает индекс для второго наименьшего значения массива f
	 */
	private int getNextBestFValueIndex(double[] f, int bestIndex) {
		// Массив может содержать только 1 элемент ,
		// поэтому изначально по умолчанию используется bestIndex
		int lidx = bestIndex;
		Double lowestSoFar = INFINITY;

		for (int i = 0; i < f.length; i++) {
			if (i != bestIndex && f[i] < lowestSoFar) {
				lowestSoFar = f[i];
				lidx = i;
			}
		}

		return lidx;
	}
}

/* SearchResult Class
 *
 * Упрощает использование алгоритма rbfs, поскольку дает ему уникальное возвращаемое значение, обладающее большей функциональностью.
 */
class SearchResult {
	public enum SearchOutcome {
		FAILURE, SOLUTION_FOUND
	};

	private Node solution;

	private SearchOutcome outcome;

	private final Double fCostLimit;

	public SearchResult(Node solution, Double fCostLimit) {
		if (null == solution) {
			this.outcome = SearchOutcome.FAILURE;
		} else {
			this.outcome = SearchOutcome.SOLUTION_FOUND;
			this.solution = solution;
		}
		this.fCostLimit = fCostLimit;
	}

	public SearchOutcome getOutcome() {
		return outcome;
	}

	public Node getSolution() {
		return solution;
	}

	public Double getFCostLimit() {
		return fCostLimit;
	}
}
