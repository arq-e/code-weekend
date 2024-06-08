import os
import argparse
import json
import code_weekend as cw

def init_submission_dir(submission_id):
    path = os.path.join("submissions")
    if not os.path.exists(path):
        os.mkdir(path)
    path = os.path.join(path,f"{submission_id}")
    if not os.path.exists(path):
        os.mkdir(path)

def get_submission_info(submission_id, wait=False) -> bool:
    init_submission_dir(submission_id)
    path = os.path.join("submissions",f"{submission_id}","info")
    if not os.path.isfile(path):
        info = cw.get_submission_info(submission_id, wait)
        if info is not None:
            with open(path, 'w') as f:
                json.dump(info, f)
            print(f"Submission {submission_id}'s info is now available at {path}.")
        else:
            return False
    else:
        print(f"Submission {submission_id}'s info is already available at {path}.")
    return True

def get_solution(submission_id):
    init_submission_dir(submission_id)
    path = os.path.join("submissions",f"{submission_id}","solution")
    if not os.path.isfile(path):
        content = cw.download_submission(submission_id)
        if content is not None:
            with open(path, 'w') as f:
                f.write(content)
            print(f"Solution from submission {submission_id} is now available at {path}.")
    else:
        print(f"Solution from submission {submission_id} is already available at {path}.")

def process_submission(submission_id, wait=False, save_solution=False):
    if get_submission_info(submission_id, wait) and save_solution:
        get_solution(submission_id)

def main():
    parser = argparse.ArgumentParser("Get Code Weekend submission.")
    parser.add_argument('submission', type=int, nargs=1, help='id of the submission to acquire')
    parser.add_argument('-s', '--save', action='store_true', default=False, help='download associated solution')
    parser.add_argument('-w', '--wait', action='store_true', default=False, help='wait for pending submission')
    args = parser.parse_args()
    submission_id = args.submission[0]

    process_submission(submission_id, args.wait, args.save)

if __name__ == "__main__":
    main()