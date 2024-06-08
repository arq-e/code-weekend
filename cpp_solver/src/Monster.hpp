#include <vector>

struct Monster {
  int hp;
  int gold;
  int exp;
  int x;
  int y;
  std::vector<int> ttk;
  Monster(int hp, int gold, int exp, int x, int y) : hp(hp), gold(gold), exp(exp), x(x), y(y) {}
  bool is_alive() { return hp > 0; }
  void damage(int damage) { hp -= damage; }
};