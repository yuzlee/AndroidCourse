// make 3.1415926 to 3.1416
// make 3.1415233 to 3.1415
var num4 = function (x , n) {
	n = n || 4;
	var base = Math.pow(10, n);
	return (~~(x * base + 0.5)) / base;
}

var _AXIS_OFFSET = 10;

var point = function(x, y){
	this.x = x;
	this.y = y;
}
point.prototype.toString = function() {
	return (num4(this.x) + ',' + num4(this.y));
};
// add or minus a value for X
point.prototype.modX = function(x) {
	this.x += x;
	return this;
};
// add or minus a value for Y
point.prototype.modY = function(y) {
	this.y += y;
	return this;
};

var path = function(option) {
	this.option = option;
	this.attr = {};
	this.d = {};
}
// add some description for path with type and param(a point)
path.prototype.add = function(type, params) {
	!this.d[type] && (this.d[type] = []);
	params && this.d[type].push(params);
	return this;
};
//set or get a attr for the path.
path.prototype.a = function(key, value) {
	if (!value) { return this.attr[key]; }
	this.attr[key] = value;
	return this;
};
// make a string from the path object, 
// order is squence of path type, likes M 0,0 l 1,1 z.
path.prototype.toString = function(order) {
	var str = [];
	if(!Array.isArray(order)) { 
		order = [];
		//throw "toString method need a order for output" ; 
		var _d = this.d;
		Object.keys(_d).forEach(function (key) {
			order.push(key);
		});
	}

	// stringify the [d] description.
	var type_str_array = [];
	for (var i = 0; i < order.length; i++) {
		var _type = order[i];
		var _d = this.d[_type];
		if(!_d) { throw "the path type in order is undefined." ;}
		type_str_array.push(_type + (_d == [] ? ' ' : '') + _d.join(' '));
	}
	str.push('d="' + type_str_array.join(' ') + '"');

	// stringify the style for the path.
	if(typeof this.option === 'object') {
		var style_str_array = [];
		var _option = this.option;
		Object.keys(_option).forEach(function (key) {
			var val = _option[key];
			style_str_array.push(key + ':' + val);
		});
		str.push('style="' + style_str_array.join(';') + '"');
	}

	//stringify the other attr.
	var _attr = this.attr;
	Object.keys(_attr).forEach(function (key) {
		var _value = _attr[key];
		_value && str.push(key + '="' + _value + '"');
	});

	return '<path ' + str.join(' ') + ' />';
};

