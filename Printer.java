import lejos.nxt.*;
import lejos.util.Delay;
/**
 * A program for the Lego Label Printer.
 * 
 * Author: John Coronite
 * Date: September, 21 2017
 * 
 *
 */
public class Printer { 
	// USER ENTERS OUTPUT IN printerOutput VARIABLE - USE LETTERS, NUMBERS, AND SPACES ONLY
	private static String printerOutput = "HELLO";
	//private static String printerOutput = "abcdefghijklmnopqrstuvwxyz 0123456789";
	private static int pagePosition = 0;
	private static SensorPort s1 = SensorPort.S1;
	private static TouchSensor ts = new TouchSensor(s1);
	public static SensorPort s3 = SensorPort.S3;
	public static ColorSensor cs = new ColorSensor(s3);
	public static NXTMotor motorA = new NXTMotor(MotorPort.A);
	public static NXTMotor motorB = new NXTMotor(MotorPort.B);
	public static NXTMotor motorC = new NXTMotor(MotorPort.C);
	
	/*
	 * Lifts the pen.
	 */
	public static void liftPen() {
		motorC.setPower(20);
		motorC.forward();
		Delay.msDelay(900);
		motorC.flt();
	}
	
	/*
	 * Lowers the pen.
	 */
	public static void lowerPen(){
		motorC.setPower(20);
		motorC.backward();
		Delay.msDelay(400);
		motorC.flt();
		motorC.forward();
		Delay.msDelay(30);
		motorC.flt();
	}
	
	/*
	 * Checks the value of the color sensor.
	 * @return - Returns true if the color sensor is reading WHITE.
	 */
	public static boolean checkColorSensor(){
		return cs.getColorID() != 6;
	}
	
	/*
	 * Loads the paper into the printer.
	 */
	public static void resetVertical(){
		motorA.setPower(30);
		cs.setFloodlight(true);
		while(checkColorSensor()){
			motorA.forward();
		}
		motorA.stop();
		Sound.beep();
	}
	
	/*
	 * Left-aligns the pen until the sensor is pressed.
	 */
	public static void resetHorizontal(){
		while(!ts.isPressed()){
			motorB.setPower(80);
			motorB.backward();
		}
		Sound.beep();
		motorB.stop();
		motorB.setPower(20);
		motorB.forward();
		Delay.msDelay(800);
		motorB.stop();
		Motor.B.resetTachoCount();
	}

	/*
	 * Performs a carriage return.
	 */
	public static void carriageReturn(){
		Motor.A.resetTachoCount();
		Motor.A.rotate(75);
		resetHorizontal();
		pagePosition = 0;
	}
	
	/*
	 * Moves the pen either after a letter, or to put the pen at the right spot to begin a letter
	 * @param offsetX - the amount to rotate horizontally
	 * @param offsetY - the amount to rotate vertically
	 * @param addSpace - whether to add space after a letter (false if putting the pen
	 * in the right spot to begin a letter.
	 */
	public static void movePen(int offsetX, int offsetY, boolean addSpace) {
		// Motor A UP DOWN / Y
		int spaceBetween;
		if (addSpace) {
			spaceBetween = 30;
		} else {
			spaceBetween = 0;
		}
		
		if (offsetY != 0) {
			Motor.A.resetTachoCount();
	    	Motor.A.rotate(offsetY);
	    	Motor.A.stop();
		}
		Motor.B.resetTachoCount();
    	Motor.B.rotate(offsetX + spaceBetween);
    	Motor.B.stop();
		
	}
	// 0 1 2 3 4
	// * * * * * 0
	// * * * * * 1
	// * * * * * 2
	// * * * * * 3
	// * * * * * 4
	
