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

package cs3114.J2.DS;
import java.util.ArrayList;
import java.util.Collections;

import cs3114.J2.Client.Point;

import java.io.FileWriter;
import java.io.IOException;


public class prQuadTree< T extends Compare2D<? super T> > {
	
	abstract class prQuadNode {
		int key;
	}
	class prQuadLeaf extends prQuadNode {

		public ArrayList<T> Elements;
	}
   class prQuadInternal extends prQuadNode {
    	public prQuadNode NW, SW, SE, NE;
   }
    
   prQuadNode root;
   long xMin, xMax, yMin, yMax;
    
   // Initialize quadtree to empty state.
   public prQuadTree(long xMin, long xMax, long yMin, long yMax) {
	   this.xMin = xMin;
	   this.xMax = xMax;
	   this.yMin = yMin;
	   this.yMax = yMax;
   }
    
   // Pre:   elem != null
   // Post:  If elem lies within the tree's region, and elem is not already 
   //        present in the tree, elem has been inserted into the tree.
   // Return true iff elem is inserted into the tree. 
   public boolean insert(T elem) {
	   // Case 1: Item to be inserted DNE
	   //if (elem == null)
		//   return false;
	   
//	   if (root == null) {
//		   prQuadLeaf leaf = new prQuadLeaf();
//		   leaf.Elements = new ArrayList<T>();
//		   leaf.Elements.add(elem);
//		   root = leaf;
//		   return true;
//	   }
	  
	  
	  root = insert(root, elem, xMin, xMax, yMin, yMax);
	  if (root.key == 1)
		  return false;
	  root.key = 0;
	  return true;
	   
	   // return false for duplicates
   }
    
   
   /**
    * Private helper method for insert for cases 4-6
    * @param sRoot		where the inserting begins
    * @param elem		element to be inserted
    * @param xMin		minimum X value of insertion
    * @param xMax		maximum X value of insertion
    * @param yMin		minimum Y value of insertion
    * @param yMax		maximum Y value of insertion
    * @return		returns where root is pointing to
    */
   @SuppressWarnings("unchecked")	// suppress warnings for casting
   private prQuadNode insert(prQuadNode sRoot, T elem, long xMin, long xMax,
		   long yMin, long yMax) {
	   if (elem.inBox(xMin, xMax, yMin, yMax)) {
		   // Base case for recursion (Case 3)
		   if (sRoot == null) {
			   prQuadLeaf leaf = new prQuadLeaf();
			   leaf.Elements = new ArrayList<T>();
			   leaf.Elements.add(elem);
			   sRoot = leaf;
			   return sRoot;
			   
		   }
			   
		   // Case 4: Current node during descent is a leaf node 
		   // and bucket is not full.
		   // 4a: Coordinate is duplicate
		   if (sRoot.getClass().equals(prQuadLeaf.class)) {
			   prQuadLeaf temp = (prQuadLeaf) sRoot;
			   // For bucket size of 1...if bucket is full
			   if (temp.Elements.size() > 0) {
				   
				   //************ not working ******************
				   if (temp.Elements.contains(elem)) {// elem in ArrayList?
					   root.key = 1;
					   return sRoot;
				   }
				   else {
					  
					   return partition(sRoot, elem, xMin, xMax, yMin, yMax);
					   
				   }
			   }
			   // For bucket that is not full or empty
			   else {
				   temp.Elements.add(elem);
				   return sRoot;	// check return value for recursion...need?
			   }
		   }
		   
		   // Case 6: Current node is an internal node
		   else {
			   Direction sq = elem.inQuadrant(xMin, xMax, yMin, yMax);
			   prQuadInternal rootInt = (prQuadInternal) sRoot;
			   
			   long xAvg = (xMin + xMax) / 2;
			   long yAvg = (yMin + yMax) / 2;
			   switch(sq) {
			   case NE:
				   if (rootInt.NE == null) {
					   prQuadLeaf neLeaf = new prQuadLeaf();
					   neLeaf.Elements = new ArrayList<T>();
					   neLeaf.Elements.add(elem);
					   rootInt.NE = neLeaf;
					   break;
				   }
				   else {
					   rootInt.NE = insert(rootInt.NE, elem, xAvg, xMax, yAvg, yMax);
					   break;
				   }
			   case NW:
				   if (rootInt.NW == null) {
					   prQuadLeaf nwLeaf = new prQuadLeaf();
					   nwLeaf.Elements = new ArrayList<T>();
					   nwLeaf.Elements.add(elem);
					   rootInt.NW = nwLeaf;
					   break;
				   }
				   else {
					   rootInt.NW = insert(rootInt.NW, elem, xMin, xAvg, yAvg, yMax);
					   break;
				   }
			   case SW:
				   if (rootInt.SW == null) {
					   prQuadLeaf swLeaf = new prQuadLeaf();
					   swLeaf.Elements = new ArrayList<T>();
					   swLeaf.Elements.add(elem);
					   rootInt.SW = swLeaf;
					   break;
				   }
				   else {
					   rootInt.SW = insert(rootInt.SW, elem, xMin, xAvg, yMin, yAvg);
					   break;
				   }
			   case SE:
				   if (rootInt.SE == null) {
					   prQuadLeaf seLeaf = new prQuadLeaf();
					   seLeaf.Elements = new ArrayList<T>();
					   seLeaf.Elements.add(elem);
					   rootInt.SE = seLeaf;
					   break;
				   }
				   else {
					   rootInt.SE = insert(rootInt.SE, elem, xAvg, xMax, yMin, yAvg);
					   break;
				   }
			   case NOQUADRANT:
				   return null;	  
			   }
		   }
	   return sRoot;  
	   }
	   
	   else {
		   return null;  
	   }
	   
   }  
   
