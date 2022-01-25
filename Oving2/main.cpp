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
        }

    [[noreturn]] void post(list<function<void()>> tasksToPost){
            this->tasks = tasksToPost;

            while (true){
                unique_lock<mutex> lock(tasks_mutex);

                if (!tasks.empty()) {
                    auto task = *tasks.begin();// Copy task tasks.pop_front();
                    tasks.pop_front();// Remove task from list task();
                    this->tasks.emplace_back(task);
                }
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

    worker_thread.post([]{
        cout << "task " << i << " runs in thread " << this_thread::get_id() << endl;
    });

    return 0;
}

