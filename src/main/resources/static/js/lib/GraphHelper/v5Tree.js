function showCluster(dataset,divID) {
    // console.log(dataset)
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


    var outPropertyColor = "#CD9B1D"; //连线颜色
    var classColor = "#2F4F4F"
    var inPropertyColor = "#CD5C5C"



    let width = 400
    let height = 600
    var main = d3.select("#"+divID).append("svg")
        .attr("width", width)
        .attr("height", height)


    var legend = main.append('defs')
        .append('g')
        .attr('id', 'graph_legend_edp')

    legend.append('line')
        .attr('x1', 0)
        .attr('y1', 0)
        .attr('x2', 15)
        .attr('y2', 0)
        .style('stroke', 'inherit')


    let ele = main.selectAll('graph-item').data(legendProperty);

    let ent = ele.enter().append('g')
        .attr('class', 'graph-item')

    ent.append('use')
        .attr('x', (d,i) => i * 110 + 50)
        .attr('y', 20)
        .attr('xlink:href', '#graph_legend_edp')
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

    var svg = main.append("g")
        .attr("transform", "translate(120,30)scale(0.7)");


// 创建一个层级布局
    var hierarchyData = d3.hierarchy(dataset).sum(function(d) {
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

    var nodes = treeData.descendants();
    var links = treeData.links();

    console.log(links)

    var generator = d3
        .linkHorizontal()
        .x(function(d) {
            return d.y;
        })
        .y(function(d) {
            return d.x;
        });

    var node = svg.selectAll(".node")
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

    var root =svg.selectAll(".root")
        .append("circle")
        .attr("r", 8.5);

    node.append("text")
        .attr("dx", function(d) {
            return d.children ? -8 : 8;
        })
        .attr("dy", 3)
        .style("text-anchor", function(d) {
            return d.children ? "end" : "start";
        })
        .attr("font-size",18)
        .text(function(d) {
            // return d.children ? "" : d.name;
            return d.data.name;
        });

    // var gs = g
    //     .append("g")
    //     .selectAll("g")
    //     .data(nodes)
    //     .enter()
    //     .append("g")
    //     .attr("transform", function(d) {
    //         var cx = d.x;
    //         var cy = d.y;
    //         return "translate(" + cy + "," + cx + ")";
    //     });



//绘制文字
//     gs.append("text")
//         .attr("x", function(d) {
//             return d.children ? -90 : 10;
//         })
//         .attr("y", -5)
//         .attr("dy", 10)
//         .text(function(d) {
//             return d.data.name;
//         })
//         .on("mouseover", function(d) {    //交互
//             d3.select(this)
//                 .attr("fill", "red")
//         })
//         .on("mouseout",function(){
//             d3.select(this)
//                 .attr("fill", "#000")
//         })

    var link = svg.selectAll(".link")    //如果有很多pattern，这样选择会修改所有的
        .data(links)
        .enter()
        .append("path")
        .attr("class", "link")
        .attr("marker-start",function (dd,i) {
            // console.log("marker-start:")
            // console.log(dd)
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
                    .attr("fill", inPropertyColor);

                return "url(#arrow_start" +divID+  i + ")";
            }
            else return;


        })
        .attr("marker-end", function (dd, i) {
            // console.log("marker-end:")
            // console.log(dd)

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

                                return outPropertyColor

                        }else{
                            return classColor
                        }
                    });

                return "url(#arrow" + divID+ i + ")";

            }


        })
        .attr("stroke",function (dd){


            if (dd.target.data.type===0 || dd.source.data.type===0){
                if(dd.target.data.inOrOut===0 || dd.source.data.inOrOut===0){
                    return inPropertyColor
                }else{
                    return outPropertyColor
                }

            }else{
                return classColor
            }

        })
        .attr("d", function(d) {
            var start = { x: d.source.x, y: d.source.y };
            var end = { x: d.target.x, y: d.target.y };
            return generator({ source: start, target: end });
        });

}