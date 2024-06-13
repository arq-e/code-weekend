import os
import json
import re
import argparse
import numpy as np
from datetime import datetime

import matplotlib as mpl
import matplotlib.pyplot as plt

class Hero:
  def __init__(self, x, y, s, p, r):
    self.x = x
    self.y = y
    self.s = s
    self.p = p
    self.r = r

class Monster:
  def __init__(self, x, y, hp, gold, exp, attack = 0, range = 0):
    self.x = x
    self.y = y
    self.hp = hp
    self.gold = gold
    self.exp = exp
  def __repr__(self):
    return f"({self.x}, {self.y}, {self.hp}, {self.gold}, {self.exp}, {self.attack}, {self.range})"

def load_monsters(jmon: list):
  monsters = []
  for mon in jmon:
    m = Monster(0, 0, 0, 0, 0)
    m.x = int(mon['x'])
    m.y = int(mon['y'])
    m.hp = int(mon['hp'])
    m.gold = int(mon['gold'])
    m.exp = int(mon['exp'])
    if 'attack' in mon:
      m.attack = int(mon['attack'])
    if 'range' in mon:
      m.range = int(mon['range'])
    monsters.append(m)
  # print(monsters)
  return monsters

def view(path: str, second_part = False):
  j = json.load(open(path, 'r'))
  monsters = load_monsters(j['monsters'])
  hero = Hero(j['start_x'], j['start_y'], j['hero']['base_speed'], j['hero']['base_power'], j['hero']['base_range'])
  (w, h) = (j['width'], j['height'])
  (start_x, start_y) = (j['start_x'], j['start_y'])
  [x_vals, y_vals] = zip(*[(m.x, m.y) for m in monsters])

  def draw_subplots(nrows, ncols, rules):
    fig, ax = plt.subplots(nrows = nrows, ncols = ncols, sharex = True, sharey = True)
    for i, rule in enumerate(rules):
      sub = ax[i//ncols][i%ncols]
      colors = [eval(rule) for m in monsters]
      im = sub.scatter(y_vals, x_vals, c=colors, cmap='cool')
      sub.set_title(rule)
      sub.set_aspect('equal')
      sub.plot(start_y, start_x, marker = 'x', markersize = 10, color = 'darkgreen')
      fig.colorbar(im, ax=sub, ticks = np.linspace(min(colors), max(colors), 10))

  def calc_danger():
    danger = np.zeros((w+1, h+1))
    for m in monsters:
      for row in range(max(0, m.x - m.range), min(w, m.x + m.range)+1):
        for col in range(max(0, m.y - m.range), min(h, m.y + m.range)+1):
          if (row - m.x)**2 + (col - m.y)**2 <= m.range**2:
            danger[row][col] += m.attack
    return danger

  def calc_profit():
    profit = np.zeros((w+1, h+1))
    for m in monsters:
      for row in range(max(0, m.x - hero.r), min(w, m.x + hero.r)+1):
        for col in range(max(0, m.y - hero.r), min(h, m.y + hero.r)+1):
          if (row - m.x)**2 + (col - m.y)**2 <= hero.r**2:
            profit[row][col] += m.exp * m.gold / m.hp
    return profit
  # def draw_circles(ax, monsters):
  #   for m in monsters:
  #     circle = plt.Circle((m.x, m.y), m.range, color='b', fill=False)
  #     ax.add_artist(circle)
  def draw_mesh(points):
    fig, ax = plt.subplots()
    sp = plt.pcolormesh(points, cmap='cool')
    fig.colorbar(sp, ax = ax, ticks = np.linspace(points.min(), points.max(), 10))
    plt.plot(start_y, start_x, marker = 'x', markersize = 10, color = 'darkgreen')
    # plt.plot(950, 100, marker = 'x', markersize = 10, color = 'darkgreen')

  if second_part:
    draw_subplots(2, 3, ("m.exp", "m.gold", "m.hp", "m.attack", "m.range", "m.exp * m.gold / m.hp * m.attack"))
    draw_mesh(calc_danger())
    # draw_mesh(calc_profit())
  else:
    draw_subplots(2, 3, ("m.exp", "m.gold", "m.hp", "m.exp / m.gold", "(m.exp + m.gold) / m.hp", "m.exp / m.hp"))
  plt.show()

def main():
  parser = argparse.ArgumentParser("Visualize given task.")
  parser.add_argument('task', type=int, nargs=1, help='id of the task to visualize')
  args = parser.parse_args()
  task = args.task[0]
  path = os.path.join("tasks", f"{task}", "input")
  view(path, second_part = task > 25)

if __name__ == "__main__":
  main()