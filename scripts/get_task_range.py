import os
import time
import argparse
import code_weekend as cw
from get_task import get_task

def main():
  print()
  parser = argparse.ArgumentParser("Download all tasks in given range.")
  parser.add_argument('range', type=int, nargs=2, help='min and max ids of task (inclusive)')
  args = parser.parse_args()
  min_id = min(args.range[0], args.range[1])
  max_id = max(args.range[0], args.range[1])

  print(f"Downloading tasks from {min_id} to {max_id}.")
  for task_id in range(min_id, max_id+1):
    get_task(task_id)
    time.sleep(1)

if __name__ == "__main__":
  main()