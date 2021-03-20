
package RBFS;

public class Node {

	// Объявление родительских и дочерних узлов
	public Node down, left, top, right, parent;

	// Объявление головоломки
	public char[][] puzzle;

	// Координаты пустой плитки и обьявление стоимости
	public int x, y, cost;

	// Конструктор узла
	public Node(char[][] puzzle, int x, int y, int newX, int newY, Node parent) {

		// клонируем родительскую головоломку в детскую
		this.puzzle = new char[puzzle.length][];
		for (int i = 0; i < puzzle.length; i++)
			this.puzzle[i] = puzzle[i].clone();

		// Для каждого нового узла устанавливаем для родительского элемента значение parent, а для дочерних - значение null.
		this.parent = parent;
		this.down = null;
		this.left = null;
		this.top = null;
		this.right = null;

		// Поменять местами плитки
		this.puzzle[x][y] = this.puzzle[newX][newY];
		this.puzzle[newX][newY] = '0';

		// Устанавливаем максимальную стоимость и получаем новые координаты x и  для пустой плитки.
		this.cost = Integer.MAX_VALUE;
		this.x = newX;
		this.y = newY;
	}
}
