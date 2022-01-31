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
    mutex tasks_mutex;
    mutex threads_mutex;
    list<function<void()>> tasks;
    mutex wait_mutex;
    condition_variable cv;
    condition_variable cvThreads;
    atomic<bool> running;
    bool wait;
    bool joinWait;


    public:
    vector<thread> threads;
        explicit Workers(int numOfThreads) {
            this->numberOfThreads = numOfThreads;
        }

        int start(){

            for (int i = 0; i < this->numberOfThreads; ++i) {
                wait = true;
                cout << threads.size() << endl;
                threads.emplace_back([this] {
                    while (true) {
                        function<void()> task;
                        {
                            unique_lock<mutex> lock(wait_mutex);
                            while (wait)
                                cv.wait(lock); // Unlock wait_mutex and wait. // When awaken, wait_mutex is locked.

                            if (!tasks.empty()) {
                                task = *tasks.begin();
                                tasks.pop_front();
                            } else{
                                break;
                            }
                        }

                        //cout << " runs in thread " << this_thread::get_id << endl;
                        if (task) {
                            task();
                        }

                    }
                });


                //this_thread::sleep_for(1s);

                {
                    unique_lock<mutex> lock(wait_mutex);
                    wait = false;
                }

                cv.notify_one(); // Awake waiting cv

            }
        }

    void post(list<function<void()>> tasksToPost){
        unique_lock<mutex> lock(tasks_mutex);
        tasks.insert(tasks.end(), tasksToPost.begin(), tasksToPost.end());
    }

    void post_timeout(list<function<void()>> tasksToPost, int ms){


        thread t([this, &ms, &tasksToPost] {
            this_thread::sleep_for(chrono::milliseconds(ms));
            post(tasksToPost);
        });

        t.join();
        start();
    }

    void stop() {

        for (auto &thread : threads) thread.join();
    }

};

int main() {

    Workers worker_thread(4);
    Workers event_loop(1);

    list<function<void()>> tasksA;
    list<function<void()>> tasksB;
    list<function<void()>> tasksC;
    list<function<void()>> tasksD;

    for (int i = 0; i < 10; ++i) {
        tasksA.emplace_back([i]{
            cout << "A " << i << " runs in thread " << endl;
        });
    }
    for (int i = 0; i < 10; ++i) {
        tasksB.emplace_back([i]{
            cout << "B " << i << " runs in thread " << endl;
        });
    }

    for (int i = 0; i < 10; ++i) {
        tasksC.emplace_back([i]{
            cout << "C " << i << " runs in thread " << endl;
        });
    }

    for (int i = 0; i < 10; ++i) {
        tasksD.emplace_back([i]{
            cout << "D " << i << " runs in thread " << endl;
        });
    }

    worker_thread.post(tasksA);
    worker_thread.post_timeout(tasksA,1000);
    worker_thread.post_timeout(tasksB,500);

    event_loop.post(tasksC);
    event_loop.post(tasksD);


    event_loop.start();
    worker_thread.start();



    worker_thread.stop();
    event_loop.stop();

    return 0;
}

