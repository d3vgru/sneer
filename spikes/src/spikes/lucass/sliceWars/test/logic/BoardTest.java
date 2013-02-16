package spikes.lucass.sliceWars.test.logic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Polygon;

import org.junit.Test;

import spikes.lucass.sliceWars.src.logic.BoardImpl;
import spikes.lucass.sliceWars.src.logic.BoardCell;
import spikes.lucass.sliceWars.src.logic.Player;


public class BoardTest {

	@Test
	public void addSomeCellsAndLinkThem_CheckIfOk(){
		BoardImpl subject = new BoardImpl();
		
		Polygon square1 = getSquare1();
		Polygon square2 = getSquare2();
		Polygon square3 = getSquare3();
		
		subject.createAndAddToBoardCellForPolygon(square1);
		subject.createAndAddToBoardCellForPolygon(square2);
		subject.createAndAddToBoardCellForPolygon(square3);
		
		subject.createAndAddToBoardCellForPolygon(square1);
		subject.createAndAddToBoardCellForPolygon(square2);
		subject.createAndAddToBoardCellForPolygon(square3);
		subject.link(square1,square2);
		subject.link(square2,square3);
		assertTrue(subject.areLinked(square1,square2));
		assertTrue(subject.areLinked(square2,square1));
		assertTrue(subject.areLinked(square2,square3));
		assertFalse(subject.areLinked(square1,square3));
	}
	
	@Test
	public void addSomeCellsAndSetOwners_ShouldSayBoardIsFull(){
		BoardImpl subject = new BoardImpl();
		
		Polygon square1 = getSquare1();
		Polygon square2 = getSquare2();
		Polygon square3 = getSquare3();
		
		subject.createAndAddToBoardCellForPolygon(square1);
		subject.createAndAddToBoardCellForPolygon(square2);
		subject.createAndAddToBoardCellForPolygon(square3);
		
		BoardCell cell1 = subject.getCellAtOrNull(square1.xpoints[0]+1, square1.ypoints[0]+1);
		cell1.setOwner(Player.PLAYER1);
		BoardCell cell2 = subject.getCellAtOrNull(square2.xpoints[0]+1, square2.ypoints[0]+1);
		cell2.setOwner(Player.PLAYER1);
		BoardCell cell3 = subject.getCellAtOrNull(square3.xpoints[0]+1, square3.ypoints[0]+1);
		cell3.setOwner(Player.PLAYER1);
		
		assertTrue(subject.isFilled());
	}

	private Polygon getSquare1() {
		int[] squareXPoints = new int[]{ 0,10,10, 0};
		int[] squareYPoints = new int[]{ 0, 0,10,10};
		int squareNPoints = 4;
		Polygon square = new Polygon(squareXPoints, squareYPoints, squareNPoints);
		return square;
	}
	
	private Polygon getSquare2() {
		int[] squareXPoints = new int[]{10,20,20,10};
		int[] squareYPoints = new int[]{ 0, 0,10,10};
		int squareNPoints = 4;
		Polygon square = new Polygon(squareXPoints, squareYPoints, squareNPoints);
		return square;
	}
	
	private Polygon getSquare3() {
		int[] squareXPoints = new int[]{20,30,30,20};
		int[] squareYPoints = new int[]{ 0, 0,10,10};
		int squareNPoints = 4;
		Polygon square = new Polygon(squareXPoints, squareYPoints, squareNPoints);
		return square;
	}
}