   /**
    * Helper function for insert that partitions
    * the tree and stores current and new elements
    * into appropriate leafs.
    * @param sRoot		prQuadNode where the partitioning begins, usually root
    * @param elemNew	new element to be added
    * @param xMin		minimum X value of partition
    * @param xMax		maximum X value of partition
    * @param yMin		minimum Y value of partition
    * @param yMax		maximum Y value of partition
    * @return		returns pointer to new internal node created for partition
    */
   private prQuadInternal partition(prQuadNode sRoot, T elemNew, long xMin, 
		   long xMax, long yMin, long yMax) {
	   
	   long xAvg = (xMin + xMax) / 2;
	   long yAvg = (yMin + yMax) / 2;
	   
	   
	   @SuppressWarnings("unchecked")
	   prQuadLeaf temp = (prQuadLeaf) sRoot;
	   prQuadInternal intern = new prQuadInternal();  // new internal node
	   
	   // for current element in bucket
	   T elem = temp.Elements.get(0); 

	   Direction quad = elem.inQuadrant(xMin, xMax, yMin, yMax); // *get quadrant
	   switch(quad) {
	   		case NE:    
		   		prQuadLeaf neLeaf = new prQuadLeaf();
		   		neLeaf.Elements = new ArrayList<T>();
		   		neLeaf.Elements.add(elem);
		   		intern.NE = neLeaf;
		   		
		   		break;
		   		/**
		   		else {
			   		@SuppressWarnings("unchecked") 
			   		prQuadLeaf neLeaf = (prQuadLeaf) sRoot;
			   		//if (neLeaf.Elements.size() == 0)
			   		//	neLeaf.Elements.add(elem);
			   		//sRoot = intern.NE;
			   		neLeaf.Elements.add(elem);
			   		intern.NE = neLeaf;
			   		
			   		break;
		   		}**/
	   		case NW:
	   			//if (sRoot.getClass() != prQuadLeaf.class) 
		   		prQuadLeaf nwLeaf = new prQuadLeaf();
		   		nwLeaf.Elements = new ArrayList<T>();
		   		nwLeaf.Elements.add(elem);
		   		intern.NW = nwLeaf;
		   		  
		   		break;
	   		case SW: 
		   			prQuadLeaf swLeaf = new prQuadLeaf();
		   			swLeaf.Elements = new ArrayList<T>();
		   			swLeaf.Elements.add(elem);
		   			intern.SW = swLeaf; 
		   			break;   			
	   			
	   		case SE: 
		   			prQuadLeaf seLeaf = new prQuadLeaf();
		   			seLeaf.Elements = new ArrayList<T>();
		   			seLeaf.Elements.add(elem);
		   			intern.SE = seLeaf;
		   			  
		   			break;
	   		case NOQUADRANT:
	   			return null;
	   
	   
	   }
	   // new element being inserted into QuadTree
	   Direction quadNew = elemNew.inQuadrant(xMin, xMax, yMin, yMax);
	   switch(quadNew) {
	   case NE: 
		   if (intern.NE != null) {
			   if (intern.NE.getClass().equals(prQuadLeaf.class)) {
				   @SuppressWarnings("unchecked")
				   prQuadLeaf refNE = (prQuadLeaf) intern.NE;
				   if (refNE.Elements.size() == 0) {
					   refNE.Elements.add(elemNew);
					   break;
				   }
				   
				
			   }
			   intern.NE = partition(intern.NE, elemNew, xAvg, xMax, yAvg, yMax);
			   break;
		   }
		   else {
			   prQuadLeaf newNE = new prQuadLeaf();
			   newNE.Elements = new ArrayList<T>();
			   newNE.Elements.add(elemNew);
			   intern.NE = newNE;
			   break;
		   }
	   case NW:
		   /**
		   prQuadLeaf refNW = (prQuadLeaf) intern.NW;
		   if (refNW.Elements.size() == 0) {
			   refNW.Elements.add(elemNew);
			   break;
		   }
		   else {
			   partition(intern.NW, elemNew, xMin, xAvg, yAvg, yMax);
			   break;
		   }**/
		   if (intern.NW != null) {
			   if (intern.NW.getClass().equals(prQuadLeaf.class)) {
				   @SuppressWarnings("unchecked")
				   prQuadLeaf refNW = (prQuadLeaf) intern.NW;
				   if (refNW.Elements.size() == 0) {
					   refNW.Elements.add(elemNew);
					   break;
				   }
				   
				
			   }
			   intern.NW = partition(intern.NW, elemNew, xMin, xAvg, yAvg, yMax);
			   break;
		   }
		   else {
			   prQuadLeaf newNW = new prQuadLeaf();
			   newNW.Elements = new ArrayList<T>();
			   newNW.Elements.add(elemNew);
			   intern.NW = newNW;
			   break;
		   }
	   case SW:
		   if (intern.SW != null) {
			   if (intern.SW.getClass().equals(prQuadLeaf.class)) {
				   @SuppressWarnings("unchecked")
				   prQuadLeaf refSW = (prQuadLeaf) intern.SW;
				   if (refSW.Elements.size() == 0) {
					   refSW.Elements.add(elemNew);
					   break;
				   }
				   
				   intern.SW = partition(intern.SW, elemNew, xMin, xAvg, yMin, yAvg);
				   break;
			   }
			   
		   }
		   else {
			   prQuadLeaf newSW = new prQuadLeaf();
			   
			   newSW.Elements = new ArrayList<T>();
			   newSW.Elements.add(elemNew);
			   intern.SW = newSW;
			   break;
		   }
	   case SE:
		   /**
		   if (sRoot.getClass() == prQuadLeaf.class) {
			   @SuppressWarnings("unchecked")
			   prQuadLeaf leafNew = (prQuadLeaf)sRoot;
			   //temp.Elements = new ArrayList<T>();
			   leafNew.Elements.add(elemNew);
			   
		   }
		   prQuadLeaf refSE = (prQuadLeaf) intern.SE;
		   if (refSE.Elements.size() == 0) {
			   refSE.Elements.add(elemNew);
			   break;
		   }
		   else {
			   partition(intern.SE, elemNew, xAvg, xMax, yMin, yAvg);
			   break;
		   }**/
		   if (intern.SE != null) {
			   if (intern.SE.getClass().equals(prQuadLeaf.class)) {
				   @SuppressWarnings("unchecked")
				   prQuadLeaf refSE = (prQuadLeaf) intern.SE;
				   if (refSE.Elements.size() == 0) {
					   refSE.Elements.add(elemNew);
					   break;
				   }
				   
				
			   }
			   intern.SE = partition(intern.SE, elemNew, xAvg, xMax, yMin, yAvg);
			   break;
		   }
		   else {
			   prQuadLeaf newSE = new prQuadLeaf();
			   newSE.Elements = new ArrayList<T>();
			   newSE.Elements.add(elemNew);
			   intern.SE = newSE;
			   break;
		   }
	   case NOQUADRANT:
		   return null;
	   }
	   
	   sRoot = intern;
	   return intern;
   }
   
