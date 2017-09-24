//
// Created by Rolrence on 2017/6/10.
//

#include "hex/hexmatch.h"
#include "player/aiplayer.h"
#include "util/misc.h"
#include "player/cmdplayer.h"

void play(Poi<HexPlayer> vert, Poi<HexPlayer> hori, HexMarkT first, char* filename) {
    HexBoard board;
    HexGame g(board, first, false);
    {
        std::ifstream is(filename);
        g.load(is);
    }
    HexMatch m(g, vert, hori);
    while (m.status() != HexMatch::MATCH_FINISHED) {
        m.doSome();
        std::cout << m.game().board();
        {
            std::ofstream os(filename);
            m.save(os);
        }
    }
    std::cout << "Game End." << std::endl;
    std::ofstream os("record.txt");
    os << DBG.str();
}

int main(int argc, char **argv) {
    std::cout << "Game Start." << std::endl;
    Carrier::init();

    char first = argv[1][0];
    char me = argv[2][0];
    char* filename = argv[4];

    std::string first_tag;
    std::string me_tag;

    HexMarkT first_player;
    if (first == 'V' || first == 'v') {
        first_player = HexMarkT::HEX_MARK_VERT;
        first_tag = "VERT";
    } else {
        first_player = HexMarkT::HEX_MARK_HORI;
        first_tag = "HORI";
    }
    if (me == 'V' || me == 'v') {
        me_tag = "VERT";
        std::cout << "first is " << first_tag << ", this client is " << me_tag << std::endl;
        play(Poi<HexPlayer>(new AiPlayer((AiPlayer::LevelT)atoi(argv[3]), false)), 
            Poi<HexPlayer>(new CmdPlayer()),
            first_player,
            filename);
    } else {
        me_tag = "HORI";
        std::cout << "first is " << first_tag << ", this client is " << me_tag << std::endl;
        play(Poi<HexPlayer>(new CmdPlayer()),
            Poi<HexPlayer>(new AiPlayer((AiPlayer::LevelT)atoi(argv[3]), false)), 
            first_player,
            filename);
    }
}