	/*
	 * Writes a digit (an int) on a 5x5 grid (above)
	 * @param digit - the digit to write
	 */
	public static void writeDigit(int digit) {
		int xStep = 30;
		int yStep = 15;
		int widthX;
		// The starting x and y values on the "grid" for each character
		int startX = 0;
		int startY = 0;
		int posX = 0;
		int posY = 0;
		String sequence;
		switch (digit) {
		case 1:
			sequence = "DDDD";
			widthX = 0;
			startX = 0;
			startY = 0;
			break;
		case 2:
			sequence = "RRRRDDLLLLDDRRRR";
			widthX = 4;
			startX = 0;
			startY = 0;
			break;
		case 3:
			sequence = "RRRRDDLLLRRRDDLLLL";
			widthX = 4;
			startX = 0;
			startY = 0;
			break;
		case 4:
			sequence = "DDRRRRLLUUDDDD";
			widthX = 4;
			startX = 0;
			startY = 0;
			break;
		case 5:
			sequence = "LLLLDDRRRRDDLLLL";
			widthX = 4;
			startX = 4;
			posX = 4;
			startY = 0;
			break;
		case 6:
			sequence = "LLLLDDDDRRRRUULLLL";
			widthX = 4;
			startX = 4;
			posX = 4;
			startY = 0;
			break;
		case 7:
			sequence = "RRRRDDDD";
			widthX = 4;
			startX = 0;
			startY = 0;
			break;
		case 8:
			sequence = "RRRRDDLLLLUUDDDDRRRRUUUU";
			widthX = 4;
			startX = 0;
			startY = 0;
			break;
		case 9:
			sequence = "RRRRUUUULLLLDDRRRR";
			widthX = 4;
			startX = 0;
			startY = 4;
			posY = 4;
			break;
		case 0:
			sequence = "RRRRDDDDLLLLUUUU";
			widthX = 4;
			startX = 0;
			startY = 0;
			break;
		default:
			sequence = "RRRR";
			widthX = 4;
			startX = 0;
			startY = 0;
			break;
		}
		// A running count of pagePosition is kept
		// if it is > 6, do a carriage return.
		if(pagePosition > 6) {
			carriageReturn();
		}
		
		// if startX and startY are not both 0, set the pen at the right spot
		if (startX != 0 || startY != 0) {
			movePen(startX * xStep, startY * yStep, false);
		}

		// DRAW CHARACTER
		lowerPen();
		for (int i = 0; i < sequence.length(); i++) {
			char direction = sequence.charAt(i);
			if (direction == 'L') {
				posX--;
				Motor.B.resetTachoCount();
				Motor.B.rotate(-1 * xStep);

			} else if (direction == 'R') {
				posX++;
				Motor.B.resetTachoCount();
				Motor.B.rotate(xStep);

			} else if (direction == 'U') {
				posY--;
				Motor.A.resetTachoCount();
				Motor.A.rotate(-1 * yStep);

			} else if (direction == 'D') {
				posY++;
				Motor.A.resetTachoCount();
				Motor.A.rotate(yStep);
			}

		}
		liftPen();
		pagePosition++;
		// Letter width - pen posX
		// 1: 0 - 0
		// 2: 4 - 4
		// 3: 4 - 0
		// 4: 4 - 2
		// 5: 4 - 0
		// 6:
		int lwmpx = widthX - posX;
		movePen(lwmpx * xStep, -1 * posY * yStep, true);
	}

	// 0 1 2 3 4x
	// * * * * * 0
	// * * * * * 1
	// * * * * * 2
	// * * * * * 3
	// * * * * * 4y
	
	// DIAGONAL (DOWN AND FORWARD): BACKSLASH \ = B
	// DIAGONAL (DOWN AND BACK): FORWARDSLASH / = F
	// DIAGONAL (UP AND FORWARD): = P
	// DIAGONAL (UP AND BACK): = C
	
