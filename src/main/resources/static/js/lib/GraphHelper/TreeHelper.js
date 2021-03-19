
function showTree(root,title,divID) {

    var width = 800 // edited
    var height = 400;


    var tree = d3.layout.tree()
        .size([width, height - 200])
        .separation(function (a, b) {
            return (a.parent == b.parent ? 1 : 2);
        });



    var diagonal = d3.svg.diagonal()
        .projection(function (d) {
            return [d.x, d.y];  // edited
        });
    var svg = d3.select(divID).append("svg")
        .attr("width", width + 80) // 画布扩大，防止边缘文字被遮挡
        .attr("height", height)
        .append("g")
        .attr("transform", "translate(40,40)"); // 将图整体下移，以防止顶部节点被遮挡






    var nodes = tree.nodes(root);
    var links = tree.links(nodes);

    console.log(nodes);
    console.log(links);

    var link = svg.selectAll(".link")
        .data(links)
        .enter()
        .append("path")
        .attr("class", "treelink")
        .attr("d", diagonal)
        .attr("marker-end", function (dd, i) {

            var arrowMarker = svg.append("marker")

                .attr("id", "arrow" + i)
                .attr("markerUnits", "userSpaceOnUse")
                .attr("markerWidth", "16")
                .attr("markerHeight", "15")
                .attr("viewBox", "0 0 12 12")
                .attr("refX", 12)
                .attr("refY", 6)
                .attr("orient", "auto")
                .append("svg:path")
                .attr("d", "M2,2 L10,6 L2,10 L6,6 L2,2")
                // .attr("d", "M2,2 L10,6 L2,10 L"+dd.y+","+dd.x+" L2,2")
                .attr("fill", "#508DA3");

            return "url(#arrow" + i + ")";
        });

    var node = svg.selectAll(".node")
        .data(nodes)
        .enter()
        .append("g")
        .attr("class", "node")
        .attr("transform", function (d) {
            return "translate(" + d.x + "," + d.y + ")"; // edited
        })

    node.append("circle")
        .attr("r", 4.5);

    node.append("text")
        .attr("dx", function (d) {
            return d.children ? -8 : 8;
        })
        .attr("dy", 15)

        .text(function (d) {
            return d.name;
        });

    let svgGroup = d3.select('svg').select('g')
    // 用来拖拽图以及扩大缩放
    var zoom = d3.behavior.zoom()
        .scaleExtent([.1, 1])
        .on('zoom', function () {
            svgGroup.attr("transform", "translate(" + d3.event.translate + ") scale(" + d3.event.scale + ")");
        });

    svg.call(zoom)

}