# Job Scheduler

## Problem Statement

Create a job scheduler for ABC company which is building a new operating system. When the
processor is idle, dispatch a new job such that its executed time is the least among all executed
jobs. A job has the following fields:

1. jobID: Unique ID for each job.
2. executed_time: The amount of time for which the job has been scheduled so far.
3. total_time: Total time required to complete the job. So, the remaining time to complete
is total_time-executed_time.

All times are in milliseconds (ms).

## Requirements

The requirements of the scheduler are:
1. Dispatch a job with the lowest executed_time to the processor. Ties may be broken
arbitrarily.
2. Print (jobID,executed_time,total_time) triplet for a given jobID.
3. Print (jobID,executed_time,total_time) triplets for all jobIDs in the range [low,high].
4. NextJob(jobID,executed_time,total_time) prints the triplet for the job with smallest
ID greater than a given jobID. Print (0,0,0) if there is no such job.
5. PreviousJob(jobID,executed_time,total_time) prints the triplet of the job with the
greatest jobID that is less than to a given jobID. Print (0,0,0) if there is no such job.
6. Insert (jobID,total_time) insert a new job into the heap and RBT.

Input test data will be given in the following format:
Insert(jobID,total_time)
PrintJob(jobID)
PrintJob(jobID1,jobID2)
NextJob(jobID)
PreviousJob(jobID)

## Implementation

Following data structures are implemented as part of this project to build the scheduler:
1. Red-Black Tree: The key of the red-black tree is jobId. Since jobId is unique, it will
take O(log n) time to search a job. Red-Black tree ensures that it will remain balanced
so there is a performance improvement as compared to using plane Binary Search Tree.
The node also contains total_time field.
2. Min Heap: The key of Min Heap is assigned as execution time of the job. So, whenever
extractMin() is called, it will correspond to smallest execution time among all jobs.
The nodes are designed in such a way that every Red-Black node will contain object reference
of corresponding Min-Heap node and vice-versa. Hence, whenever there is an insert operation,
nodes will be inserted in both the data structures and will point to each other.

Global time counter is a simple int variable that simulates current system time. At every unit
of time, first it is checked if there is any command to be executed, then current job details are
updated.
