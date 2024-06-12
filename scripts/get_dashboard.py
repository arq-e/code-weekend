import os
import argparse
import code_weekend as cw

def main():
    board = cw.get_team_dashboard()
    path = os.path.abspath("../bestScores")
    with open(path, 'w') as f:
        for t in board['tasks']:
            bs = int(t['best_score'])
            f.write(f"{bs} ")
            print(f"{bs}")
    #cw.show(cw.get_team_dashboard())

if __name__ == "__main__":
    main()