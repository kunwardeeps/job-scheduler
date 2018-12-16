import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class BTreePrinter {
    public static final String RED = "\033[0;31m";
    public static final String BLACK = "\033[0;30m";
    public static final String RESET = "\033[0m";

    public static void printNode(RedBlackTree tree) {
        RBNode root = tree.root;
        int maxLevel = BTreePrinter.maxLevel(root);

        printRBNodeInternal(Collections.singletonList(root), 1, maxLevel);
    }

    private static void printRBNodeInternal(List<RBNode> RBNodes, int level, int maxLevel) {
        if (RBNodes.isEmpty() || BTreePrinter.isAllElementsNull(RBNodes))
            return;

        int floor = maxLevel - level;
        int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
        int firstSpaces = (int) Math.pow(2, (floor)) - 1;
        int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

        BTreePrinter.printWhitespaces(firstSpaces);

        List<RBNode> newRBNodes = new ArrayList<RBNode>();
        for (RBNode RBNode : RBNodes) {
            if (null != RBNode && RBNode.key != -999) {
                if (RBNode.color == RedBlackTree.COLOR.BLACK) {
                    System.out.print(RBNode.key);
                } else {
                    System.out.print(RED + RBNode.key + RESET);
                }
                newRBNodes.add(RBNode.left);
                newRBNodes.add(RBNode.right);
            } else {
                newRBNodes.add(null);
                newRBNodes.add(null);
                System.out.print(" ");
            }

            BTreePrinter.printWhitespaces(betweenSpaces);
        }
        System.out.println("");

        for (int i = 1; i <= endgeLines; i++) {
            for (int j = 0; j < RBNodes.size(); j++) {
                BTreePrinter.printWhitespaces(firstSpaces - i);
                if (RBNodes.get(j) == null || RBNodes.get(j).key == -999) {
                    BTreePrinter.printWhitespaces(endgeLines + endgeLines + i + 1);
                    continue;
                }

                if (RBNodes.get(j).left != null && RBNodes.get(j).left.key != -999)
                    System.out.print("/");
                else
                    BTreePrinter.printWhitespaces(1);

                BTreePrinter.printWhitespaces(i + i - 1);

                if (RBNodes.get(j).right != null && RBNodes.get(j).right.key != -999)
                    System.out.print("\\");
                else
                    BTreePrinter.printWhitespaces(1);

                BTreePrinter.printWhitespaces(endgeLines + endgeLines - i);
            }

            System.out.println("");
        }

        printRBNodeInternal(newRBNodes, level + 1, maxLevel);
    }

    private static void printWhitespaces(int count) {
        for (int i = 0; i < count; i++)
            System.out.print(" ");
    }

    private static int maxLevel(RBNode rBNode) {
        if (rBNode == null || rBNode.key == -999)
            return 0;

        return Math.max(BTreePrinter.maxLevel(rBNode.left), BTreePrinter.maxLevel(rBNode.right)) + 1;
    }

    private static <T> boolean isAllElementsNull(List<T> list) {
        for (Object object : list) {
            if (object != null)
                return false;
        }

        return true;
    }

    private static void print(MinHeap heap) {
        StringBuilder sb = new StringBuilder();
        int max=0;
        for(int i=0;i<heap.size+1;i++){
            for(int j=0;j<Math.pow(2,i)&&j+Math.pow(2,i)<heap.size+1;j++){

                if(j>max){
                    max=j;
                }
            }

        }

        for(int i=0;i<heap.size+1;i++){
            for(int j=0;j<Math.pow(2,i)&&j+Math.pow(2,i)<heap.size+1;j++){

                for(int k=0;(k<max/((int)Math.pow(2, i)));k++){
                    sb.append(" ");
                }
                sb.append(heap.arr[j+(int)Math.pow(2,i)-1].key+" ");

            }
            sb.append("\n");

        }
        System.out.println(sb.toString());
    }

}