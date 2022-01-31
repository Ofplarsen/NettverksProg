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
    vector<thread> threads;
    bool end;
    bool wait;



    public:

        explicit Workers(int numOfThreads) {
            this->numberOfThreads = numOfThreads;
        }

        int start(){
            threads.clear();

            for (int i = 0; i < this->numberOfThreads; ++i) {


                threads.emplace_back([this] {
                    bool fin = false;
                    while (!fin) {
                        function<void()> task;
                        {

                            if(tasks.empty()){
                                unique_lock<mutex> lock(wait_mutex);
                                while (wait)
                                    cv.wait(lock); // Unlock wait_mutex and wait. // When awaken, wait_mutex is locked.

                            }

                            {
                                unique_lock<mutex> lock(tasks_mutex);
                                if (!tasks.empty()) {
                                    task = *tasks.begin();
                                    tasks.pop_front();
                                } else {

                                    {
                                        unique_lock<mutex> lock(threads_mutex);
                                        if(end){
                                            fin = true;
                                        }else{
                                            unique_lock<mutex> lock(wait_mutex);
                                            wait = true;
                                            break;
                                        }
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
                unique_lock<mutex> lock(tasks_mutex);
                tasks.emplace_back(taskToPost);
            }

            {
                unique_lock<mutex> lock(wait_mutex);
                wait = false;
            }

            cv.notify_all();
        }

        void post_timeout(function<void()> taskToPost, int ms){
            {
                unique_lock<mutex> lock(tasks_mutex);
                tasks.emplace_back([&ms, &taskToPost]{
                    this_thread::sleep_for(chrono::milliseconds(ms));
                    taskToPost();
                });
            }

            cv.notify_all();
        }

        void stop() {
            {
                unique_lock<mutex> lock(threads_mutex);
                end = false;
            }
            {
                unique_lock<mutex> lock(wait_mutex);
                wait = false;
            }
            cv.notify_one();
        }

        void join(){

            for (auto &thread : threads) thread.join();

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

