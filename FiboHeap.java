/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 * 
 * @author Kareen Bishara
 * @author Shir Basa       
 * 
 */
 
import java.lang.Math;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

public class FibonacciHeap{
	private HeapNode min;
	private HeapNode first;
	private int size;
	private int numOfMarked;
	private int numOfTrees;
	public static int totalLinks = 0;
	public static int totalCuts = 0;

	FibonacciHeap(){
		this.min = null;
		this.first = null;
		this.size = 0;
		this.numOfMarked = 0;
		this.numOfTrees = 0;
	}
	
   /**
    * public boolean isEmpty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean isEmpty(){
    	if(this.size == 0) {
    		return true;
    	}return false;
    }
	 
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * 
    * Returns the new node created. 
    */
    public HeapNode insert(int key){
    	HeapNode node = new HeapNode(key);
    	if(isEmpty()) {
    		this.min = node;
    		this.first = node;
    		node.next = node;
    		node.prev = node;
    	}else{
    		first.addBrother(node);
    		first = node;
    		if(key<min.key) {
    			this.min = node;
    		}
    	}
    	size++;
    	numOfTrees++;
    	return node;
    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin(){
    	if(this.isEmpty()) {
    		return;
    	}
    	else if(this.size() == 1) {
    		//heap contains one node
    		this.min = null;
    		this.first = null;
    		this.size--;
        	this.numOfTrees--;
    		return;
    	}else if(min.child==null) {
    		if(this.min.equals(this.first));
    		this.first = min.next;
    	}
    	else if(min.child != null){
    		//min root has a child
    		HeapNode firstChild = min.child;
    		HeapNode child = min.child ;
    		for (int j=0; j<min.rank; j++) {
    			child.parent = null;
    			if(child.marked) {
    				child.marked = false;
    				this.numOfMarked--;
    			}
    			this.numOfTrees++;
    			child = child.next;
    		}
    		//adding all children of min node as root nodes		
    		HeapNode lastChild = firstChild.prev;
    		firstChild.prev = min.prev;
    		min.prev.next = firstChild;
    		lastChild.next = min;
    		min.prev = lastChild;
        	if(this.min.equals(this.first)){
    			this.first = firstChild;	
    		}
    	}
    	//if (min.child == null) function continues straight to this point
    	min.removeBrother();
    	this.size--;
    	this.numOfTrees--; 
    	consolidate();
    	return;
    }
    
   /**
    * private void consolidate()
    * 
    * updates the minimum value, and consolidates the heap
    * result heap has no two root values with the same rank
    * 
    */
   private void consolidate() {
	   this.min = this.first;
	   HeapNode[] linkSizes = new HeapNode[(int) log2(size)+1];	
	   List<HeapNode> roots = rootsToList(first);
	   for (HeapNode node: roots) {
		   if(node.key < min.key) {
			   min = node;
		   }
		  int rank = node.rank;
		  if(linkSizes[rank] == null){
			  linkSizes[rank] = node;
		  }else{
			  while(linkSizes[rank] != null) {
				  node = link(node, linkSizes[rank]);
			  	  linkSizes[rank] = null;
			  	  rank+=1;
			  	  }linkSizes[rank] = node;
			  }
	   }fixRoots(linkSizes);	   
   }
   
   /**
    * private void fixRoots (HeapNode[] linkSizes)
    * 
    * @param linkSizes a list of size (log_2(n)+1) the contains the consolidated root
    * function updates pointers of the new root list
    * 
    */
   private void fixRoots (HeapNode[] linkSizes) {
	   HeapNode node1 =null;
	   HeapNode node2 = null;
	   for (HeapNode node: linkSizes) {
		   if (node==null) {
			   continue;
		   }
		   if (node1==null) {
			   node1=node;
		   }
		   if (node2==null) {
			   node2=node;
			   continue;
		   }
		   node.prev= node2;
		   node2.next= node;
		   node2=node;
	   }
	   node2.next= node1;
	   node1.prev= node2;
   }
     
   /**
    * private HeapNode link (HeapNode x, HeapNode y)
    * 
    * @param x - first node
    * @param y - second node
    * 
    * x,y has the same rank
    * @return links x y to a new tree
    */
   private HeapNode link (HeapNode x, HeapNode y) {
	   HeapNode newRoot, newChild;
	   if(x.key>y.key) {
		   newRoot = y;
		   newChild = x;
	   }else{
		   //(y>x)
		   newRoot = x;
		   newChild = y;
	   }
	   if(newChild == this.first) {
		   this.first = newRoot;
	   }
	   // add newChild as newRoot's son
	   newChild.removeBrother();
	   newChild.parent = newRoot;
	   if (newRoot.child == null) {
		   newRoot.child = newChild;
		   newChild.next = newChild;
		   newChild.prev = newChild;
	   } else {
		   newRoot.child.addBrother(newChild);
	   }
	   newRoot.rank = newRoot.rank+1;
	   totalLinks ++;
	   numOfTrees --;
	   return newRoot;
   }
   
   /**
    * private List<HeapNode> rootsToList (HeapNode node)
    * 
    * @param node 
    * @return HeaopNode list that contains all roots
    * 
    */
   private List<HeapNode> rootsToList (HeapNode node){
	   if (node == null) {
		   //min is the only root
		   return new LinkedList<>();
	   }
	   List <HeapNode> toReturn = new LinkedList<>();
	   HeapNode brother = node;
	   toReturn.add(brother);
	   for (brother = node.next; brother!=node; brother = brother.next) {
		   toReturn.add(brother);
	   }return toReturn;
   }

/**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin(){
    	return this.min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2){
    	this.numOfTrees = this.numOfTrees + heap2.numOfTrees;
    	this.numOfMarked += heap2.numOfMarked;
    	if(heap2.isEmpty()) {
    		return;
    	}if(this.isEmpty()) {
    		//this heap is empty
    		this.min = heap2.min;
    		this.size = heap2.size;
    		this.first = heap2.first;
    		this.numOfMarked = heap2.numOfMarked;
    		this.numOfTrees = heap2.numOfTrees;
    	}
    	HeapNode thisLast = this.first.prev;
    	HeapNode heap2Last = heap2.first.prev;

    	heap2Last.next = first;
    	this.first.prev = heap2Last;
    	thisLast.next = heap2.first;
    	heap2.first.prev = thisLast;	
    	if (heap2.min.key < min.key) {
    		min= heap2.min;
    	}
    	this.size += heap2.size;
    	return; 		
    }

    
   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size(){
    	return size;
    }
    	
    
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep(){	
	int[] arr = new int[(int) (log2(size) + 1)];
	HeapNode node= min.next;
	int maxRank= min.rank;
	arr[min.rank] ++ ; 
	while (node != min) {
		arr[node.rank] ++;
		if (node.rank > maxRank) {
			maxRank= node.rank;		
		}node = node.next;
	}	
        return Arrays.copyOfRange(arr,0,maxRank+1); 
    }
	
    
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) {
    	decreaseKey(x, Integer.MAX_VALUE);
    	deleteMin();
    	return;
    }
    
 
   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta){
    	boolean updateMin = false;
    	if(x.key-delta < min.key) {
			updateMin = true;
		}
    	x.key -= delta;
    	if(!x.isRoot()) {
    		if (x.key < x.parent.key) {
    			cascadingCuts(x, x.parent);
    		}
    	}
    	if(updateMin) {
    		min = x;
    	}
    	return;
    }
    
    /**
     * private void cascadingCuts(HeapNode x, HeapNode y)
     * 
     * @param x - node
     * @param y - y is parent of x
     * 
     * cascading cuts runs while y is not a root
     */
    private void cascadingCuts(HeapNode x, HeapNode y) {
    	cut(x,y);
    	if (y.parent != null) {
    		if (!y.marked) {
    			y.marked= true;
    			numOfMarked++;
    		}
    		else {
    			cascadingCuts(y, y.parent);
    		}
    	}
    	return;
    }
    
    /**
     * private void cut(HeapNode x, HeapNode y)
     * 
     * node y is parent of x
     * function cuts y from x and add it to the root list
     */
    private void cut(HeapNode x, HeapNode y) {
    	//y is parent of x
    	totalCuts++;
    	numOfTrees++;
    	x.parent=null;
    	if (x.marked) {
    		x.marked=false;
    		numOfMarked--;
    	};
    	//
    	y.rank -= 1;
    	if (x.next == x) {
    		y.child= null;
    	}
    	else {
    		if(y.child==x) {
    			y.child = x.next;
    		}
    		x.prev.next = x.next;
    		x.next.prev = x.prev;
    	}
    	first.addBrother(x);
    	this.first = x;	
    	return;
    }
    
   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential(){
    	return (numOfTrees + (2*numOfMarked)); 
    }
    
   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks(){    
    	return totalLinks;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts(){    
    	return totalCuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k*deg(H)). 
    * You are not allowed to change H.
    */
    public static int[] kMin(FibonacciHeap H, int k){    
        int[] arr = new int[k];
        if (k>0) {
	        FibonacciHeap H2 = new FibonacciHeap();
	        HeapNode node = H2.insert(H.min.key);
	        node.pointer = H.min;
	        for(int i=0; i<k; i++) {
	        	node = H2.min.pointer;
	        	arr[i] = node.key;
	        	H2.min.pointer = null;
	        	H2.deleteMin();	 
	        	//inserting the node's children
	        	HeapNode child = node.child;
	        	for(int j=0; j<node.rank; j++) {
	        		HeapNode currChild = H2.insert(child.key);
	        		currChild.pointer = child;
	        		child = child.next;
	        	}
	        }
        }return arr;
    }
    
    /**
     * public static int log2(int N)
     * 
     * return log of N in base 2
     */
    public static int log2(int N) { 
        int result = (int) Math.ceil(Math.log(N) / Math.log(2)); 
        return result; 
    }
    
    public int getNumberOfTrees() {
    	return this.numOfTrees;
    }

    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{	
    	public int key;
    	private int rank;
    	private boolean marked;
    	private HeapNode child, prev, next, parent;
    	private HeapNode pointer;
    	
    	private HeapNode(int key) {
    		this.key = key;
    		this.rank = 0;
    		this.marked = false;
    		this.parent = null;
    		this.child = null;
    	}


    /**
     * public void addBrother(HeapNode node)
     * 
     * @param node to be added
     * adds node next of this 
     */
  	public void addBrother(HeapNode node) {
  		if(node==null) { //can be deleted
  			return;
  		}
  		this.prev.next = node;	
  		node.next = this;
  		node.prev = this.prev;
  		this.prev = node;
		}
  	
  	/**
  	 * public void removeBrother()
  	 * 
  	 * @param node - a node to be removed
  	 * function removes node form tree
  	 * 
  	 */
  	public void removeBrother() {
  		if (next == this) {
  			if (this.parent != null) {
  				this.parent.child= null;
  				this.parent.rank--;
  			}
  		} else if (this.parent != null) {
  				this.parent.child= this.next;
				this.parent.rank--;
  		}
  		this.next.prev = this.prev;
  		this.prev.next = this.next;
  		return;	
  	}
  	
	/**
	 * 
	 * @return true if node has no parent
	 */
	private boolean isRoot() {
		return this.parent==null;
	}
  	
	/**
	 * public int getKey()
	 * 
	 * returns the node's key
	 */
	public int getKey() {
	    return this.key;
    }
	
	/**
	 * public int getKey()
	 * 
	 * returns the node's child if exist, otherwise null
	 */
	public HeapNode getChild() {
		return this.child;
	}
	
	
	public void setChild(HeapNode node) {
		this.child = node;
	}
	
	/**
	 * public int getRank()
	 * 
	 * returns the node's rank
	 */
	public int getRank() {
		return this.rank;
	}
	
	/**
	 * public HeapNpde getPrev()
	 * 
	 * returns the node's prev
	 */
	public HeapNode getPrev() {
		return this.prev;
	}
	
	public void setPrev(HeapNode node) {
		this.prev = node;
	}
	
	/**
	 * public HeapNode getParent()
	 * 
	 * returns the node's parent
	 */
	public HeapNode getParent() {
		return this.parent;
	}
	
	public void setParent(HeapNode newParent) {
		this.parent = newParent;
	}
	
	/**
	 * public HeapNode getNext()
	 * 
	 * returns the node's next node
	 */
	public HeapNode getNext() {
		return this.next;
	}
	
	public void setNext(HeapNode node) {
		this.next = node;
	}
	
    }
}
