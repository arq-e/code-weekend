#include <bits/stdc++.h>
#include <nlohmann/json.hpp>
#include <argparse/argparse.hpp>

using namespace std;
using json = nlohmann::json;

#include "World.hpp"

json solve(json task) {
  World world(task);

  json ans;
  ans = task;
  return ans;
}


int main(int argc, char** argv) {
  argparse::ArgumentParser program("sc");

  string input;
  string output;
  int solver_id = 0;

  program.add_argument("-i", "--input").required().help("input json").store_into(input);
  program.add_argument("-o", "--output").required().help("output json").store_into(output);
  program.add_argument("-s", "--solver").help("solver id").store_into(solver_id);

  try {
    program.parse_args(argc, argv);
  }
  catch (const std::exception& err) {
    std::cerr << err.what() << std::endl;
    std::cerr << program;
    return 1;
  }

  ifstream ifs(input);
  ofstream ofs(output);

  cout << "Reading input from " << input << endl;
  json task = json::parse(ifs);
  ifs.close();

  World world(task);
  json ans = world.solve(solver_id);
  if (ans.empty()) {
    return 1;
  }

  cout << "Writing output to " << output << endl;
  ofs << ans.dump(4) << endl;
  ofs.close();

  return 0;
}