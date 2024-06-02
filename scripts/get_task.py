import os
import argparse
import code_weekend as cw

def init_task_dir(task_id):
    path = os.path.join("tasks")
    if not os.path.exists(path):
        os.mkdir(path)
    path = os.path.join(path,f"{task_id}")
    if not os.path.exists(path):
        os.mkdir(path)

def get_task(task_id):
    path = os.path.join("tasks",f"{task_id}", "input");
    if os.path.isfile(path):
        print(f"Task input is already downloaded.")
    else:
        content = cw.get_test(task_id)
        with open(path, 'wb') as f:
            f.write(content)
            print(f"Task {task_id} input is now available at {path}.")    

def main():
    parser = argparse.ArgumentParser("Get Code Weekend submission.")
    parser.add_argument('task', type=int, nargs=1, help='id of the task to download')
    args = parser.parse_args()
    task_id = args.task[0]

    init_task_dir(task_id)
    get_task(task_id)


main()