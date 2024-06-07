class Hero:
  def __init__(self, base_speed, base_power, base_range, level_speed_coeff, level_power_coeff, level_range_coeff):
    self.base_speed = base_speed
    self.base_power = base_power
    self.base_range = base_range
    self.level_speed_coeff = level_speed_coeff
    self.level_power_coeff = level_power_coeff
    self.level_range_coeff = level_range_coeff
    self.level = 0
    self.exp = 0
    self.exp_to_level = self.level_up_cost(0)

  def level_up_cost(self):
    return 1000 + (self.level+1) * self.level * 100

  def level_up(self):
    while self.exp >= self.exp_to_level:
      self.level += 1
      self.exp -= self.exp_to_level
      self.exp_to_level = self.level_up_cost()

  def get_speed(self, level):
    return self.base_speed * ( 1 + level * self.level_speed_coeff / 100)

  def get_power(self, level):
    return self.base_power * ( 1 + level * self.level_power_coeff / 100)

  def get_range(self, level):
    return self.base_range * ( 1 + level * self.level_range_coeff / 100)

  def attack(self, target: Monster):
    target.take_damage(self.get_power(self.level))
    if not target.is_alive():
      self.exp += target.exp
      self.gold += target.gold
      self.level_up()

  def kill(self, target: Monster):
    target.take_damage(target.hp)
    if not target.is_alive():
      self.exp += target.exp
      self.gold += target.gold
      self.level_up()


  def get_total_exp(self):

  def time_to_kill(self, level, hp) -> bool:

  def time_to_move(self, level, x, y) -> bool:

class World:
  def __init__(self, width, height, turns):
    self.width = width
    self.height = height
    self.turns = turns

  def in_bounds(self, x, y):
    return 0 <= x < self.width and 0 <= y < self.height

  def get_distance(self, x1, y1, x2, y2):
    return ((x1 - x2) ** 2 + (y1 - y2) ** 2) ** 0.5

class Monster:
  def __init__(self, x, y, hp, gold, exp):
    self.x = x
    self.y = y
    self.hp = hp
    self.gold = gold
    self.exp = exp

  def is_alive(self):
    return self.hp > 0

  def take_damage(self, damage):
    self.hp -= damage
