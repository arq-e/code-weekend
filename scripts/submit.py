import os
import argparse
import code_weekend as cw

def init_submission_dir(submission_id):
    path = os.path.join("submissions")
    if not os.path.exists(path):
        os.mkdir(path)
    path = os.path.join(path,f"{submission_id}")
    if not os.path.exists(path):
        os.mkdir(path)

def save_info(submission_id, submission_info):
    path = os.path.join("submissions", f"{submission_id}", "info")
    if not os.path.isfile(path):
        with open(path, 'wb') as f:
            f.write(submission_info)
    print(f"Submission {submission_id}'s result is available at {path}")

def save_solution(submission_id, solution):
    path = os.path.join("submissions", f"{submission_id}", "solution")
    if not os.path.isfile(path):
        with open(path, 'w') as f:
            f.write(solution)
    print(f"Submission {submission_id}'s solution is available at {path}")        

def main():
    parser = argparse.ArgumentParser("Submit a solution for a Code Weekend task.")
    parser.add_argument('task', type=int, nargs=1, help='id of the task to submit')
    parser.add_argument('solution', type=str, nargs=1, help='solution for the task')
    parser.add_argument('-f', '--file', action='store_const', const='file', 
                        default='solution', help='read solution from the file')
    parser.add_argument('-s', '--save', action='store_const', const='save', 
                        default='read', help='save submission info')
    args = parser.parse_args()
    task_id = args.task[0]
    solution = args.solution[0]
    save = args.save
    if args.file == 'file':
        with open(args.solution[0], 'r') as f:
            solution = f.read()

    submission_id = cw.submit(task_id, solution)
    print(f'Submission {submission_id} for task {task_id} is sent.')
    info = cw.get_submission_info(submission_id, wait=True)
    print(f'Result for submission {submission_id}: {info}')

    if (save):
        init_submission_dir(submission_id)
        save_info(submission_id, info)
        save_solution(submission_id, solution)

main()






