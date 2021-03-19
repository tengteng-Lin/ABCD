document.write("<script type='text/javascript' src='http://d3js.org/d3.v5.min.js'></script>")

function initData2(svgEle) {
    var legendProperty = [{
        "name":"typed object",
        "color":"#CD9B1D"

    },{
        "name":"general node",
        "color":"#4682B4"

    },
        {
            "name":"literal",
            "color":"#2F4F4F"

        },
    ]

    var lineColor_0 = "#CD9B1D"; //typed object
    var lineColor_1 = "#2F4F4F" //literal
    var lineColor_2 = "#4682B4"


    //初始化
    let g, gPosition;
    //初始化力导向布局
    let forceSimulation = d3.forceSimulation()
        .force('charge', d3.forceManyBody().strength(-100))//电荷力
        .force('forceCollide', d3.forceCollide().radius(50))//检测碰撞
        .force('link', d3.forceLink().id(function (d) {
            return d.id
        }))//link

    initSVG()

    //首次执行
    function initSVG() {
        let zoom = d3.zoom()
            .scaleExtent([0.1, 10])
            .on("zoom", function (d) {
                g.attr("transform", function () {//初始化
                    return `translate(${d3.event.transform.x + gPosition.x},${d3.event.transform.y + gPosition.y}) scale(${d3.event.transform.k})`
                })
            })
        var legend = svgEle.append('defs')
            .append('g')
            .attr('id', 'graph_legend_relation')

        legend.append('line')
            .attr('x1', 0)
            .attr('y1', 0)
            .attr('x2', 15)
            .attr('y2', 0)
            .style('stroke', 'inherit')


        let ele = svgEle.selectAll('graph-item').data(legendProperty);

        let ent = ele.enter().append('g')
            .attr('class', 'graph-item')

        ent.append('use')
            .attr('x', (d,i) => i * 110 + 50)
            .attr('y', 20)
            .attr('xlink:href', '#graph_legend_relation')
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

        svgEle.call(zoom).on('dblclick.zoom', null);
        g = svgEle.append('g')
        g.append("g").attr('class', 'links_relation')
    }

    //初始化位置
    function changePort() {
        gPosition = svgEle.select('g').node().getBBox()
        gPosition.x = -gPosition.x + 20
        gPosition.y = -gPosition.y + 20

        g.transition().delay(0).attr('transform', function () {
            return `translate(${gPosition.x},${gPosition.y}) scale(1)`
        })
    }


    //绘制

    let allTables, addTablesData, allLinks, addLinksData, edgelabels;
    var arrowMarker1,arrowMarker2,arrowMarker1_fan,arrowMarker2_fan;

    //参数
    typeLineColor = "#abe2df"
    classCircleColor = "#f2a"

    function svgAddNode() {
        //关系分组
        // var linkGroup = {};
        // //对连接线进行统计和分组，不区分连接线的方向，只要属于同两个实体，即认为是同一组
        // var linkmap = {};
        // for (var i = 0; i < linksForill.length; i++) {
        //     var key = linksForill[i].source < linksForill[i].target ? linksForill[i].source + ':' + linksForill[i].target : linksForill[i].target + ':' + linksForill[i].source;
        //     if (!linkmap.hasOwnProperty(key)) {
        //         linkmap[key] = 0;
        //     }
        //     linkmap[key] += 1;
        //     if (!linkGroup.hasOwnProperty(key)) {
        //         linkGroup[key] = [];
        //     }
        //     linkGroup[key].push(linksForill[i]);
        // }
        // //为每一条连接线分配size属性，同时对每一组连接线进行编号
        // for (var i = 0; i < linksForill.length; i++) {
        //     var key = linksForill[i].source < linksForill[i].target ? linksForill[i].source + ':' + linksForill[i].target : linksForill[i].target + ':' + linksForill[i].source;
        //     linksForill[i].size = linkmap[key];
        //     //同一组的关系进行编号
        //     var group = linkGroup[key];
        //     //给节点分配编号
        //     setLinkNumber(group);
        // }
        arrowMarker1 = g.append("marker")
            .attr("id", "arrowForOne1_relation")
            .attr("markerUnits", "userSpaceOnUse")
            .attr("markerWidth", "16")
            .attr("markerHeight", "15")
            .attr("viewBox", "0 0 12 12")
            .attr("refX", 10)
            .attr("refY", 6)
            .attr("orient", "auto")
            .append("path")
            .attr("d", "M2,2 L10,6 L2,10 L6,6 L2,2")
            .attr("fill",  function (){
                // if(d.relation==="rdf:type") return typeLineColor
                return '#000'
            });

        arrowMarker2 = g.append("marker")
            .attr("id", "arrowForOne1_1_relation")
            .attr("markerUnits", "userSpaceOnUse")
            .attr("markerWidth", "16")
            .attr("markerHeight", "15")
            .attr("viewBox", "0 0 12 12")
            .attr("refX", 3)
            .attr("refY", 6)
            .attr("orient", "auto")
            .append("path")
            .attr("d", "M10,2 L2,6 L10,10 L6,6 L10,2")
            .attr("fill",  function (){
                // if(d.relation==="rdf:type") return typeLineColor
                return '#0fe'
            });

        // arrowMarker1_fan = g.append("marker")
        //     .attr("id", "arrowForOne1_relation_fan")
        //     .attr("markerUnits", "userSpaceOnUse")
        //     .attr("markerWidth", "16")
        //     .attr("markerHeight", "15")
        //     .attr("viewBox", "0 0 12 12")
        //     .attr("refX", 10)
        //     .attr("refY", 6)
        //     .attr("orient", "auto")
        //     .append("path")
        //     .attr("d", "M2,2 L10,6 L2,10 L6,6 L2,2")
        //     .attr("fill",  function (){
        //         // if(d.relation==="rdf:type") return typeLineColor
        //         return '#000'
        //     });
        //
        // arrowMarker2_fan = g.append("marker")
        //     .attr("id", "arrowForOne1_1_relation_fan")
        //     .attr("markerUnits", "userSpaceOnUse")
        //     .attr("markerWidth", "16")
        //     .attr("markerHeight", "15")
        //     .attr("viewBox", "0 0 12 12")
        //     .attr("refX", 3)
        //     .attr("refY", 6)
        //     .attr("orient", "auto")
        //     .append("path")
        //     .attr("d", "M10,2 L2,6 L10,10 L6,6 L10,2")
        //     .attr("fill",  function (){
        //         // if(d.relation==="rdf:type") return typeLineColor
        //         return '#0fe'
        //     });



        linksForill.forEach(function (e) {
            let sourceNode = nodesForill.filter(function (n) {

                if ((n.nsAndPre !== undefined) && (n.nsAndPre !== null) && (n.nsAndPre !== '')) {
                    return n.nsAndPre === e.sourceNs && n.type !== 1;
                }

            })[0];

            let targetNode = nodesForill.filter(function (n) {
                if ((n.nsAndPre !== undefined) && (n.nsAndPre !== null) && (n.nsAndPre !== '')) {
                    return n.nsAndPre === e.targetNs;
                }

            })[0];

            // console.log(sourceNode)
            e.source = sourceNode;
            e.target = targetNode;

        });

        var linkGroup = {};  //用来分组，将两点之间的连线进行归类
        var linkMap = {};  //对连接线的计数
        for (var i = 0; i < linksForill.length; i++) {
            var key = linksForill[i].source.nsAndPre < linksForill[i].target.nsAndPre ? linksForill[i].source.nsAndPre + ':' + linksForill[i].target.nsAndPre : linksForill[i].target.nsAndPre + ':' + linksForill[i].source.nsAndPre;
            if (!linkMap.hasOwnProperty(key)) {
                linkMap[key] = 0 ;
            }
            linkMap[key] += 1;
            if (!linkGroup.hasOwnProperty(key)) {
                linkGroup[key] = [];
            }
            linkGroup[key].push(linksForill[i]);
        }
        //为每一条连接线分配size属性，同时对每一组连接线进行编号
        for (var i = 0; i < linksForill.length; i++) {
            var key = linksForill[i].source.nsAndPre < linksForill[i].target.nsAndPre ? linksForill[i].source.nsAndPre + ':' + linksForill[i].target.nsAndPre : linksForill[i].target.nsAndPre + ':' + linksForill[i].source.nsAndPre;
            linksForill[i].size = linkMap[key];
            //同一组的关系进行编号
            var group = linkGroup[key];
            var keyPair = key.split(':');
            var type = 'noself';
            if (keyPair[0] == keyPair[1]) {  //指向两个不同实体还是同一个实体
                type = 'self';
            }
            setLinkNumber(group, type); //给关系编号
        }


        // console.log(tables)
        // console.log(links)

        forceInit(nodesForill, linksForill)
        addTables()
        addLinks()
        initDrag()
    }

    function tick() {//force 迭代执行函数
        allTables.attr("transform", function (d) {
            return `translate(${d.x},${d.y})`
        })
        allLinks.attr('d', test)


        addLinksData.attr("marker-end", function (d, i) {
            if (d.relation === "rdf:type") return "url(#arrowForOne1_1_relation)";
            return "url(#arrowForOne1_relation)";


        })

    }

    function end() {//force end函数

    }

    function test(d,i) {


        // console.log(d)
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
        // if (d.target.x - d.source.x === 0 || tan === 0) { //斜率无穷大的情况或为0时
        //     y1 = d.target.y - d.source.y > 0 ? d.source.y + radius : d.source.y - radius;
        //     y2 = d.target.y - d.source.y > 0 ? d.target.y - radius : d.target.y + radius;
        // }
        // 防报错
        // if (!x1 || !y1 || !x2 || !y2) {
        //     return
        // }
        if (d.linknum === 0) { //设置编号为0的连接线为直线，其他连接线会均分在两边
            d.x_start = x1;
            d.y_start = y1;
            d.x_end = x2;
            d.y_end = y2;
            return 'M' + x1 + ' ' + y1 + ' L ' + x2 + ' ' + y2;
        }
        var a = radius*d.linknum/3;
        var xm = d.target.x - d.source.x > 0 ? d.source.x + Math.sqrt((radius * radius - a * a) / (1 + tan * tan)) :
            d.source.x - Math.sqrt((radius * radius - a * a) / (1 + tan * tan));
        var ym = d.target.y - d.source.y > 0 ? d.source.y + Math.sqrt((radius * radius - a * a) * tan * tan / (1 + tan * tan)) :
            d.source.y - Math.sqrt((radius * radius - a * a) * tan * tan / (1 + tan * tan));
        var xn = d.target.x - d.source.x > 0 ? d.target.x - Math.sqrt((radius * radius - a * a) / (1 + tan * tan)) :
            d.target.x + Math.sqrt((radius * radius - a * a) / (1 + tan * tan));
        var yn = d.target.y - d.source.y > 0 ? d.target.y - Math.sqrt((radius * radius - a * a) * tan * tan / (1 + tan * tan)) :
            d.target.y + Math.sqrt((radius * radius - a * a) * tan * tan / (1 + tan * tan));
        if (d.target.x - d.source.x === 0 || tan === 0) {//斜率无穷大或为0时
            ym = d.target.y - d.source.y > 0 ? d.source.y + Math.sqrt(radius * radius - a * a) : d.source.y - Math.sqrt(radius * radius - a * a);
            yn = d.target.y - d.source.y > 0 ? d.target.y - Math.sqrt(radius * radius - a * a) : d.target.y + Math.sqrt(radius * radius - a * a);
        }

        var k = (x1 - x2) / (y2 - y1);//连线垂线的斜率
        var dx = Math.sqrt(a * a / (1 + k * k)); //相对垂点x轴距离
        var dy = Math.sqrt(a * a * k * k / (1 + k * k)); //相对垂点y轴距离
        if ((y2 - y1) === 0) {
            dx = 0;
            dy = Math.sqrt(a * a);
        }
        if (a > 0) {
            var xs = k > 0 ? xm - dx : xm + dx;
            var ys = ym - dy;
            var xt = k > 0 ? xn - dx : xn + dx;
            var yt = yn - dy;
        } else {
            var xs = k > 0 ? xm + dx : xm - dx;
            var ys = ym + dy;
            var xt = k > 0 ? xn + dx : xn - dx;
            var yt = yn + dy;
        }
        //记录连线起始和终止坐标，用于定位线上文字
        d.x_start = xs;
        d.y_start = ys;
        d.x_end = xt;
        d.y_end = yt;
        return 'M' + xs + ' ' + ys + ' L' + xt + ' ' + yt;

        // var linePadding = 0;  //给连线到节点间的距离
        // var deltaX = d.target.x - d.source.x,
        //     deltaY = d.target.y - d.source.y,
        //     dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY),
        //     normX = deltaX / dist,
        //     normY = deltaY / dist;
        // var sourceX = d.source.x + (linePadding * normX),
        //     sourceY = d.source.y + (linePadding * normY),
        //     targetX = d.target.x - (linePadding * normX),
        //     targetY = d.target.y - (linePadding * normY);
        // if (d.target === d.source) {
        //     dr = 38/d.linknum;
        //     return"M" + sourceX + "," + sourceY + "A" + dr + "," + dr + " 0 1,1 " + targetX + "," + (targetY+1);
        // }else if (d.size%2!=0&&d.linknum===0) {
        //     return 'M'+ sourceX +' '+sourceY+' L '+ targetX +' '+targetY;
        // }
        // var curve =1.5;
        // var homogeneous=1.2;
        // var dr = Math.sqrt(deltaX * deltaX + deltaY * deltaY) * (d.linknum + homogeneous) / (curve * homogeneous);
        // //当节点编号为负数时，对弧形进行反向凹凸，达到对称效果
        // if(d.linknum<0){
        //     dr = Math.sqrt(deltaX*deltaX+deltaY*deltaY)*(-1*d.linknum+homogeneous)/(curve*homogeneous);
        //     return "M" + sourceX + "," + sourceY + "A" + dr + "," + dr + " 0 0,0 " + targetX + "," + targetY;
        // }
        // return "M" + sourceX + "," + sourceY + "A" + dr + "," + dr + " 0 0,1 " + targetX + "," + targetY;

    }

    function forceInit(nodes, links) {//在nodes、links原对象上添加位置属性


        forceSimulation.nodes(nodes)
            .on('end', end)
            .on('tick', tick);


        forceSimulation.force('link')
            .links(links)
            .distance(function (d) {  //线的长短
                return 200
            })
    }

    function addTables() {
        addTablesData = g.selectAll('.tbClass_relation').data(nodesForill)
            .enter()
            .append('g')
            // .on('click', addNode)
            .attr("cursor", "pointer")
            .attr('class', 'tbClass_relation')
            .attr("transform", function (d) {
                return `translate(${d.x},${d.y})`
            })
            .on("mouseover", function (d, i) {
                // console.log(d)

                d3.select(".tooltip")
                    .text(d.nsAndPre)
                    .style("left", (d3.event.pageX) + "px")
                    .style("top", (d3.event.pageY + 20) + "px")
                    .style("display", "block")
                    .style("visibility", "visible");

            })
            .on("mouseout", function (d, i) {

                d3.select(".tooltip")
                    .style("visibility", "hidden");

            })

        addTablesData.append('circle')
            .attr("r", 15)

            .attr("fill", function (d) {
                if (d.type === 0) return lineColor_0  //type
                else if (d.type === 1) return lineColor_1  //literal
                else return lineColor_2  //普通 2
            })

        // addTablesData.append('text')
        //     .text(function (d) {
        //         return d.nsAndPre
        //     })
        //     .attr('text-anchor', "middle")
        //     .attr('font-size', 12)

        allTables = g.selectAll('.tbClass_relation').data(nodesForill)
    }

    function initDrag() {
        allTables.call(d3.drag()//添加拖动事件
            .on('start', startFn)
            .on('drag', dragFn)
            .on('end', endFn)
        )
    }

    //左右连线计算
    function linkFn(d) {
        let res = []
        if (d.source.x < d.target.x) {
            res[2] = [d.target.x, d.target.y]
            res[3] = [d.target.x, d.target.y]
            res[0] = [d.source.x, d.source.y]
            res[1] = [d.source.x, d.source.y]
        } else {
            res[1] = [d.source.x, d.source.y]
            res[0] = [d.source.x, d.source.y]
            res[3] = [d.target.x, d.target.y]
            res[2] = [d.target.x, d.target.y]

        }

        return res.map(v => v.join(',')).join(' ')
    }

    function startFn(d) {//拖动开始
        if (!d3.event.active) {
            forceSimulation.alphaTarget(0.8).restart()//[0,1]
        }
        d.fx = d.x
        d.fy = d.y
    }

    function dragFn(d) {//拖动中
        d.fx = d3.event.x
        d.fy = d3.event.y
    }

    function endFn(d) {//拖动结束
        if (!d3.event.active) {
            forceSimulation.alphaTarget(0)
        }
        // d.fx = null
        // d.fy = null
    }

    //添加连线
    function addLinks() {

        addLinksData = d3.select('.links_relation')
            .selectAll("path")
            .data(linksForill)

            .enter()
            .append("path")
            .attr("cursor", "pointer")
            .attr("fill", function (d) {
                if (d.relation === "rdf:type") return typeLineColor
                return '#000'


            })
            .attr("stroke", function (d) {
                if (d.relation === "rdf:type") return typeLineColor
                return '#000'
            })
            .attr("stroke-width", 2)
            .attr('id', function (d, i) {
                return 'edgepath_relation' + i
            })
            .on("mouseover", function (d, i) {
                console.log(d)

                d3.select(".tooltip")
                    .text(d.uri)
                    .style("left", (d3.event.pageX) + "px")
                    .style("top", (d3.event.pageY + 20) + "px")
                    .style("display", "block")
                    .style("visibility", "visible");

            })
            .on("mouseout", function (d, i) {

                d3.select(".tooltip")
                    .style("visibility", "hidden");

            });
        // .attr('class', 'link-polyline')


        edgelabels = g.selectAll(".edgelabel_relation")
            .data(linksForill)
            .enter()
            .append('text')
            .attr("cursor", "pointer")
            // .style("pointer-events", "none")
            .attr('class', 'edgelabel_relation')
            .attr('id', function (d, i) {
                return 'edgelabel_relation' + i
            })
            // .attr('text-anchor',"middle")
            .attr('dx', 60)
            .attr('dy', -2)
            .attr('font-size', 12)
            .attr('fill', function (d){
                if (d.relation === "rdf:type") return typeLineColor
                return '#000'
            })
            .on("mouseover", function (d, i) {
                console.log(d)

                d3.select(".tooltip")
                    .text(d.uri)
                    .style("left", (d3.event.pageX) + "px")
                    .style("top", (d3.event.pageY + 20) + "px")
                    .style("display", "block")
                    .style("visibility", "visible");

            })
            .on("mouseout", function (d, i) {

                d3.select(".tooltip")
                    .style("visibility", "hidden");

            });

        edgelabels.append('textPath')
            .attr('xlink:href', function (d, i) {
                return '#edgepath_relation' + i
            })
            .attr("cursor", "pointer")
            // .style("pointer-events", "none")
            .text(function (d, i) {
                return d.relation
            })


        allLinks = d3.select('.links_relation')
            .selectAll('path')
            .data(linksForill)
    }


    //节点高亮
    function colsLight(data, isEnter) {
        lineLight(data, isEnter)
        d3.select(this)
            .select('rect')
            .transition()
            // .delay(50)
            .attr('fill', isEnter ? colsStyle.fillLight : colsStyle.fillDefalt)

    }

    //线高亮
    function lineLight(data, isEnter) {
        d3.selectAll('.link-polyline')
            .attr('stroke', function (d) {
                let source = d.source.cols[d.sourceIndex - 1]
                let target = d.target.cols[d.targetIndex - 1]
                if (data.id === source.id || data.id === target.id) {
                    return isEnter ? lineStyle.strokeLight : lineStyle.strokeDefault
                } else {
                    return lineStyle.strokeDefault
                }
            })
            .attr('stroke-width', function (d) {
                let source = d.source.cols[d.sourceIndex - 1]
                let target = d.target.cols[d.targetIndex - 1]
                if (data.id === source.id || data.id === target.id) {
                    return isEnter ? lineStyle.strokeWidthLight : lineStyle.strokeWidthDefault
                } else {
                    return lineStyle.strokeWidthDefault
                }
            })
    }

    function setLinkNumber(group,type) {
        // if (group.length == 0) return;
        // if (group.length == 1) {
        //     group[0].linknum = 0;
        //     return;
        // }
        // var maxLinkNumber = group.length % 2 == 0 ? group.length / 2 : (group.length - 1) / 2;
        // if (group.length % 2 == 0) {
        //     var startLinkNum = -maxLinkNumber + 0.5;
        //     for (var i = 0; i < group.length; i++) {
        //         group[i].linknum = startLinkNum++;
        //     }
        // } else {
        //     var startLinkNum = -maxLinkNumber;
        //     for (var i = 0; i < group.length; i++) {
        //         group[i].linknum = startLinkNum++;
        //     }
        // }
        if(group.length==0) return;
        //对该分组内的关系按照方向进行分类，此处根据连接的实体ASCII值大小分成两部分
        var linksA = [], linksB = [];
        for(var i = 0 ;i < group.length; i++){
            var link = group[i];
            if(link.source.nsAndPre < link.target.nsAndPre){
                linksA.push(link);
            }else{
                linksB.push(link);
            }
        }
        console.log(linksA,linksB)
        //确定关系最大编号。为了使得连接两个实体的关系曲线呈现对称，根据关系数量奇偶性进行平分。
        //特殊情况：当关系都是连接到同一个实体时，不平分
        var maxLinkNumber = 1;
        if(type==='self'){
            maxLinkNumber = group.length;
        }else{
            maxLinkNumber = group.length%2==0?group.length/2:(group.length+1)/2;
        }
        console.log(maxLinkNumber)
        //如果两个方向的关系数量一样多，直接分别设置编号即可
        if(linksA.length===linksB.length){
            var startLinkNumber = 1;
            for(var i=0;i<linksA.length;i++){
                linksA[i].linknum = startLinkNumber++;
            }
            startLinkNumber = 1;
            for(var i=0;i<linksB.length;i++){
                linksB[i].linknum = startLinkNumber++;
            }
        }else{//当两个方向的关系数量不对等时，先对数量少的那组关系从最大编号值进行逆序编号，然后在对另一组数量多的关系从编号1一直编号到最大编号，再对剩余关系进行负编号
            //如果抛开负号，可以发现，最终所有关系的编号序列一定是对称的（对称是为了保证后续绘图时曲线的弯曲程度也是对称的）
            var biggerLinks,smallerLinks;
            if(linksA.length>linksB.length){
                biggerLinks = linksA;
                smallerLinks = linksB;
            }else{
                biggerLinks = linksB;
                smallerLinks = linksA;
            }

            var startLinkNumber = maxLinkNumber;
            for(var i=0;i<smallerLinks.length;i++){
                smallerLinks[i].linknum = startLinkNumber--;
            }
            var tmpNumber = startLinkNumber;

            startLinkNumber = 1;
            var p = 0;
            while(startLinkNumber<=maxLinkNumber){
                biggerLinks[p++].linknum = startLinkNumber++;
            }
            //开始负编号
            startLinkNumber = 0-tmpNumber;
            for(var i=p;i<biggerLinks.length;i++){
                biggerLinks[i].linknum = startLinkNumber++;
            }
        }


    }

    return {
        changePort,
        svgAddNode,
    }
}


