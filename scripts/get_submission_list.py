import argparse
import code_weekend as cw

def main():
    parser = argparse.ArgumentParser("Get at most 50 submissions with given offset and task id.")
    parser.add_argument('-o', '--offset', type=int, default=0, help='used offset')
    parser.add_argument('-t', '--task_id', type=int, default=None, help='task id')
    args = parser.parse_args()
    cw.show(cw.get_team_submissions(args.offset, args.task_id))

if __name__ == "__main__":
    main()