// H: the height of the hexagon grid
// size: the board size, likes 11 * 11.
// style: <path> style.
var draw = function (H, size, angle, style, aux_node) {
	H = H || 200;
	size = size || 11;
	style = style || { "stroke": "#ff0000", "stroke-width": "2", "fill": "transparent" }
	aux_node = aux_node || ''
	// h: height for each little hexagon.
	var h = H / size;
	// d: half of h.
	var d = h / 2;
	// offset for hexagon grid.
	var x_offset_base = d * Math.sqrt(3) / 3,
		y_offset_base = d;
	// the point of the vertex(top) of the hexagon grid
	var X0 = (H + h / 3) * Math.sqrt(3) / 2,
		Y0 = d / 3 + d;

	var W_a = 2 * X0,
		H_a = H + h / 3
	
	var path_array = [];

	var vertex = {
		top: new point(X0, 0),
		left: new point(0, H_a / 2),
		bottom: new point(X0, H_a), // H + 2 * d / 3
		right: new point(2 * X0, H_a / 2),
		center: new point((H + d) * Math.sqrt(3) / 2, (H + d) / 2)
	}
	// lift-top edge(red)
	var edge_path_left_top = new path({ "fill": "#ff0000" });
	edge_path_left_top.add('M', vertex.center)
			 .add('L', vertex.left)
			 .add('L', vertex.top)
			 .add('z')
			 .a('id', 'hex_grid_edge_left_top')
			 .a('class', 'hex-grid-edge-left-top');
	path_array.push(edge_path_left_top);
	// right-bottom edge(red)
	var edge_path_right_bottom = new path({ "fill": "#ff0000" });
	edge_path_right_bottom.add('M', vertex.center)
			 .add('L', vertex.right)
			 .add('L', vertex.bottom)
			 .add('z')
			 .a('id', 'hex_grid_edge_right_bottom')
			 .a('class', 'hex-grid-edge-right-bottom');
	path_array.push(edge_path_right_bottom);
	// left-bottom edge(blue)
	var edge_path_left_bottom = new path({ "fill": "#0000ff" });
	edge_path_left_bottom.add('M', vertex.center)
			 .add('L', vertex.left)
			 .add('L', vertex.bottom)
			 .add('z')
			 .a('id', 'hex_grid_edge_left_bottom')
			 .a('class', 'hex-grid-edge-left-bottom');
	path_array.push(edge_path_left_bottom);
	// right-top edge(blue)
	var edge_path_right_top = new path({ "fill": "#0000ff" });
	edge_path_right_top.add('M', vertex.center)
			 .add('L', vertex.right)
			 .add('L', vertex.top)
			 .add('z')
			 .a('id', 'hex_grid_edge_right_top')
			 .a('class', 'hex-grid-edge-right-top');
	path_array.push(edge_path_right_top);

	for(var i = 0; i < size; i++){
		var start = new point(X0 + h * i * Math.sqrt(3) / 2, Y0 + d * i);
		for(var j = 0; j < size; j++){
			var current = new point(start.x - h * j * Math.sqrt(3) / 2, start.y + d * j);
			var _path = new path(style);
			_path.add('M', current.modX(-x_offset_base).modY(y_offset_base))
				.add('l', new point(2 * x_offset_base, 0))
				.add('l', new point(x_offset_base, -y_offset_base))
				.add('l', new point(-x_offset_base, -y_offset_base))
				.add('l', new point(-2 * x_offset_base, 0))
				.add('l', new point(-x_offset_base, y_offset_base))
				.add('z');
			_path.a('id', 'hex_' + (size - j - 1) + '_' + (i))
				 .a('class', 'hex-hexagon');
			//path_array.push(_path.toString(['M', 'l', 'z']));
			path_array.push(_path);
		}
	}
	var svg_attr = {
	    xmlns: "http://www.w3.org/2000/svg",
	    "xmlns:xlink": "http://www.w3.org/1999/xlink",
	    version: "1.1",
	    viewBox: "0 0 " + num4(2 * X0) + ' ' + num4(H_a),
	    transform: 'rotate(' + angle + ' ' + vertex.center + ')',
	    		  // +'translate(' + (-X0 * (1 - Math.sqrt(3) / 2)) + ',' + (-H_a * (1 - Math.sqrt(3) / 2) / 2) + ')',
	    id: 'svg_root',
	    width: "100%",
	    height: "100%",
	    // preserveAspectRatio: 'xMaxYMax meet'
	};
	var svg_attr_array = [];
	Object.keys(svg_attr).forEach(function (key) {
		svg_attr_array.push(key + '="' + svg_attr[key] + '"');
	});
	var svg_str = '<svg ' 
			    + svg_attr_array.join(' ')
			    + '>'
			    +'<g id="svg_g">'
			    + path_array.join('') 
			    + '</g>'
			    + aux_node
			    + '</svg>';
	return svg_str;
}

