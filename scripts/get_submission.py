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

def get_submission_info(submission_id):
    path = os.path.join("submissions",f"{submission_id}","info")
    if  not os.path.isfile(path):
        with open(path, 'w') as f:
            f.write(cw.get_submission_info(submission_id))
    print(f"Submission {submission_id}'s result is available at {path}.")

def get_solution(submission_id):
    content = cw.download_submission(submission_id)
    path = os.path.join("submissions",f"{submission_id}","solution")
    if not os.path.isfile(path):
        with open(path, 'w') as f:
            f.write(content)
    print(f"Solution from submission {submission_id} is available at {path}.")        

def main():
    parser = argparse.ArgumentParser("Get Code Weekend submission.")
    parser.add_argument('submission', type=int, nargs=1, help='id of the submission to acquire')
    parser.add_argument('-s', '--solution', action='store_const', const='save', 
                        default='ignore', help='download associated solution')
    args = parser.parse_args()
    submission_id = args.submission[0]
    save = args.save == 'save'

    init_submission_dir(submission_id)
    get_submission_info(submission_id)
    if (save):
        get_solution(submission_id)

main()