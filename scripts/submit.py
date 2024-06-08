import os
import argparse
import code_weekend as cw
from get_submission import process_submission

def submit(task_id: int, solution: str, wait = False, save = False):
    submission_id = cw.submit(task_id, solution)
    if submission_id is None:
        print(f'Failed to send submission for task {task_id}.')
        return
    print(f'Submission {submission_id} for task {task_id} is sent.')
    info = cw.get_submission_info(submission_id, wait=True)
    print(f'Result for submission {submission_id}: {info}')
    process_submission(submission_id, wait, save)

def main():
    parser = argparse.ArgumentParser("Submit a solution for a Code Weekend task.")
    parser.add_argument('task', type=int, nargs=1, help='id of the task to submit')
    parser.add_argument('solution', type=str, nargs=1, help='solution for the task')
    parser.add_argument('-f', '--file', action='store_true', default=False, help='treat solution as file path')
    parser.add_argument('-s', '--save', action='store_true', default=False, help='save submission info')
    args = parser.parse_args()
    task_id = args.task[0]

    solution = None
    if args.file:
        with open(args.solution[0], 'r') as f:
            solution = f.read()
    else:
        solution = args.solution[0]

    submit(task_id, solution, args.save)

if __name__ == "__main__":
    main()
