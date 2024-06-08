#include "Hero.hpp"

struct World {
  int width;
  int height;
  int turns;
  int start_x;
  int start_y;
  json out;

  vector<Monster> monsters;
  Hero hero;

  World(json task) {
    width = task["width"];
    height = task["height"];
    turns = task["num_turns"];
    start_x = task["start_x"];
    start_y = task["start_y"];
    monsters = parse_monsters(task);
    hero = parse_hero(task);

    int max_level = hero.count_max_level(calc_total_exp(monsters));
    hero.calc_power(max_level);
    hero.calc_speed(max_level);
    hero.calc_range(max_level);
    for (auto &m : monsters) {
      hero.calc_ttk(max_level, m);
    }
    out["moves"] = json::array();
  }

  json stay_with_natural_order() {
    cout << "Solver stay_with_natural_order launched." << endl;

    bool changed = true;
    while (turns > 0 && changed) {
      changed = false;
      for (int i = 0; i < monsters.size(); ++i) {
        auto &m = monsters[i];
        if (m.is_alive() && hero.in_range(m)) {
          int ttk = m.ttk[hero._level];
          if (ttk < turns) {
            changed = true;
            for (int j = 0; j < ttk; ++j) {
              attack(i);
            }
          }
        }
      }
    }
    return out;
  }

  json go_and_sin() {
    return out;
  }

  json solve(int id) {
    //TODO: add benchmark
    cout << "Launching solver " << id << endl;

    json ans;
    switch (id) {
    case 0:
      ans = stay_with_natural_order();
      break;
    default:
      cout << "Solver " << id << " not found" << endl;
      ans = json();
    }
    if (ans.empty() || ans["moves"].empty()) {
      ans = json();
      cout << "Solver " << id << " failed to generate valid solution" << endl;
    }
    return ans;
  }

  void attack(int id) {
    hero.attack(monsters[id]);
    --turns;

    json j;
    j["type"] = "attack";
    j["target_id"] = id;
    out["moves"].push_back(j);
  }

  void move(int x, int y) {
    hero.move(x, y);
    --turns;

    json j;
    j["type"] = "move";
    j["target_x"] = x;
    j["target_y"] = y;
    out["moves"].push_back(j);
  }

  bool inside(int x, int y) {
    return x >= 0 && x < width && y >= 0 && y < height;
  }

  vector<Monster> parse_monsters(json task) {
    vector<Monster> monsters;
    for (auto m : task["monsters"]) {
      monsters.push_back(Monster(m["hp"], m["gold"], m["exp"], m["x"], m["y"]));
    }
    return monsters;
  }

  Hero parse_hero(json task) {
    json hero = task["hero"];
    return Hero(hero["base_speed"], hero["base_power"], hero["base_range"], hero["level_speed_coeff"], hero["level_power_coeff"], hero["level_range_coeff"], task["start_x"], task["start_y"]);
  }

  long long calc_total_exp(const vector<Monster> &monsters) {
    long long total_exp = 0;
    for (auto m : monsters) {
      total_exp += m.exp;
    }
    return total_exp;
  }
};