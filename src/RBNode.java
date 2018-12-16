/**
 * Node object has left, right and parent object references
 *
 * @author KUNWAR
 */
public class RBNode {

        public static final RBNode nil = new RBNode(-999, RedBlackTree.COLOR.BLACK);//sentinel

        public int key; //Unique JobID
        public int totalTime;//Total executed time of current job
        public RBNode left = nil;
        public RBNode right = nil;
        public RBNode parent = nil;
        public RedBlackTree.COLOR color;//RED/Black color
        public HeapNode heapNode;//Object reference to heapNode in MinHeap

        //Constructors:
        public RBNode(int key, HeapNode heapNode){
            this.key = key;
            this.heapNode = heapNode;
        }

        public RBNode(int key){
            this.key = key;
        }

        public RBNode(int key, RedBlackTree.COLOR color){
            this.key = key;
            this.color = color;
        }

        @Override
        public String toString() {
            return "Key:"+this.key+",Color:"+this.color.name();
        }
    }