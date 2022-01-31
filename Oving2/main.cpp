
#include <iostream>
#include <thread>
#include <vector>
#include <mutex>
#include <cmath>
#include <algorithm>
#include <condition_variable>
#include <functional>
#include <list>
#include <atomic>

using namespace std;

class Workers{
    private:
    int numberOfThreads;
    mutex tasks_mutex; //Mutex for when doing something with tasks
    mutex threads_mutex; //Mutex for when doing somthing with if the threads has stopped or is going
    list<function<void()>> tasks;
    mutex wait_mutex; //Mutex for waiting boolean
    condition_variable cv;
    vector<thread> threads;
    bool end;
    bool wait;

    public:

        explicit Workers(int numOfThreads) {
            this->numberOfThreads = numOfThreads;
        }

        int start(){
            threads.clear(); //Clears all earlier threads

            for (int i = 0; i < this->numberOfThreads; ++i) { //Creates x number of threads

                threads.emplace_back([this] {
                    bool fin = false;
                    bool tasksLeft = false;
                    while (!fin) { //While thread is not finished
                        function<void()> task;

                        if(!tasksLeft){
                            unique_lock<mutex> lock(wait_mutex);
                            while (wait)
                                cv.wait(lock); // Unlock wait_mutex and wait. // When awaken, wait_mutex is locked.

                        }

                        { //Scopes used to release mutexes faster than without
                            unique_lock<mutex> lock(tasks_mutex); //Lock tasks
                            if (!tasks.empty()) {
                                task = *tasks.begin();
                                tasks.pop_front();      //Code from presentation
                                tasksLeft = true;       //Checks if there are any tasks left
                            } else {

                                {
                                    unique_lock<mutex> lock(threads_mutex);
                                    if(end){ //If stop is called, the thread should make fin = true
                                        fin = true;
                                    }else{ //IF not, wait for new tasks
                                        unique_lock<mutex> lock(wait_mutex); //Lock waiting boolean
                                        wait = true;
                                        tasksLeft = true;
                                    }
                                }

                            }
                        }

                        if (task) {
                            task();
                            cv.notify_one();
                        }

                    }
                });

            }
        }

        void post(function<void()> taskToPost){

            {
                unique_lock<mutex> lock(tasks_mutex); //Lock tasks to add new task
                tasks.emplace_back(taskToPost);
            }

            {
                unique_lock<mutex> lock(wait_mutex); //Lock wait boolean (to make sure wait = false) so the threads can start working
                wait = false;
            }

            cv.notify_all();
        }

        void post_timeout(function<void()> taskToPost, int ms){
            {
                unique_lock<mutex> lock(tasks_mutex); //Lock tasks again
                tasks.emplace_back([&ms, &taskToPost]{
                    this_thread::sleep_for(chrono::milliseconds(ms)); //Sleeps for duration, before adding to tasks
                    taskToPost();
                });
            }

            cv.notify_all();
        }

        void stop() {
            {
                unique_lock<mutex> lock(threads_mutex); //Lock end boolean and make true
                end = true;
            }
            {
                unique_lock<mutex> lock(wait_mutex); //Lock wait boolean and make false
                wait = false;
            }
            cv.notify_all();
        }

        void join(){

            for (auto &thread : threads) thread.join(); //Join all threads

        }

};

int main() {

    Workers worker_thread(4);
    Workers event_loop(1);


    event_loop.start();
    worker_thread.start();

    worker_thread.post_timeout([]{
        cout << "Task A" << endl;
    }, 2000);

    worker_thread.post([]{
        cout << "Task B" << endl;
    });

    event_loop.post([]{
        cout << "Task C" << endl;
    });

    event_loop.post([]{
        cout << "Task D" << endl;
    });


    worker_thread.stop();
    event_loop.stop();
    worker_thread.join();
    event_loop.join();
    return 0;
}

