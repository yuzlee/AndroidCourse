var svger = require('./generator');
var draw = svger.draw, draw_flat = svger.draw_flat;
var fs = require('fs');

var style = {
	"stroke": "#aaa",
	"stroke-width": "1",
	"fill": "#ffffff"
};

var script = '<script type="text/javascript" xlink:href="jquery.min.js"></script>'
           + '<script type="text/javascript" xlink:href="svg.min.js"></script>'
           + '<script type="text/javascript" xlink:href="app.js"></script>'

var svg_normal = draw(500, 7, 0, style, script);
// var svg_flat = draw_flat(500, 11, 30, style, script)

var prefix = 
'<!DOCTYPE html>\n\
<html>\n\
<head>\n\
    <title>Hex Game</title>\n\
    <link rel="stylesheet" href="style.css" />\n\
    <script src="jquery.min.js"></script>\n\
    <script src="app.js"></script>\n\
</head>\n\
<body>\n\t<div class="container">\n\t\t';
var suffix = 
'\n\t<div>\n</body>\n\
</html>\n';

fs.writeFileSync('hex_main.svg', svg_normal);
// fs.writeFileSync('hex_main_flat.svg', svg_flat);