   // Pre:  elem != null
   // Returns reference to an element x within the tree such that elem.equals(x)
   // is true, provided such a matching element occurs within the tree; returns 
   // null otherwise.
   public T find(T Elem) {
	   return find(root, Elem);   
   }
   
   /**
    * Recursive helper function for find. 
    * Traverses through the prQuadTree to look
    * for element.
    * 
    * @param 	sRoot root node
    * @param 	elem  element to search for
    * @return	returns reference to element being searched for
    * 		  	and null if not found
    */
   private T find(prQuadNode sRoot, T elem) {
	   long centerX = (xMin + xMax) / 2;
	   long centerY = (yMin + yMax) /2;
	   if (sRoot == null)
		   return null;
	   //long elemX = elem.getX();
	   //long elemY = elem.getY();
	   Direction quad = elem.directionFrom(centerX, centerY);
	   if (sRoot.getClass().equals(prQuadLeaf.class)) {
		   @SuppressWarnings("unchecked")
		   prQuadLeaf temp = (prQuadLeaf) sRoot;
		   T x = temp.Elements.get(0);
		   if (elem.equals(x)) {
			   elem = x;
			   return elem;
		   }
		   return null;
	   }
	   else {
		   @SuppressWarnings("unchecked")
		   prQuadInternal temp = (prQuadInternal) sRoot;
		   switch (quad) {
		   case NE:
			   return find(temp.NE, elem);  
		   case NW:
			   return find(temp.NW, elem);
		   case SW:
			   return find(temp.SW, elem);
			   
		   case SE:
			   return find(temp.SE, elem);
		   case NOQUADRANT:
			   return null; 
		   }
		   return null;
	   }
	   
	   
   }
   
   

