function translate() {
    var svg = SVG.get('svg_g');
    length = svg.node.children.length;

    var min_x = 0, min_y = 0, max_x = 0, max_y = 0;
    t1 = svg.get(3);
    console.log(t1.x(), t1.y(), t1.width(), t1.height())
    for (var i = 0; i < length; i++) {
        t = svg.get(i);
        x = t.x();
        y = t.y();
        w = t.width() + x;
        h = t.height() + y;
        min_x = Math.min(min_x, x);
        min_y = Math.min(min_y, y);
        max_x = Math.max(max_x, x);
        max_y = Math.max(max_y, y);
    }

    console.log(min_x, min_y, max_x, max_y);
}

function getID(x, y) {
    return `hex_${x}_${y}`
}

is_work = false
current_role = 'V'
history = {}

SVG.get("svg_g").click(e => {
    var p = e.path[0]
    var id = p.id
    var pos = id.split('_')

    if (!is_work && pos.length == 3) {
        x = parseInt(pos[1])
        y = parseInt(pos[2])
        if ([x,y] in history) {
            alert(`(${x},${y}) is ${current_role}`)
            return
        }

        console.log('current', x, y, current_role)

        history[[x,y]] = current_role
        SVG.get(id).style({
            fill: current_role == 'V' ? '#ff0000' : '#0000ff'
        })
        current_role = current_role == 'V' ? 'H' : 'V'

        is_work = true

        kotlin.play(x, y)
    }
})

function quit_ok(e) {
    alert(e)
}

function play_ok(e) {
    alert('[play_ok]' + e)
}

function genmove_ok(e) {
    alert('genmove_ok' + e)
    move = e.split(',')

    var x = parseInt(move[0])
    var y = parseInt(move[1])
    is_work = false
    SVG.get(getID(x, y)).style({
        fill: current_role == 'V' ? '#ff0000' : '#0000ff'
    })
}
