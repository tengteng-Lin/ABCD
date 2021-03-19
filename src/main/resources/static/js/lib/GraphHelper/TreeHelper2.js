
function showTree(root,title,divID) {

    var width = 800 // edited
    var height = 400;

    var svg = d3.select("#"+divID).append("svg")
        .attr("width", width + 80) // 画布扩大，防止边缘文字被遮挡
        .attr("height", height)
        .append("g")
        .attr("transform", "translate(40,40)"); // 将图整体下移，以防止顶部节点被遮挡

    var tree = d3.tree()
        .size([width, height - 200])
        .separation(function (a, b) {
            // return 10
            return (a.parent === b.parent ? 1 : 2);
        });


    var hierarchyData = d3.hierarchy(root).sum(function(d) {
        return d.value;
    });

    var generator = d3
        .linkVertical()
        .x(function(d) {
            return d.x;
        })
        .y(function(d) {
            return d.y;
        });



    var treeData = tree(hierarchyData);

    var nodes = treeData.descendants();
    var links = treeData.links();

    var link = svg.selectAll(".link")
        .data(links)
        .enter()
        .append("path")
        .attr("class", "treelink")
        .attr("d", function(d) {

            var start = { x: d.source.x, y: d.source.y };
            var end = { x: d.target.x, y: d.target.y };

            return generator({ source: start, target: end });
        })
        .attr("marker-end", function (dd, i) {

            var arrowMarker = svg.append("marker")

                .attr("id", "arrow" + i)
                .attr("markerUnits", "userSpaceOnUse")
                .attr("markerWidth", "16")
                .attr("markerHeight", "15")
                .attr("viewBox", "0 0 12 12")
                .attr("refX", 10)
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
        .on("mouseover",function(d){

            d3.select(".tooltip")
                .text(d.data.name)
                .style("left", (d3.event.pageX) + "px")
                .style("top", (d3.event.pageY + 20 ) + "px")
                .style("display", "block")
                .style("visibility", "visible");
        })
        .on("mouseout", function (d, i) {

            d3.select(".tooltip")
                .style("visibility", "hidden");

        })

    node.append("circle")
        .attr("fill","#808080")
        .attr("r", 4.5);

    // node.append("rect")
    //     .attr("y",function (d) {
    //         return 0
    //         // idx = nodes.indexOf(d)
    //         // if(idx%2===1){
    //         //     return 0
    //         // }else{
    //         //     return 30
    //         // }
    //
    //     })
    //     .attr("x",function (d) {
    //         console.log(d)
    //         return -d.data.name.length/2*7;
    //
    //     })
    //     .attr("height",15)
    //     .attr("width",function(d){
    //         return d.data.name.length*7
    //     })
    //     .attr('fill', '#fff')
    //     .attr('stroke', 'steelblue')
    //     .attr('strokeWidth', '1px')

    node.append("text")
        // .attr("dx", function (d) {
        //     return -d.data.name.length/2*8+2;
        // })
        .attr("text-anchor", function (d) {
            return "middle";
        })
        .attr("dy", function (d) {

            return d.depth===0? -5:15
        })

        .text(function (d) {
            return d.depth===0?d.data.name:"";
        });

    // let svgGroup = d3.select('svg').select('g')
    // // 用来拖拽图以及扩大缩放
    // var zoom = d3.zoom()
    //     .scaleExtent([.1, 1])
    //     .on('zoom', function () {
    //         svgGroup.attr("transform", "translate(" + d3.event.translate + ") scale(" + d3.event.scale + ")");
    //     });
    //
    // svg.call(zoom)

}