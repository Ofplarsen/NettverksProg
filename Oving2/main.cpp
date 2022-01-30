#include <iostream>
#include <thread>
#include <vector>
#include <mutex>
#include <cmath>
#include <algorithm>
#include <condition_variable>
#include <functional>
#include <list>

using namespace std;

class Workers{
    private:
    vector<thread> threads;
    int numberOfThreads;
    mutex tasks_mutex;
    list<function<void()>> tasks;

    void stop(){
        join();
    }

    public:
        explicit Workers(int numOfThreads) {
            this->numberOfThreads = numOfThreads;
        }

        int start(){
            for(int i = 0; i < numberOfThreads; i++){
            }
            return 1;
        }

    void post(list<function<void()>> tasksToPost){
        bool wait(true);
        mutex wait_mutex;
        condition_variable cv;


        this->tasks = tasksToPost;
        for (int i = 0; i < this->numberOfThreads; ++i) {
            cout << threads.size() << endl;
            threads.emplace_back([this, &wait, &wait_mutex, &cv] {
                while (true) {
                    function<void()> task;
                    {
                        unique_lock<mutex> lock(wait_mutex);
                        while (wait)
                            cv.wait(lock); // Unlock wait_mutex and wait. // When awaken, wait_mutex is locked.

                        if (!tasks.empty()) {
                            task = *tasks.begin();
                            tasks.pop_front();
                        }else{
                            break;
                        }
                    }

                    //cout << " runs in thread " << this_thread::get_id << endl;
                    if (task) {
                        task();
                    }

                }
            });


            this_thread::sleep_for(1s);

            {
                unique_lock<mutex> lock(wait_mutex);
                wait = false;
            }

            cv.notify_one(); // Awake waiting cv
        }
    }

    void post_timeout(list<function<void()>> tasksToPost, int ms){

    }

    void join() {
            for (auto &thread: threads) thread.join();
        }

};

int main2(){
    bool wait(true);
    mutex wait_mutex;
    condition_variable cv;

    thread t([&wait, &wait_mutex, &cv] {
        unique_lock<mutex> lock(wait_mutex);
        while (wait) cv.wait(lock); // Unlock wait_mutex and wait. // When awaken, wait_mutex is locked.
        cout << "thread: finished waiting" << endl;
    });

    this_thread::sleep_for(1s);

    {
        unique_lock<mutex> lock(wait_mutex);
        wait = false;
    }

    cv.notify_one(); // Awake waiting cv
    t.join();
}

int main() {

    Workers worker_thread(4);
    Workers event_loop(1);

    list<function<void()>> tasksA;
    list<function<void()>> tasksB;
    list<function<void()>> tasksC;
    list<function<void()>> tasksD;

    for (int i = 0; i < 10000; ++i) {
        tasksA.emplace_back([i]{
            cout << "taskA " << i << " runs in thread " << endl;
        });
    }
    for (int i = 0; i < 10000; ++i) {
        tasksB.emplace_back([i]{
            cout << "taskB " << i << " runs in thread " << endl;
        });
    }

    for (int i = 0; i < 10000; ++i) {
        tasksC.emplace_back([i]{
            cout << "taskC " << i << " runs in thread " << endl;
        });
    }

    for (int i = 0; i < 10000; ++i) {
        tasksD.emplace_back([i]{
            cout << "taskD " << i << " runs in thread " << endl;
        });
    }
    worker_thread.start();

    worker_thread.post(tasksA);
    //worker_thread.post(tasksB);

    event_loop.post(tasksC);
    event_loop.post(tasksD);

    worker_thread.join();
    event_loop.join();
    //main2();

    return 0;
}

