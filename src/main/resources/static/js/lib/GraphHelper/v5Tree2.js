function showCluster3(data,data1,property,divID) {
    console.log(property)
    //定义legend
    legendProperty = [{
        "name":"out property",
        "color":"#CD9B1D"

    },{
        "name":"in property",
        "color":"#CD5C5C"

    },
        {
            "name":"class",
            "color":"#2F4F4F"

        },
    ]

    var lineColor_0 = "#CD9B1D"; //连线颜色
    var lineColor_1 = "#2F4F4F" //class
    var lineColor_2 = "#CD5C5C" //in

    // 树状图因为默认是上往下渲染的，改成从左往右渲染后会发现width和height都倒过来了，可以看具体参数的细节
    let width = 700
    let height = 600



    var svg = d3.select("#"+divID).append("svg")
        .attr("width", width)
        .attr("height", height)

    var legend = svg.append('defs')
        .append('g')
        .attr('id', 'graph_legend_lp')

    legend.append('line')
        .attr('x1', 0)
        .attr('y1', 0)
        .attr('x2', 15)
        .attr('y2', 0)
        .style('stroke', 'inherit')


    let ele = svg.selectAll('graph-item').data(legendProperty);

    let ent = ele.enter().append('g')
        .attr('class', 'graph-item')

    ent.append('use')
        .attr('x', (d,i) => i * 110 + 50)
        .attr('y', 20)
        .attr('xlink:href', '#graph_legend_lp')
        .attr('stroke', d => d.color)
        .style('cursor', 'pointer')

    ent.append('text')
        .attr('x', (d,i) => i * 110 + 70)
        .attr('y', 20)
        .attr('dy', '.2em')
        .attr('fill', '#444')
        .style('font-size', '13px')
        .style('cursor', 'pointer')
        .text(d => d.name)

    var g1 = svg
        .append("g")
        .attr("transform", "rotate(180,180,120)translate(50,-150)scale(0.5)");

    var g2 = svg
        .append("g")
        .attr("transform", "translate(340,90)scale(0.5)");

    var hierarchyData = d3.hierarchy(data).sum(function(d) {
        return d.value;
    });

    var hierarchyData2 = d3.hierarchy(data1).sum(function(d) {
        return d.value;
    });

// 创建一个树状图
    var tree = d3
        .tree()
        .size([height, width- 200])
        .separation(function(a, b) {
            return (a.parent == b.parent ? 1 : 2) / a.depth;
        });

    var treeData = tree(hierarchyData);
    var treeData2 = tree(hierarchyData2);

    var nodes = treeData.descendants();
    var links = treeData.links();

    var nodes2 = treeData2.descendants();
    var links2 = treeData2.links();

    var generator = d3
        .linkHorizontal()
        .x(function(d) {
            return d.y;
        })
        .y(function(d) {
            return d.x;
        });

    // var nodes = cluster.nodes(data);
    // var links = cluster.links(nodes);
    // console.log(links)
    //
    //
    // var nodes2 = cluster.nodes(data1);
    // var links2 = cluster.links(nodes2)
    // console.log(links2)

    //先node再link，箭头不会被node覆盖
    var node = g1.selectAll(".node")
        .data(nodes)
        .enter()
        .append("g")
        .attr("class", function (d,i) {
            if(i===0){
                return "root"
            }
            else return "node"
        })
        .attr("transform", function(d) {
            return "translate(" + d.y + "," + d.x + ")";
        })

    var node2 = g2.selectAll(".node")
        .data(nodes2)
        .enter()
        .append("g")
        .attr("class", function (d,i) {
            if(i===0){
                return "root"
            }
            else return "node"
        })

        .attr("transform", function(d) {
            return "translate(" + d.y + "," + d.x + ")";
        })


    var root = g1.selectAll(".root")
        .append("circle")
        .attr("r", 8.5);

    var root2 = g2.selectAll(".root")
        .append("circle")
        .attr("r", 8.5);

    var link = g1.selectAll(".link")    //如果有很多pattern，这样选择会修改所有的
        .data(links)
        .enter()
        .append("path")
        .attr("class", "link")
        .attr("marker-start",function (dd,i) {
            if(dd.target.data.inOrOut===0) {

                var arrowMarker = svg.append("marker")

                    .attr("id", "arrow_start_1" +divID+  i)
                    .attr("markerUnits", "userSpaceOnUse")
                    .attr("markerWidth", "16")
                    .attr("markerHeight", "15")
                    .attr("viewBox", "0 0 12 12")
                    .attr("refX", function () {
                        return 6

                    })
                    .attr("refY", 6)
                    .attr("orient", function () {
                        return "auto"

                    })
                    .append("svg:path")
                    .attr("d", "M10,2 L6,6 L10,10 L2,6 L10,2")
                    .attr("fill", function () {
                        if (dd.target.data.type===0 || dd.source.data.type===0){
                            if(dd.target.data.inOrOut===0 || dd.source.data.inOrOut===0){
                                return lineColor_2
                            }else{
                                return lineColor_0
                            }

                        }else{
                            return lineColor_1
                        }
                    });

                return "url(#arrow_start_1" +divID+  i + ")";
            }
            else return;


        })
        .attr("marker-end", function (dd, i) {
            if(dd.target.data.inOrOut===0) {
                return;

            }
            else{
                var arrowMarker = svg.append("marker")

                    .attr("id", "arrow_start_1" + divID+ i)
                    .attr("markerUnits", "userSpaceOnUse")
                    .attr("markerWidth", "16")
                    .attr("markerHeight", "15")
                    .attr("viewBox", "0 0 12 12")
                    .attr("refX", function () {
                        return 9

                    })
                    .attr("refY", 6)
                    .attr("orient", function () {
                        return "auto"
                    })
                    .append("svg:path")
                    .attr("d", "M2,2 L10,6 L2,10 L6,6 L2,2")
                    .attr("fill", function () {
                        if (dd.target.data.type===0 || dd.source.data.type===0){
                            // if(dd.target.data.inOrOut===0 || dd.source.data.inOrOut===0){
                            //     return lineColor_2
                            // }else{
                                return lineColor_0
                            // }

                        }else{
                            return lineColor_1
                        }
                    });

                return "url(#arrow_start_1" + divID+ i + ")";

            }


        })
        .attr("stroke",function (d){
            if(d.target.data.type===0){
                if(d.target.data.inOrOut===0){
                    return lineColor_2
                }else{
                    return lineColor_0
                }
            }else{
                return lineColor_1
            }
        })
        .attr("d", function(d) {
            var start = { x: d.source.x, y: d.source.y };
            var end = { x: d.target.x, y: d.target.y-150 };
            return generator({ source: start, target: end });
        });

    var link2 = g2.selectAll(".link")    //如果有很多pattern，这样选择会修改所有的
        .data(links2)
        .enter()
        .append("path")
        .attr("class", "link")
        .attr("marker-start",function (dd,i) {

            if(dd.target.data.inOrOut===0) {

                var arrowMarker = svg.append("marker")

                    .attr("id", "arrow_start" +divID+  i)
                    .attr("markerUnits", "userSpaceOnUse")
                    .attr("markerWidth", "16")
                    .attr("markerHeight", "15")
                    .attr("viewBox", "0 0 12 12")
                    .attr("refX", function () {
                        return 6

                    })
                    .attr("refY", 6)
                    .attr("orient", function () {
                        return "auto"

                    })
                    .append("svg:path")
                    .attr("d", "M10,2 L6,6 L10,10 L2,6 L10,2")
                    .attr("fill", function () {
                        if (dd.target.data.type===0 || dd.source.data.type===0){
                            if(dd.target.data.inOrOut===0 || dd.source.data.inOrOut===0){
                                return lineColor_2
                            }else{
                                return lineColor_0
                            }

                        }else{
                            return lineColor_1
                        }
                    });

                return "url(#arrow_start" +divID+  i + ")";
            }
            else return;


        })
        .attr("marker-end", function (dd, i) {
            if(dd.target.data.inOrOut===0) {
                return;

            }
            else{
                var arrowMarker = svg.append("marker")

                    .attr("id", "arrow" + divID+ i)
                    .attr("markerUnits", "userSpaceOnUse")
                    .attr("markerWidth", "16")
                    .attr("markerHeight", "15")
                    .attr("viewBox", "0 0 12 12")
                    .attr("refX", function () {
                        return 9

                    })
                    .attr("refY", 6)
                    .attr("orient", function () {
                        return "auto"

                    })
                    .append("svg:path")
                    .attr("d", "M2,2 L10,6 L2,10 L6,6 L2,2")
                    .attr("fill", function () {
                        if (dd.target.data.type===0 || dd.source.data.type===0){
                            if(dd.target.data.inOrOut===0 || dd.source.data.inOrOut===0){
                                return lineColor_2
                            }else{
                                return lineColor_0
                            }

                        }else{
                            return lineColor_1
                        }
                    });

                return "url(#arrow" + divID+ i + ")";

            }


        })
        .attr("stroke",function (d){
            if(d.target.data.type===0 ){
                if(d.target.data.inOrOut===0){
                    return lineColor_2
                }else if(d.target.data.inOrOut===1){
                    return lineColor_0
                }
            }else{

                return lineColor_1
            }

        })
        .attr("d", function(d) {
            var start = { x: d.source.x, y: d.source.y };
            var end = { x: d.target.x, y: d.target.y-150 };
            return generator({ source: start, target: end });
        });




    //
    //
    //
    node.append("text")
        .attr("dx", function(d) {
            return d.children ? 90 : 8;
        })
        .attr("dy", function (d) {
            return d.children ? 25:3
        })
        .attr("transform", "rotate(180,18,120)translate(170,240)")
        // .attr('transform', function (d) { return d.x < 180 ? null : 'rotate(270)' })
        .style("text-anchor", function(d) {
            return d.children ? "end" : "end";
        })
        .attr("font-size",17)
        .text(function(d) {
            // return d.data.name;
            return d.children ? "" : d.data.name;
        });
    //
    node2.append("text")
        .attr("dx", function(d) {
            return d.children ? 18 : 8;
        })
        .attr("dy", function (d) {
            if(! d.children){
                return 3
            }else{
                return 25
            }
        })
        .attr("transform", "translate(-150,0)")
        .attr("font-size",17)
        .style("text-anchor", function(d) {
            return d.children ? "end" : "start";
        })
        .text(function(d) {
            return d.children ? "" : d.data.name;
        });

    svg.append('g')
        .attr("transform", "translate(260,180)scale(0.4)")
        .append("line")
        // .data(root)
        .attr("x1", function (d) {

            return 125;
        })
        .attr("y1", function (d) {
            return 150;
        })
        .attr("x2", function (d) {
            return 200
        })
        .attr("y2", function (d) {
            return 150
        })
        .attr("stroke", "black")
        .attr("stroke-width", "2px")
        .attr("marker-end","url(#arrowProperty)");

    svg.append('g')
        .attr("transform", "translate(300,230)scale(0.6)")
        .append("text")
        .text(property)
        .data(root)
        .attr("dx", 45)
        .style("text-anchor", "start")
        .attr("dy", 140)
        // .attr("font-size",20)

        .attr("textLength",80)
        // .attr("lengthAdjust","spacingAndGlyphs")
        // .attr("font-style","italic")
        // .attr("font-weight","bold")
        // .attr("font-family","Georgia, serif")



    var defs = svg.append("defs");
    var arrowMarker = defs.append("marker")
        .attr("id","arrowProperty")
        .attr("markerUnits","strokeWidth")
        .attr("markerWidth","120")
        .attr("markerHeight","120")
        .attr("viewBox","0 0 120 120")
        .attr("refX","6")
        .attr("refY","6")
        .attr("orient","auto");

    var arrow_path = "M2,2 L10,6 L2,10 L6,6 L2,2";

    arrowMarker.append("path")
        .attr("d",arrow_path)
        .attr("fill","#000");





}