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
    mutex wait_mutex;
    mutex tasks_mutex;
    condition_variable cv;
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
                threads.emplace_back([] {// i is copied to the thread, do not capture i as reference (&i) as it might be freed before all the threads finishes.

                });
            }
            return 1;
        }

    void post(list<function<void()>> tasksToPost){
        this->tasks = tasksToPost;
        for (int i = 0; i < 4; ++i) {

            threads.emplace_back([this] {
                while (true) {
                    function<void()> task;
                    unique_lock<mutex> lock(tasks_mutex);

                    if (!tasks.empty()) {
                        task = *tasks.begin();
                        tasks.pop_front();
                    }else{
                        break;
                    }
                    cout << " runs in thread " << this_thread::get_id << endl;
                    if (task) {
                        task();
                    }
                }
            });
        }
    }

    void post_timeout(list<function<void()>> tasksToPost, int ms){

    }

    void join() {
            for (auto &thread: threads) thread.join();
        }

};


int main() {
    Workers worker_thread(4);
    list<function<void()>> tasks;

    for (int i = 0; i < 1000; ++i) {
        tasks.emplace_back([i]{
            cout << "task " << i << " runs in thread " << endl;
        });
    }
    worker_thread.start();
    worker_thread.post(tasks);
    worker_thread.join();

    return 0;
}

