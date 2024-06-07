#include <bits/stdc++.h>
#include <nlohmann/json.hpp>
#include <argparse/argparse.hpp>

using namespace std;
using json = nlohmann::json;


json solve(json task) {
  json ans;
  ans = task;
  return ans;
}


int main(int argc, char** argv) {
  argparse::ArgumentParser program("sc");

  string input;
  string output;

  program.add_argument("-i", "--input").required().help("input json").store_into(input);
  program.add_argument("-o", "--output").required().help("output json").store_into(output);

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

  json ans = solve(task);

  cout << "Writing output to " << output << endl;
  ofs << ans.dump(4) << endl;
  ofs.close();

  return 0;
}