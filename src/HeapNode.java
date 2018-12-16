/**
 * Node structure of MinHeap
 * @author KUNWAR
 */
public class HeapNode {

        public int key;//executed time
        public RBNode rbNode;//Object reference of Red-Black Node

        //Constructor
        public HeapNode(int k){
            this.key = k;
        }

        @Override
        public String toString() {
            return Integer.toString(key);
        }
    }
