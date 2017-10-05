function getID(x, y) {
    return `hex_${x}_${y}`;
}

is_work = false;
current_role = 'V';
history = {};

roles = {
    'V': 'AI',
    'H': 'YOU'
};

function clearBoard() {
    is_work = false;
    history = {};
    for (var i = 0; i < 7; i++) {
        for (var j = 0; j < 7; j++) {
            SVG.get(getID(i, j)).style({
                fill: '#ffffff'
            });
        }
    }
}

function setOptions(current, ai) {
    current_role = current;
    clearBoard();

    if (current == ai) {
        is_work = true;
        kotlin.gen_move();
    } else {
        roles['V'] = 'YOU';
        roles['H'] = 'AI';
    }
}

function changeCurrent() {
    current_role = current_role == 'V' ? 'H' : 'V'
}

SVG.get("svg_g").click(e => {
    var p = e.path[0];
    var id = p.id;
    var pos = id.split('_');

    if (!is_work && pos.length == 3) {
        x = parseInt(pos[1]);
        y = parseInt(pos[2]);
        if (id in history) {
            alert(`(${x},${y}) is ${current_role}`);
            return;
        }

        console.log('current', x, y, current_role);

        history[id] = current_role;
        SVG.get(id).style({
            fill: current_role == 'V' ? '#ff0000' : '#0000ff'
        });
        changeCurrent();

        is_work = true;

        kotlin.play(x, y);
    }
})

function quit_ok(e) {
    alert(e);
}

function resolveCallback(e) {
    try {
        move = e.split(',');
        var x = parseInt(move[0]);
        var y = parseInt(move[1]);
        var winner = move[2];
        var over = parseInt(move[3]) == 1
        return [x, y, winner, over];
    } catch (ex) {
        alert("bad callback info");
    }
}

function play_ok(e) {
    try {
        is_work = false;
        result = resolveCallback(e);
        game_finished(result[3]);

        if (!is_work) {
            kotlin.gen_move();
        }
    } catch (ex) {
        alert(ex);
    }
}

function genmove_ok(e) {
    result = resolveCallback(e);

    var x = result[0];
    var y = result[1];

    is_work = false;
    SVG.get(getID(x, y)).style({
        fill: current_role == 'V' ? '#ff0000' : '#0000ff'
    });
    changeCurrent();
    game_finished(result[3]);
}

function game_finished(e) {
    if (e) {
        alert("Finished!");
        is_work = true;
    }
}
