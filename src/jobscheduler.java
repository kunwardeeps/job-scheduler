import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main executing class for scheduling jobs.
 * Uses Red-Black tree and Min-Heap
 * @author KUNWAR
 */
public class jobscheduler {

    private static String INPUT_FILE;
    private static final String OUT_FILE = "output_file.txt";
    private RedBlackTree tree = new RedBlackTree();
    private MinHeap heap = new MinHeap();
    private FileWriter fileWriter;
    private HeapNode currentJob=null;//Current Job being executed
    private int t = 0;//Global time counter
    private int currentSlotEndTime =0;//Time at which the 5s slot will end
    private int currentJobCompletionTime = 0;//Time at which the current job being executed will end if continued indefinitely
    private boolean debug = false;//set true for console outputs

    /**
     * Accept input file name as argument
     * @param args
     */
    public static void main(String[] args) {

        INPUT_FILE = args[0];
        jobscheduler jobscheduler = new jobscheduler();
        jobscheduler.begin();

    }

    /**
     * Main jobscheduler logic is written in this method.
     * -Read commands for each line and calls the corresponding method
     * -Each command is processed only when the Global time is equal to
     * the command execution time
     * -In a unit of time, command will be processed and current job
     * will be updated, if needed.
     *
     */
    private void begin() {
        BufferedReader br = null;
        FileReader fr = null;

        try {

            URL path = ClassLoader.getSystemResource(INPUT_FILE);
            fr = new FileReader(new File(path.toURI()));
            br = new BufferedReader(fr);

            fileWriter = new FileWriter(OUT_FILE);

            String sCurrentLine;
            String[] params;

            while ((sCurrentLine = br.readLine()) != null) {
                if (debug) System.out.println("Time:" + t);
                if (debug) System.out.println(sCurrentLine);

                //Sample Input: "13: PrintJob(10,300)"
                Pattern p = Pattern.compile("(^\\d+): ([a-zA-Z]+)\\((.+)\\)");
                Matcher m = p.matcher(sCurrentLine);

                if (m.find()) {
                    params = m.group(3).split(",");
                    int cmdExecTime = Integer.parseInt(m.group(1));//Time at which next command will be executed

                    while (cmdExecTime != t){//If not global time, update current job and timings
                        dispatchOrUpdateJob();
                        incrementTime();
                        if (debug) System.out.println("Time:" + t);
                    }

                    if (debug) System.out.println("Processing:" + m.group(1) +","+m.group(2));

                    switch (m.group(2)) {
                        case "Insert": {
                            insertJob(params);
                            break;
                        }
                        case "PrintJob": {
                            printJob(params);
                            break;
                        }
                        case "NextJob": {
                            getNextJob(params);
                            break;
                        }
                        case "PreviousJob": {
                            getPreviousJob(params);
                            break;
                        }
                    }
                }
                dispatchOrUpdateJob();
                incrementTime();
            }
            executeRemainingJobs();//Case when all lines have been read but jobs are still waiting to be executed
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                fileWriter.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Searches tree for greatest job ID less than given job ID and prints output
     * @param params
     * @throws IOException
     */
    private void getPreviousJob(String[] params) throws IOException {
        int jobId = Integer.parseInt(params[0]);
        RBNode node = tree.greatestLessThanKey(jobId);
        if (node == null){
            printEmpty();
        }
        else {
            printJobInFormat(node);
        }
    }

    /**
     * Searches tree for smallest jobID but greater than given jobID and prints output
     * @param params
     * @throws IOException
     */
    private void getNextJob(String[] params) throws IOException {
        int jobId = Integer.parseInt(params[0]);
        RBNode node = tree.smallestGreaterThanK(jobId);
        if (node == null){
            printEmpty();
        }
        else {
            printJobInFormat(node);
        }
    }

    /**
     * When all commands are processed but jobs still remain
     * @throws IOException
     */
    private void executeRemainingJobs() throws IOException {
        while (currentJob != null){
            if (debug) System.out.println("Time:" + t);
            dispatchOrUpdateJob();
            incrementTime();
        }
    }

    /**
     * Increments both global time and current job executed time
     */
    private void incrementTime() {
        t++;
        if (currentJob != null) {
            currentJob.key = currentJob.key + 1;
        }
    }

    /**
     * In heap, the keys are executed times of all jobs. So, extract min will
     * output the job that will be of least executed time. This job will run
     * upto 5 ms or upto it's total execution time if it lies in current slot.
     * Otherwise, we will re-insert the job in heap at the end of current slot.
     *
     * @throws IOException
     */
    private void dispatchOrUpdateJob() throws IOException {
        if (currentJob == null){
            if (heap.isEmpty()){
                if (debug) System.out.println("No Job to dispatch!");
                return;
            }
            else {
                currentJob = heap.extractMin();

                currentSlotEndTime = t+5;
                currentJobCompletionTime = t + currentJob.rbNode.totalTime - currentJob.key -1;
                if (debug) System.out.println("Dispatched Job:"+currentJob.rbNode.key+" at time:"+t);
            }
        }
        else {
            if (currentJobCompletionTime <= currentSlotEndTime){
                if (t == currentJobCompletionTime){
                    //Job completed, so remove from tree and reset current job fields
                    if (debug) System.out.println("Job Completed:"+currentJob.rbNode.key+" at time"+t);
                    tree.delete(currentJob.rbNode);
                    currentJob = null;
                    currentSlotEndTime = 0;
                    currentJobCompletionTime = 0;
                    dispatchOrUpdateJob();//recur to dispatch next job as processor is idle
                }
            }
            else {
                //Slot ends
                if (t == currentSlotEndTime){
                    //re-insert job in heap and reset current job fields
                    heap.insert(currentJob);
                    currentJob = null;
                    currentSlotEndTime = 0;
                    currentJobCompletionTime = 0;
                    dispatchOrUpdateJob();//recur to dispatch next job as processor is idle
                }
            }
        }
    }

    /**
     * Executes PrintJob Command
     * if cmd of form PrintJob(jobId), search for jobID. If found, print in format, else print empty tuple
     * if cmd of form PrintJob(jobId1, jobId2), searches for jobIDs in given range.
     * @param params
     * @throws IOException
     */
    private void printJob(String[] params) throws IOException {

        if (params.length == 1){
            int jobId = Integer.parseInt(params[0]);
            RBNode rbNode = tree.search(jobId);
            if (rbNode == null) {
                printEmpty();
            }
            else {
                printJobInFormat(rbNode);
            }
        }
        else {
            int jobId1 = Integer.parseInt(params[0]);
            int jobId2 = Integer.parseInt(params[1]);
            List<RBNode> list = tree.searchInRange(jobId1, jobId2);//Search in tree

            if (!list.isEmpty()){
                StringBuilder sb = new StringBuilder();
                for (RBNode node: list){
                    sb.append("("+node.key+","+node.heapNode.key+","+node.totalTime + "),");
                }
                sb.deleteCharAt(sb.length()-1);//remove last comma
                sb.append("\n");
                fileWriter.write(sb.toString());
            }
            else {
                printEmpty();
            }
        }
    }

    private void printEmpty() throws IOException {
        fileWriter.write("(0,0,0)\n");
    }

    /**
     * If job to be printed is being executed, then the executed time needs to be fetched from current job
     * Else heap node pointer to the red-black node will give the executed time
     * @param node
     * @throws IOException
     */
    private void printJobInFormat(RBNode node) throws IOException {
        if (node.key == currentJob.rbNode.key){
            fileWriter.write("("+node.key+","+currentJob.key+","+node.totalTime + ")\n");
        }
        else {
            fileWriter.write("(" + node.key + "," + node.heapNode.key + "," + node.totalTime + ")\n");
        }
    }

    /**
     * Process insert job command.
     * Create red-black and min-heap nodes and point to each other
     * Then insert in tree and heap
     * @param params
     */
    private void insertJob(String[] params) {
        int id = Integer.parseInt(params[0]);
        int totTime = Integer.parseInt(params[1]);
        if (debug) System.out.println("Inserting jobId:"+id+", total time:"+totTime);
        RBNode rbNode = new RBNode(id);
        rbNode.totalTime = totTime;
        HeapNode heapNode = new HeapNode(0);
        rbNode.heapNode = heapNode;
        heapNode.rbNode = rbNode;
        tree.insertNode(rbNode);
        heap.insert(heapNode);
    }

}
