import os
import json
import re
import argparse
import numpy as np
from datetime import datetime

import matplotlib as mpl
import matplotlib.pyplot as plt


class Monster:
  def __init__(self, x, y, hp, gold, exp):
    self.x = x
    self.y = y
    self.hp = hp
    self.gold = gold
    self.exp = exp
  def __repr__(self):
    return f"({self.x}, {self.y}, {self.hp}, {self.gold}, {self.exp})"

def load_monsters(jmon: list):
  monsters = []
  for mon in jmon:
    m = Monster(0, 0, 0, 0, 0)
    m.x = int(mon['x'])
    m.y = int(mon['y'])
    m.hp = int(mon['hp'])
    m.gold = int(mon['gold'])
    m.exp = int(mon['exp'])
    monsters.append(m)
  #print(monsters)
  return monsters

def view(path: str):
  j = json.load(open(path, 'r'))
  monsters = load_monsters(j['monsters'])
  (start_x, start_y) = (j['start_x'], j['start_y'])
  [x_vals, y_vals] = zip(*[(m.x, m.y) for m in monsters])

  def draw_subplots(nrows, ncols, rules):
    fig, ax = plt.subplots(nrows = nrows, ncols = ncols, sharex = True, sharey = True)
    for i, rule in enumerate(rules):
      sub = ax[i//ncols][i%ncols]
      colors = [eval(rule) for m in monsters]
      im = sub.scatter(x_vals, y_vals, c=colors, cmap='cool')
      sub.set_title(rule)
      sub.set_aspect('equal')
      sub.plot(start_x, start_y, marker = 'x', markersize = 10, color = 'darkgreen')
      fig.colorbar(im, ax=sub, ticks = np.linspace(min(colors), max(colors), 10))


  draw_subplots(2, 3, ("m.exp", "m.gold", "m.hp", "m.exp * m.gold / m.hp", "m.exp + m.gold", "m.gold / m.hp"))
  plt.show()

def main():
  parser = argparse.ArgumentParser("Visualize given task.")
  parser.add_argument('task', type=int, nargs=1, help='id of the task to visualize')
  args = parser.parse_args()
  task = args.task[0]
  path = os.path.join("tasks", f"{task}", "input")
  view(path)

if __name__ == "__main__":
  main()