   // Pre:  xLo, xHi, yLo and yHi define a rectangular region
   // Returns a collection of (references to) all elements x such that x is in
   // the tree and x lies at coordinates within the defined rectangular region,
   // including the boundary of the region.
   public ArrayList<T> find(long xLo, long xHi, long yLo, long yHi) {
	   
	   ArrayList<T> collection = new ArrayList<T>();
	   
	   findHelper(root, collection, xMin, xMax, yMin, yMax, xLo, xHi, yLo, yHi);
	   

	   return collection;
   }
   
   /**
    * Helper method for region search to traverse
    * QuadTree in specific region of tree.
    * @param sRoot		node to start traversal at
    * @param xLo		minimum value of x for search
    * @param xHi		maximum value of x for search
    * @param yLo		minimum value of y for search
    * @param yHi		maximum value of y for search
    * @return		returns pointer to node for traversal
    * 				of QuadTree
    */
   @SuppressWarnings("unchecked")
   private void findHelper(prQuadNode sRoot, ArrayList<T> list, long xMin, long xMax, 
		   				long yMin, long yMax, long xLo, long xHi, long yLo, long yHi) {
	   // need region search coordinates
	   // in box as base case
	   // make some functions to do calculations...return true/false and call it 4 times
	   if (sRoot.getClass().equals(prQuadLeaf.class)) {
		   prQuadLeaf guide = (prQuadLeaf) sRoot;
		   T elem = guide.Elements.get(0);
		   if (elem.inBox(xLo, xHi, yLo, yHi)) {
			   list.add(elem);
			   
		   }
		   
	   } 
	   
	   else {
		   prQuadInternal intern = (prQuadInternal) sRoot;
		   if (inBox(xMin, xMax, yMin, yMax, xLo, xHi, yLo, yHi)) {
			   
			   long xAvg = (xMin + xMax) / 2;
			   long yAvg = (yMin + yMax) / 2;
			   if (inNERegion(xMin, xMax, yMin, yMax, xLo, xHi, yLo, yHi)) {
				   if (intern.NE != null) {
					   findHelper(intern.NE, list, xAvg, xMax, yAvg, yMax, xLo,
							   		xHi, yLo, yHi);							   
				   }
			   }
			   
			   if (inNWRegion(xMin, xMax, yMin, yMax, xLo, xHi, yLo, yHi)) {
				   if (intern.NW != null) {
					   findHelper(intern.NW, list, xMin, xAvg, yAvg, yMax, xLo,
							   		xHi, yLo, yHi);							   
				   }
			   }
			   
			   if (inSWRegion(xMin, xMax, yMin, yMax, xLo, xHi, yLo, yHi)) {
				   if (intern.SW != null) {
					   findHelper(intern.SW, list, xMin, xAvg, yMin, yAvg, xLo,
							   		xHi, yLo, yHi);							   
				   }
			   }
			   
			   if (inSERegion(xMin, xMax, yMin, yMax, xLo, xHi, yLo, yHi)) {
				   if (intern.SE != null) {
					   findHelper(intern.SE, list, xAvg, xMax, yMin, yAvg, xLo,
							   		xHi, yLo, yHi);							   
				   }
			   }
			   
			   return;
		   }
		   
			
		   else {
			   // search region bigger than quadtree
			 
			   if (inNERegion(xMin, xMax, yMin, yMax, xLo, xHi, yLo, yHi)) {
				   if (intern.NE != null) {
					   findHelper(intern.NE, list, xMin, xMax, yMin, yMax, xLo,
							   		xHi, yLo, yHi);							   
				   }
			   }
			   
			   if (inNWRegion(xMin, xMax, yMin, yMax, xLo, xHi, yLo, yHi)) {
				   if (intern.NW != null) {
					   findHelper(intern.NW, list, xMin, xMax, yMin, yMax, xLo,
							   		xHi, yLo, yHi);							   
				   }
			   }
			   
			   if (inSWRegion(xMin, xMax, yMin, yMax, xLo, xHi, yLo, yHi)) {
				   if (intern.SW != null) {
					   findHelper(intern.SW, list, xMin, xMax, yMin, yMax, xLo,
							   		xHi, yLo, yHi);							   
				   }
			   }
			   
			   if (inSERegion(xMin, xMax, yMin, yMax, xLo, xHi, yLo, yHi)) {
				   if (intern.SE != null) {
					   findHelper(intern.SE, list, xMin, xMax, yMin, yMax, xLo,
							   		xHi, yLo, yHi);							   
				   }
			   }
			   
		   }
		   return;
	   }
   }
   
