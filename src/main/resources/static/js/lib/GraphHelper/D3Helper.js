


function showSnippet(nodes,links,divid,tooltip,options) {


    if(options.flag){
        links.forEach(function(e){
            let sourceNode = nodes.filter(function(n){

                if((n.name !== undefined) && (n.name !== null) && (n.name !== '')){
                    return n.name === e.source;
                }

            })[0];

            let targetNode = nodes.filter(function(n){
                if((n.name !== undefined) && (n.name !== null) && (n.name !== '')){
                    return n.name === e.target;
                }

            })[0];

            // console.log(sourceNode)
            e.source = sourceNode;
            e.target = targetNode;

        });
    }

    var nodesFontType = "Times New Roman"; //节点字体
    var nodesFontSize = 14; //节点字号
    var lineFontType = "Times New Roman"; //关系字体
    var lineFontSize = 14; //关系字号
    var lineColor_0 = "#CD9B1D"; //连线颜色
    var lineColor_1 = "#2F4F4F"
    var lineColor_2 ="#4682B4"
    var radius = 15;//node的半径

    var width = options.width//498 //画布宽
    var height = options.height//178; //画布高


    //D3力导向布局
    var force = d3.layout.force()
        .nodes(nodes)
        .links(links)
        .size([width, height])
        .linkDistance(200)
        .charge(-1500)
        .start();

    // 全图缩放器
    var zoom = d3.behavior.zoom()
        .scaleExtent([0.25, 2])
        .on('zoom', zoomFn);

    var svg = d3.select("#" + divid).append("svg")
        .attr("width", width)
        .attr("height", height)
        // .attr("style", "background-color:" + backgroundColor)
        .call(zoom)
        .on('dblclick.zoom', null);

    // 缩放层（位置必须在 container 之前）
    var zoomOverlay = svg.append('rect')
        .attr('width', width)
        .attr('height', height)
        .style('fill', 'none')
        .style('pointer-events', 'all');

    var container = svg.append('g')
        .attr('transform', 'scale(' + 0.8 + ')')
        .attr('class', 'container');


    //移动
    function zoomFn() {
        console.log('开始移动了');
        const {
            translate,
            scale
        } = d3.event;
        console.log(container);

        container.attr('transform', 'translate(' + translate + ')scale(' + scale * 0.6 + ')');
    }

    //设置连接线
    var edges_path = container.selectAll(".edgepath")
        .data(links)
        .enter()
        .append("path")
        .attr("marker-end", function (dd, i) {
            console.log(dd)
            var arrowMarker = container.append("marker")

                .attr("id", "arrow" + i)
                .attr("markerUnits", "userSpaceOnUse")
                .attr("markerWidth", "16")
                .attr("markerHeight", "15")
                .attr("viewBox", "0 0 12 12")
                .attr("refX", 9)
                .attr("refY", 6)
                .attr("orient", "auto")
                .append("svg:path")
                .attr("d", "M2,2 L10,6 L2,10 L6,6 L2,2")
                .attr("fill", function () {
                    if (dd.target.type===0){
                        return lineColor_0
                    }else{
                        return lineColor_1
                    }
                });

            return "url(#arrow" + i + ")";
        })
        .style("stroke", function (d) {


            if (d.target.type===0){
                return lineColor_0
            }else{
                return lineColor_1
            }
        })
        .style("stroke-width", 1.5)
        .on("mouseover", function (d) {
            console.log('放到连接线');

            edges_text.style("fill-opacity", function (edge) {
                if (edge === d) {
                    return 1;
                }
                return 0;
            })
            edges_path.style("stroke-width", function (edge) {
                if (edge === d) {
                    return 4;
                }
                return 1.5;
            })

            tooltip.html("<span>" + d.relation + "</span>")
                .style("left", (d3.event.pageX) + "px")
                .style("top", (d3.event.pageY + 20) + "px")
                .style("display", "block")
                .style("visibility", "visible");


        })
        .on("mouseout", function (d, i) {
            //显示连线上的文字
            edges_text.style("fill-opacity", 1);
            edges_path.style("stroke-width", 1.5);
            // 隐藏提示信息
            tooltip.style("visibility", "hidden");
        });



    //边上的文字（人物之间的关系），连接线
    var edges_text = container.selectAll(".linetext")
        .data(links)
        .enter()
        .append("svg:g")
        .attr("class", "linetext")
        .attr("fill-opacity", 1);

    edges_text.append("svg:text")
        .style("font-size", (12 + "px"))
        .style("font-family", lineFontType)
        .style("fill", "#000000")
        // .attr("y", ".36em")
        .attr('text-anchor', "middle")
        .text(function (d) {
            var str = d.relation.trim().split(" ")
            console.log(str)
            if(str.length==1) return str[0]
            else return str[0]+"…"

            return str[0];
        })
        .on("mouseover", function (d, i) {

            tooltip.html("<span>" + d.uri + "</span>")
                .style("left", (d3.event.pageX) + "px")
                .style("top", (d3.event.pageY + 20) + "px")
                .style("display", "block")
                .style("visibility", "visible");
        })
        .on("mouseout", function (d, i) {

            tooltip.style("visibility", "hidden");

        });



    // 节点设置，包含圆形图片节点（人物头像）
    var circle = container.selectAll("circle")
        .data(nodes)
        .enter()
        .append("circle")

        .attr("r", radius)
        .attr("fill", function (d) {
            console.log(d)
            if(d.type === 0) return lineColor_0
            else if(d.type === 1) return lineColor_1
            else return lineColor_2
        })
        .on("mouseover", function (d, i) {
            console.log('放到人物头像');
            //隐藏其它连线上文字
            edges_text.style("fill-opacity", function (edge) {
                if (edge.source === d || edge.target === d) {
                    return 1;
                }
                if (edge.source !== d && edge.target !== d) {
                    return 0;
                }
            })
            //其它节点亮度调低
            circle.style("opacity", function (edge) {
                var v = d.name;
                if (edge.name == v || (edge[v] != undefined && edge[v].name == v)) {
                    return 1;
                } else {
                    return 0.2;
                }
            })
            //其他连线亮度调低
            edges_path.style("opacity", function (edge) {
                if (edge.source === d || edge.target === d) {
                    return 1;
                }
                if (edge.source !== d && edge.target !== d) {
                    return 0.2;
                }
            })
            //其他节点文字亮度调低
            nodes_text.style("opacity", function (edge) {
                var v = d.name;
                if (edge.name == v || (edge[v] != undefined && edge[v].name == v)) {
                    return 1;
                } else {
                    return 0.2;
                }
            })



        })
        .on("mouseout", function (d, i) {
            //显示连线上的文字
            edges_text.style("fill-opacity", 1);
            edges_path.style("opacity", 1);
            circle.style("opacity", 1);
            nodes_text.style("opacity", 1);
            tooltip.style("visibility", "hidden");

        })
        .call(force.drag);




    // 节点文字设置
    var nodes_text = container.selectAll(".nodetext")
        .data(nodes)
        .enter()
        .append("text")
        .style("font-size", (nodesFontSize + "px"))
        .style("fill", "#000000")
        .style("font-family", nodesFontType)
        .attr('x', function (d) {
            var str = d.name
            var result =""

            if(str.length<10) result = str
            else result = str.substring(0,10)+"…"

            // console.log(str.length)



            d3.select(this).append('tspan')
                .attr("dx", -nodesFontSize * (result.length/4))
                .attr("dy", 8)
                .text(function () {
                    return result;
                });

        })
        .on("mouseover", function (d, i) { //设置到文字上，别设置到node上，否则拖拽的时候会卡（似乎也没问题？？

            console.log('放到node文字:'+ d3.event.pageY);
            //隐藏其它连线上文字
            edges_text.style("fill-opacity", function (edge) {
                if (edge.source === d || edge.target === d) {
                    return 1;
                }
                if (edge.source !== d && edge.target !== d) {
                    return 0;
                }
            })
            //其他节点亮度调低
            circle.style("opacity", function (edge) {
                var v = d.name;
                if (edge.name == v || (edge[v] != undefined && edge[v].name == v)) {
                    return 1;
                } else {
                    return 0.2;
                }
            })
            //其他连线亮度调低
            edges_path.style("opacity", function (edge) {
                if (edge.source === d || edge.target === d) {
                    return 1;
                }
                if (edge.source !== d && edge.target !== d) {
                    return 0.2;
                }
            })
            //其他节点文字亮度调低
            nodes_text.style("opacity", function (edge) {
                var v = d.name;
                if (edge.name == v || (edge[v] != undefined && edge[v].name == v)) {
                    return 1;
                } else {
                    return 0.2;
                }
            })

            tooltip.html("<span>" + d.name + "</span>")
                .style("left", (d3.event.pageX) + "px")
                .style("top", (d3.event.pageY + 20 ) + "px")
                .style("display", "block")
                .style("visibility", "visible");
        })
        .on("mouseout", function (d, i) {
            //显示连线上的文字
            edges_text.style("fill-opacity", 1);
            edges_path.style("opacity", 1);
            circle.style("opacity", 1);
            nodes_text.style("opacity", 1);
            tooltip.style("visibility", "hidden");

            // d3.select("#tooltip").remove();

        })
        .call(force.drag);


    //   拖动节点
    var drag = force.drag()
        .on("dragstart", function (d, i) {
            d.fixed = true; //拖拽开始后设定被拖拽对象为固定
            d3.event.sourceEvent.stopPropagation();
        })
        .on("dragend", function (d, i) {})
        .on("drag", function (d, i) {});

    //力学图运动开始时
    force.on("start", function () {});

    //力学图运动结束时
    force.on("end", function () {});

    force.on("tick", function () {
        edges_path.attr("d", function (d) {
            var tan = Math.abs((d.target.y - d.source.y) / (d.target.x - d.source
                .x)); //圆心连线tan值
            var x1 = d.target.x - d.source.x > 0 ? Math.sqrt(radius * radius / (
                tan * tan + 1)) + d.source.x :
                d.source.x - Math.sqrt(radius * radius / (tan * tan +
                1)); //起点x坐标
            var y1 = d.target.y - d.source.y > 0 ? Math.sqrt(radius * radius *
                tan * tan / (tan * tan + 1)) + d.source.y :
                d.source.y - Math.sqrt(radius * radius * tan * tan / (tan *
                tan + 1)); //起点y坐标
            var x2 = d.target.x - d.source.x > 0 ? d.target.x - Math.sqrt(radius * radius / (1 + tan * tan)) :
                d.target.x + Math.sqrt(radius * radius / (1 + tan *
                tan)); //终点x坐标
            var y2 = d.target.y - d.source.y > 0 ? d.target.y - Math.sqrt(radius * radius * tan * tan / (1 + tan * tan)) :
                d.target.y + Math.sqrt(radius * radius * tan * tan / (1 + tan *
                tan)); //终点y坐标
            if (d.target.x - d.source.x == 0 || tan == 0) { //斜率无穷大的情况或为0时
                y1 = d.target.y - d.source.y > 0 ? d.source.y + radius : d.source.y - radius;
                y2 = d.target.y - d.source.y > 0 ? d.target.y - radius : d.target.y + radius;
            }
            // 防报错
            if (!x1 || !y1 || !x2 || !y2) {
                return
            }
            // if (d.linknum == 0) { //设置编号为0的连接线为直线，其他连接线会均分在两边
            d.x_start = x1;
            d.y_start = y1;
            d.x_end = x2;
            d.y_end = y2;
            return 'M' + x1 + ' ' + y1 + ' L ' + x2 + ' ' + y2;
            // }


        });

        //更新连接线上文字的位置
        edges_text.attr("transform", function (d) {
            // 防止报错
            if (!d.x_start || !d.y_start || !d.x_end || !d.y_end) {
                return
            }
            return "translate(" + (d.x_start + d.x_end) / 2 + "," + ((+d.y_start) + (+d
                    .y_end)) / 2 +
                ")" + " rotate(" + Math.atan((d.y_end - d.y_start) / (d.x_end - d.x_start)) *
                180 / Math.PI + ")";
        });


        //更新结点图片和文字
        circle.attr("cx", function (d) {
            return d.x
        });
        circle.attr("cy", function (d) {
            return d.y
        });

        nodes_text.attr("x", function (d) {
            return d.x
        });
        nodes_text.attr("y", function (d) {
            return d.y
        });
    });

}