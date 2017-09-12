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