   // Private helper to check if region search is in box
   private boolean inBox(long xMin, long xMax, long yMin,
		   			long yMax, long xLo, long xHi,long yLo, long yHi) {
       if (xLo >= xMin && xLo <= xMax && yHi >= yMin && yHi <= yMax)
    	   return true;
       if (xHi >= xMin && xHi <= xMax && yHi >= yMin && yHi <= yMax)
    	   return true;
       if (xHi >= xMin && xHi <= xMax && yLo >= yMin && yLo <= yMax)
    	   return true;
       if (xLo >= xMin && xLo <= xMax && yLo >= yMin && yLo <= yMax)
    	   return true;
       return false;
	}
   
   // Private helper to check if search region contains NE quadrant
   private boolean inNERegion(long xMin, long xMax, long yMin, long yMax, long xLo, 
		   long xHi, long yLo, long yHi) {
	   long xAvg = (xMin + xMax) / 2;
	   long yAvg = (yMin + yMax) / 2;
	   if ((xHi - xAvg > 0) && (yHi - yAvg >= 0) || (xLo - xAvg == 0 && yLo - yAvg == 0)) {
		   return true;
	   }
	   return false;
   }
   
   // Private helper to check if search region contains NW quadrant
   private boolean inNWRegion(long xMin, long xMax, long yMin, long yMax, long xLo, 
		   long xHi, long yLo, long yHi) {
	   long xAvg = (xMin + xMax) / 2;
	   long yAvg = (yMin + yMax) / 2;
	   if ((xLo - xAvg <= 0) && (yHi - yAvg > 0)) {
		   return true;
	   }
	   return false;
   }
   
   // Private helper to check if search region contains SW quadrant
   private boolean inSWRegion(long xMin, long xMax, long yMin, long yMax, long xLo, 
		   long xHi, long yLo, long yHi) {
	   long xAvg = (xMin + xMax) / 2;
	   long yAvg = (yMin + yMax) / 2;
	   if ((xLo - xAvg < 0) && (yLo - yAvg <= 0)) {
		   return true;
	   }
	   return false;
   }
   
   // Private helper to check if search region contains SE quadrant
   private boolean inSERegion(long xMin, long xMax, long yMin, long yMax, long xLo, 
		   long xHi, long yLo, long yHi) {
	   long xAvg = (xMin + xMax) / 2;
	   long yAvg = (yMin + yMax) / 2;
	   if ((xHi - xAvg >= 0) && (yLo - yAvg < 0)) {
		   return true;
	   }
	   return false;
   }
}
