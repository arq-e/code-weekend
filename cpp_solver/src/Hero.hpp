#include "Monster.hpp"
#include <vector>

struct Hero {
  int _base_speed;
  int _base_power;
  int _base_range;
  int _level_speed_coeff;
  int _level_power_coeff;
  int _level_range_coeff;
  int _x;
  int _y;
  int _level = 0;
  int _exp = 0;
  int _gold = 0;
  int _exp_to_level;
  vector<int> speed;
  vector<int> power;
  vector<int> range;
  Hero() {}
  Hero(int base_speed, int base_power, int base_range, int level_speed_coeff, int level_power_coeff, int level_range_coeff, int x, int y) :
    _base_speed(base_speed), _base_power(base_power), _base_range(base_range), _level_speed_coeff(level_speed_coeff), _level_power_coeff(level_power_coeff), _level_range_coeff(level_range_coeff), _x(x), _y(y) {
      _exp_to_level = level_up_cost(_level);
    }

  int level_up_cost(int level) {
    return 1000 + (level+1) * level * 100;
  }

  int count_max_level(long long total_exp) {
    int level = 0;
    long long exp_to_level = level_up_cost(level);
    while (total_exp >= exp_to_level) {
      ++level;
      total_exp -= exp_to_level;
      exp_to_level = level_up_cost(level);
    }
    return level;
  }

  void calc_ttk(int max_level, Monster &monster) {
    monster.ttk = std::vector<int>(max_level+1, 0);
    for (int i = 0; i <= max_level; ++i) {
      int p = power[i];
      monster.ttk[i] = monster.hp / p + (monster.hp % p == 0 ? 0 : 1);
    }
  }

  void level_up() {
    while (_exp >= _exp_to_level) {
      ++_level;
      _exp -= _exp_to_level;
      _exp_to_level = level_up_cost(_level);
    }
  }

  void calc_speed(int max_level) {
    speed = vector<int>(max_level+1, 0);
    for (int i = 0; i <= max_level; ++i) {
      speed[i] = get_speed(i);
    }
  }

  void calc_power(int max_level) {
    power = vector<int>(max_level+1, 0);
    for (int i = 0; i <= max_level; ++i) {
      power[i] = get_power(i);
    }
  }

  void calc_range(int max_level) {
    range = vector<int>(max_level+1, 0);
    for (int i = 0; i <= max_level; ++i) {
      range[i] = get_range(i);
    }
  }

  int get_speed(int level) {
    return _base_speed + (1 + _level_speed_coeff * level / 100);
  }

  int get_power(int level) {
    return _base_power + (1 + _level_power_coeff * level / 100);
  }

  int get_range(int level) {
    return _base_range + (1 + _level_range_coeff * level / 100);
  }

  void attack(Monster &m) {
    m.damage(power[_level]);
    if (!m.is_alive()) {
      _exp += m.exp;
      _gold += m.gold;
      level_up();
    }
  }

  int _turns_to_move(int x, int y) {
    return std::max(abs(x - _x), abs(y - _y));
  }

  void move(int x, int y) {
    _x = x;
    _y = y;
  }


  double distance(int x1, int y1, int x2, int y2) {
    return sqrt((double)pow(x1 - x2, 2) + (double)pow(y1 - y2, 2));
  }

  int time_to_move(int x, int y) {
    return ceil(distance(_x, _y, x, y) / speed[_level]);
  }

  bool in_range(const Monster &m) {
    return distance(_x, _y, m.x, m.y) <= (double)range[_level];
  }

  // std::pair<int, int> shooting_distance(const Monster &m) {

  // }
};