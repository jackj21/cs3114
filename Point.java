// On my honor:
//
// - I have not discussed the Java language code in my program with
// anyone other than my instructor or the teaching assistants
// assigned to this course.
//
// - I have not used Java language code obtained from another student,
// or any other unauthorized source, including the Internet, either
// modified or unmodified.
//
// - If any Java language code or documentation used in my program
// was obtained from another source, such as a text book or course
// notes, that has been clearly noted with a proper citation in
// the comments of my program.
//
// - I have not designed this program in such a way as to defeat or
// interfere with the normal operation of the grading code.
//
// Jack Jiang
// jackj21
package cs3114.J2.Client;
import cs3114.J2.DS.Compare2D;
import cs3114.J2.DS.Direction;


public class Point implements Compare2D<Point> {

	private long xcoord;
	private long ycoord;
	
	public Point() {
		xcoord = 0;
		ycoord = 0;
	}
	public Point(long x, long y) {
		xcoord = x;
		ycoord = y;
	}
	
	// For the following methods, let P designate the Point object on which
	// the method is invoked (e.g., P.getX()).

    // Reporter methods for the coordinates of P.
	public long getX() {
		return xcoord;
	}
	public long getY() {
		return ycoord;
	}
	
	// Determines which quadrant of the region centered at P the point (X, Y),
	// consistent with the relevent diagram in the project specification;
	// returns NODQUADRANT if P and (X, Y) are the same point.
	public Direction directionFrom(long X, long Y) {
		long x = this.getX();
		long y = this.getY();
		long xDiff = x - X;
		long yDiff = y - Y;
		if (xDiff == 0)
			return Direction.NOQUADRANT;
		double deg = Math.toDegrees(Math.atan(yDiff/xDiff));
		
		if (x < X && y < Y) {
			if (deg < 0)
				deg += 270;
			else
				deg += 180;
		}
		if (x < X && y > Y) {
			if (deg < 0)
				deg += 180;
			else
				deg += 90;
		}
		if (x > X && y < Y) {
			if (deg<0)
				deg += 360;
			else
				deg += 270;
		}
		
			
		
		if (deg >= 0 && deg < 90)
			return Direction.NE;
		else if (deg >= 90 && deg < 180)
			return Direction.NW;
		else if (deg >= 180 && deg < 270)
			return Direction.SW;
		else if (deg >= 270 && deg < 360)
			return Direction.SE;
		else
			return Direction.NOQUADRANT;        // actually not reachable
	}
	
	// Determines which quadrant of the specified region P lies in,
	// consistent with the relevent diagram in the project specification;
	// returns NOQUADRANT if P does not lie in the region. 
	public Direction inQuadrant(double xLo, double xHi, double yLo, double yHi) {
		double xAvg = (xLo + xHi) / 2;
		double yAvg = (yLo + yHi) / 2;
		if (this.inBox(xLo, xHi, yLo, yHi))
			if ((this.getY() >= yAvg && this.getX() > xAvg) || 
				this.getX() == 0 && this.getY() == 0)
				return Direction.NE;
			else if (this.getY() < yAvg && this.getX() >= xAvg)
				return Direction.SE;
			else if (this.getY() > yAvg && this.getX() <= xAvg)
				return Direction.NW;
			else
				return Direction.SW;
				
		
        return Direction.NOQUADRANT;
	}
	
	// Returns true iff P lies in the specified region.
	public boolean inBox(double xLo, double xHi, double yLo, double yHi) {
        if (this.getX() >= xLo && this.getX() <= xHi && this.getY() >= yLo && this.getY() <= yHi)
        	return true;
		return false;
	}
	
    // Returns a String representation of P.
	public String toString() {
		
		return new String("(" + xcoord + ", " + ycoord + ")");
	}
	
	// Returns true iff P and o specify the same point.
	public boolean equals(Object o) {
		Point temp = (Point) o;
		if (this.directionFrom(temp.getX(), temp.getY()) != Direction.NOQUADRANT) 
			return false;
		if (o == null)
			return false;
		if (!this.getClass().equals(o.getClass()) )
			return false;
		Point handle = (Point) o;
		if (this != handle)
			return false;
		return true;
	}
}