	/*
	 * Writes a letter (a char) on a 5x5 grid
	 * @param letter - the letter as a char
	 */
	public static void writeLetter(char letter) {
		int xStep = 30;
		int yStep = 15;
		int widthX;
		// The starting x and y values on the "grid" for each character
		int startX = 0;
		int startY = 0;
		int lPosX = 0;
		int lPosY = 0;
		String sequence;
		switch (letter) {
		case 'A':
			sequence = "UUUURRRRDDDDUULLLL";
			widthX = 4;
			startX = 0;
			startY = 4;
			lPosY = 4;
			break;
		case 'B':
			sequence = "RDDDDLRRRRUULLLRRRUULLL";
			widthX = 4;
			startX = 0;
			startY = 0;
			break;
		case 'C':
			sequence = "LLLLDDDDRRRR";
			widthX = 4;
			startX = 4;
			startY = 0;
			lPosX = 4;
			break;
		case 'D':
			sequence = "RRRBDDFLLLUUUU";
			widthX = 4;
			startX = 0;
			startY = 0;
			lPosX = 0;
			break;
		case 'E':
			sequence = "RRRRLLLLDDDDRRRRLLLLUURRRR";
			widthX = 4;
			startX = 0;
			startY = 0;
			break;
		case 'F':
			sequence = "LLLLDDDDUURRRR";
			widthX = 4;
			startX = 4;
			startY = 0;
			lPosX = 4;
			break;
		case 'G':
			sequence = "LLLLDDDDRRRRUULL";
			widthX = 4;
			startX = 4;
			startY = 0;
			lPosX = 4;
			break;
		case 'H':
			sequence = "DDDDUURRRRUUDDDD";
			widthX = 4;
			startX = 0;
			startY = 0;
			lPosX = 0;
			break;
		case 'I':
			sequence = "RRRRLLDDDDLLRRRR";
			widthX = 4;
			startX = 0;
			startY = 0;
			lPosX = 0;
			break;
		case 'J':
			sequence = "RRRRLLDDDDLL";
			widthX = 4;
			startX = 0;
			startY = 0;
			lPosX = 0;
			break;
		case 'K':
			sequence = "DDDDUUPPFFBB";
			widthX = 2;
			startX = 0;
			startY = 0;
			lPosX = 0;
			break;
		case 'L':
			sequence = "DDDDRRRR";
			widthX = 4;
			startX = 0;
			startY = 0;
			lPosX = 0;
			break;
		case 'M':
			sequence = "UUUURRDDUURRDDDD";
			widthX = 4;
			startX = 0;
			startY = 4;
			lPosX = 0;
			lPosY = 4;
			break;
		case 'N':
			sequence = "UUUUBBBBUUUU";
			widthX = 4;
			startX = 0;
			startY = 4;
			lPosX = 0;
			lPosY = 4;
			break;
		case 'O':
			sequence = "DDDDRRRRUUUULLLL";
			widthX = 4;
			startX = 0;
			startY = 0;
			lPosX = 0;
			break;
		case 'P':
			sequence = "UUUURRRRDDLLLL";
			widthX = 4;
			startX = 0;
			startY = 4;
			lPosX = 0;
			lPosY = 4;
			break;
		case 'Q':
			sequence = "LLLUUURRRDDDB";
			widthX = 4;
			startX = 3;
			startY = 3;
			lPosX = 3;
			lPosY = 3;
			break;
		case 'R':
			sequence = "UUUURRRRDDLLLLRRRRDD";
			widthX = 4;
			startX = 0;
			startY = 4;
			lPosX = 0;
			lPosY = 4;
			break;
		case 'S':
			sequence = "LLLLDDRRRRDDLLLL";
			widthX = 4;
			startX = 4;
			startY = 0;
			lPosX = 4;
			lPosY = 0;
			break;
		case 'T':
			sequence = "RRRRLLDDDD";
			widthX = 4;
			startX = 0;
			startY = 0;
			lPosX = 0;
			break;
		case 'U':
			sequence = "DDDDRRRRUUUU";
			widthX = 4;
			startX = 0;
			startY = 0;
			lPosX = 0;
			break;
		case 'V':
			sequence = "DDBBPPUU";
			widthX = 4;
			startX = 0;
			startY = 0;
			lPosX = 0;
			break;
		case 'W':
			sequence = "DDDDRRUUDDRRUUUU";
			widthX = 4;
			startX = 0;
			startY = 0;
			lPosX = 0;
			break;
		case 'X':
			sequence = "BBBBCCFFPPPP";
			widthX = 4;
			startX = 0;
			startY = 0;
			lPosX = 0;
			break;
		case 'Y':
			sequence = "BBDDUUPP";
			widthX = 4;
			startX = 0;
			startY = 0;
			lPosX = 0;
			break;
		case 'Z':
			sequence = "RRRRFFFFRRRR";
			widthX = 4;
			startX = 0;
			startY = 0;
			lPosX = 0;
			break;
		default:
			sequence = "RRRR";
			widthX = 4;
			startX = 0;
			startY = 0;
			break;
		}
		// A running count of pagePosition is kept
		// if it is > 6, do a carriage return.
		if (pagePosition > 6) {
			carriageReturn();
		}
		// if startX and startY are not both 0, set the pen at the right spot
		if (startX != 0 || startY != 0) {
			movePen(startX * xStep, startY * yStep, false);
		}

		// DRAW CHARACTER
		if (letter != ' ') {
			lowerPen();
		}

		for (int i = 0; i < sequence.length(); i++) {
			char direction = sequence.charAt(i);
			if (direction == 'L') {
				lPosX--;
				Motor.B.resetTachoCount();
				Motor.B.rotate(-1 * xStep);

			} else if (direction == 'R') {
				lPosX++;
				Motor.B.resetTachoCount();
				Motor.B.rotate(xStep);

			} else if (direction == 'U') {
				lPosY--;
				Motor.A.resetTachoCount();
				Motor.A.rotate(-1 * yStep);

			} else if (direction == 'D') {
				lPosY++;
				Motor.A.resetTachoCount();
				Motor.A.rotate(yStep);

			} else if (direction == 'B') {
				lPosY++;
				lPosX++;
				Motor.A.resetTachoCount();
				Motor.B.resetTachoCount();
				Motor.A.rotate(yStep, true);
				Motor.B.rotate(xStep, true);
				
			} else if (direction == 'P') {
				lPosY--;
				lPosX++;
				Motor.A.resetTachoCount();
				Motor.B.resetTachoCount();
				Motor.A.rotate(-1 * yStep, true);
				Motor.B.rotate(xStep, true);
				
			} else if (direction == 'F') {
				lPosY++;
				lPosX--;
				Motor.A.resetTachoCount();
				Motor.B.resetTachoCount();
				Motor.A.rotate(yStep, true);
				Motor.B.rotate(-1 * xStep, true);
				
			} else if (direction == 'C') {
				lPosY--;
				lPosX--;
				Motor.A.resetTachoCount();
				Motor.B.resetTachoCount();
				Motor.A.rotate(-1 * yStep, true);
				Motor.B.rotate(-1 * xStep, true);
			}
		}
		liftPen();
		pagePosition++;
		// Letter width - pen posX
		// A:
		int lwmpx = widthX - lPosX;
		movePen(lwmpx * xStep, -1 * lPosY * yStep, true);
	}
	
	public static void stringReader(String input) {
		String cleanInput = input.toUpperCase().trim();
		char[] array = cleanInput.toCharArray();
		char c;

		for (int i = 0; i < array.length; i++) {
			c = array[i];
			if (c == 'A' || c == 'B' || c == 'C' || c == 'D' || c == 'E' || c == 'F' || c == 'G' || c == 'H' || c == 'I'
					|| c == 'J' || c == 'K' || c == 'L' || c == 'M' || c == 'N' || c == 'O' || c == 'P' || c == 'Q'
					|| c == 'R' || c == 'S' || c == 'T' || c == 'U' || c == 'V' || c == 'W' || c == 'X' || c == 'Y'
					|| c == 'Z' || c == ' '
			) {
				writeLetter(c);
			} else if (c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '7' || c == '8' || c == '9'
					|| c == '0') {
				writeDigit(Character.digit(c, 10));
			}
		}
	}

	
	public static void main (String[] args) {
		
		liftPen();
		resetHorizontal();
		resetVertical();
		stringReader(printerOutput);
	}
}