var draw_flat = function (H, size, style, aux_node) {
	H = H || 200;
	size = size || 11;
	style = style || { "stroke": "#ff0000", "stroke-width": "2", "fill": "transparent" }
	aux_node = aux_node || ''
	// h: height for each little hexagon.
	var h = H / size;
	// d: half of h.
	var d = h / 2;
	// offset for hexagon grid.
	var x_offset_base = d,
		y_offset_base = d * Math.sqrt(3) / 3;
	// the point of the vertex(top) of the hexagon grid
	var X0 = d / 3 + d,
		Y0 = d / (Math.sqrt(3) / 2);
	
	var path_array = [];

	var vertex = {
		top: new point(H, 0),
		left: new point(0, 0),
		bottom: new point(H / 2, H * Math.sqrt(3) / 2),
		right: new point(H * 3 / 2, H * Math.sqrt(3) / 2),
		center: new point(H * 3 / 4, H * Math.sqrt(3) / 4)
	}
	// lift-top edge(red)
	var edge_path_left_top = new path({ "fill": "#ff0000" });
	edge_path_left_top.add('M', vertex.center)
			 .add('L', vertex.left)
			 .add('L', vertex.top)
			 .add('z')
			 .a('id', 'hex_grid_edge_left_top')
			 .a('class', 'hex-grid-edge-left-top');
	path_array.push(edge_path_left_top);
	// right-bottom edge(red)
	var edge_path_right_bottom = new path({ "fill": "#ff0000" });
	edge_path_right_bottom.add('M', vertex.center)
			 .add('L', vertex.right)
			 .add('L', vertex.bottom)
			 .add('z')
			 .a('id', 'hex_grid_edge_right_bottom')
			 .a('class', 'hex-grid-edge-right-bottom');
	path_array.push(edge_path_right_bottom);
	// left-bottom edge(blue)
	var edge_path_left_bottom = new path({ "fill": "#0000ff" });
	edge_path_left_bottom.add('M', vertex.center)
			 .add('L', vertex.left)
			 .add('L', vertex.bottom)
			 .add('z')
			 .a('id', 'hex_grid_edge_left_bottom')
			 .a('class', 'hex-grid-edge-left-bottom');
	path_array.push(edge_path_left_bottom);
	// right-top edge(blue)
	var edge_path_right_top = new path({ "fill": "#0000ff" });
	edge_path_right_top.add('M', vertex.center)
			 .add('L', vertex.right)
			 .add('L', vertex.top)
			 .add('z')
			 .a('id', 'hex_grid_edge_right_top')
			 .a('class', 'hex-grid-edge-right-top');
	path_array.push(edge_path_right_top);

	for(var i = 0; i < size; i++){
		var start = new point(X0 + h * i * Math.sqrt(3) / 2, Y0 + d * i);
		for(var j = 0; j < size; j++){
			var current = new point(start.x - h * j * Math.sqrt(3) / 2, start.y + d * j);
			var _path = new path(style);
			_path.add('M', current.modX(-x_offset_base).modY(y_offset_base))
				.add('l', new point(2 * x_offset_base, 0))
				.add('l', new point(x_offset_base, -y_offset_base))
				.add('l', new point(-x_offset_base, -y_offset_base))
				.add('l', new point(-2 * x_offset_base, 0))
				.add('l', new point(-x_offset_base, y_offset_base))
				.add('z');
			_path.a('id', 'hex_' + (size - j) + '_' + (i + 1))
				 .a('class', 'hex-hexagon');
			//path_array.push(_path.toString(['M', 'l', 'z']));
			path_array.push(_path);
		}
	}
	var svg_attr = {
	    xmlns: "http://www.w3.org/2000/svg",
	    "xmlns:xlink": "http://www.w3.org/1999/xlink",
	    version: "1.1",
	    viewBox: "0 0 " + num4(Math.sqrt(3) * H) + ' ' + num4(H + h),
	    // transform: 'rotate(' + angle + ' ' + vertex.center + ')',
	    id: 'svg_root',
	    preserveAspectRatio: 'xMinYMin'
	};
	var svg_attr_array = [];
	Object.keys(svg_attr).forEach(function (key) {
		svg_attr_array.push(key + '="' + svg_attr[key] + '"');
	});
	var svg_str = '<svg ' 
			    + svg_attr_array.join(' ')
			    + '>' + aux_node
			    +'<g id="svg_g">'
			    + path_array.join('') 
			    + '</g></svg>';
	return svg_str;
}

module.exports = {
	draw: draw,
	draw_flat: draw_flat
};
