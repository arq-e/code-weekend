import os
import argparse
import subprocess
import code_weekend as cw
from submit import submit


def solve(path: str, task_id: int, solver_id: int, send = False) -> str:
  #TODO: write to file here and do not download from site
  #base = os.path.basename(path)
  #solution_path = os.path.join("solutions", f"{task_id}", f"{base}", f"{solver_id}")
  input_path = os.path.abspath(os.path.join("tasks", f"{task_id}", "input"))
  output_path = os.path.abspath(os.path.join("tasks", f"{task_id}", "last_output"))

  result = subprocess.run([f'{path}', '-i', f'{input_path}', '-o', f'{output_path}', '-s', f'{solver_id}'], capture_output=True)
  print(result.args)
  if result.returncode != 0:
    print(f"Failed to solve task {task_id} with solver {path} {solver_id}")
    print(result.stdout)
    print(result.stderr)
    return

  solution = None
  with open(output_path, 'r') as f:
    solution = f.read()

  if send and solution is not None:
    submit(task_id, solution, wait=True, save=True)
  print(f"Solution for task {task_id} with solver {path} {solver_id}")
  print(solution)

def main():
    parser = argparse.ArgumentParser("Solve task with the given solver.")
    # parser.add_argument('task', type=int, nargs=1, help='id of the task to solve')
    parser.add_argument('task', type=int, nargs=2, help='task range (inclusive)')
    parser.add_argument('solver', type=str, nargs=1, help='path to the solver')
    parser.add_argument('-i', '--id', type=int, default=0, help='internal id of the solver')
    parser.add_argument('-s', '--submit', action='store_true', default=False, help='submit solution')
    args = parser.parse_args()

    min_id = min(args.task[0], args.task[1])
    max_id = max(args.task[0], args.task[1])

    solver = args.solver[0]

    for task_id in range(min_id, max_id+1):
      solution = solve(solver, task_id, args.id, args.submit)
      if args.submit:
        submit(task_id, solution, wait=True, save=True)

if __name__ == "__main__":
    main()