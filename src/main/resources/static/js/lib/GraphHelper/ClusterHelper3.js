
function showCluster3(data,data1,property,divID) {
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
    var lineColor_1 = "#2F4F4F"
    var lineColor_2 = "#CD5C5C"

    // 树状图因为默认是上往下渲染的，改成从左往右渲染后会发现width和height都倒过来了，可以看具体参数的细节
    let width = 500
    let height = 300

    var cluster = d3.layout.cluster()
        .size([height, width-200]);

    var diagonal = d3.svg.diagonal()
        .projection(function(d) {

            return [d.y, d.x];
        });



    var svg = d3.select("#"+divID).append("svg")
        .attr("width", width)
        .attr("height", height)

    var legend = svg.append('defs')
        .append('g')
        .attr('id', 'graph')

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
        .attr('xlink:href', '#graph')
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
        .attr("transform", "rotate(180,180,120)translate(150,10)scale(0.4)");

    var g2 = svg
        .append("g")
        .attr("transform", "translate(300,110)scale(0.4)");

    var nodes = cluster.nodes(data);
    var links = cluster.links(nodes);
    console.log(links)


    var nodes2 = cluster.nodes(data1);
    var links2 = cluster.links(nodes2)
    console.log(links2)

    //先node再link，箭头不会被node覆盖
    var node = g1.selectAll(".node")
        .data(nodes)
        .enter()
        .append("g")
        .attr("class", function (d,i) {
            // console.log(d)
            // console.log(i)
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
            // console.log(d)

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
            console.log("marker-start_11:")
            console.log(dd)
            if(dd.target.inOrOut===0) {

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
                        if (dd.target.type===0 || dd.source.type===0){
                            if(dd.target.inOrOut===0 || dd.source.inOrOut===0){
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
            console.log("marker-end:")
            console.log(dd)

            if(dd.target.inOrOut===0) {
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
                        if (dd.target.type===0 || dd.source.type===0){
                            if(dd.target.inOrOut===0 || dd.source.inOrOut===0){
                                return lineColor_2
                            }else{
                                return lineColor_0
                            }

                        }else{
                            return lineColor_1
                        }
                    });

                return "url(#arrow_start_1" + divID+ i + ")";

            }


        })
        .attr("stroke",function (d){
            if(d.target.type===0){
                if(d.target.inOrOut===0){
                    return lineColor_2
                }else{
                    return lineColor_0
                }
            }else{
                return lineColor_1
            }
        })
        .attr("d", diagonal);

    var link2 = g2.selectAll(".link")    //如果有很多pattern，这样选择会修改所有的
        .data(links2)
        .enter()
        .append("path")
        .attr("class", "link")
        .attr("marker-start",function (dd,i) {
            console.log("marker-start:")
            console.log(dd)
            if(dd.target.inOrOut===0) {

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
                        if (dd.target.type===0 || dd.source.type===0){
                            if(dd.target.inOrOut===0 || dd.source.inOrOut===0){
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
            console.log("marker-end:")
            console.log(dd)

            if(dd.target.inOrOut===0) {
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
                        if (dd.target.type===0 || dd.source.type===0){
                            if(dd.target.inOrOut===0 || dd.source.inOrOut===0){
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
            if(d.target.type===0 ){

                if(d.target.inOrOut===0){
                    return lineColor_2
                }else if(d.target.inOrOut===1){
                    return lineColor_0
                }
            }else{

                return lineColor_1
            }

        })
        .attr("d", diagonal);




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
        .attr("transform", "rotate(180,18,120)translate(0,240)")
        // .attr('transform', function (d) { return d.x < 180 ? null : 'rotate(270)' })
        .style("text-anchor", function(d) {
            return d.children ? "end" : "end";
        })
        .attr("font-size",16)
        .text(function(d) {
            return d.children ? "" : d.name;
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
        .attr("font-size",16)
        .style("text-anchor", function(d) {
            return d.children ? "end" : "start";
        })
        .text(function(d) {
            return d.children ? "" : d.name;
        });

    svg.append('g')
        .attr("transform", "translate(210,110)scale(0.4)")
        .append("line")
        .data(root)
        .attr("x1", function (d) {

            return 0;
        })
        .attr("y1", function (d) {
            return 150;
        })
        .attr("x2", function (d) {
            return 210
        })
        .attr("y2", function (d) {
            return 150
        })
        .attr("stroke", "black")
        .attr("stroke-width", "2px")
        .attr("marker-end","url(#arrowProperty)");

    svg.append('g')
        .attr("transform", "translate(210,80)scale(0.6)")
        .append("text")
        .data(root)
        .attr("dx", 45)
        .style("text-anchor", "start")
        .attr("dy", 140)
        // .attr("font-size",20)

        .attr("textLength",50)
        .attr("lengthAdjust","spacingAndGlyphs")
        .attr("font-style","italic")
        .attr("font-weight","bold")
        .attr("font-family","Georgia, serif")
        .text(property)


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