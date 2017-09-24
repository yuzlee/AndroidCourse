//
// Created by Rolrence on 2017/6/11.
//

#include "hex/hexgamewrapper.h"

int main() {
    HexGameWrapper wrapper;
    char yourRole = 'V', first = 'V';
    bool yourTurn = yourRole == first;
    wrapper.init(yourRole, first, 3);
    while (wrapper.match->status() != HexMatch::MATCH_FINISHED) {
        if (yourTurn) {
            std::cout << "Please input: ";
            char xs;
            int ys;
            std::cin >> xs >> ys;
            int x = (xs - 'A'), y = (ys - 1);
            wrapper.play(x, y);
        } else {
            yourTurn = true;
        }
        std::cout << wrapper.gen() << std::endl;
    }
}
