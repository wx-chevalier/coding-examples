package mapreduce

import (
	"fmt"
	"sync"
)

type FailRecord struct{
	sync.Mutex
	workers map[string]int
}

func (r *FailRecord) add(src string){
	r.Lock()
	r.workers[src]++
	defer r.Unlock()
}

func (r *FailRecord) get(src string)int {
	r.Lock()
	defer r.Unlock()
	return r.workers[src]

}

const MAX_WORKER_FAILER = 5
//
// schedule() starts and waits for all tasks in the given phase (mapPhase
// or reducePhase). the mapFiles argument holds the names of the files that
// are the inputs to the map phase, one per map task. nReduce is the
// number of reduce tasks. the registerChan argument yields a stream
// of registered workers; each item is the worker's RPC address,
// suitable for passing to call(). registerChan will yield all
// existing registered workers (if any) and new ones as they register.
//
func schedule(jobName string, mapFiles []string, nReduce int, phase jobPhase, registerChan chan string) {
	var ntasks int
	var n_other int // number of inputs (for reduce) or outputs (for map)
	switch phase {
	case mapPhase:
		ntasks = len(mapFiles)
		n_other = nReduce

	case reducePhase:
		ntasks = nReduce
		n_other = len(mapFiles)

	}

	var waitGroup sync.WaitGroup
	readyChan := make(chan string,ntasks)
	retryChan := make(chan *DoTaskArgs,0)
	failRecord := FailRecord{workers:make(map[string]int)}
	tasks := make([]*DoTaskArgs,0)

	for index,file := range mapFiles {
		args := &DoTaskArgs{
			JobName:       jobName,
			File:          file, // Ignored for reduce phase
			Phase:         phase,
			TaskNumber:    index,
			NumOtherPhase: n_other,
		}
		tasks = append(tasks, args)
	}

	startTask := func(worker string,args *DoTaskArgs){
		defer waitGroup.Done()
		flag := call(worker,"Worker.DoTask",args,nil)
		readyChan <- worker
		if(!flag){
			retryChan <- args
			failRecord.add(worker)

		}
	}

	fmt.Printf("Schedule: %v %v tasks (%d I/Os)\n", ntasks, phase, n_other)
	for len(tasks)>0{
		select {
		case taskArgs := <- retryChan:
			tasks = append(tasks, taskArgs)

		case worker:= <- registerChan:
			readyChan <- worker

		case worker :=  <- readyChan:
			if(failRecord.get(worker) < MAX_WORKER_FAILER) {
				waitGroup.Add(1)
				index := len(tasks) - 1
				args := tasks[index]
				tasks = tasks[:index]
				go startTask(worker, args)
			} else{
				fmt.Printf("Worker %s failed %d times and will be no longer used\n",worker,MAX_WORKER_FAILER)
		}



		}
	}
	waitGroup.Wait()

	fmt.Printf("Schedule: %v %v tasks (%d I/Os)\n", ntasks, phase, n_other)

	// All ntasks tasks have to be scheduled on workers. Once all tasks
	// have completed successfully, schedule() should return.
	//
	// Your code here (Part III, Part IV).
	//
	fmt.Printf("Schedule: %v done\n", phase)
}
